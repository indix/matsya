package in.ashwanthkumar.config;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.List;

public class ConfigReader {

    public static final String NAMESPACE = "matsya";

    public static MatsyaConfig load(String name) {
        return load(ConfigFactory.load(name));
    }

    public static MatsyaConfig load(Config config) {
        Config globalConfig = config.getConfig(NAMESPACE);
        List<ClusterConfig> clusters = Lists.transform(globalConfig.getConfigList("clusters"), new Function<Config, ClusterConfig>() {
            @Override
            public ClusterConfig apply(Config input) {
                return ClusterConfig.from(input);
            }
        });

        String stateDir = globalConfig.getString("state-dir");
        return new MatsyaConfig()
                .setClusters(clusters)
                .setStateDir(stateDir);
    }

    public static MatsyaConfig readFrom(String file) {
        return load(ConfigFactory.parseFile(new File(file)));
    }
}
