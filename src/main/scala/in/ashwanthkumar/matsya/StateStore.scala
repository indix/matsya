package in.ashwanthkumar.matsya


case class State(name: String,
                 az: String,
                 price: Double,
                 nrOfTimes: Int,
                 spotCount: Int,
                 odCount: Int,
                 timestamp: Long) {

  def crossedThreshold() = this.copy(nrOfTimes = nrOfTimes + 1)
  def resetCount() = this.copy(nrOfTimes = 0)
  def updateAz(az: String, newPrice: Double) = this.copy(az = az, nrOfTimes = 0, price = newPrice, timestamp = System.currentTimeMillis())
}

trait StateStore extends AutoCloseable {
  def exists(clusterName: String): Boolean
  def get(clusterName: String): State
  def save(clusterName: String, state: State): Unit

  def updateLastRun(identifier: String): Unit
  def lastRun(identifier: String): Long
}
