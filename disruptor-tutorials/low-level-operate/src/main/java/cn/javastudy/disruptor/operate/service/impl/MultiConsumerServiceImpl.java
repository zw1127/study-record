package cn.javastudy.disruptor.operate.service.impl;

import cn.javastudy.disruptor.basic.service.StringEvent;
import cn.javastudy.disruptor.basic.service.StringEventFactory;
import cn.javastudy.disruptor.basic.service.StringEventHandler;
import cn.javastudy.disruptor.basic.service.StringEventProducer;
import cn.javastudy.disruptor.operate.service.LowLevelOperateService;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("multiConsumer")
public class MultiConsumerServiceImpl implements LowLevelOperateService {

    private static final Logger LOG = LoggerFactory.getLogger(MultiConsumerServiceImpl.class);

    private RingBuffer<StringEvent> ringBuffer;

    private StringEventProducer producer;

    /**
     * 统计消息总数
     */
    private final AtomicLong eventCount = new AtomicLong();

    /**
     * 生产一个BatchEventProcessor实例，并且启动独立线程开始获取和消费消息
     *
     * @param executorService executorService
     */
    private void addProcessor(ExecutorService executorService) {
        // 准备一个匿名类，传给disruptor的事件处理类，
        // 这样每次处理事件时，都会将已经处理事件的总数打印出来
        Consumer<?> eventCountPrinter = new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                long count = eventCount.incrementAndGet();
                LOG.info("receive [{}] event", count);
            }
        };

        BatchEventProcessor<StringEvent> batchEventProcessor = new BatchEventProcessor<>(
            ringBuffer,
            ringBuffer.newBarrier(),
            new StringEventHandler(eventCountPrinter));

        // 将当前消费者的sequence实例传给ringBuffer
        ringBuffer.addGatingSequences(batchEventProcessor.getSequence());

        // 启动独立线程获取和消费事件
        executorService.submit(batchEventProcessor);
    }

    @PostConstruct
    private void init() {

        ringBuffer = RingBuffer.createSingleProducer(new StringEventFactory(), BUFFER_SIZE);

        ExecutorService executorService = Executors.newFixedThreadPool(CONSUMER_NUM);

        // 创建多个消费者，并在独立线程中获取和消费事件
        for (int i = 0; i < CONSUMER_NUM; i++) {
            addProcessor(executorService);
        }

        // 生产者
        producer = new StringEventProducer(ringBuffer);
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
