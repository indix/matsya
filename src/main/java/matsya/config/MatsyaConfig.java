package matsya.config;

import com.google.common.collect.Lists;

import java.util.List;

public class MatsyaConfig {
    private List<ClusterConfig> clusters = Lists.newArrayList();

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
}
