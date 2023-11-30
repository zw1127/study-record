package cn.javastudy.springboot.mqtt.configuration;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.event.MqttIntegrationEvent;
import org.springframework.integration.mqtt.event.MqttMessageDeliveredEvent;
import org.springframework.integration.mqtt.event.MqttMessageSentEvent;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MqttConfig.class);

    private static final byte[] WILL_DATA;

    static {
        WILL_DATA = "offline".getBytes();
    }

    public static final String MQTT_INBOUND_CHANNEL = "mqttInboundChannel";


    public static final String MQTT_OUTBOUND_CHANNEL = "mqttOutboundChannel";

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.urls}")
    private String urls;

    @Value("${mqtt.default-subscribe-topic}")
    private String defaultSubscribeTopic;

    @Value("${mqtt.default-publish-topic}")
    private String defaultPublishTopic;

    @Value("${mqtt.client-id-subscribe}")
    private String clientIdSub;

    @Value("${mqtt.client-id-publish}")
    private String clientIdPub;

    /**
     * 连接mqtt配置
     */
    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // false，服务器会保留客户端的连接记录 true，表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setServerURIs(StringUtils.splitByWholeSeparator(urls, ","));
        // 超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 会话心跳时间 单位: s, 间隔时间：1.5*20秒向客户端发送心跳判断客户端是否在线
        options.setKeepAliveInterval(20);
        // 设置是否自动重连
        options.setAutomaticReconnect(true);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
        options.setWill("willTopic", WILL_DATA, 2, false);
        return options;
    }


    /**
     * MQTT客户端
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    /**
     * 发送者消息通道
     */
    @Bean(name = MQTT_OUTBOUND_CHANNEL)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 发送者消息处理
     */
    @Bean
    @ServiceActivator(inputChannel = MQTT_OUTBOUND_CHANNEL)
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientIdPub, mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setAsyncEvents(true);
        messageHandler.setDefaultTopic(defaultPublishTopic);
        return messageHandler;
    }

    /**
     * 消息订阅
     */
    @Bean
    public MessageProducer inbound() {
        // 可同时消费（订阅）多个Topic
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
            clientIdSub, mqttClientFactory(), StringUtils.splitByWholeSeparator(defaultSubscribeTopic, ","));
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    /**
     * 消费者消息通道
     */
    @Bean(name = MQTT_INBOUND_CHANNEL)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    /**
     * 消费者消息处理
     */
    @Bean
    @ServiceActivator(inputChannel = MQTT_INBOUND_CHANNEL)
    public MessageHandler mqttInbound() {
        return (message -> {
            LOG.info("[MQTT]- message received - [{}] - [{}]", message.getPayload(), message.getHeaders());
            // TODO 处理消息
        });
    }

    /**
     * 监听事件
     */
    @EventListener
    public void handleEvent(MqttIntegrationEvent event) {
        if (event instanceof MqttConnectionFailedEvent) {
            // 连接失败
            MqttConnectionFailedEvent connectionFailedEvent = (MqttConnectionFailedEvent) event;
            LOG.error("[MQTT]- connection failed - [{}] - [{}]", event.getClass().getSimpleName(),
                connectionFailedEvent.getCause().getMessage());
            return;
        }
        if (event instanceof MqttMessageSentEvent) {
            MqttMessageSentEvent sentEvent = (MqttMessageSentEvent) event;
            LOG.info("[MQTT]- message sent - [{}] - [{}] - [{}]", event.getClass().getSimpleName(),
                sentEvent.getMessageId(), sentEvent.getMessage());
        }
        if (event instanceof MqttMessageDeliveredEvent) {
            MqttMessageDeliveredEvent deliveredEvent = (MqttMessageDeliveredEvent) event;
            LOG.info("[MQTT]-message delivered - [{}] - [{}]", event.getClass().getSimpleName(),
                deliveredEvent.getMessageId());
            return;
        }
        if (event instanceof MqttSubscribedEvent) {
            MqttSubscribedEvent subscribedEvent = (MqttSubscribedEvent) event;
            LOG.info("[MQTT]-message subscribed [{}] - [{}]", event.getClass().getSimpleName(),
                subscribedEvent.getMessage());
            return;
        }
        LOG.info("[MQTT]-other event - [{}] - [{}] - [{}]", event.getClass().getSimpleName(),
            event.getSource().toString(), event.getCause().getMessage());
    }
}
