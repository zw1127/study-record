package cn.javastudy.springboot.mqtt;

import cn.javastudy.springboot.mqtt.configuration.MqttProducer;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Resource
    private MqttProducer mqttProducer;

    @PostMapping("/sendMessage")
    public String sendMessage(String topic, String message) {
        // 发送消息到指定topic
        mqttProducer.send(topic, 0, message);
        return "send topic: " + topic + ", message : " + message;
    }
}
