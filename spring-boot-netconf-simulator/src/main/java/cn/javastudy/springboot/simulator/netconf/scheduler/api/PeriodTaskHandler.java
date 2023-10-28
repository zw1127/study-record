package cn.javastudy.springboot.simulator.netconf.scheduler.api;

import com.google.common.util.concurrent.Service;

public interface PeriodTaskHandler extends AutoCloseable {

    /**
     * 启动任务，如果任务本身已经启动，调用没有影响；注意已经调用 {@link #close()} 的任务不能再调用这个方法.
     */
    void start();

    /**
     * 停止任务，但不移除；与 {@link #close()} 不同，这样停止的任务可以通过 {@link #start()} 再启动，关闭的任务不行.
     */
    void stop();

    /**
     * 查询任务当前的状态.
     * @return 任务状态
     */
    Service.State state();

}