import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

public interface TableProcessor {

    String tableName();

    void process(EventType eventType, FlatMessage flatMessage);

    default void doProcess(String messageId, EventType eventType, FlatMessage flatMessage) {
        beforeProcess(messageId, eventType);
        process(eventType, flatMessage);
        afterProcess(messageId, eventType);
    }

    void beforeProcess(String messageId, EventType eventType);

    void afterProcess(String messageId, EventType eventType);

    /**
     * 是否检查消息的超时事件.
     *
     * @return 默认检查，返回true.
     */
    default boolean checkMessageOverTime() {
        return true;
    }
}
