package cn.javastudy.disruptor.operate.service;

public interface LowLevelOperateService {

    /**
     * 消费者数量
     */
    int CONSUMER_NUM = 3;

    /**
     * 环形缓冲区大小
     */
    int BUFFER_SIZE = 16;

    /**
     * 发布一个事件
     *
     * @param value value
     */
    void publish(String value);

    /**
     * 返回已经处理的任务总数
     *
     * @return size
     */
    long eventCount();
}
