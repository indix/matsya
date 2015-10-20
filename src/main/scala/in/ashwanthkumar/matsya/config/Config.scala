package in.ashwanthkumar.matsya.config

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

case class ClusterConfig(name: String,
                         spotASG: String,
                         odASG: String,
                         machineType: String,

                         /**
                          * Key -> AZ
                          * Value -> Subnet
                          */
                         subnets: Map[String, String],
                         maxThreshold: Double,
                         maxNrOfTimes: Int, // TODO - Should we use duration insted of nr of times? 
                         maxBidPrice: Double,
                         odPrice: Double,
                         fallBackToOnDemand: Boolean)

object ClusterConfig {
  def from(config: Config): ClusterConfig = {
    val subnetConfigs = config.getConfig("subnets")
    import scala.collection.JavaConversions._
    val subnets = subnetConfigs.entrySet.map(entry => (entry.getKey, entry.getValue.unwrapped().toString)).toMap
    ClusterConfig(
      name = config.getString("name"),
      spotASG = config.getString("spot-asg"),
      odASG = config.getString("od-asg"),
      machineType = config.getString("machine-type"),
      subnets = subnets,
      maxThreshold = config.getDouble("max-threshold"),
      maxNrOfTimes = config.getInt("nr-of-times"),
      maxBidPrice = config.getDouble("bid-price"),
      odPrice = config.getDouble("od-price"),
      fallBackToOnDemand = config.getBoolean("fallback-to-od")
    )
  }
}

case class MatsyaConfig(clusters: List[ClusterConfig], workingDir: String) {
  def stateDir = workingDir + "/" + "state"
  def historyDir = workingDir + "/" + "history"
  def getRegion = "us-east-1"
}

object MatsyaConfig {
  def from(config: Config) = {
    import scala.collection.JavaConversions._
    val clusters = config.getConfigList("clusters").map(ClusterConfig.from).toList
    MatsyaConfig(
      clusters = clusters,
      workingDir = config.getString("working-dir")
    )
  }
}

object ConfigReader {
  val NAMESPACE = "matsya"

  def readFrom(file: String): MatsyaConfig = load(ConfigFactory.parseFile(new File(file)))

  def load(name: String): MatsyaConfig = load(ConfigFactory.load(name))

  def load(config: Config): MatsyaConfig = {
    val globalConfig: Config = config.getConfig(NAMESPACE)
    MatsyaConfig.from(globalConfig)
  }
}