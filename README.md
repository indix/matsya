[![Build Status](https://snap-ci.com/ashwanthkumar/matsya/branch/master/build_image)](https://snap-ci.com/ashwanthkumar/matsya/branch/master)

# Matsya
[Matsya](https://en.wikipedia.org/wiki/Matsya) is a process that helps you choose the lowest priced AZ for a machine type when running behind Auto Scaling Groups on AWS.

## Use Case(s)
- You always need a fleet of machines - Hadoop / Mesos / YARN cluster requirements
- You want to save money by using Spot but also fallback to OD because you need the processing power
- Switch back to Spot once on OD to save money again.

## Architecture
[![Matsya Architecture](https://raw.githubusercontent.com/ashwanthkumar/matsya/master/docs/matsya-architecture-1.png)](https://docs.google.com/drawings/d/1SGMrtrwjvlZeIdoTjz8tqrsNkRE4jxohllba0totT34/edit?usp=sharing)

## Configuration
Sample configuration would be something like
```
matsya {
  # Path on the local FS to write the Cluster states and Time series data we create
  # ${working-dir}/state and ${working-dir}/history respectively for the same
  working-dir = "local_run"

  clusters = [{
    # Unique Identifier - Please don't change once assigned
    name = "Staging Hadoop Cluster"

    # AutoScaling group backing the spot machines
    spot-asg = "as-datapipeline-staging-spot"

    # AutoScaling grooup backing the OD machines
    od-asg = "as-datapipeline-staging-od"

    # Instance type that this cluster needs
    machine-type = "c3.2xlarge"

    # max bid price (as configured in the launch configuration)
    bid-price = 0.420

    # On Demand price of the Instance Type
    od-price = 0.420

    # maximum % of the bid price after which we should swap the AZ
    max-threshold = 0.99

    # This many _consecutive_ number of crossing the max-threshold
    # would result in the change
    nr-of-times = 3

    # Should we fallback to On Demand instance if we can't find any AZ
    # within the bid-price range?
    fallback-to-od = false

    # Subnets that're configured for each AZ
    # We don't support swapping classic ASGs
    # The format is availability-zone = subnet-identifier
    # (you can only have 1 subnet on an AZ)
    subnets = {
      "us-east-1a" = "subnet-d68cfbfe"
      "us-east-1b" = "subnet-2d230246"
      "us-east-1c" = "subnet-2d6ef374"
    }
  }]
}
```

## Usage
```
$ java -cp matsya-<version>.jar in.ashwanthkumar.matsya.MatsyaApp conf/matsya.conf
```

## Motivation
We've a running Hadoop Cluster whose TTs are backed by Auto Scaling Groups (ASG). In order to provide better availability of TTs to our clusters we run these across AZs. We had 2 problems that needed manual intervention
- We started incurring huge Data Transfer costs
- Once we assigned AZs to ASG we forgot about it. In our case we ended up choosing a subnet one at us-east-1c and us-east-1e. It so happened that us-east-1e had very high price flucation (close to OD price) for most of the time so we ended up paying more. We wanted to swap the machines out of a higher price AZ to a cheaper one. 

### Why nots ...?
#### Why not use multiple AZs for the Spot machines? <br />
Multiple AZs come with a cost - Data Transfer and variable Spot prices. Autoscaling Groups are currently limited in functionality when it comes to work with Spot machines. It doesn't support fall back to OD nor does it support spinning up the machines on the same AZ when one of the AZs has a higher price.

#### Why not use Spot Blocks? <br />
Spot blocks are a great step forward but our use cases where we wanted to build something which runs 24x7.

#### Why not use Spot Fleets?
Matsya has 2 features that're missing in Spot Fleets (today)
- Automatic Fallback to OD
- Automatic fallback to a single AZ (for the entire set of nodes)

#### Presentations
- [j.mp/to-matsya](http://j.mp/to-matsya) - Matsya: A new Avatar, as given on Chennai Devops User meetup. 

## Status
This project is being actively developed and should be considered alpha quality.

## License
Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
