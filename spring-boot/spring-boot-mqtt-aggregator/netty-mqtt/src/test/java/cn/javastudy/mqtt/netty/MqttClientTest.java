/*
 * Copyright (c) 2023 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
package cn.javastudy.mqtt.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.concurrent.Future;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(MqttClientTest.class);

    static final int KEEPALIVE_TIMEOUT_SECONDS = 2;
    static final long RECONNECT_DELAY_SECONDS = 10L;

    EventLoopGroup eventLoopGroup;

    MqttClient mqttClient;

    @Before
    public void init() throws Exception {
        this.eventLoopGroup = new NioEventLoopGroup();
    }

    @After
    public void destroy() throws InterruptedException {
        if (this.mqttClient != null) {
            this.mqttClient.disconnect();
        }
        if (this.eventLoopGroup != null) {
            this.eventLoopGroup.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void testConnect() throws Exception {
        MqttClientConfig config = new MqttClientConfig();
        config.setTimeoutSeconds(KEEPALIVE_TIMEOUT_SECONDS);
        config.setReconnectDelay(RECONNECT_DELAY_SECONDS);

        this.mqttClient = initClient();

        mqttClient.on("MTN_mac", (topic, payload) -> {
            ByteBuf byteBuf = payload.copy();

            byteBuf.markReaderIndex();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String string = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).toString();
            LOG.info("on topic:{}, payload:{}", topic, string);
        });
        LOG.warn("Sending publish messages...");
        CountDownLatch latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            Thread.sleep(30);
            Future<Void> pubFuture = publishMsg();
            pubFuture.addListener(future -> latch.countDown());
        }

        LOG.warn("Waiting for messages acknowledgments...");
        boolean awaitResult = latch.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(awaitResult);
        LOG.warn("Messages are delivered successfully...");

        //when
        LOG.warn("Starting idle period...");
        Thread.sleep(150000);
    }

    private Future<Void> publishMsg() {
        return this.mqttClient.publish(
            "test/topic",
            Unpooled.wrappedBuffer("payload".getBytes(StandardCharsets.UTF_8)),
            MqttQoS.AT_MOST_ONCE);
    }

    private MqttClient initClient() throws Exception {
        MqttClientConfig config = new MqttClientConfig();
        config.setTimeoutSeconds(KEEPALIVE_TIMEOUT_SECONDS);
        config.setReconnectDelay(RECONNECT_DELAY_SECONDS);
        MqttClient client = MqttClient.create(config, new MqttHandler() {
            @Override
            public void onMessage(String topic, ByteBuf payload) {
                LOG.info("topic:{}, payload:{}", topic, new String(payload.copy().array(), StandardCharsets.UTF_8));
            }
        });
        client.setEventLoop(this.eventLoopGroup);
        String host = "10.190.49.234";
        int port = 1883;
        Future<MqttConnectResult> connectFuture = client.connect(host, 1883);

        String hostPort = host + ":" + port;
        MqttConnectResult result;
        try {
            result = connectFuture.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            connectFuture.cancel(true);
            client.disconnect();
            throw new RuntimeException(String.format("Failed to connect to MQTT server at %s.", hostPort));
        }
        if (!result.isSuccess()) {
            connectFuture.cancel(true);
            client.disconnect();
            String message = String.format("Failed to connect to MQTT server at %s. Result code is: %s", hostPort,
                result.getReturnCode());
            throw new RuntimeException(message);
        }
        return client;
    }
}
