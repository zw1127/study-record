package cn.javastudy.springboot.quartz.job;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class DemoJob01 extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(DemoJob01.class);

    @Resource
    private ApplicationContext applicationContext;
    private final AtomicInteger counts = new AtomicInteger();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("executeInternal, {} times process.", counts.incrementAndGet());
    }
}
