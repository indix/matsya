package matsya.aws;

import matsya.aws.AZ.US_WEST_1;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class US_WEST_1_AZTest {
    @Test
    public void shouldConvertFromString() {
        assertThat(US_WEST_1.fromString("us-west-1a"), is(US_WEST_1.US_WEST_1A));
        assertThat(US_WEST_1.fromString("us-west-1c"), is(US_WEST_1.US_WEST_1C));
    }

}