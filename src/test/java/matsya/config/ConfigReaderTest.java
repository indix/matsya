package matsya.config;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigReaderTest {
    @Test
    public void shouldReadConfigProperly() {
        MatsyaConfig matsyaConfig = ConfigReader.load("test-clusters");
        assertThat(matsyaConfig.getStateDir(), is("state"));
        assertThat(matsyaConfig.getClustes().size(), is(1));

        ClusterConfig clusterConfig = matsyaConfig.getClustes().get(0);
        assertThat(clusterConfig.getSpotASG(), is("test-asg-spot"));
        assertThat(clusterConfig.getOdASG(), is("test-asg-od"));
        assertThat(clusterConfig.getBidPrice(), is(0.420));
        assertThat(clusterConfig.getMachineType(), is("c3.2xlarge"));
        assertThat(clusterConfig.getThreshold(), is(0.8));
        assertThat(clusterConfig.getNrOfTimes(), is(5));
        Map<String, String> subnets = ImmutableMap.of(
                "us-east-1a", "subnet-1",
                "us-east-1b", "subnet-2",
                "us-east-1c", "subnet-3",
                "us-east-1d", "subnet-4",
                "us-east-1e", "subnet-5"
        );
        assertThat(clusterConfig.getSubnets(), is(subnets));
        assertThat(clusterConfig.isFallbackToOnDemand(), is(true));
    }
}