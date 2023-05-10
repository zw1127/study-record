package cn.javastudy.springboot.mqtt;

import cn.javastudy.springboot.mqtt.configuration.MqttGateway;
import cn.javastudy.springboot.mqtt.models.BizMqttException;
import cn.javastudy.springboot.mqtt.utils.MqttUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Resource
    private MqttGateway mqttGateway;

    @PostMapping("/sendMessage")
    public String sendMessage(String topic, String message) throws BizMqttException, JsonProcessingException {
        // 发送消息到指定topic
        mqttGateway.sendToMqtt(topic, message);
        return "send topic: " + topic + ", message : " + message;
    }
}
