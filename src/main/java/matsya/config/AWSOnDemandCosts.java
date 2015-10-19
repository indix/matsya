package matsya.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import matsya.aws.EC2InstanceType;

import java.util.Map;

import static matsya.aws.EC2InstanceType.*;

public class AWSOnDemandCosts {
    // US East
    private final static Map<EC2InstanceType, Double> usEast1Pricing = Maps.newEnumMap(EC2InstanceType.class);

    public static Map<EC2InstanceType, Double> US_EAST_PRICING() {
        if (usEast1Pricing.isEmpty()) {
            usEast1Pricing.put(M1_SMALL, 0.044);
            usEast1Pricing.put(M1_MEDIUM, 0.087);
            usEast1Pricing.put(M1_LARGE, 0.175);
            usEast1Pricing.put(M1_XLARGE, 0.35);
            usEast1Pricing.put(C1_MEDIUM, 0.13);
            usEast1Pricing.put(C1_XLARGE, 0.52);
            usEast1Pricing.put(CG1_4XLARGE, 2.1);
            usEast1Pricing.put(M2_XLARGE, 0.245);
            usEast1Pricing.put(M2_2XLARGE, 0.49);
            usEast1Pricing.put(M2_4XLARGE, 0.98);
            usEast1Pricing.put(CR1_8XLARGE, 3.5);
            usEast1Pricing.put(HI1_4XLARGE, 3.1);
            usEast1Pricing.put(HS1_8XLARGE, 4.6);
            usEast1Pricing.put(T1_MICRO, 0.02);
            usEast1Pricing.put(T2_MICRO, 0.013);
            usEast1Pricing.put(T2_SMALL, 0.026);
            usEast1Pricing.put(T2_MEDIUM, 0.052);
            usEast1Pricing.put(T2_LARGE, 0.104);
            usEast1Pricing.put(M4_LARGE, 0.126);
            usEast1Pricing.put(M4_XLARGE, 0.252);
            usEast1Pricing.put(M4_2XLARGE, 0.504);
            usEast1Pricing.put(M4_4XLARGE, 1.008);
            usEast1Pricing.put(M4_10XLARGE, 2.52);
            usEast1Pricing.put(M3_MEDIUM, 0.067);
            usEast1Pricing.put(M3_LARGE, 0.133);
            usEast1Pricing.put(M3_XLARGE, 0.266);
            usEast1Pricing.put(M3_2XLARGE, 0.532);
            usEast1Pricing.put(C4_LARGE, 0.11);
            usEast1Pricing.put(C4_XLARGE, 0.22);
            usEast1Pricing.put(C4_2XLARGE, 0.441);
            usEast1Pricing.put(C4_4XLARGE, 0.882);
            usEast1Pricing.put(C4_8XLARGE, 1.763);
            usEast1Pricing.put(C3_LARGE, 0.105);
            usEast1Pricing.put(C3_XLARGE, 0.21);
            usEast1Pricing.put(C3_2XLARGE, 0.42);
            usEast1Pricing.put(C3_4XLARGE, 0.84);
            usEast1Pricing.put(C3_8XLARGE, 1.68);
            usEast1Pricing.put(CC2_8XLARGE, 2.0);
            usEast1Pricing.put(G2_2XLARGE, 0.65);
            usEast1Pricing.put(G2_8XLARGE, 2.6);
            usEast1Pricing.put(R3_LARGE, 0.175);
            usEast1Pricing.put(R3_XLARGE, 0.35);
            usEast1Pricing.put(R3_2XLARGE, 0.7);
            usEast1Pricing.put(R3_4XLARGE, 1.4);
            usEast1Pricing.put(R3_8XLARGE, 2.8);
            usEast1Pricing.put(I2_XLARGE, 0.853);
            usEast1Pricing.put(I2_2XLARGE, 1.705);
            usEast1Pricing.put(I2_4XLARGE, 3.41);
            usEast1Pricing.put(I2_8XLARGE, 6.82);
            usEast1Pricing.put(D2_XLARGE, 0.69);
            usEast1Pricing.put(D2_2XLARGE, 1.38);
            usEast1Pricing.put(D2_4XLARGE, 2.76);
            usEast1Pricing.put(D2_8XLARGE, 5.52);
        }

        return ImmutableMap.copyOf(usEast1Pricing);
    }

