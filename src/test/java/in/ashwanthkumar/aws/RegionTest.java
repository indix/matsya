package in.ashwanthkumar.aws;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;

public class RegionTest {
    @Test
    public void shouldConvertRegionStringToEnum() {
        assertThat(Region.fromString("us-east-1"), is(Region.US_EAST_1));
        assertThat(Region.fromString("us-west-1"), is(Region.US_WEST_1));
        assertThat(Region.fromString("us-west-2"), is(Region.US_WEST_2));
        assertThat(Region.fromString("eu-west-1"), is(Region.EU_WEST_1));
        assertThat(Region.fromString("eu-central-1"), is(Region.EU_CENTRAL_1));
        assertThat(Region.fromString("ap-southeast-1"), is(Region.AP_SOUTHEAST_1));
        assertThat(Region.fromString("ap-southeast-2"), is(Region.AP_SOUTHEAST_2));
        assertThat(Region.fromString("ap-northeast-1"), is(Region.AP_NORTHEAST_1));
        assertThat(Region.fromString("sa-east-1"), is(Region.SA_EAST_1));
    }

    @Test
    public void shouldReturnTheRightAZ() {
        assertThat(Region.US_EAST_1.getAvailabilityZone("us-east-1a").id(), is(AZ.US_EAST_1.US_EAST_1A.id()));
        assertThat(Region.US_WEST_1.getAvailabilityZone("us-west-1a").id(), is(AZ.US_WEST_1.US_WEST_1A.id()));
        assertThat(Region.US_WEST_2.getAvailabilityZone("us-west-2a").id(), is(AZ.US_WEST_2.US_WEST_2A.id()));
        // Other Regions are not yet implemented
    }

    @Test
    public void shouldReturnAllAZsInTheRegion() {
        assertThat(Region.US_EAST_1.getAZs(), arrayContainingInAnyOrder((AZ.AZType[]) AZ.US_EAST_1.values()));
        assertThat(Region.US_WEST_1.getAZs(), arrayContainingInAnyOrder((AZ.AZType[]) AZ.US_WEST_1.values()));
        assertThat(Region.US_WEST_2.getAZs(), arrayContainingInAnyOrder((AZ.AZType[]) AZ.US_WEST_2.values()));
        // Other regions are not yet implemented
    }


}