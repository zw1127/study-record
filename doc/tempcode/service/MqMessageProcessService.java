import org.apache.rocketmq.common.message.MessageExt;

public interface MqMessageProcessService {

    void process(MessageExt message);
}
