package cn.javastudy.disruptor.consumer.service.impl;

import cn.javastudy.disruptor.consumer.service.ConsumeModeService;
import cn.javastudy.disruptor.consumer.service.MailEventHandler;
import org.springframework.stereotype.Service;

@Service("scene5")
public class Scene5 extends ConsumeModeService {

    @Override
    protected void disruptorOperate() {
        MailEventHandler c1 = new MailEventHandler(eventCountPrinter);
        MailEventHandler c2 = new MailEventHandler(eventCountPrinter);
        MailEventHandler c3 = new MailEventHandler(eventCountPrinter);

        disruptor
            // C1、C2独立消费
            .handleEventsWith(c1, c2)
            // C3依赖C1和C2
            .then(c3);
    }
}
