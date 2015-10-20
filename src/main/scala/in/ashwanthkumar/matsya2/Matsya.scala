package in.ashwanthkumar.matsya2

import java.io.File
import java.lang.reflect.{ParameterizedType, Type}
import java.util.{List => JList}

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.common.primitives.{Doubles, Longs}
import com.typesafe.scalalogging.slf4j.Logger
import in.ashwanthkumar.config.{ConfigReader, MatsyaConfig}
import org.joda.time.DateTime
import org.rocksdb.{Options, RocksDB}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

case class Metric(machineType: String,
                  az: String,
                  price: Double,
                  timestamp: Long)

trait TimeSeriesStore extends AutoCloseable {
  def get(machineType: String, az: String): List[Metric]
  def exists(machineType: String, az: String): Boolean
  def batchPut(machineType: String, az: String, metrics: List[Metric])
}

case class State(name: String,
                 az: String,
                 price: Double,
                 nrOfTimes: Int,
                 spotCount: Int,
                 odCount: Int,
                 timestamp: Long) {

  def crossedThreshold() = this.copy(nrOfTimes = nrOfTimes + 1)
  def updateAz(az: String, newPrice: Double) = this.copy(az = az, nrOfTimes = 0, price = newPrice, timestamp = System.currentTimeMillis())
}

trait StateStore extends AutoCloseable {
  def exists(clusterName: String): Boolean
  def get(clusterName: String): State
  def save(clusterName: String, state: State): Unit

  def updateLastRun(identifier: String): Unit
  def lastRun(identifier: String): Long
}

class RocksDBStore(input: String) extends TimeSeriesStore with StateStore {

  RocksDB.loadLibrary()

  private lazy val delegate = {
    val dbOptions = new Options()
      .setCreateIfMissing(true)
      .setMaxBackgroundCompactions(2)
    new File(input).mkdirs
    RocksDB.open(dbOptions, input)
  }

  override def exists(name: String, az: String): Boolean = {
    delegate.get(bytes(name + az)) != null
  }
  override def exists(name: String): Boolean = {
    delegate.get(bytes(name)) != null
  }
  override def get(name: String, az: String): List[Metric] = {
    val value = delegate.get(bytes(name + az))
    if (value != null) JSONUtil.fromJson[List[Metric]](new String(value))
    else List.empty[Metric]
  }
  override def batchPut(name: String, az: String, metrics: List[Metric]): Unit = {
    delegate.put(bytes(name + az), JSONUtil.toJSON(metrics).getBytes)
  }
  override def get(name: String): State = {
    JSONUtil.fromJson[State](new String(delegate.get(name.getBytes)))
  }
  override def save(name: String, state: State): Unit = {
    delegate.put(bytes(name), JSONUtil.toJSON(state).getBytes)
  }
  override def close(): Unit = delegate.close()

  override def updateLastRun(identifier: String): Unit = {
    delegate.put(bytes(identifier), Longs.toByteArray(System.currentTimeMillis()))
  }
  override def lastRun(identifier: String): Long = {
    val value = delegate.get(bytes(identifier))
    if (value != null) Longs.fromByteArray(value)
    else 0
  }

  private def bytes(str: String) = str.getBytes
}

object JSONUtil {
  private val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
  def toJSON(o: AnyRef) = mapper.writeValueAsString(o)
  def fromJson[T: Manifest](json: String) = mapper.readValue[T](json, typeReference[T])