    // US WEST 1 - Nothern California
    private final static Map<EC2InstanceType, Double> usWest1Pricing = Maps.newEnumMap(EC2InstanceType.class);

    public static Map<EC2InstanceType, Double> US_WEST_1_PRICING() {
        if (usWest1Pricing.isEmpty()) {
            usWest1Pricing.put(M1_SMALL, 0.047);
            usWest1Pricing.put(M1_MEDIUM, 0.095);
            usWest1Pricing.put(M1_LARGE, 0.19);
            usWest1Pricing.put(M1_XLARGE, 0.379);
            usWest1Pricing.put(C1_MEDIUM, 0.148);
            usWest1Pricing.put(C1_XLARGE, 0.592);
//            usWest1Pricing.put(CG1_4XLARGE, null);
            usWest1Pricing.put(M2_XLARGE, 0.275);
            usWest1Pricing.put(M2_2XLARGE, 0.55);
            usWest1Pricing.put(M2_4XLARGE, 1.1);
//            usWest1Pricing.put(CR1_8XLARGE, null);
//            usWest1Pricing.put(HI1_4XLARGE, null);
//            usWest1Pricing.put(HS1_8XLARGE, null);
            usWest1Pricing.put(T1_MICRO, 0.025);
            usWest1Pricing.put(T2_MICRO, 0.017);
            usWest1Pricing.put(T2_SMALL, 0.034);
            usWest1Pricing.put(T2_MEDIUM, 0.068);
            usWest1Pricing.put(T2_LARGE, 0.136);
            usWest1Pricing.put(M4_LARGE, 0.147);
            usWest1Pricing.put(M4_XLARGE, 0.294);
            usWest1Pricing.put(M4_2XLARGE, 0.588);
            usWest1Pricing.put(M4_4XLARGE, 1.176);
            usWest1Pricing.put(M4_10XLARGE, 2.94);
            usWest1Pricing.put(M3_MEDIUM, 0.077);
            usWest1Pricing.put(M3_LARGE, 0.154);
            usWest1Pricing.put(M3_XLARGE, 0.308);
            usWest1Pricing.put(M3_2XLARGE, 0.616);
            usWest1Pricing.put(C4_LARGE, 0.138);
            usWest1Pricing.put(C4_XLARGE, 0.276);
            usWest1Pricing.put(C4_2XLARGE, 0.552);
            usWest1Pricing.put(C4_4XLARGE, 1.104);
            usWest1Pricing.put(C4_8XLARGE, 2.208);
            usWest1Pricing.put(C3_LARGE, 0.12);
            usWest1Pricing.put(C3_XLARGE, 0.239);
            usWest1Pricing.put(C3_2XLARGE, 0.478);
            usWest1Pricing.put(C3_4XLARGE, 0.956);
            usWest1Pricing.put(C3_8XLARGE, 1.912);
            usWest1Pricing.put(CC2_8XLARGE, null);
            usWest1Pricing.put(G2_2XLARGE, 0.702);
            usWest1Pricing.put(G2_8XLARGE, 2.808);
            usWest1Pricing.put(R3_LARGE, 0.195);
            usWest1Pricing.put(R3_XLARGE, 0.39);
            usWest1Pricing.put(R3_2XLARGE, 0.78);
            usWest1Pricing.put(R3_4XLARGE, 1.56);
            usWest1Pricing.put(R3_8XLARGE, 3.12);
            usWest1Pricing.put(I2_XLARGE, 0.938);
            usWest1Pricing.put(I2_2XLARGE, 1.876);
            usWest1Pricing.put(I2_4XLARGE, 3.751);
            usWest1Pricing.put(I2_8XLARGE, 7.502);
//            usWest1Pricing.put(D2_XLARGE, null);
//            usWest1Pricing.put(D2_2XLARGE, null);
//            usWest1Pricing.put(D2_4XLARGE, null);
//            usWest1Pricing.put(D2_8XLARGE, null);
        }

        return ImmutableMap.copyOf(usWest1Pricing);
    }
}
