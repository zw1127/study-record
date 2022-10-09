package cn.javastudy.disruptor.basic.service;

public interface BasicEventService {

    /**
     * 发布一个事件
     *
     * @param value value
     */
    void publish(String value);

    /**
     * 返回已经处理的任务总数
     *
     * @return long
     */
    long eventCount();
}
