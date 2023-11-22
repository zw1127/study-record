package cn.javastudy.springboot.aspect;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.SettableFuture;
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


    @Test
    public void testFuturesAddCallback() throws Exception {
        Futures.addCallback(futures(), new FutureCallback<>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println("test success");
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("test failed");
            }
        }, MoreExecutors.directExecutor());
        TimeUnit.SECONDS.sleep(30);
    }

    private ListenableFuture<Integer> futures() {
        SettableFuture<Integer> future = SettableFuture.create();
        future.set(throwRuntimeException());
        return future;
    }

    private Integer throwRuntimeException() {
        throw new RuntimeException("test");
    }
}
