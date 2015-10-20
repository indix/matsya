package in.ashwanthkumar.matsya

import java.lang.{Double => JDouble}

import com.amazonaws.regions.{Region => AWSRegion}
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest
import in.ashwanthkumar.aws.Region
import in.ashwanthkumar.config.{ConfigReader, MatsyaConfig}
import in.ashwanthkumar.store.RocksDBStore
import org.joda.time.DateTime
import org.rocksdb.RocksDB

import scala.collection.JavaConverters._

class Matsya(ec2: AmazonEC2Client,
             config: MatsyaConfig,
             timeSeriesStore: MatsyaTimeSeriesStore,
             stateStore: MatsyaStateStore) {

  def updateSpotPrices(): Unit = {
    val machineTypes = config.machineTypes()
    // FIXME - Should Group By Region
    val request = new DescribeSpotPriceHistoryRequest()
      .withStartTime(DateTime.now.minusMinutes(30).toDate)
      .withEndTime(DateTime.now.toDate)
      .withProductDescriptions("Linux/UNIX (Amazon VPC)") // FIXME - Add support for more Product Types
      .withInstanceTypes(machineTypes)

    ec2.describeSpotPriceHistory(request)
      .getSpotPriceHistory.asScala
      .groupBy(_.getInstanceType)
      .foreach((keyValue) => {
        val instanceType = keyValue._1
        val prices = keyValue._2
        val points = prices.map(p => new Point()
          .setAvailabilityZone(p.getAvailabilityZone)
          .setPrice(JDouble.valueOf(p.getSpotPrice))
          .setTimestamp(p.getTimestamp.getTime)
        )

        timeSeriesStore.addPoints(instanceType, config.getRegion, points.asJava)
      })
  }

  def checkClusters(): Unit = {
    config.getClustes.asScala.foreach(cluster => {
      val state = stateStore.get(cluster.getName)
      val region = Region.fromString(MatsyaConfiguration.REGION.getName)
//      timeSeriesStore.historyFor(cluster.getMachineType, MatsyaConfiguration.REGION.getName, region.getAvailabilityZone(state))
    })
  }

  def shutdown(): Unit = {
    timeSeriesStore.close()
    stateStore.close()
  }
}

object MatsyaConfiguration {
  val REGION = Regions.US_EAST_1
}

object OldMatsyaApp extends App {
  val configPath = args(0)
  val config = ConfigReader.readFrom(configPath)

  val ec2 = new AmazonEC2Client()
  ec2.setRegion(AWSRegion.getRegion(MatsyaConfiguration.REGION)) // FIXME
  val system = new Matsya(
    ec2,
    config,
    // FIXME - Move this inside Config and let the Matsya use the Factory methods to create objects
    new MatsyaTimeSeriesStore(new RocksDBStore(config.timeseriesDir())),
    new MatsyaStateStore(RocksDB.open(config.stateDir())) // FIXME - Use the Store abstraction from megadutha
  )

  system.updateSpotPrices()
  system.shutdown()

}
