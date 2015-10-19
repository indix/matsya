package matsya.aws;

import matsya.aws.AZ.US_EAST_1;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class US_EAST_1_AZTest {
    @Test
    public void shouldConvertFromStringToEnum() throws IllegalAccessException, InstantiationException {
        assertThat(US_EAST_1.fromString("us-east-1a"), is(US_EAST_1.US_EAST_1A));
        assertThat(US_EAST_1.fromString("us-east-1b"), is(US_EAST_1.US_EAST_1B));
        assertThat(US_EAST_1.fromString("us-east-1c"), is(US_EAST_1.US_EAST_1C));
        assertThat(US_EAST_1.fromString("us-east-1d"), is(US_EAST_1.US_EAST_1D));
        assertThat(US_EAST_1.fromString("us-east-1e"), is(US_EAST_1.US_EAST_1E));
    }

}