package cn.javastudy.springboot.simulator.netconf.scheduler.impl;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import cn.javastudy.springboot.simulator.netconf.scheduler.AsyncScheduledService;
import cn.javastudy.springboot.simulator.netconf.scheduler.AsyncTask;
import cn.javastudy.springboot.simulator.netconf.scheduler.ImmutableAsyncTask;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.JobScheduler;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.JobSchedulerMonitor;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.OneShotTask;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.PeriodTaskHandler;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.ScheduledJobInfo;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

@ToString
public class JobSchedulerImpl implements JobScheduler {

    private static final Logger LOG = getLogger(JobSchedulerImpl.class);

    private static String providerFromCaller() {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        StackTraceElement target = null;
        // 前两层分别是 getStackTrace 和当前方法
        for (int i = 2; i < stacks.length; i++) {
            StackTraceElement stack = stacks[i];
            String callerName = stack.getClassName();
            if (callerName.startsWith("cn.javastudy.springboot.simulator.netconf.jobscheduler")) {
                continue;
            }
            target = stack;
            break;
        }
        if (target == null) {
            target = stacks[stacks.length - 1];
        }
        return String.format("%s#%s:%s", target.getClassName(), target.getMethodName(), target.getLineNumber());
    }

    private static Runnable combineRunnable(Runnable runnable, Runnable then) {
        if (then == null) {
            return runnable;
        }
        return () -> {
            runnable.run();
            then.run();
        };
    }

    private static <T> Consumer<T> combineConsumer(Consumer<T> consumer, Consumer<T> then) {
        if (then == null) {
            return consumer;
        }
        return consumer.andThen(then);
    }

    private final ScheduledExecutorService scheduler;
    private final Map<String, AsyncScheduledService<?>> tasks;
    private final Map<String, ScheduledJobInfo> taskMonitorInfo;
    private final JobSchedulerMonitor monitor;

    public JobSchedulerImpl(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        this.tasks = new ConcurrentHashMap<>();
        this.taskMonitorInfo = new ConcurrentHashMap<>();
        this.monitor = new JobSchedulerMonitor() {

            @Override
            public Map<String, ScheduledJobInfo> scheduledJobInfo() {
                if (taskMonitorInfo.isEmpty()) {
                    return Collections.emptyMap();
                }
                return Collections.unmodifiableMap(taskMonitorInfo);
            }

            @Override
            public ScheduledJobInfo scheduledJobInfoOf(String key) {
                return taskMonitorInfo.get(key);
            }
        };
    }

    @Override
    public <T> PeriodTaskHandler submitTask(AsyncTask<T> task) {
        return submitTask(task, true);
    }

    @Override
    public <T> PeriodTaskHandler submitTask(AsyncTask<T> task, boolean startImmediately) {
        requireNonNull(task);
        return submitTaskIfNeeded(task, startImmediately);
    }


    @Override
    public <T> FluentFuture<T> submitOneshotTask(OneShotTask<T> task) {
        return FluentFuture.from(Futures.scheduleAsync(
            new AsyncCallabbleTask<>(task.task(), task.executor(), () -> {
            }),
            Optional.ofNullable(task.delayMillisecond()).orElse(0L), TimeUnit.MILLISECONDS, this.scheduler));
    }

    public JobSchedulerMonitor asMonitor() {
        return this.monitor;
    }

    private <T> PeriodTaskHandler submitTaskIfNeeded(AsyncTask<T> task, boolean startImmedately) {
        requireNonNull(task);
        AtomicBoolean added = new AtomicBoolean(false);
        String id = Integer.toHexString(task.hashCode());

        AsyncScheduledService<?> computed = this.tasks.computeIfAbsent(id, key -> {
            added.set(true);
            AsyncCallable<T> callable = task.task();
            ScheduledTaskInfo taskInfo = new ScheduledTaskInfo(id, providerFromCaller());
            AsyncCallabbleTask<T> wrappedTask =
                new AsyncCallabbleTask<>(callable, task.executor(), () -> {
                    this.stopTask(id, true);
                    taskInfo.onTerminatedForce();
                });
            AsyncScheduledService<T> scheduleTask =
                ImmutableAsyncTask.<T>builder()
                    .from(task)
                    .task(wrappedTask)
                    // 这里为了检测线程是否切换，不直接向 AsyncScheduledService 传线程池
                    // 这样调回到这边的 wrappedTask 的 call 方法时就还是保留了调度线程
                    .executor(MoreExecutors.directExecutor())
                    .onStart(combineRunnable(taskInfo::onStart, task.onStart()))
                    .onStop(task.onStop())
                    .onFail(combineConsumer(ex -> taskInfo.onFail(ex.getMessage()), task.onFail()))
                    .onStateChange(combineConsumer(taskInfo::onStateChange, task.onStateChange()))
                    .onSuccess(combineConsumer(val -> taskInfo.onDone(null), task.onSuccess()))
                    .onError(combineConsumer(taskInfo::onDone, task.onError()))
                    .onEachStart(combineRunnable(taskInfo::onEachStart, task.onEachStart()))
                    .build()
                    .forSchedule(this.scheduler);
            this.taskMonitorInfo.put(id, taskInfo);
            return scheduleTask;
        });

        if (added.get() && startImmedately) {
            computed.start();
            LOG.debug("submit periodic task {} for execution success.", task);
        }

        return new PeriodTaskHandler() {
            @Override
            public void start() {
                AsyncScheduledService<?> current = tasks.get(id);
                if (current != null) {
                    if (!Objects.equals(current, computed)) {
                        throw new IllegalStateException(String.format(
                            "service %s is replaced by %s but its start method is called.", task, computed));
                    }
                    current.start();
                } else {
                    throw new IllegalStateException(
                        String.format("service %s is not found but the start method is called.", task));
                }
            }

            @Override
            public void stop() {
                stopTask(id);
            }

            @Override
            public Service.State state() {
                return computed.state();
            }

            @Override
            public void close() throws Exception {
                removeTask(id);
            }
        };
    }

