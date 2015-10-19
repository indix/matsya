package in.ashwanthkumar.matsya;

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import in.ashwanthkumar.config.ConfigReader;
import in.ashwanthkumar.config.MatsyaConfig;
import in.ashwanthkumar.store.RocksDBStore;
import in.ashwanthkumar.tsdb.TSDB;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Set;

public class OldMatsya {
    private MatsyaTimeSeriesStore store;
    private RocksDBStore systemStateStore;
    private MatsyaConfig config;

    public OldMatsya(MatsyaConfig config, RocksDBStore systemStateStore, TSDB timeSeriesStore) {
        this.systemStateStore = systemStateStore;
        this.config = config;
        this.store = new MatsyaTimeSeriesStore(timeSeriesStore);
    }

    public void updateSpotPrices() {
        Set<String> machineTypes = config.machineTypes();
        AmazonEC2Client client = new AmazonEC2Client();
        DescribeSpotPriceHistoryRequest request = new DescribeSpotPriceHistoryRequest()
                .withStartTime(DateTime.now().minusMinutes(5).toDate())
                .withEndTime(DateTime.now().toDate())
                .withProductDescriptions("Linux/UNIX (Amazon VPC)") // FIXME - Add support for other product types as well
                .withInstanceTypes(machineTypes);
        DescribeSpotPriceHistoryResult spotPriceHistoryResult = client.describeSpotPriceHistory(request);
        Lists.transform(spotPriceHistoryResult.getSpotPriceHistory(), new Function<SpotPrice, Void>() {
            @Override
            public Void apply(SpotPrice input) {
                return null;
            }
        });
    }

    public static void main(String[] args) {
        MatsyaConfig config = ConfigReader.load("matsya");
        OldMatsya system = new OldMatsya(config, new RocksDBStore(config.stateDir()), new RocksDBStore(config.timeseriesDir()));

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
