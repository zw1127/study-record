package cn.javastudy.springboot.simulator.netconf.scheduler.api;

import java.util.Map;

public interface JobSchedulerMonitor {

    /**
     * 返回周期任务的信息.
     *
     * @return 周期任务的信息
     */
    Map<String, ScheduledJobInfo> scheduledJobInfo();

    /**
     * 返回某一个周期任务的信息.
     *
     * @param key 任务 key
     * @return 周期任务信息
     */
    ScheduledJobInfo scheduledJobInfoOf(String key);

}