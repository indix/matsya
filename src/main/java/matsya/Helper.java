package matsya;

import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.util.StringUtils;

public class Helper {
    public static boolean isASGUnderVPC(AutoScalingGroup asg) {
        return !StringUtils.isNullOrEmpty(asg.getVPCZoneIdentifier());
    }
}
