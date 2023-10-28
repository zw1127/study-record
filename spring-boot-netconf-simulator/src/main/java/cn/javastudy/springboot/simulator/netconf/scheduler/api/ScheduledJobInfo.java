package cn.javastudy.springboot.simulator.netconf.scheduler.api;

import com.google.common.util.concurrent.Service;
import org.eclipse.jdt.annotation.Nullable;

public interface ScheduledJobInfo {

    /**
     * 任务的 key.
     *
     * @return 任务的 key
     */
    String key();

    /**
     * 任务启动的时间.
     * @return 启动时间
     */
    Long startAtEpochMillisecond();

    /**
     * 任务的状态.
     *
     * @return 任务的状态
     */
    Service.State state();

    /**
     * 如果任务是失败的状态，这是失败的原因消息；这和 {@link #latestFailCause()} 表示的单次执行失败不同.
     *
     * @return 失败原因
     */
    @Nullable
    String failMessage();

    /**
     * 提供任务的调用者.
     *
     * @return 提供任务的调用者
     */
    String provider();

    /**
     * 任务最近一次开始执行的时间.
     *
     * @return 任务最近一次开始执行的时间
     */
    @Nullable
    Long latestStartAtEpochMillisecond();

    /**
     * 任务最近一次执行结束的时间.
     *
     * @return 任务最近一次执行结束的时间
     */
    @Nullable
    Long latestDoneAtEpochMillisecond();

    /**
     * 最近一次执行是否成功.
     * @return 最近一次执行是否成功
     */
    boolean latestSuccess();

    /**
     * 如果最近一次执行失败，导致失败的异常.
     * @return 如果最近一次执行失败，导致失败的异常；一旦执行成功一次就会被清除为 null.
     */
    @Nullable
    Throwable latestFailCause();

}