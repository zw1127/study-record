package cn.javastudy.disruptor.consumer.service;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

public class OrderEventProducerWithTranslator {

    // 存储数据的环形队列
    private final RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducerWithTranslator(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<OrderEvent, String> TRANSLATOR =
        (event, sequence, arg0) -> event.setValue(arg0);

    public void onData(String content) {
        ringBuffer.publishEvent(TRANSLATOR, content);
    }
}