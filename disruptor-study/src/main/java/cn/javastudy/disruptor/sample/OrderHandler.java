package cn.javastudy.disruptor.sample;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

//EventHandler用于EventHandlerGroup，WorkHandler用于WorkPool。
// 同时实现两接口，该类对象可同时用于EventHandlerGroup和WorkPool
public class OrderHandler implements EventHandler<Order>, WorkHandler<Order> {

    private final String consumerId;

    public OrderHandler(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public void onEvent(Order event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("OrderHandler1 " + this.consumerId + ", consume message: " + event.getId());
    }

    @Override
    public void onEvent(Order event) throws Exception {
        System.out.println("OrderHandler2 " + this.consumerId + ", consume message: " + event.getId());
    }
}
