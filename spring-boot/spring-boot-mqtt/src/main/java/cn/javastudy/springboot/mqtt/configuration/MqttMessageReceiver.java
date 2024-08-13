package cn.javastudy.springboot.mqtt.configuration;

import cn.javastudy.springboot.mqtt.service.EncryptionService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class MqttMessageReceiver implements MessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MqttMessageReceiver.class);

    @Resource
    private EncryptionService encryptionService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {

            MessageHeaders headers = message.getHeaders();
            //获取消息Topic
            String receivedTopic = (String) headers.get(MqttHeaders.RECEIVED_TOPIC);
            //获取消息体
            String payload = (String) message.getPayload();
            LOG.info("MQTT topic [{}] receive message [{}].", receivedTopic, encryptionService.decrypt(payload));
        } catch (Exception e) {
            LOG.warn("exception:", e);
        }
    }
}
