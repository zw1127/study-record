package cn.javastudy.springboot.simulator.netconf.scheduler;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import cn.javastudy.springboot.simulator.netconf.utils.RecoverableFuture;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

public class AsyncScheduledService<V> {

    private static final Logger LOG = getLogger(AsyncScheduledService.class);

    private final AsyncTask<V> task;
    private final ScheduledExecutorService scheduler;

    private final AtomicReference<Service.State> state;
    private String failureCause;
    private volatile ListenableFuture<V> executeFuture;

    public AsyncScheduledService(AsyncTask<V> task, ScheduledExecutorService scheduler) {
        this.task = requireNonNull(task);
        this.scheduler = requireNonNull(scheduler);
        this.executeFuture = null;
        this.state = new AtomicReference<>(Service.State.NEW);
    }

    @CanIgnoreReturnValue
    public AsyncScheduledService<V> start() {
        if (this.changeState(Service.State.STARTING)) {
            Runnable onStart = this.task.onStart();
            if (onStart != null) {
                onStart.run();
            }
            return forChangeState(() -> {
                doStartInitSchedule();
                this.changeState(Service.State.RUNNING);
                return this;
            });
        }
        return this;
    }

    public ListenableFuture<V> stop() {
        this.changeState(Service.State.STOPPING);
        return forChangeState(() -> {
            ListenableFuture<V> ret = Optional.of(stopSchedule()).orElse(Futures.immediateFuture(null));
            ret.addListener(() -> this.changeState(Service.State.TERMINATED), MoreExecutors.directExecutor());
            return ret;
        });
    }

    public Service.State state() {
        return this.state.get();
    }

    public String failMessage() {
        return this.failureCause;
    }

    private boolean willStop() {
        Service.State cur = state();
        return Service.State.STOPPING.equals(cur) || Service.State.TERMINATED.equals(cur);
    }

