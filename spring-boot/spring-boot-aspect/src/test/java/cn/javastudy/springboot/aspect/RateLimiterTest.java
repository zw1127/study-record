package cn.javastudy.springboot.aspect;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RateLimiterTest {

    @Test
    public void testWithRateLimiter() throws Exception {
        RateLimiter limiter = RateLimiter.create(10.0); // 每秒不超过1个任务被提交
        Instant started = Instant.now();
        for (int i = 0; i < 101; i++) {
            if (limiter.tryAcquire(1, 500 + i, TimeUnit.MILLISECONDS)) {
                System.out.println(Instant.now() + ", acquired: " + i);
            }
        }
        System.out.println("cost:" + Duration.between(started, Instant.now()).toMillis());
    }

}
