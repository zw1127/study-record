package cn.javastudy.springboot.mqtt.utils;

import cn.javastudy.springboot.mqtt.models.BizMqttException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

public final class MqttUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MqttUtil.class);

    public static final Map<String, MqttPahoMessageHandler> HANDLER_MAP = new HashMap<>(16);

    public static final int DEFAULT_QOS = 0;

    private MqttUtil() {
    }

    /**
     * 获取默认生产者的消息处理器
     *
     * @return 消息处理器
     */
    public static MqttPahoMessageHandler getDefaultHandler() throws BizMqttException {
        Collection<MqttPahoMessageHandler> handlers = HANDLER_MAP.values();
        Iterator<MqttPahoMessageHandler> iterator = handlers.iterator();
        MqttPahoMessageHandler handler = iterator.next();
        if (handler == null) {
            throw new BizMqttException("please make mqtt has one producer");
        }
        return handler;
    }

    /**
     * 给主题发送消息
     *
     * @param topic 主题
     * @param data  消息内容
     */
    public static void sendMessage(String topic, Object data) throws BizMqttException, JsonProcessingException {
        sendMessage(getDefaultHandler(), topic, data);
    }

    /**
     * 使用指定生产者通道给主题发送消息
     *
     * @param producerName 生产者名称
     * @param topic        主题
     * @param data         消息
     */
    public static void sendMessage(String producerName, String topic, Object data) throws BizMqttException,
        JsonProcessingException {
        if (!HANDLER_MAP.containsKey(producerName)) {
            throw new BizMqttException(String.format("producer [%s] does not exists", producerName));
        }
        sendMessage(HANDLER_MAP.get(producerName), topic, data);
    }

    /**
     * 使用消息处理器给主题发送消息
     *
     * @param handler 消息处理器
     * @param topic   主题
     * @param data    消息
     */
    private static void sendMessage(MqttPahoMessageHandler handler, String topic, Object data)
        throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);
        Message<String> mqttMessage = MessageBuilder.withPayload(json).setHeader(MqttHeaders.TOPIC, topic)
            .setHeader(MqttHeaders.QOS, DEFAULT_QOS).build();
        handler.handleMessage(mqttMessage);
        LOG.info("MQTT: send message:{} to topic:{}]", json, topic);
    }

}
