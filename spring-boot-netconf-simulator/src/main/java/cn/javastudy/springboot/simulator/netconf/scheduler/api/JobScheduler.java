package cn.javastudy.springboot.simulator.netconf.scheduler.api;

import cn.javastudy.springboot.simulator.netconf.scheduler.AsyncTask;
import com.google.common.util.concurrent.FluentFuture;

/**
 * 实现周期性调度任务的任务。
 * 任务保证严格被按周期调度，即任务的下一次执行时间在上一次执行完成之后固定时间后再触发再次执行。
 * 因此任务的实际执行周期并不是指定的周期，实际调度周期是：指定的固定周期 + 任务执行耗时，不是一个固定值。
 * 所以这个服务实现的任务调度逻辑不适用于对时间周期很敏感的任务。
 * 如果对时间周期要求非常严格，还是应该使用 {@linkplain java.util.concurrent.ScheduledExecutorService} 或者
 * {@linkplain com.google.common.util.concurrent.AbstractScheduledService.Scheduler} 相关的工具；
 * 另外，这个服务提供的是任务周期调度功能，任务执行的逻辑由调用者指定，因此保证的是任务定时触发一次调度，但是否执行
 * 取决于调用者在任务被调度后的回调的实现。比如如果提交调度任务指定的 task executor 是 jobcoordinator，则只是
 * 保证任务定时提交到 jobcoordinator，任务的执行还需要进一步排队。
 * 因此这个服务提供的是对时间周期要求不严格，但又需要周期性执行的任务的统一调度功能。
 */
public interface JobScheduler {

    /**
     * 提交要周期调度执行的任务.
     *
     * @param task 任务对象，构造这个对象时的 executor 必须设置并且必须是一个线程池，否则任务会中止执行
     * @param <T>  任务每次执行的结果类型
     * @return 用来取消任务的回调函数
     */
    <T> PeriodTaskHandler submitTask(AsyncTask<T> task);

    /**
     * 提交要周期调度执行的任务.
     *
     * @param task             任务对象，构造这个对象时的 executor 必须设置并且必须是一个线程池，否则任务会中止执行
     * @param startImmediately 是否马上启动，也可以不启动，通过返回的对象来控制在合适的时候启动
     * @param <T>              任务每次执行的结果类型
     * @return 用来取消任务的回调函数
     */
    <T> PeriodTaskHandler submitTask(AsyncTask<T> task, boolean startImmediately);


    /**
     * 提交要调度执行的一次性任务。
     *
     * @param task 要执行的任务
     * @param <T>  任务返回结果类型
     * @return 任务返回值
     */
    <T> FluentFuture<T> submitOneshotTask(OneShotTask<T> task);

}
