package in.ashwanthkumar.aws;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class US_EAST_1_AZTest {
    @Test
    public void shouldConvertFromStringToEnum() throws IllegalAccessException, InstantiationException {
        assertThat(AZ.US_EAST_1.fromString("us-east-1a"), CoreMatchers.is(AZ.US_EAST_1.US_EAST_1A));
        assertThat(AZ.US_EAST_1.fromString("us-east-1b"), CoreMatchers.is(AZ.US_EAST_1.US_EAST_1B));
        assertThat(AZ.US_EAST_1.fromString("us-east-1c"), CoreMatchers.is(AZ.US_EAST_1.US_EAST_1C));
        assertThat(AZ.US_EAST_1.fromString("us-east-1d"), CoreMatchers.is(AZ.US_EAST_1.US_EAST_1D));
        assertThat(AZ.US_EAST_1.fromString("us-east-1e"), CoreMatchers.is(AZ.US_EAST_1.US_EAST_1E));
    }

}