/*
 * Copyright (c) 2023 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
package cn.javastudy.springboot.simulator.netconf.service.impl;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class FuturesTest {

    @Test
    public void testGuavaFutures() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 创建两个 Future
        ListenableFuture<String> future1 = Futures.submit(() -> "Result 1", executorService);
        ListenableFuture<String> future2 = Futures.submit(() -> {
            throw new RuntimeException("Future 2 failed");
        }, executorService);
        ListenableFuture<String> future3 = Futures.submit(() -> "Result 3", executorService);
        ListenableFuture<String> future4 = Futures.submit(() -> {
            TimeUnit.SECONDS.sleep(5);
            return "Result 3";
        }, executorService);

        // 使用 successfulAsList() 处理多个 Future 的完成事件
        ListenableFuture<List<String>> successfulFutures = Futures.successfulAsList(future1, future2, future3, future4);

        Futures.addCallback(successfulFutures, new FutureCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> results) {
                for (int i = 0; i < results.size(); i++) {
                    System.out.println("Future " + (i + 1) + " result: " + results.get(i));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("An error occurred: " + throwable.getMessage());
            }
        }, MoreExecutors.directExecutor());
        future4.cancel(true);
        executorService.shutdown();
    }
}
