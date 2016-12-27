package com.indix.matsya

import java.lang.{Double => JDouble}

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model.{DescribeAutoScalingGroupsRequest, UpdateAutoScalingGroupRequest}
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest
import com.typesafe.scalalogging.slf4j.Logger
import in.ashwanthkumar.slack.webhook.Slack
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class Matsya(ec2: AmazonEC2Client,
             asgClient: AmazonAutoScalingClient,
             config: MatsyaConfig,
             timeSeriesStore: TimeSeriesStore,
             stateStore: StateStore,
             notifier: Notifier) {

  private val LAST_RUN_IDENTIFIER = "MatsyaLastRun"

  private val logger = Logger(LoggerFactory.getLogger(getClass))
  private val lastRunOn = stateStore.lastRun(LAST_RUN_IDENTIFIER)
  logger.info("Last sync happened on {}", new DateTime(lastRunOn))

  def updatePriceHistory(): Unit = {
    val machineTypes = config.machineTypes
    val startDate = new DateTime(lastRunOn)
    val request = new DescribeSpotPriceHistoryRequest()
      .withStartTime(startDate.toDate)
      .withEndTime(now.toDate)
      .withProductDescriptions("Linux/UNIX (Amazon VPC)") // FIXME - Add support for more Product Types
      .withInstanceTypes(machineTypes.asJava)

    val newPrices = ec2.describeSpotPriceHistory(request)
      .getSpotPriceHistory.asScala.toList
      .filter(_.getTimestamp.getTime > lastRunOn)

    if (newPrices.isEmpty) logger.warn("No new spot price changes detected.")
    else {
      newPrices
        .groupBy(s => (s.getInstanceType, s.getAvailabilityZone))
        .foreach((tuple) => {
          val ((instanceType, az), prices) = tuple
          val metrics = prices.map(p => {
            val price = JDouble.valueOf(p.getSpotPrice)
            Metric(instanceType, az, price, p.getTimestamp.getTime)
          }).sortBy(_.timestamp)(implicitly[Ordering[Long]].reverse)
          logger.info(s"${metrics.size} new price points found for $instanceType in $az")

          pushPriceChanges(startDate, instanceType, az, metrics)
        })
    }
  }

  // As of now, we only find new settings that were added to the config
  def syncClusterSettings(): Unit = {
    config.clusters
      .filterNot(c => stateStore.exists(c.name))
      .foreach(c => {
        logger.info(s"New cluster ${c.name} found")
        val spot = describeASG(c.spotASG)
        val od = describeASG(c.odASG)
        val (az, mode) = (spot, od) match {
          case (None, None) =>
            notifier.error("both Spot and OD ASGs are not to be found. We don't create ASGs if not present yet")
            throw new RuntimeException("both Spot and OD ASGs are not to be found. We don't create ASGs if not present yet")
          case (Some(spotASG), Some(odASG)) if spotASG.getDesiredCapacity > 0 && odASG.getDesiredCapacity > 0 =>
            notifier.error("both Spot and OD ASGs have > 0 as desired capacity. Matsya can work with only 1 ASG having desired > 0.")
            throw new RuntimeException("both Spot and OD ASGs have > 0 as desired capacity. Matsya can work with only 1 ASG having desired > 0.")
          // TODO - Look into this, If in future we decide to support minOD instances per cluster as a feature
          case (_, Some(odASG)) if odASG.getDesiredCapacity > 0 => (odASG.getAvailabilityZones.asScala.head, ClusterMode.OnDemand)
          case (Some(spotASG), _) => (spotASG.getAvailabilityZones.asScala.head, ClusterMode.Spot)
        }
        val lastKnownPrice = timeSeriesStore.get(c.machineType, az).maxBy(_.timestamp)
        val state = State(c.name, az, lastKnownPrice.price, nrOfTimes = 0, mode, lastModeChangedTimestamp = 0, System.currentTimeMillis())
        stateStore.save(c.name, state)
      })
  }

  def pushPriceChanges(startDate: DateTime, instanceType: String, az: String, metrics: List[Metric]): Unit = {
    val historySoFar: List[Metric] = timeSeriesStore.get(instanceType, az)

    // Always store only latest 100 data points - Do we need more?
    val newHistory = (historySoFar ++ metrics)
      .sortBy(_.timestamp)(implicitly[Ordering[Long]].reverse)
      .take(100)

    logger.info(s"Syncing Price History for $instanceType in $az from $startDate")
    timeSeriesStore.batchPut(instanceType, az, newHistory)
  }

  def checkClusters(): Unit = {
    config.clusters.foreach(clusterConfig => {
      val previousState = stateStore.get(clusterConfig.name)
      val currentPrice = timeSeriesStore.get(clusterConfig.machineType, previousState.az).maxBy(_.timestamp).price
      val updatedState = previousState.copy(price = currentPrice)
      val verifier = new DefaultVerifier(clusterConfig, updatedState)
      updatedState match {
        case s if s.onSpot && verifier.hasViolated =>
          moveToCheapestAZOnSpotIfAvailable(clusterConfig, s, fallbackToOD = clusterConfig.fallBackToOnDemand)
        case s if s.onSpot && verifier.isPriceViolation =>
          val thresholdMessage = s"${clusterConfig.name} has crossed the threshold ${s.nrOfTimes + 1}/${clusterConfig.maxNrOfTimes} times"
          logger.info(thresholdMessage)
          notifier.info(thresholdMessage)
          logger.info(s"Existing price=${s.price} and max bid price=${clusterConfig.maxBidPrice}")
          stateStore.save(clusterConfig.name, s.crossedThreshold())
        case s if s.onOD && hasCooledOff(clusterConfig, s) =>
          moveToCheapestAZOnSpotIfAvailable(clusterConfig, s, fallbackToOD = false)
        case s if s.onSpot =>
          stateStore.save(clusterConfig.name, s.resetCount())
          logger.info(s"CurrentSpotPrice = ${s.price} is within the MaxBidPrice = ${clusterConfig.maxBidPrice} on AZ = ${s.az}")
      }
    })
  }

  def moveToCheapestAZOnSpotIfAvailable(clusterConfig: ClusterConfig, state: State, fallbackToOD: Boolean = false): Unit = {
    findCheapestAZForSpot(clusterConfig, state, timeSeriesStore) match {
      case Some((cheapestAZ, costOnAZ)) =>
        moveToNewAZ(clusterConfig, state, cheapestAZ, costOnAZ)
      case None =>
        logger.warn(s"Can't find a AZ where the spot price is lower than the maxBidPrice ($$${clusterConfig.maxBidPrice})")
        if (fallbackToOD) {
          logger.info(s"Moving to On Demand for ${clusterConfig.name}")
          moveToOnDemand(clusterConfig, state)
        } else {
          logger.error("Fallback to OD is disabled or we're already on OD, not trying")
        }
    }
  }

  def moveToOnDemand(clusterConfig: ClusterConfig, state: State): Unit = {
    val fleetSize = syncFleetSize(clusterConfig, state)
    asgClient.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
      .withAutoScalingGroupName(clusterConfig.odASG)
      .withDesiredCapacity(fleetSize)
      .withVPCZoneIdentifier(clusterConfig.subnets(state.az))
    )
    logger.info(s"Moved ${clusterConfig.name} to On Demand (${clusterConfig.odASG}) with size = $fleetSize in ${clusterConfig.subnets(state.az)}")
    asgClient.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
      .withAutoScalingGroupName(clusterConfig.spotASG)
      .withDesiredCapacity(0)
    )
    stateStore.save(clusterConfig.name, state.toOnDemand(clusterConfig.odPrice))
  }

  def findCheapestAZForSpot(clusterConfig: ClusterConfig, state: State, timeseriesStore: TimeSeriesStore) = {
    logger.info("Finding next cheapest AZ for {}", clusterConfig.name)
    val candidates = (clusterConfig.allAZs - state.az).map(az => {
      val history = timeseriesStore.get(clusterConfig.machineType, az)
      az -> history.head.price
    }).filter(tuple => {
      val (az, price) = tuple
      val cutOffThreshold = clusterConfig.maxThreshold * clusterConfig.maxBidPrice
      price < cutOffThreshold
    })
    if (candidates.nonEmpty) Some(candidates.minBy(_._2))
    else None
  }

  def moveToNewAZ(clusterConfig: ClusterConfig, state: State, newLowestAZ: String, costInNewAZ: Double): Unit = {
    logger.info(s"Switching the AZ for the Cluster ${clusterConfig.name} from ${state.az} to $newLowestAZ")
    notifier.info(s"Switching the AZ for the Cluster ${clusterConfig.name} from ${state.az} to $newLowestAZ")
    logger.info(s"Cost of new AZ=$costInNewAZ while max bid price is=${clusterConfig.maxBidPrice}")
    notifier.info(s"Cost of new AZ=$costInNewAZ while max bid price is=${clusterConfig.maxBidPrice}")
    val fleetSize: Int = syncFleetSize(clusterConfig, state)
    // Always reset the OD Desired capacity
    asgClient.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
      .withAutoScalingGroupName(clusterConfig.spotASG)
      .withVPCZoneIdentifier(clusterConfig.subnets(newLowestAZ))
      .withDesiredCapacity(fleetSize)
    )
    asgClient.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
      .withAutoScalingGroupName(clusterConfig.odASG)
      .withDesiredCapacity(0)
    )
    val newState = state.toSpot(newLowestAZ, costInNewAZ)
    stateStore.save(clusterConfig.name, newState)
  }

  def hasCooledOff(clusterConfig: ClusterConfig, s: State): Boolean = {
    now.getMillis - s.lastModeChangedTimestamp > clusterConfig.odCoolOffPeriodInMillis
  }

  def syncFleetSize(clusterConfig: ClusterConfig, state: State): Int = {
    val asg = if (state.onOD) describeASG(clusterConfig.odASG) else describeASG(clusterConfig.spotASG)
    asg match {
      case Some(a) => a.getDesiredCapacity
      case _ =>
        logger.error(s"Can't find any ASG for ${clusterConfig.name} when on ${state.mode}. Going ahead with 0.")
        notifier.error(s"Can't find any ASG for ${clusterConfig.name} when on ${state.mode}. Go ahead with 0.")
        0
    }
  }

  def shutdown(): Unit = {
    stateStore.updateLastRun(LAST_RUN_IDENTIFIER)
    timeSeriesStore.close()
    stateStore.close()
  }

  // for mocking in tests
  private def now: DateTime = DateTime.now

  private def describeASG(name: String) = {
    val asg = asgClient.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest().withAutoScalingGroupNames(name))
    asg.getAutoScalingGroups.asScala match {
      case x if x.isEmpty => None
      case x => Option(x.head)
    }
  }
}

object MatsyaApp extends App {
  val logger = Logger(LoggerFactory.getLogger(MatsyaApp.getClass))
  val configPath = args(0)
  val config = ConfigReader.readFrom(configPath)
  val system = new Matsya(
    new AmazonEC2Client(),
    new AmazonAutoScalingClient(),
    config,
    new RocksDBStore(config.historyDir),
    new RocksDBStore(config.stateDir),
    new SlackNotifier(config.slackWebHook.map(new Slack(_)))
  )

  system.updatePriceHistory()
  system.syncClusterSettings()
  system.checkClusters()
  system.shutdown()
}
