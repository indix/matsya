package in.ashwanthkumar.config;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class MatsyaConfig {
    private List<ClusterConfig> clusters = Lists.newArrayList();
    private String stateDir;

    public MatsyaConfig addConfig(ClusterConfig clusterConfig) {
        this.clusters.add(clusterConfig);
        return this;
    }

    public MatsyaConfig setClusters(List<ClusterConfig> clusters) {
        this.clusters = clusters;
        return this;
    }

    public List<ClusterConfig> getClustes() {
        return clusters;
    }

    public String getStateDir() {
        return stateDir;
    }

    public MatsyaConfig setStateDir(String stateDir) {
        this.stateDir = stateDir;
        return this;
    }

    public Set<String> machineTypes() {
        return Sets.newHashSet(Lists.transform(clusters, new Function<ClusterConfig, String>() {
            @Override
            public String apply(ClusterConfig input) {
                return input.getMachineType();
            }
        }));
    }

    public String stateDir() {
        return getStateDir() + "/" + "_cluster_state";
    }

    public String timeseriesDir() {
        return getStateDir() + "/" + "history";
    }

    public String getRegion() {
        // FIXME - Make this configurable
        return "us-east-1";
    }
}
