package cn.javastudy.springboot.simulator.netconf.properties;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "simulator.netconf", ignoreInvalidFields = true)
@Component
public class NetconfSimulatorProperties {

    private int threadPoolSize = 8;

    private int generateConfigsTimeout = (int) TimeUnit.MINUTES.toMillis(30);

    private boolean runningWritable = false;

    private List<DynamicConfig> dynamicConfigList;
}
