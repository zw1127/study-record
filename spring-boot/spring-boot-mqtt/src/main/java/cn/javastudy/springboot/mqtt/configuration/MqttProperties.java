package cn.javastudy.springboot.mqtt.configuration;

import cn.javastudy.springboot.mqtt.models.ChannelConfig;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("mqtt")
public class MqttProperties {

    /**
     * 所有的配置
     */
    private final Map<String, ChannelConfig> producers;

    private final Map<String, ChannelConfig> consumers;
}
