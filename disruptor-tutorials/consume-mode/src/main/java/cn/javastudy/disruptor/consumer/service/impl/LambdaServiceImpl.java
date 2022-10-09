package cn.javastudy.disruptor.consumer.service.impl;

import cn.javastudy.disruptor.consumer.service.ConsumeModeService;
import cn.javastudy.disruptor.consumer.service.OrderEvent;
import cn.javastudy.disruptor.consumer.service.OrderEventProducerWithTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("lambdaService")
public class LambdaServiceImpl extends ConsumeModeService {

    private static final Logger LOG = LoggerFactory.getLogger(LambdaServiceImpl.class);

    @PostConstruct
    @Override
    protected void init() {
        // lambda类型的实例化
        disruptor = new Disruptor<>(OrderEvent::new, BUFFER_SIZE, DaemonThreadFactory.INSTANCE);

        // 留给子类实现具体的事件消费逻辑
        disruptorOperate();

        // 启动
        disruptor.start();

        // 第一个生产者
        setProducerWithTranslator(new OrderEventProducerWithTranslator(disruptor.getRingBuffer()));

    }

    @Override
    protected void disruptorOperate() {
        // lambda表达式指定具体消费逻辑
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            LOG.info("lambda操作, sequence [{}], endOfBatch [{}], event : {}", sequence, endOfBatch, event);

            // 这里延时100ms，模拟消费事件的逻辑的耗时
            Thread.sleep(100);
            // 计数
            eventCountPrinter.accept(null);
        });
    }
}
