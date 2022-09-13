package cn.javastudy.springboot.schedule;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 简单计划任务
 */
@Component
public class SimpleTask {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTask.class);

    @Async
    @Scheduled(cron = "*/5 * * * * *")
    public void printTask() {
        LOG.info("this is a simple task.");
    }

}
