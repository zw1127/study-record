package cn.javastudy.springboot.schedule;


import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * 按条件自动停止任务
 */
@Component
@RequiredArgsConstructor
public class AutoStopTask {

    private static final Logger LOG = LoggerFactory.getLogger(AutoStopTask.class);

    private final CustomTaskScheduler customTaskScheduler;

    private int count;

    @Scheduled(cron = "*/5 * * * * *")
    public void printTask() {
        LOG.info("task execute times: {}", count + 1);

        count++;

        // 执行3次后自动停止
        if (count >= 3) {
            LOG.info("task execute config times, now auto stop.");
            boolean cancelled = customTaskScheduler.getScheduledTasks().get(this).cancel(true);
            if (cancelled) {
                count = 0;
                ScheduledMethodRunnable runnable = new ScheduledMethodRunnable(this,
                    Objects.requireNonNull(ReflectionUtils.findMethod(this.getClass(), "printTask")));
                customTaskScheduler.schedule(runnable, new CronTrigger("*/5 * * * * *"));
            }
        }
    }

}
