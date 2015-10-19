package in.ashwanthkumar.aws;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class US_WEST_1_AZTest {
    @Test
    public void shouldConvertFromString() {
        assertThat(AZ.US_WEST_1.fromString("us-west-1a"), CoreMatchers.is(AZ.US_WEST_1.US_WEST_1A));
        assertThat(AZ.US_WEST_1.fromString("us-west-1c"), CoreMatchers.is(AZ.US_WEST_1.US_WEST_1C));
    }

}