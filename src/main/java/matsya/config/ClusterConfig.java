package matsya.config;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

import java.util.Map;

public class ClusterConfig {
    private String name;
    private String machineType;
    // FIXME - Add support for non-VPC based setup as well
    /**
     * Key -> AZ
     * Value -> Subnet
     */
    private Map<String, String> subnets;
    private double threshold;
    private int nrOfTimes;
    private double bidPrice;

    /* default */ static ClusterConfig from(Config config) {
        Config subnetConfigs = config.getConfig("subnets");
        Map<String, String> subnets = Maps.newHashMap();
        for (Map.Entry<String, ConfigValue> azAndSubnet : subnetConfigs.entrySet()) {
            subnets.put(azAndSubnet.getKey(), azAndSubnet.getValue().unwrapped().toString());
        }

        return new ClusterConfig()
                .setName(config.getString("name"))
                .setMachineType(config.getString("machine-type"))
                .setBidPrice(config.getDouble("bid-price"))
                .setSubnets(subnets)
                .setThreshold(config.getDouble("threshold"))
                .setNrOfTimes(config.getInt("nr-of-times"));
    }

    public String getName() {
        return name;
    }

    public ClusterConfig setName(String name) {
        this.name = name;
        return this;
    }

    public String getMachineType() {
        return machineType;
    }

    public ClusterConfig setMachineType(String machineType) {
        this.machineType = machineType;
        return this;
    }

    public Map<String, String> getSubnets() {
        return subnets;
    }

    public ClusterConfig setSubnets(Map<String, String> subnets) {
        this.subnets = subnets;
        return this;
    }

    public double getThreshold() {
        return threshold;
    }

    public ClusterConfig setThreshold(double threshold) {
        this.threshold = threshold;
        return this;
    }

    public int getNrOfTimes() {
        return nrOfTimes;
    }

    public ClusterConfig setNrOfTimes(int nrOfTimes) {
        this.nrOfTimes = nrOfTimes;
        return this;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public ClusterConfig setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
        return this;
    }
}
