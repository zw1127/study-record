package cn.javastudy.disruptor.common;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public abstract class AbstractDisruptorConsumer<T>
    implements EventHandler<ObjectEvent<T>>, WorkHandler<ObjectEvent<T>> {

    @Override
    public void onEvent(ObjectEvent<T> event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(ObjectEvent<T> event) throws Exception {
        this.consume(event.getObj());
        event.clear();
    }

    public abstract void consume(T object);
}
