package cn.javastudy.springboot.mqtt;

import cn.javastudy.springboot.mqtt.configuration.MqttGateway;
import cn.javastudy.springboot.mqtt.service.EncryptionService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Resource
    private MqttGateway mqttGateway;

    @Resource
    private EncryptionService encryptionService;

    @PostMapping("/sendMessage")
    public String sendMessage(String topic, String message) {
        // 发送消息到指定topic
        mqttGateway.sendToMqtt(topic, encryptionService.encrypt(message));
        return "send topic: " + topic + ", message : " + message;
    }
}
