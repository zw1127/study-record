package cn.javastudy.springboot.netconf.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchExample {

    static class Worker implements Runnable {
        private final int taskId;
        private final CountDownLatch countDownLatch;

        public Worker(int taskId, CountDownLatch countDownLatch) {
            this.taskId = taskId;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                System.out.println("Task " + taskId + " is running...");
                TimeUnit.SECONDS.sleep(2);
                System.out.println("Task " + taskId + " is completed.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            executor.execute(new Worker(i, latch));
        }
        System.out.println("Main thread waiting for all tasks to finish");
        latch.await();
        System.out.println("All tasks finished. Main thread resume.");
        executor.shutdown();
    }
}