    private <T> void removeTask(String id) {
        AsyncScheduledService<?> wrapped = tasks.remove(id);
        taskMonitorInfo.remove(id);
        if (wrapped != null) {
            LOG.debug("task {} is stopped.", wrapped);
            wrapped.stop();
        }
    }

    private <T> void stopTask(String id) {
        stopTask(id, false);
    }

    private <T> void stopTask(String id, boolean forced) {
        AsyncScheduledService<?> wrapped = tasks.get(id);
        if (wrapped != null) {
            if (forced) {
                LOG.warn("task {} is force stopped.", wrapped);
            }
            wrapped.stop();
        }
    }

    @ToString
    @EqualsAndHashCode
    private static final class ScheduledTaskInfo implements ScheduledJobInfo {

        private final String key;
        private final String provider;

        private Long startedAt;
        private Service.State state;
        private String failMessage = null;
        private Long latestStartAtEpochMillisecond = null;
        private Long latestDoneAtEpochMillisecond = null;
        private boolean latestSuccess = false;
        private Throwable latestFailCause = null;
        private boolean terminatedForce = false;

        private ScheduledTaskInfo(String key, String provider) {
            this.key = key;
            this.provider = provider;
            this.state = Service.State.STARTING;
        }

        @Override
        public String key() {
            return this.key;
        }

        @Override
        public Long startAtEpochMillisecond() {
            return this.startedAt;
        }

        @Override
        public Service.State state() {
            return this.state;
        }

        @Nullable
        @Override
        public String failMessage() {
            return this.failMessage;
        }

        @Override
        public String provider() {
            return this.provider;
        }

        @Nullable
        @Override
        public Long latestStartAtEpochMillisecond() {
            return this.latestStartAtEpochMillisecond;
        }

        @Nullable
        @Override
        public Long latestDoneAtEpochMillisecond() {
            return this.latestDoneAtEpochMillisecond;
        }

        @Override
        public boolean latestSuccess() {
            return this.latestSuccess;
        }

        @Nullable
        @Override
        public Throwable latestFailCause() {
            return this.latestFailCause;
        }

        private void onStateChange(Service.State newState) {
            this.state = newState;
        }

        private void onFail(String message) {
            this.failMessage = message;
        }

        private void onStart() {
            this.startedAt = System.currentTimeMillis();
        }

        private void onEachStart() {
            this.latestStartAtEpochMillisecond = System.currentTimeMillis();
        }

        private void onDone(Throwable ex) {
            this.latestDoneAtEpochMillisecond = System.currentTimeMillis();
            this.latestSuccess = null == ex;
            this.latestFailCause = ex;
        }

        private void onTerminatedForce() {
            this.terminatedForce = true;
        }
    }

    private static final class AsyncCallabbleTask<V> implements AsyncCallable<V> {

        private final AsyncCallable<V> task;
        private final Executor executor;
        private final Runnable cancelHandler;

        private volatile ExecutorTaskFuture executorTaskFuture;

        private AsyncCallabbleTask(Callable<ListenableFuture<V>> task, Executor executor, Runnable cancelHandler) {
            this((AsyncCallable<V>) task::call, executor, cancelHandler);
        }

        private AsyncCallabbleTask(AsyncCallable<V> task, Executor executor, Runnable cancelHandler) {
            this.task = task;
            // 如果没有给 executor，不直接报错，会以任务中止的方式来处理
            this.executor = null == executor ? MoreExecutors.directExecutor() : executor;
            this.cancelHandler = cancelHandler;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public ListenableFuture<V> call() throws Exception {
            // 被调度，这里一定是调度线程池的线程
            long tid = Thread.currentThread().getId();
            if (LOG.isTraceEnabled()) {
                LOG.trace("task {} is scheduled.", this.task);
            }
            this.executorTaskFuture = new ExecutorTaskFuture(tid);
            this.executor.execute(this.executorTaskFuture);
            return executorTaskFuture;
        }

        @SuppressWarnings("IllegalCatch")
        private final class ExecutorTaskFuture extends AbstractFuture<V> implements Runnable {

            private final long schedulerTid;

            private ExecutorTaskFuture(long schedulerTid) {
                this.schedulerTid = schedulerTid;
            }

            @Override
            public void run() {
                // 这里换了 executor，禁止用调度线程池来执行任务
                long executeTid = Thread.currentThread().getId();
                if (schedulerTid == executeTid) {
                    LOG.warn("the task {} is submitted with a non-thread-pool executor; "
                        + "we won't schedule it any more.", task);
                    AsyncCallabbleTask.this.cancelHandler.run();
                    return;
                }
                if (LOG.isTraceEnabled()) {
                    LOG.trace("task {} will be executed.", task);
                }

                if (this.isDone()) {
                    return;
                }

                try {
                    ListenableFuture<V> taskFuture = AsyncCallabbleTask.this.task.call();
                    this.setFuture(taskFuture);
                    if (this.isDone() && !taskFuture.isDone()) {
                        taskFuture.cancel(this.wasInterrupted());
                    }
                } catch (Exception ex) {
                    this.setException(ex);
                }
            }
        }
    }
}
