package in.ashwanthkumar.aws;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class US_WEST_2_AZTest {
    @Test
    public void shouldConvertFromString() {
        assertThat(AZ.US_WEST_2.fromString("us-west-2a"), CoreMatchers.is(AZ.US_WEST_2.US_WEST_2A));
        assertThat(AZ.US_WEST_2.fromString("us-west-2b"), CoreMatchers.is(AZ.US_WEST_2.US_WEST_2B));
        assertThat(AZ.US_WEST_2.fromString("us-west-2c"), CoreMatchers.is(AZ.US_WEST_2.US_WEST_2C));
    }


}