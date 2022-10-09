package cn.javastudy.disruptor.consumer.service;

import com.lmax.disruptor.WorkHandler;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailWorkHandler implements WorkHandler<OrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(MailWorkHandler.class);

    // 外部可以传入Consumer实现类，每处理一条消息的时候，consumer的accept方法就会被执行一次
    private final Consumer<?> consumer;

    public MailWorkHandler(Consumer<?> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEvent(OrderEvent event) throws Exception {
        LOG.info("共同消费模式的邮件服务 : {}", event);

        // 这里延时100ms，模拟消费事件的逻辑的耗时
        Thread.sleep(100);

        // 如果外部传入了consumer，就要执行一次accept方法
        if (null != consumer) {
            consumer.accept(null);
        }
    }
}
