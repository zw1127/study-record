package cn.javastudy.disruptor.basic.service.impl;

import cn.javastudy.disruptor.basic.service.BasicEventService;
import cn.javastudy.disruptor.basic.service.StringEvent;
import cn.javastudy.disruptor.basic.service.StringEventFactory;
import cn.javastudy.disruptor.basic.service.StringEventHandler;
import cn.javastudy.disruptor.basic.service.StringEventProducer;
import com.lmax.disruptor.dsl.Disruptor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

@Service
public class BasicEventServiceImpl implements BasicEventService {

    private static final Logger LOG = LoggerFactory.getLogger(BasicEventServiceImpl.class);
    private static final int BUFFER_SIZE = 16;

    private StringEventProducer producer;

    /**
     * 统计消息总数
     */
    private final AtomicLong eventCount = new AtomicLong();

    @PostConstruct
    private void init() {
        // 实例化
        Disruptor<StringEvent> disruptor = new Disruptor<>(new StringEventFactory(),
            BUFFER_SIZE,
            new CustomizableThreadFactory("event-handler-"));

        // 准备一个匿名类，传给disruptor的事件处理类，
        // 这样每次处理事件时，都会将已经处理事件的总数打印出来
        Consumer<?> eventCountPrinter = new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                long count = eventCount.incrementAndGet();
                LOG.info("receive [{}] event", count);
            }
        };

        // 指定处理类
        disruptor.handleEventsWith(new StringEventHandler(eventCountPrinter));

        // 启动
        disruptor.start();

        // 生产者
        producer = new StringEventProducer(disruptor.getRingBuffer());
    }

    @Override
    public void publish(String value) {
        producer.onData(value);
    }

    @Override
    public long eventCount() {
        return eventCount.get();
    }
}
