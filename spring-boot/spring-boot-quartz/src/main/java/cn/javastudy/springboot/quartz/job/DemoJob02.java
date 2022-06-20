package cn.javastudy.springboot.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class DemoJob02 extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(DemoJob02.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("executeInternal is started.");
    }
}
