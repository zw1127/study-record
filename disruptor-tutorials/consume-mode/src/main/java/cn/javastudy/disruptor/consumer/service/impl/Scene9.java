package cn.javastudy.disruptor.consumer.service.impl;

import cn.javastudy.disruptor.consumer.service.ConsumeModeService;
import cn.javastudy.disruptor.consumer.service.MailEventHandler;
import cn.javastudy.disruptor.consumer.service.MailWorkHandler;
import org.springframework.stereotype.Service;

// C1和C2共同消费，C3和C4独立消费，但C3和C4都依赖C1和C2，然后C5依赖C3和C4
@Service("scene9")
public class Scene9 extends ConsumeModeService {

    @Override
    protected void disruptorOperate() {
        MailWorkHandler c1 = new MailWorkHandler(eventCountPrinter);
        MailWorkHandler c2 = new MailWorkHandler(eventCountPrinter);
        MailEventHandler c3 = new MailEventHandler(eventCountPrinter);
        MailEventHandler c4 = new MailEventHandler(eventCountPrinter);
        MailEventHandler c5 = new MailEventHandler(eventCountPrinter);

        disruptor
            // C1和C2共同消费
            .handleEventsWithWorkerPool(c1, c2)
            // C3和C4独立消费，但C3和C4都依赖C1和C2
            .then(c3, c4)
            // 然后C5依赖C3和C4
            .then(c5);
    }
}
