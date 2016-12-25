package in.ashwanthkumar.matsya

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.ec2.AmazonEC2Client
import org.joda.time.{DateTime, DateTimeUtils}
import org.mockito.Mockito.{mock, when}
import org.scalatest.FlatSpec
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper}


class MatsyaTest extends FlatSpec {

  "hasCooledOff " should "return true when cool off time is greater than given in config" in {
    val clusterConfig = mock(classOf[ClusterConfig])

    val c2Client = mock(classOf[AmazonEC2Client])
    val autoScalingClient = mock(classOf[AmazonAutoScalingClient])
    val timeSeriesStore = mock(classOf[TimeSeriesStore])
    val stateStore = mock(classOf[StateStore])
    val notifier = mock(classOf[Notifier])

    val matsyaConfig = MatsyaConfig(List(clusterConfig), "", None)

    val Matsya = new Matsya(c2Client, autoScalingClient, matsyaConfig, timeSeriesStore, stateStore, notifier)

    val state = mock(classOf[State])

    val nowTimeInMilli = DateTime.now.getMillis

    val lastModeChangedTimestamp = DateTime.now.minusHours(1).getMillis

    DateTimeUtils.setCurrentMillisFixed(nowTimeInMilli)

    when(state.lastModeChangedTimestamp).thenReturn(lastModeChangedTimestamp)

    val millisecsFor30Secs = 30000

    when(clusterConfig.odCoolOffPeriodInMillis).thenReturn(millisecsFor30Secs)

    Matsya.hasCooledOff(clusterConfig, state) should be(true)


  }

  it should "return false when cool off time is lesser than given in config" in {
    val clusterConfig = mock(classOf[ClusterConfig])

    val c2Client = mock(classOf[AmazonEC2Client])
    val autoScalingClient = mock(classOf[AmazonAutoScalingClient])
    val timeSeriesStore = mock(classOf[TimeSeriesStore])
    val stateStore = mock(classOf[StateStore])
    val notifier = mock(classOf[Notifier])

    val matsyaConfig = MatsyaConfig(List(clusterConfig), "", None)

    val Matsya = new Matsya(c2Client, autoScalingClient, matsyaConfig, timeSeriesStore, stateStore, notifier)

    val state = mock(classOf[State])

    val nowTimeInMilli = DateTime.now.getMillis

    val lastModeChangedTimestamp = DateTime.now.minusSeconds(1).getMillis

    DateTimeUtils.setCurrentMillisFixed(nowTimeInMilli)

    when(state.lastModeChangedTimestamp).thenReturn(lastModeChangedTimestamp)

    val millisecsFor30Secs = 30000

    when(clusterConfig.odCoolOffPeriodInMillis).thenReturn(millisecsFor30Secs)

    Matsya.hasCooledOff(clusterConfig, state) should be(false)


  }

}
