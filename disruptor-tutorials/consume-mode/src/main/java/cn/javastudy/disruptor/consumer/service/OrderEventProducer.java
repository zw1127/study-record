package cn.javastudy.disruptor.consumer.service;

import com.lmax.disruptor.RingBuffer;

public class OrderEventProducer {

    // 存储数据的环形队列
    private final RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(String content) {

        // ringBuffer是个队列，其next方法返回的是下最后一条记录之后的位置，这是个可用位置
        long sequence = ringBuffer.next();

        try {
            // sequence位置取出的事件是空事件
            OrderEvent orderEvent = ringBuffer.get(sequence);
            // 空事件添加业务信息
            orderEvent.setValue(content);
        } finally {
            // 发布
            ringBuffer.publish(sequence);
        }
    }
}