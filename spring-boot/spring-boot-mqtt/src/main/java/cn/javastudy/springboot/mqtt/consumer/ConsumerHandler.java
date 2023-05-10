package cn.javastudy.springboot.mqtt.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class ConsumerHandler implements MessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerHandler.class);

    @Override
    @ServiceActivator(inputChannel = "consumer-test-01")
    public void handleMessage(Message<?> message) throws MessagingException {
        Message<String> result = (Message<String>) message;
        LOG.info("MQTT topic [{}] receive message [{}]", result.getHeaders().get(MqttHeaders.RECEIVED_TOPIC),
            result.getPayload());
    }
}
