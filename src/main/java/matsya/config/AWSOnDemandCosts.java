package matsya.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class AWSOnDemandCosts {
    // US East
    private final static Map<String, String> usEast1Pricing = Maps.newHashMap();

    public static Map<String, String> US_EAST_PRICING() {
        if (usEast1Pricing.isEmpty()) {
            usEast1Pricing.put("m1.small", "0.044");
            usEast1Pricing.put("m1.medium", "0.087");
            usEast1Pricing.put("m1.large", "0.175");
            usEast1Pricing.put("m1.xlarge", "0.35");
            usEast1Pricing.put("c1.medium", "0.13");
            usEast1Pricing.put("c1.xlarge", "0.52");
            usEast1Pricing.put("cc2.8xlarge", "2");
            usEast1Pricing.put("cg1.4xlarge", "2.1");
            usEast1Pricing.put("m2.xlarge", "0.245");
            usEast1Pricing.put("m2.2xlarge", "0.49");
            usEast1Pricing.put("m2.4xlarge", "0.98");
            usEast1Pricing.put("cr1.8xlarge", "3.5");
            usEast1Pricing.put("hi1.4xlarge", "3.1");
            usEast1Pricing.put("hs1.8xlarge", "4.6");
            usEast1Pricing.put("t1.micro", "0.02");
            usEast1Pricing.put("t2.micro", "0.013");
            usEast1Pricing.put("t2.small", "0.026");
            usEast1Pricing.put("t2.medium", "0.052");
            usEast1Pricing.put("t2.large", "0.104");
            usEast1Pricing.put("m4.large", "0.126");
            usEast1Pricing.put("m4.xlarge", "0.252");
            usEast1Pricing.put("m4.2xlarge", "0.504");
            usEast1Pricing.put("m4.4xlarge", "1.008");
            usEast1Pricing.put("m4.10xlarge", "2.52");
            usEast1Pricing.put("m3.medium", "0.067");
            usEast1Pricing.put("m3.large", "0.133");
            usEast1Pricing.put("m3.xlarge", "0.266");
            usEast1Pricing.put("m3.2xlarge", "0.532");
            usEast1Pricing.put("c4.large", "0.11");
            usEast1Pricing.put("c4.xlarge", "0.22");
            usEast1Pricing.put("c4.2xlarge", "0.441");
            usEast1Pricing.put("c4.4xlarge", "0.882");
            usEast1Pricing.put("c4.8xlarge", "1.763");
            usEast1Pricing.put("c3.large", "0.105");
            usEast1Pricing.put("c3.xlarge", "0.21");
            usEast1Pricing.put("c3.2xlarge", "0.42");
            usEast1Pricing.put("c3.4xlarge", "0.84");
            usEast1Pricing.put("c3.8xlarge", "1.68");
            usEast1Pricing.put("g2.2xlarge", "0.65");
            usEast1Pricing.put("g2.8xlarge", "2.6");
            usEast1Pricing.put("r3.large", "0.175");
            usEast1Pricing.put("r3.xlarge", "0.35");
            usEast1Pricing.put("r3.2xlarge", "0.7");
            usEast1Pricing.put("r3.4xlarge", "1.4");
            usEast1Pricing.put("r3.8xlarge", "2.8");
            usEast1Pricing.put("i2.xlarge", "0.853");
            usEast1Pricing.put("i2.2xlarge", "1.705");
            usEast1Pricing.put("i2.4xlarge", "3.41");
            usEast1Pricing.put("i2.8xlarge", "6.82");
            usEast1Pricing.put("d2.xlarge", "0.69");
            usEast1Pricing.put("d2.2xlarge", "1.38");
            usEast1Pricing.put("d2.4xlarge", "2.76");
            usEast1Pricing.put("d2.8xlarge", "5.52");
        }

        return ImmutableMap.copyOf(usEast1Pricing);
    }

    // US WEST 1 - Nothern California
    private final static Map<String, String> usWest1Pricing = Maps.newHashMap();

    public static Map<String, String> US_WEST_1_PRICING() {
        if (usWest1Pricing.isEmpty()) {
            usWest1Pricing.put("m1.small", "0.047");
            usWest1Pricing.put("m1.medium", "0.095");
            usWest1Pricing.put("m1.large", "0.19");
            usWest1Pricing.put("m1.xlarge", "0.379");
            usWest1Pricing.put("c1.medium", "0.148");
            usWest1Pricing.put("c1.xlarge", "0.592");
//            usWest1Pricing.put("cc2.8xlarge",);
//            usWest1Pricing.put("cg1.4xlarge",);
            usWest1Pricing.put("m2.xlarge", "0.275");
            usWest1Pricing.put("m2.2xlarge", "0.55");
            usWest1Pricing.put("m2.4xlarge", "1.1");
//            usWest1Pricing.put("cr1.8xlarge",);
//            usWest1Pricing.put("hi1.4xlarge",);
//            usWest1Pricing.put("hs1.8xlarge",);
            usWest1Pricing.put("t1.micro", "0.025");
            usWest1Pricing.put("t2.micro", "0.017");
            usWest1Pricing.put("t2.small", "0.034");
            usWest1Pricing.put("t2.medium", "0.068");
            usWest1Pricing.put("t2.large", "0.136");
            usWest1Pricing.put("m4.large", "0.147");
            usWest1Pricing.put("m4.xlarge", "0.294");
            usWest1Pricing.put("m4.2xlarge", "0.588");
            usWest1Pricing.put("m4.4xlarge", "1.176");
            usWest1Pricing.put("m4.10xlarge", "2.94");
            usWest1Pricing.put("m3.medium", "0.077");
            usWest1Pricing.put("m3.large", "0.154");
            usWest1Pricing.put("m3.xlarge", "0.308");
            usWest1Pricing.put("m3.2xlarge", "0.616");
            usWest1Pricing.put("c4.large", "0.138");
            usWest1Pricing.put("c4.xlarge", "0.276");
            usWest1Pricing.put("c4.2xlarge", "0.552");
            usWest1Pricing.put("c4.4xlarge", "1.104");
            usWest1Pricing.put("c4.8xlarge", "2.208");
            usWest1Pricing.put("c3.large", "0.12");
            usWest1Pricing.put("c3.xlarge", "0.239");
            usWest1Pricing.put("c3.2xlarge", "0.478");
            usWest1Pricing.put("c3.4xlarge", "0.956");
            usWest1Pricing.put("c3.8xlarge", "1.912");
            usWest1Pricing.put("g2.2xlarge", "0.702");
            usWest1Pricing.put("g2.8xlarge", "2.808");
            usWest1Pricing.put("r3.large", "0.195");
            usWest1Pricing.put("r3.xlarge", "0.39");
            usWest1Pricing.put("r3.2xlarge", "0.78");
            usWest1Pricing.put("r3.4xlarge", "1.56");
            usWest1Pricing.put("r3.8xlarge", "3.12");
            usWest1Pricing.put("i2.xlarge", "0.938");
            usWest1Pricing.put("i2.2xlarge", "1.876");
            usWest1Pricing.put("i2.4xlarge", "3.751");
            usWest1Pricing.put("i2.8xlarge", "7.502");
//            usWest1Pricing.put("d2.xlarge",);
//            usWest1Pricing.put("d2.2xlarge",);
//            usWest1Pricing.put("d2.4xlarge",);
//            usWest1Pricing.put("d2.8xlarge",);
        }

        return ImmutableMap.copyOf(usWest1Pricing);
    }
}