    private boolean changeState(Service.State newState) {
        Service.State origin = this.state.getAndUpdate(old -> {
            if (!Objects.equals(old, newState)) {
                return newState;
            }
            return old;
        });
        if (!Objects.equals(origin, newState)) {
            Consumer<Service.State> listener = this.task.onStateChange();
            if (listener != null) {
                listener.accept(newState);
            }
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("IllegalCatch")
    private <T> T forChangeState(Supplier<T> op) {
        try {
            return op.get();
        } catch (Throwable ex) {
            this.changeState(Service.State.FAILED);
            this.failureCause = ex.getMessage();
            Consumer<Throwable> onFail = this.task.onFail();
            if (onFail != null) {
                onFail.accept(ex);
            }
            throw ex;
        }
    }

    private void doStartInitSchedule() {
        doSchedule(initDelayOrZero(), true);
    }

    @SuppressWarnings("IllegalCatch")
    private void doSchedule(long delay, boolean init) {
        if (willStop() || init && this.executeFuture != null || !init && this.executeFuture == null) {
            return;
        }
        ListenableFuture<V> newFuture = null;
        synchronized (this) {
            if (init && this.executeFuture != null || !init && this.executeFuture == null) {
                return;
            }
            AsyncCallable<V> executedTask =
                timeoutDecorator(asyncDecorator(initDecorator(requireNonNull(task.task()), init)));
            newFuture = Futures.scheduleAsync(
                executedTask,
                delay,
                TimeUnit.MILLISECONDS,
                scheduler
            );
            this.executeFuture = newFuture;
        }
        @Nullable Runnable doneCallback = task.onDone();
        if (doneCallback != null) {
            newFuture.addListener(() -> {
                try {
                    doneCallback.run();
                } catch (Throwable ex) {
                    LOG.debug("error on done callback of task {} : {}-{}",
                        task, ex.getClass().getName(), ex.getMessage());
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("detail caused by:", ex);
                    }
                }
            }, MoreExecutors.directExecutor());
        }
        Futures.addCallback(
            newFuture,
            new FutureCallback<V>() {
                @Override
                @SuppressWarnings("IllegalCatch")
                public void onSuccess(@org.checkerframework.checker.nullness.qual.Nullable V result) {
                    try {
                        Consumer<V> callback = task.onSuccess();
                        if (callback != null) {
                            callback.accept(result);
                        }
                    } catch (Throwable ex) {
                        LOG.debug("error on success callback of task {} with result {}: {}-{}",
                            task, result, ex.getClass().getName(), ex.getMessage());
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("detail caused by:", ex);
                        }
                    }
                    doSchedule(nextInterval(null), false);
                }

                @Override
                @SuppressWarnings("IllegalCatch")
                public void onFailure(Throwable ex) {
                    try {
                        @Nullable Consumer<Throwable> callback = task.onError();
                        if (callback != null) {
                            callback.accept(ex);
                        }
                    } catch (Throwable ex1) {
                        LOG.debug("error on failure callback of task {} with failure {}-{} cause: {}-{}",
                            task,
                            ex.getClass().getName(), ex.getMessage(),
                            ex1.getClass().getName(), ex1.getMessage());
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("detail caused by:", ex);
                        }
                    }
                    doSchedule(nextInterval(ex), false);
                }
            },
            MoreExecutors.directExecutor());
    }

    @SuppressWarnings("IllegalCatch")
    private ListenableFuture<V> stopSchedule() {
        ListenableFuture<V> taskFuture = null;
        if (null != this.executeFuture) {
            synchronized (this) {
                if (this.executeFuture != null) {
                    taskFuture = this.executeFuture;
                    this.executeFuture = null;
                }
            }
        }
        if (null != taskFuture) {
            taskFuture.cancel(true);
        } else {
            taskFuture = Futures.immediateFuture(null);
        }
        @Nullable Runnable callback = this.task.onStop();
        if (callback != null) {
            taskFuture.addListener(() -> {
                try {
                    callback.run();
                } catch (Exception ex) {
                    LOG.warn("Exception on notify on stop callback of task {}: {}-{}",
                        task, ex.getClass().getName(), ex.getMessage());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Detail cause is:", ex);
                    }
                }
            }, MoreExecutors.directExecutor());
        }
        return RecoverableFuture.alwaysResume(taskFuture, MoreExecutors.directExecutor());
    }

    private long initDelayOrZero() {
        Long delay = task.initDelayMillisecond();
        if (delay == null) {
            return 0L;
        }
        Preconditions.checkArgument(delay > 0, "init delay of task {} is not a positive number.", delay);
        return delay;
    }

    private AsyncCallable<V> initDecorator(AsyncCallable<V> decorated, boolean init) {
        if (init) {
            @Nullable Runnable callback = this.task.onEachStart();
            if (null != callback) {
                return () -> {
                    callback.run();
                    return decorated.call();
                };
            }
        }
        return decorated;
    }

    private AsyncCallable<V> asyncDecorator(AsyncCallable<V> decorated) {
        @Nullable Executor executor = this.task.executor();
        if (executor != null) {
            return () -> Futures.submitAsync(decorated, executor);
        }
        return decorated;
    }

    private AsyncCallable<V> timeoutDecorator(AsyncCallable<V> decorated) {
        @Nullable Long timeout = this.timeoutOrNull();
        if (timeout != null) {
            return () -> Futures.withTimeout(decorated.call(), timeout, TimeUnit.MILLISECONDS, this.scheduler);
        }
        return decorated;
    }

    private long nextInterval(Throwable ex) {
        Long period = Optional.ofNullable(task.intervalMillisecond())
            .orElseGet(() ->
                Optional.ofNullable(task.intervalMillisecondSupplier()).map(Supplier::get).orElseGet(() ->
                    Optional.ofNullable(task.intervalMillisecondMapper()).map(m -> m.apply(ex)).orElse(null)));
        if (period == null || period < 0) {
            throw new IllegalArgumentException(
                String.format("cannot get legal task interval %s for task %s", period, task));
        }
        return period;
    }

    private Long timeoutOrNull() {
        Long timeout = Optional.ofNullable(task.timeoutMillisecond())
            .orElseGet(() -> Optional.ofNullable(task.timeoutMillisecondSupplier()).map(Supplier::get).orElse(null));
        if (timeout != null && timeout < 0) {
            throw new IllegalArgumentException(
                String.format("illegal timeout value %s got from task %s", timeout, task));
        }
        return timeout;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("task", task)
            .add("scheduler", scheduler)
            .add("executeFuture", executeFuture)
            .toString();
    }
}
