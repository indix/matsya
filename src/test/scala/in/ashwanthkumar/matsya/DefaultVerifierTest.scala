package in.ashwanthkumar.matsya

import org.scalatest.FlatSpec
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper}

class DefaultVerifierTest extends FlatSpec {
  "Scalar" should "return true for hasViolated when conditions are met" in {
    val state = testState(0.420, 3)
    val clusterConfig = testCluster(0.8, 0.420, 0.420, 3, fallBackToOnDemand = true)

    val scalar = new DefaultVerifier(clusterConfig, state)
    scalar.hasViolatedPrice should be(true)
    scalar.hasViolatedMaxTimes should be(true)
    scalar.hasViolated should be(true)
  }

  it should "return false for hasViolated when number of violations are less than threshold" in {
    val state = testState(0.420, 2)
    val clusterConfig = testCluster(0.8, 0.420, 0.420, 3, fallBackToOnDemand = true)

    val scalar = new DefaultVerifier(clusterConfig, state)
    scalar.hasViolatedPrice should be(true)
    scalar.hasViolatedMaxTimes should be(false)
    scalar.hasViolated should be(false)
  }

  def testCluster(maxThreshold: Double, maxBidPrice: Double, odPrice: Double,
                  maxNrOfTimesForViolation: Int, fallBackToOnDemand: Boolean): ClusterConfig = {
    val odCoolOffPeriod = 10 * 60 * 1000 // in millis
    ClusterConfig("foo", "spot-asg", "od-asg", "c3.2xlarge", Map(), maxThreshold, maxNrOfTimesForViolation,
      maxBidPrice, odPrice, fallBackToOnDemand, odCoolOffPeriod)
  }

  def testState(price: Double, nrOfTimesViolated: Int): State = {
    State("foo", "az", price, nrOfTimesViolated, ClusterMode.Spot, 0l, 1l)
  }
}
