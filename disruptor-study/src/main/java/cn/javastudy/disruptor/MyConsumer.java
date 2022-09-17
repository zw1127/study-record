package cn.javastudy.disruptor;

import cn.javastudy.disruptor.common.AbstractDisruptorConsumer;
import java.util.Calendar;

public class MyConsumer extends AbstractDisruptorConsumer<Integer> {

    private final String name;

    public MyConsumer(String name) {
        this.name = name;
    }

    @Override
    public void consume(Integer object) {
        System.out.println(now() + this.name + "：拿到队列中的数据：" + object);
        //等待1秒钟
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 获取当前时间（分:秒）
    public String now() {
        Calendar now = Calendar.getInstance();
        return "[" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + "] ";
    }
}
