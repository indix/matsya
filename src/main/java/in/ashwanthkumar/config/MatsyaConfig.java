package in.ashwanthkumar.config;

import com.google.common.collect.Lists;

import java.util.List;

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
}
