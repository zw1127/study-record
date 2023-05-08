package cn.javastudy.springboot.mqtt.models;

public class BizMqttException extends Exception {

    private static final long serialVersionUID = 346996733783882638L;

    public BizMqttException(final String message) {
        super(message);
    }
}
