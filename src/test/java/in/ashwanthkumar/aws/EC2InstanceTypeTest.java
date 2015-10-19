package in.ashwanthkumar.aws;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class EC2InstanceTypeTest {
    @Test
    public void shouldConvertStringToEC2InstanceType() {
        assertThat(EC2InstanceType.fromString("c3.2xlarge"), is(EC2InstanceType.C3_2XLARGE));
    }

}