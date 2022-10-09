package cn.javastudy.disruptor.consumer.service.impl;

import cn.javastudy.disruptor.consumer.service.ConsumeModeService;
import cn.javastudy.disruptor.consumer.service.MailEventHandler;
import cn.javastudy.disruptor.consumer.service.SmsEventHandler;
import org.springframework.stereotype.Service;

@Service("independentModeService")
public class IndependentModeServiceImpl extends ConsumeModeService {

    @Override
    protected void disruptorOperate() {
        // 调用handleEventsWith，表示创建的多个消费者，每个都是独立消费的
        // 这里创建两个消费者，一个是短信的，一个是邮件的
        disruptor.handleEventsWith(new SmsEventHandler(eventCountPrinter), new MailEventHandler(eventCountPrinter));
    }
}