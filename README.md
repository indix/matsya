[![Build Status](https://snap-ci.com/ashwanthkumar/matsya/branch/master/build_image)](https://snap-ci.com/ashwanthkumar/matsya/branch/master)

# Matsya
[Matsya](https://en.wikipedia.org/wiki/Matsya) is a process that helps you choose the lowest AZ for a machine type when running machines behind Auto Scaling Groups on AWS. 

## Context
We've a running Hadoop Cluster whose TTs are backed by Auto Scaling Groups (ASG). In order to provide better availability of TTs to our clusters we run these across AZs. We had 2 problems that needed manual intervention
- We started incurring huge Data Transfer costs
- Once we assigned AZs to ASG we forgot about it. In our case we ended up choosing a subnet one at us-east-1c and us-east-1e. It so happened that us-east-1e had very high price flucation (close to OD price) for most of the time so we ended up paying more. We wanted to swap the machines out of a higher price AZ to a cheaper one. 

## Why nots ...?
### Why not use multiple AZs for the Spot machines? <br />
Multiple AZs come with a cost - Data Transfer and variable Spot prices. Autoscaling Groups are currently limited in functionality when it comes to work with Spot machines. It doesn't support fall back to OD nor does it support spinning up the machines on the same AZ when one of the AZs has a higher price.

### Why not use Spot Blocks? <br />
Spot blocks are a great step forward but our use cases where we wanted to build something which runs 24x7.

## Status
This project is a WIP as of now.
