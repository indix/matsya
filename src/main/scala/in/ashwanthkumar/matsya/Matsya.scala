package in.ashwanthkumar.matsya

import java.lang.{Double => JDouble}

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model.{DescribeAutoScalingGroupsRequest, UpdateAutoScalingGroupRequest}
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest
import com.typesafe.scalalogging.slf4j.Logger
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class Matsya(ec2: AmazonEC2Client,
             asgClient: AmazonAutoScalingClient,
             config: MatsyaConfig,
             timeSeriesStore: TimeSeriesStore,
             stateStore: StateStore) {

  private val LAST_RUN_IDENTIFIER = "MatsyaLastRun"

  private val logger = Logger(LoggerFactory.getLogger(classOf[Matsya]))
  private val lastRunOn = stateStore.lastRun(LAST_RUN_IDENTIFIER)
  logger.info("Last sync happened on {}", new DateTime(lastRunOn))

  // As of now, we only find new settings that were added to the config
  def syncClusterSettings(): Unit = {
    config.clusters
      .filterNot(c => stateStore.exists(c.name))
      .foreach(c => {
        logger.info("New cluster {} found", c.name)
        val spot = describeASG(c.spotASG)
        val od = describeASG(c.odASG)
        val (spotCount, odCount, az) = (spot, od) match {
          case (Some(spotASG), None) => (spotASG.getDesiredCapacity.intValue(), 0, spotASG.getAvailabilityZones.asScala.head)
          case (None, Some(odASG)) => (0, odASG.getDesiredCapacity.intValue(), odASG.getAvailabilityZones.asScala.head)
          case (None, None) =>
            throw new RuntimeException("both Spot and OD ASGs are not to be found. We don't create ASGs if not present yet")
          case (Some(spotASG), Some(odASG)) if spotASG.getDesiredCapacity > 0 && odASG.getDesiredCapacity > 0 =>
            throw new RuntimeException("both Spot and OD ASGs have > 0 as desired capacity")
        }
        val state = State(c.name, az, c.maxBidPrice, 0, spotCount, odCount, System.currentTimeMillis())
        stateStore.save(c.name, state)
      })
  }

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
          })
          logger.info(s"${metrics.size} new price points found for $instanceType in $az")

          pushMetricsToHistory(startDate, instanceType, az, metrics)
        })
    }
  }

  def pushMetricsToHistory(startDate: DateTime, instanceType: String, az: String, metrics: List[Metric]): Unit = {
    val historySoFar: List[Metric] = timeSeriesStore.get(instanceType, az)

    // Always store only latest 100 data points - Do we need more?
    val newHistory = (historySoFar ++ metrics)
      .sortBy(_.timestamp)(implicitly[Ordering[Long]].reverse)
      .take(100)

    logger.info(s"Syncing Price History for $instanceType in $az from $startDate")
    timeSeriesStore.batchPut(instanceType, az, newHistory)
  }

  def monitorClusters(): Unit = {
    config.clusters.foreach(clusterConfig => {
      val state = stateStore.get(clusterConfig.name)
      if (hasViolatedPrice(clusterConfig, state)) {
        if (hasViolatedOnDuration(clusterConfig, state)) {
          thresholdCrossed(clusterConfig, state)
        } else {
          logger.info(s"${clusterConfig.name} has crossed the threshold ${state.nrOfTimes + 1} times so far out of ${clusterConfig.maxNrOfTimes} ")
          logger.info(s"Existing price=${state.price} and max bid price=${clusterConfig.maxBidPrice}")
          stateStore.save(clusterConfig.name, state.crossedThreshold())
        }
      } else {
        stateStore.save(clusterConfig.name, state.resetCount())
        // The bid price in the current AZ is well within the threshold
        logger.info(s"current price=${state.price} is within the max bid price=${clusterConfig.maxBidPrice} on az=${state.az}")
      }
    })
  }

  def thresholdCrossed(clusterConfig: ClusterConfig, state: State): Unit = {
    findCheapestAZ(clusterConfig, state, timeSeriesStore) match {
      case Some((cheapestAZ, costOnAZ)) =>
        moveASGToNewAZ(clusterConfig, state, cheapestAZ, costOnAZ)
      case _ =>
        // TODO - Move to OD in here
        logger.warn(s"Can't find a AZ where the spot price is lower than the maxBidPrice ($$${clusterConfig.maxBidPrice})")
    }
  }

  // TODO - Make this pluggable
  def hasViolatedPrice(clusterConfig: ClusterConfig, state: State): Boolean = {
    val currentThreshold = state.price / clusterConfig.maxBidPrice
    logger.info(s"Current price threshold is ${currentThreshold * 100}% of ($$${clusterConfig.maxBidPrice}), " +
      s"acceptable threshold is ${clusterConfig.maxThreshold * 100}% of $$${state.price} (=$$${clusterConfig.maxThreshold * state.price}) in az=${state.az}")
    currentThreshold > clusterConfig.maxThreshold
  }

  // TODO - Make thie pluggable
  def hasViolatedOnDuration(clusterConfig: ClusterConfig, state: State): Boolean = {
    (state.nrOfTimes + 1) >= clusterConfig.maxNrOfTimes
  }

  def findCheapestAZ(clusterConfig: ClusterConfig, state: State, timeseriesStore: TimeSeriesStore) = {
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

  def moveASGToNewAZ(clusterConfig: ClusterConfig, state: State, newLowestAZ: String, costInAz: Double): Unit = {
    logger.info(s"Switching the AZ for the Cluster ${clusterConfig.name} from ${state.az} to $newLowestAZ")
    logger.info(s"Cost of new AZ=$costInAz while max bid price is=${clusterConfig.maxBidPrice}")
    asgClient.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
      .withAutoScalingGroupName(clusterConfig.spotASG)
      .withVPCZoneIdentifier(clusterConfig.subnets(newLowestAZ))
    )
    // TODO - Add support for swapping out to OD as well
    stateStore.save(clusterConfig.name, state.updateAz(newLowestAZ, costInAz))
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
    new RocksDBStore(config.stateDir)
  )

  system.syncClusterSettings()
  val newPrices = system.updatePriceHistory()
  system.monitorClusters()
  system.shutdown()
}
