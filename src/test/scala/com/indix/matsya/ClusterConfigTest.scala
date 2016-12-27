package com.indix.matsya

import com.typesafe.config.ConfigFactory
import org.scalatest.FlatSpec
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper}


class ClusterConfigTest extends FlatSpec {

  "ClusterConfig Object" should "create ClusterConfig from config " in {

    val config = ConfigFactory.parseURL(this.getClass.getResource("/test-cluster-config.conf"))

    val actualClusterConfig = ClusterConfig.from(config)

    actualClusterConfig.name should be("Test Hadoop Cluster")
    actualClusterConfig.spotASG should be("test-asg-spot")
    actualClusterConfig.odASG should be("test-asg-od")
    actualClusterConfig.machineType should be("c3.2xlarge")
    actualClusterConfig.maxBidPrice should be(0.420)
    actualClusterConfig.maxThreshold should be(0.8)
    actualClusterConfig.maxNrOfTimes should be(5)
    actualClusterConfig.odPrice should be(0.420)
    actualClusterConfig.fallBackToOnDemand should be(true)
    actualClusterConfig.odCoolOffPeriodInMillis should be(2700000)

    val expectedSubnetsMap = Map("us-east-1c" -> "subnet-3",
      "us-east-1d" -> "subnet-4",
      "us-east-1a" -> "subnet-1",
      "us-east-1e" -> "subnet-5",
      "us-east-1b" -> "subnet-2")

    actualClusterConfig.subnets should be(expectedSubnetsMap)

  }

}
