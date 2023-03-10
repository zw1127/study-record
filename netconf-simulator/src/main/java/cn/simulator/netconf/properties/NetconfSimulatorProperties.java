package cn.simulator.netconf.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "simulator.netconf", ignoreInvalidFields = true)
@Component
public class NetconfSimulatorProperties {

    private int threadPoolSize = 8;
}
