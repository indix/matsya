package in.ashwanthkumar;

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.SpotPrice;
import org.joda.time.DateTime;

import java.util.Date;

public class Matsya {
    public static void main(String[] args) {
        describeSpotHistory("c3.2xlarge", DateTime.now().minusHours(12).toDate(), DateTime.now().toDate(), "Linux/UNIX (Amazon VPC)");
        describeASG("as-datapipeline-staging-spot", "subnet-2d6ef374");
    }

    private static void describeASG(String name, String subnetsAsCSV) {
        AmazonAutoScalingClient asgClient = new AmazonAutoScalingClient();
        DescribeAutoScalingGroupsResult groupsResult = asgClient.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest().withAutoScalingGroupNames(name));
        for (AutoScalingGroup autoScalingGroup : groupsResult.getAutoScalingGroups()) {
            System.out.println(autoScalingGroup.getAutoScalingGroupName() + " " + autoScalingGroup.getAvailabilityZones() + " " + autoScalingGroup.getVPCZoneIdentifier());
        }

        asgClient.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest().withVPCZoneIdentifier(subnetsAsCSV).withAutoScalingGroupName(name));
    }

    private static void describeSpotHistory(String machineType, Date startTime, Date endTime, String productType) {
        AmazonEC2Client client = new AmazonEC2Client();
        DescribeSpotPriceHistoryRequest request = new DescribeSpotPriceHistoryRequest()
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withProductDescriptions(productType)
                .withInstanceTypes(machineType);
        DescribeSpotPriceHistoryResult spotPriceHistoryResult = client.describeSpotPriceHistory(request);
        System.out.println("# Changes - " + spotPriceHistoryResult.getSpotPriceHistory().size());
        for (SpotPrice spotPrice : spotPriceHistoryResult.getSpotPriceHistory()) {
            System.out.println(spotPrice.getAvailabilityZone() + " " + spotPrice.getInstanceType() + " " + spotPrice.getTimestamp() + " " + spotPrice.getSpotPrice() + " " + spotPrice.getProductDescription());
        }
    }
}
