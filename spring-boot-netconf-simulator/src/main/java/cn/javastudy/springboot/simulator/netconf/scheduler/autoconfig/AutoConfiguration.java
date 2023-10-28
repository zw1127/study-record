/*
 * Copyright (c) 2021 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
package cn.javastudy.springboot.simulator.netconf.scheduler.autoconfig;

import static org.slf4j.LoggerFactory.getLogger;

import cn.javastudy.springboot.simulator.netconf.properties.NetconfSimulatorProperties;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.JobScheduler;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.JobSchedulerMonitor;
import cn.javastudy.springboot.simulator.netconf.scheduler.impl.JobSchedulerImpl;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AutoConfiguration {

    private static final Logger LOG = getLogger(AutoConfiguration.class);

    @Resource
    private NetconfSimulatorProperties netconfSimulatorProperties;

    @Bean
    @ConditionalOnMissingBean
    public ScheduledThreadPoolExecutor jobSchedulerExecutor() {
        Integer poolSize = Optional.ofNullable(netconfSimulatorProperties)
            .map(NetconfSimulatorProperties::getSchedulePoolSize)
            .orElse(32);
        return new ScheduledThreadPoolExecutor(
            poolSize,
            new ThreadFactoryBuilder()
                .setNameFormat("simulate-notification-sender-%d")
                .setUncaughtExceptionHandler((th, ex) -> LOG.error("thread {} has uncaught exception.", th, ex))
                .build()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public JobScheduler jobScheduler(ScheduledExecutorService jobSchedulerExecutor) {
        return new JobSchedulerImpl(jobSchedulerExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(value = JobScheduler.class)
    public JobSchedulerMonitor jobSchedulerMonitor(JobScheduler scheduler) {
        if (scheduler instanceof JobSchedulerImpl) {
            return ((JobSchedulerImpl) scheduler).asMonitor();
        }
        if (scheduler instanceof JobSchedulerMonitor) {
            return (JobSchedulerMonitor) scheduler;
        }
        throw new IllegalStateException(
            String.format(
                "bean %s is neither '%s' nor implement interface '%s';"
                    + "so we cannot construct service with type '%s' by it;"
                    + "you should override us with your own job-scheduler implementation.",
                scheduler,
                JobSchedulerImpl.class.getName(),
                JobSchedulerMonitor.class.getName(),
                JobSchedulerMonitor.class.getName()));
    }

}
