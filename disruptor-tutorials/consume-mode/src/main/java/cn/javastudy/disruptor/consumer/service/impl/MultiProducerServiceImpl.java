package cn.javastudy.disruptor.consumer.service.impl;

import cn.javastudy.disruptor.consumer.service.ConsumeModeService;
import cn.javastudy.disruptor.consumer.service.MailEventHandler;
import cn.javastudy.disruptor.consumer.service.OrderEventFactory;
import cn.javastudy.disruptor.consumer.service.OrderEventProducer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import javax.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

@Service("multiProducerService")
public class MultiProducerServiceImpl extends ConsumeModeService {

    /**
     * 第二个生产者
     */
    @Setter
    protected OrderEventProducer producer2;

    @PostConstruct
    @Override
    protected void init() {
        // 实例化
        disruptor = new Disruptor<>(new OrderEventFactory(),
            BUFFER_SIZE,
            new CustomizableThreadFactory("event-handler-"),
            // 生产类型是多生产者
            ProducerType.MULTI,
            // BlockingWaitStrategy是默认的等待策略
            new BlockingWaitStrategy());

        // 留给子类实现具体的事件消费逻辑
        disruptorOperate();

        // 启动
        disruptor.start();

        // 第一个生产者
        setProducer(new OrderEventProducer(disruptor.getRingBuffer()));

        // 第二个生产者
        setProducer2(new OrderEventProducer(disruptor.getRingBuffer()));
    }

    @Override
    protected void disruptorOperate() {
        // 一号消费者
        MailEventHandler c1 = new MailEventHandler(eventCountPrinter);

        // 二号消费者
        MailEventHandler c2 = new MailEventHandler(eventCountPrinter);

        // handleEventsWith，表示创建的多个消费者以共同消费的模式消费
        disruptor.handleEventsWith(c1, c2);
    }

    @Override
    public void publishWithProducer2(String value) throws Exception {

        producer2.onData(value);
    }
}
