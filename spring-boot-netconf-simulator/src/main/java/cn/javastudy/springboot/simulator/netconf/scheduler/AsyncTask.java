package cn.javastudy.springboot.simulator.netconf.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public interface AsyncTask<V> {

    static <V> ImmutableAsyncTask.Builder<V> builder() {
        return ImmutableAsyncTask.builder();
    }

    static <V> ImmutableAsyncTask.Builder<V> fromCallable(Callable<ListenableFuture<V>> callable) {
        return AsyncTask.<V>builder().task(callable::call);
    }

    static <V> ImmutableAsyncTask.Builder<V> fromAsyncCallable(AsyncCallable<V> callable) {
        return AsyncTask.<V>builder().task(callable);
    }

    /**
     * 返回要执行的任务本身.
     *
     * @return 任务对象
     */
    AsyncCallable<V> task();

    @Nullable
    Long initDelayMillisecond();

    /**
     * 指定固定的间隔周期.
     * 如果指定了，{@link #intervalMillisecondSupplier()} 失效。
     *
     * @return 固定周期，单位是毫秒
     */
    @Nullable
    Long intervalMillisecond();

    /**
     * 用来计算任务调用周期，单位是毫秒.
     *
     * @return 用来计算任务调用周期的回调函数
     */
    @Nullable
    Supplier<Long> intervalMillisecondSupplier();

    /**
     * 用来计算任务调用周期，单位是毫秒，会传入上一轮执行结果，如果上一轮没有异常，会传 null.
     *
     * @return 用来计算任务调用周期的回调函数
     */
    @Nullable
    Function<Throwable, Long> intervalMillisecondMapper();

    /**
     * 指定固定的超时时间，单位毫秒.
     * 如果有返回值，则 {@link #timeoutMillisecondSupplier()} 失效。
     *
     * @return 固定超时时间
     */
    @Nullable
    Long timeoutMillisecond();

    /**
     * 用来计算任务超时时间，单位是毫秒.
     *
     * @return 计算超时时间的调度器
     */
    @Nullable
    Supplier<Long> timeoutMillisecondSupplier();

    /**
     * 指定在任务启动的时候被调用的监听器.
     *
     * @return 任务启动时调用的监听器
     */
    @Nullable
    Runnable onStart();

    /**
     * 指定在任务停止的时候被调用的监听器.
     *
     * @return 任务停止时调用的监听器
     */
    @Nullable
    Runnable onStop();

    /**
     * 在任务因异常转到失败状态时要执行的监听器.
     *
     * @return 在任务因异常转到失败状态时要执行的监听器
     */
    @Nullable
    Consumer<Throwable> onFail();

    /**
     * 执行任务状态变化后要执行的监听器.
     *
     * @return 任务状态变化后的监听器
     */
    @Nullable
    Consumer<Service.State> onStateChange();

    /**
     * 指定在任务每一次开始的时候要执行的监听器.
     *
     * @return 任务每一次执行之前的监听器
     */
    @Nullable
    Runnable onEachStart();

    /**
     * 在每一次执行结束后，不管是成功还是失败，都会调用一次.
     *
     * @return 任务每一轮执行结束后调用
     */
    @Nullable
    Runnable onDone();

    /**
     * 指定一个回调函数，在每一次任务执行后得到的是成功结果后调用.
     *
     * @return 成功操作的监听器
     */
    @Nullable
    Consumer<V> onSuccess();

    /**
     * 指定一个回调函数，在每一次执行得到异常时调用.
     * 其中，如果指定了超时时间并且超时了，会得到的异常是 {@code java.util.concurrent.TimeoutException}；
     * 也可以通过 {@link AsyncTask#isTimeout(Throwable)} 来辅助判断。
     *
     * @return 异常操作的监听器
     */
    @Nullable
    Consumer<Throwable> onError();

    /**
     * 用来执行任务的执行器，否则执行任务的将是调度线程池的线程.
     * 注意调度线程池的线程不应该被过度/阻塞式占用，否则可能造成调度延迟。
     *
     * @return 执行任务的线程池
     */
    @Nullable
    Executor executor();

    @Value.Check
    default void check() {
        int supplierCount = 0;
        if (null != intervalMillisecond()) {
            ++supplierCount;
        }
        if (null != intervalMillisecondSupplier()) {
            ++supplierCount;
        }
        if (null != intervalMillisecondMapper()) {
            ++supplierCount;
        }
        Preconditions.checkArgument(
            1 == supplierCount,
            "more than one rather than only one interval value or supplier function is provided.");
    }

    static boolean isTimeout(Throwable ex) {
        return ex instanceof TimeoutException;
    }

    @Value.Derived
    default AsyncScheduledService<V> forSchedule(ScheduledExecutorService scheduledExecutorService) {
        return new AsyncScheduledService<>(this, scheduledExecutorService);
    }

    default <R> R submit(Function<AsyncTask<V>, R> scheduler) {
        return scheduler.apply(this);
    }
}
