package cn.javastudy.disruptor.common;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import java.util.List;

public class DisruptorQueue<T> {

    private Disruptor<ObjectEvent<T>> disruptor;
    private RingBuffer<ObjectEvent<T>> ringBuffer;

    public DisruptorQueue(Disruptor<ObjectEvent<T>> disruptor) {
        this.disruptor = disruptor;
        this.ringBuffer = disruptor.getRingBuffer();
        this.disruptor.start();
    }

    public void add(T object) {
        if (object != null) {
            long sequence = this.ringBuffer.next();

            try {
                ObjectEvent<T> event = this.ringBuffer.get(sequence);

                event.setObj(object);
            } finally {
                this.ringBuffer.publish(sequence);
            }
        }
    }

    public void addAll(List<T> list) {
        if (list != null) {

            for (T object : list) {
                if (object != null) {
                    this.add(object);
                }
            }
        }
    }

    public long cursor() {
        return this.disruptor.getRingBuffer().getCursor();
    }

    public void shutdown() {
        this.disruptor.shutdown();
    }

    public Disruptor<ObjectEvent<T>> getDisruptor() {
        return this.disruptor;
    }

    public void setDisruptor(Disruptor<ObjectEvent<T>> disruptor) {
        this.disruptor = disruptor;
    }

    public RingBuffer<ObjectEvent<T>> getRingBuffer() {
        return this.ringBuffer;
    }

    public void setRingBuffer(RingBuffer<ObjectEvent<T>> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

}
