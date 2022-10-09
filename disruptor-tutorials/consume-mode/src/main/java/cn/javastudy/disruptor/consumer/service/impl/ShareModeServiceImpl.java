package cn.javastudy.disruptor.consumer.service.impl;

import cn.javastudy.disruptor.consumer.service.ConsumeModeService;
import cn.javastudy.disruptor.consumer.service.MailWorkHandler;
import org.springframework.stereotype.Service;

@Service("shareModeService")
public class ShareModeServiceImpl extends ConsumeModeService {
    @Override
    protected void disruptorOperate() {
        // mailWorkHandler1模拟一号邮件服务器
        MailWorkHandler mailWorkHandler1 = new MailWorkHandler(eventCountPrinter);

        // mailWorkHandler2模拟一号邮件服务器
        MailWorkHandler mailWorkHandler2 = new MailWorkHandler(eventCountPrinter);

        // 调用handleEventsWithWorkerPool，表示创建的多个消费者以共同消费的模式消费
        disruptor.handleEventsWithWorkerPool(mailWorkHandler1, mailWorkHandler2);
    }
}
