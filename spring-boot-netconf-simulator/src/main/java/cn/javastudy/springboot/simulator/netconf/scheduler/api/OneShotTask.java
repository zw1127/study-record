package cn.javastudy.springboot.simulator.netconf.scheduler.api;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(stagedBuilder = true)
public interface OneShotTask<V> {

    static <V> ImmutableOneShotTask.TaskBuildStage<V> builder() {
        return ImmutableOneShotTask.builder();
    }

    static <V> ImmutableOneShotTask.ExecutorBuildStage<V> builderFromCallable(Callable<ListenableFuture<V>> task) {
        return ImmutableOneShotTask.<V>builder().task(task);
    }

    static <V> ImmutableOneShotTask.ExecutorBuildStage<V> builderFromCallableSync(Callable<V> task) {
        return ImmutableOneShotTask.<V>builder().task(() -> Futures.immediateFuture(task.call()));
    }

    static ImmutableOneShotTask.ExecutorBuildStage<Void> builderFromRunnable(Runnable task) {
        return ImmutableOneShotTask.<Void>builder().task(() -> {
            task.run();
            return Futures.immediateFuture(null);
        });
    }

    /**
     * 任务.
     *
     * @return 任务
     */
    Callable<ListenableFuture<V>> task();

    /**
     * 执行任务的执行器，必须是一个线程池.
     *
     * @return 线程池执行器
     */
    Executor executor();

    /**
     * 任务启动延迟，单位是毫秒；如果不指定就立即执行，不能指定负数.
     *
     * @return 启动延迟
     */
    @Nullable
    Long delayMillisecond();

    @Value.Check
    default void check() {
        Long delay = delayMillisecond();
        if (null != delay) {
            if (delay <= 0) {
                throw new IllegalArgumentException(
                    String.format("delay for %s MUST be a positive number rather than %s.",
                        OneShotTask.class.getName(), delay));
            }
        }
    }

    default FluentFuture<V> submit(JobScheduler jobScheduler) {
        return jobScheduler.submitOneshotTask(this);
    }

}