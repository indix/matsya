package in.ashwanthkumar.matsya

object ClusterMode {
  val Spot = 1
  val OnDemand = 2
}

case class State(name: String,
                 az: String,
                 price: Double,
                 nrOfTimes: Int,
                 clusterMode: Int,
                 lastModeChangedTimestamp: Long,
                 timestamp: Long) {

  def crossedThreshold() = this.copy(nrOfTimes = nrOfTimes + 1, timestamp = System.currentTimeMillis())
  def resetCount() = this.copy(nrOfTimes = 0)
  def toSpot(az: String, newPrice: Double) = this.resetCount().copy(lastModeChangedTimestamp = System.currentTimeMillis(),
    az = az, price = newPrice, timestamp = System.currentTimeMillis(), clusterMode = ClusterMode.Spot)
  def toOnDemand(onDemandPrice: Double) = this.resetCount().copy(price = onDemandPrice, timestamp = System.currentTimeMillis(),
    clusterMode = ClusterMode.OnDemand)


  def onSpot = clusterMode == ClusterMode.Spot
  def onOD = clusterMode == ClusterMode.OnDemand

  def mode = if(onSpot) "Spot" else "On-Demand"
}

trait StateStore extends AutoCloseable {
  def exists(clusterName: String): Boolean
  def get(clusterName: String): State
  def save(clusterName: String, state: State): Unit

  def updateLastRun(identifier: String): Unit
  def lastRun(identifier: String): Long
}
