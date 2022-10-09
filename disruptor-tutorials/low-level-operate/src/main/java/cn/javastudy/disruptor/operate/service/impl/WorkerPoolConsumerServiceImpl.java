package cn.javastudy.disruptor.operate.service.impl;

import cn.javastudy.disruptor.basic.service.StringEvent;
import cn.javastudy.disruptor.basic.service.StringEventFactory;
import cn.javastudy.disruptor.basic.service.StringEventProducer;
import cn.javastudy.disruptor.operate.service.LowLevelOperateService;
import cn.javastudy.disruptor.operate.service.StringWorkHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("workerPoolConsumer")
public class WorkerPoolConsumerServiceImpl implements LowLevelOperateService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerPoolConsumerServiceImpl.class);

    private StringEventProducer producer;

    /**
     * 统计消息总数
     */
    private final AtomicLong eventCount = new AtomicLong();


    @PostConstruct
    private void init() {
        RingBuffer<StringEvent> ringBuffer = RingBuffer.createSingleProducer(new StringEventFactory(), BUFFER_SIZE);

        ExecutorService executorService = Executors.newFixedThreadPool(CONSUMER_NUM);

        StringWorkHandler[] handlers = new StringWorkHandler[CONSUMER_NUM];

        // 创建多个StringWorkHandler实例，放入一个数组中
        for (int i = 0; i < CONSUMER_NUM; i++) {
            handlers[i] = new StringWorkHandler(o -> {
                long count = eventCount.incrementAndGet();
                LOG.info("receive [{}] event", count);
            });
        }

        // 创建WorkerPool实例，将StringWorkHandler实例的数组传进去，代表共同消费者的数量
        WorkerPool<StringEvent> workerPool = new WorkerPool<>(ringBuffer, ringBuffer.newBarrier(),
            new IgnoreExceptionHandler(), handlers);

        // 这一句很重要，去掉就会出现重复消费同一个事件的问题
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());

        workerPool.start(executorService);

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
