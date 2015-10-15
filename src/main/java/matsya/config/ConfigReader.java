package matsya.config;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public class ConfigReader {

    public static final String NAMESPACE = "matsya";

    public static MatsyaConfig load(String name) {
        Config globalConfig = ConfigFactory.load(name).getConfig(NAMESPACE);
        List<ClusterConfig> clusters = Lists.transform(globalConfig.getConfigList("clusters"), new Function<Config, ClusterConfig>() {
            @Override
            public ClusterConfig apply(Config input) {
                return ClusterConfig.from(input);
            }
        });

        return new MatsyaConfig().setClusters(clusters);
    }
}
