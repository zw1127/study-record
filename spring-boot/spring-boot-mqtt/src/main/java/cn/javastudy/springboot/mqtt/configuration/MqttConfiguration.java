package cn.javastudy.springboot.mqtt.configuration;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttConfiguration {

    private String username;
    private String password;
    private String url;
    private String clientId;
    private String topic = "TOPIC_DEFAULT";
    private Integer completionTimeout = 2000;

    /**
     * 注册MQTT客户端工厂
     *
     * @return MqttPahoClientFactory
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        //如果设置为 false，客户端和服务器将在客户端、服务器和连接重新启动时保持状态。随着状态的保持：
        //  即使客户端、服务器或连接重新启动，消息传递也将可靠地满足指定的 QOS。服务器将订阅视为持久的。
        // 如果设置为 true，客户端和服务器将不会在客户端、服务器或连接重新启动时保持状态。
        options.setCleanSession(true);
        //该值以秒为单位，必须>0，定义了客户端等待与 MQTT 服务器建立网络连接的最大时间间隔。
        // 默认超时为 30 秒。值 0 禁用超时处理，这意味着客户端将等待直到网络连接成功或失败。
        options.setConnectionTimeout(0);
        //此值以秒为单位，定义发送或接收消息之间的最大时间间隔，必须>0
        options.setKeepAliveInterval(90);
        //自动重新连接
        options.setAutomaticReconnect(true);
        options.setUserName(this.getUsername());
        options.setPassword(this.getPassword().toCharArray());
        options.setServerURIs(new String[]{this.getUrl()});

        factory.setConnectionOptions(options);
        return factory;
    }
}
