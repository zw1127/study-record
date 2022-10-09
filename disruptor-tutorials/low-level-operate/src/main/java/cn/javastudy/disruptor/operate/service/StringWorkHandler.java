package cn.javastudy.disruptor.operate.service;

import cn.javastudy.disruptor.basic.service.StringEvent;
import com.lmax.disruptor.WorkHandler;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringWorkHandler implements WorkHandler<StringEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(StringWorkHandler.class);

    // 外部可以传入Consumer实现类，每处理一条消息的时候，consumer的accept方法就会被执行一次
    private final Consumer<?> consumer;

    public StringWorkHandler(Consumer<?> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEvent(StringEvent event) throws Exception {
        LOG.info("work handler event : {}", event);

        // 这里延时100ms，模拟消费事件的逻辑的耗时
        Thread.sleep(100);

        // 如果外部传入了consumer，就要执行一次accept方法
        if (null != consumer) {
            consumer.accept(null);
        }
    }
}
