package matsya.aws;

import matsya.aws.AZ.US_WEST_2;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class US_WEST_2_AZTest {
    @Test
    public void shouldConvertFromString() {
        assertThat(US_WEST_2.fromString("us-west-2a"), is(US_WEST_2.US_WEST_2A));
        assertThat(US_WEST_2.fromString("us-west-2b"), is(US_WEST_2.US_WEST_2B));
        assertThat(US_WEST_2.fromString("us-west-2c"), is(US_WEST_2.US_WEST_2C));
    }


}