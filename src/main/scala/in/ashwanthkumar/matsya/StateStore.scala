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

  def crossedThreshold() = this.copy(nrOfTimes = nrOfTimes + 1)
  def resetCount() = this.copy(nrOfTimes = 0)
  def updateAz(az: String, newPrice: Double) = this.copy(az = az, nrOfTimes = 0, price = newPrice, timestamp = System.currentTimeMillis(), clusterMode = ClusterMode.Spot)
  def movedToSpot() = this.copy(lastModeChangedTimestamp = System.currentTimeMillis())
  def movedToOnDemand(onDemandPrice: Double) = this.copy(nrOfTimes = 0, price = onDemandPrice, timestamp = System.currentTimeMillis(), clusterMode = ClusterMode.OnDemand)
}

trait StateStore extends AutoCloseable {
  def exists(clusterName: String): Boolean
  def get(clusterName: String): State
  def save(clusterName: String, state: State): Unit

  def updateLastRun(identifier: String): Unit
  def lastRun(identifier: String): Long
}
