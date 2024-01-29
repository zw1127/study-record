/*
 * Copyright (c) 2024 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
package cn.javastudy.springboot.mqtt.configuration;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.stream.CharacterStreamReadingMessageSource;
import org.springframework.messaging.MessageHandler;


public class MqttIntegrationConfig {

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"tcp://localhost:1883"});
        options.setUserName("guest");
        options.setPassword("guest".toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    // publisher

    @Bean
    public IntegrationFlow mqttOutFlow() {
        return IntegrationFlows.from(CharacterStreamReadingMessageSource.stdin(),
                e -> e.poller(Pollers.fixedDelay(1000)))
            .transform(p -> p + " sent to MQTT")
            .handle(mqttOutbound())
            .get();
    }

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("siSamplePublisher", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("siSampleTopic");
        return messageHandler;
    }

    // consumer

    @Bean
    public IntegrationFlow mqttInFlow() {
        return IntegrationFlows.from(mqttInbound())
            .transform(p -> p + ", received from MQTT")
            .handle(logger())
            .get();
    }

    private LoggingHandler logger() {
        LoggingHandler loggingHandler = new LoggingHandler("INFO");
        loggingHandler.setLoggerName("siSample");
        return loggingHandler;
    }

    @Bean
    public MessageProducerSupport mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("siSampleConsumer",
            mqttClientFactory(), "siSampleTopic");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }
}