  private[this] def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private[this] def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) {
      m.erasure
    }
    else new ParameterizedType {
      def getRawType = m.erasure
      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray
      def getOwnerType = null
    }
  }
}

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
    config.getClustes.asScala
      .filterNot(c => stateStore.exists(c.getName))
      .foreach(c => {
        logger.info("New cluster {} found", c.getName)
        val spot = describeASG(c.getSpotASG)
        val od = describeASG(c.getOdASG)
        val (spotCount, odCount, az) = (spot, od) match {
          case (Some(spotASG), None) => (spotASG.getDesiredCapacity.intValue(), 0, spotASG.getAvailabilityZones.asScala.head)
          case (None, Some(odASG)) => (0, odASG.getDesiredCapacity.intValue(), odASG.getAvailabilityZones.asScala.head)
          case (None, None) =>
            throw new RuntimeException("both Spot and OD ASGs are not to be found. We don't create ASGs if not present yet")
          case (Some(spotASG), Some(odASG)) if spotASG.getDesiredCapacity > 0 && odASG.getDesiredCapacity > 0 =>
            throw new RuntimeException("both Spot and OD ASGs have > 0 as desired capacity")
        }
        val state = State(c.getName, az, c.getBidPrice, 0, spotCount, odCount, System.currentTimeMillis())
        stateStore.save(c.getName, state)
      })
  }

  def updatePriceHistory(): Boolean = {
    val machineTypes = config.machineTypes()
    val startDate = new DateTime(lastRunOn).toDate
    val request = new DescribeSpotPriceHistoryRequest()
      .withStartTime(startDate)
      .withEndTime(DateTime.now.toDate)
      .withProductDescriptions("Linux/UNIX (Amazon VPC)") // FIXME - Add support for more Product Types
      .withInstanceTypes(machineTypes)

    val newPrices = ec2.describeSpotPriceHistory(request)
      .getSpotPriceHistory.asScala.toList
      .filter(_.getTimestamp.getTime > lastRunOn)

    newPrices
      .groupBy(s => (s.getInstanceType, s.getAvailabilityZone))
      .foreach((tuple) => {
        val ((name, az), prices) = tuple
        val metrics = prices.map(p => Metric(
          machineType = name,
          az = az,
          price = Doubles.stringConverter().convert(p.getSpotPrice),
          timestamp = p.getTimestamp.getTime
        ))
        logger.info("{} new price points found on {} in {}", metrics.size.toString, name, az)

        val historySoFar: List[Metric] = timeSeriesStore.get(name, az)

        // Always store only latest 100 data points
        val newHistory = (historySoFar ++ metrics)
          .sortBy(_.timestamp)(implicitly[Ordering[Long]].reverse)
          .take(100)

        logger.info("Syncing Price History for {} in {} from {}", name, az, startDate)
        timeSeriesStore.batchPut(name, az, newHistory)
      })

    newPrices.nonEmpty // Did we process anything at all?
  }

  def checkClusters(): Unit = {
    config.getClustes.asScala.foreach(clusterConfig => {
      val state = stateStore.get(clusterConfig.getName)
      val currentThreshold = state.price / clusterConfig.getBidPrice
      logger.info(s"Current price threshold is $currentThreshold, acceptable threshold is ${clusterConfig.getMaxThreshold}")
      if (currentThreshold > clusterConfig.getMaxThreshold) {
        if ((state.nrOfTimes + 1) >= clusterConfig.getNrOfTimes) {
          logger.info("Finding next cheapest AZ for {}", clusterConfig.getName)
          val (newLowestAZ, costInAz) = (clusterConfig.allAZs().asScala.toSet - state.az).map(az => {
            val history = timeSeriesStore.get(clusterConfig.getMachineType, az)
            az -> history.head.price
          }).minBy(_._2)
          // TODO - Make the switch on the ASG here
          // TODO - Add support for swapping out to OD as well
          logger.info(s"Switching the AZ for the Cluster ${clusterConfig.getName} from ${state.az} to $newLowestAZ")
          logger.info(s"Cost of new AZ=$costInAz while max bid price is=${clusterConfig.getBidPrice}")
          stateStore.save(clusterConfig.getName, state.updateAz(newLowestAZ, costInAz))
        } else {
          logger.info(s"${clusterConfig.getName} has crossed the threshold ${state.nrOfTimes + 1} times so far out of ${clusterConfig.getNrOfTimes} ")
          logger.info(s"Existing price=${state.price} and max bid price=${clusterConfig.getBidPrice}")
          stateStore.save(clusterConfig.getName, state.crossedThreshold())
        }
      } else {
        // The bid price in the current AZ is well within the threshold
        logger.info(s"current price=${state.price} is within the max bid price=${clusterConfig.getBidPrice} on az=${state.az}")
      }
    })
  }

  def shutdown(): Unit = {
    stateStore.updateLastRun(LAST_RUN_IDENTIFIER)
    timeSeriesStore.close()
    stateStore.close()
  }

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
    new RocksDBStore(config.timeseriesDir()),
    new RocksDBStore(config.stateDir())
  )

  system.syncClusterSettings()
  val newPrices = system.updatePriceHistory()
  if (!newPrices) {
    logger.warn("No new spot price changes detected.")
//    system.shutdown()
//    System.exit(0)
  }
  system.checkClusters()
  system.shutdown()
}
