import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMonitorTableProcessor implements TableProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMonitorTableProcessor.class);

    @Resource
    protected TableProcessorRegisterService tableProcessorRegisterService;

    @PostConstruct
    public void init() {
        tableProcessorRegisterService.registerTableProcessor(this);
        LOG.info("{} init", getClass());
    }

    @PreDestroy
    public void close() {
        tableProcessorRegisterService.unRegisterTableProcessor(this.tableName());
        LOG.info("close {}", getClass());
    }

    @Override
    public void beforeProcess(String messageId, EventType eventType) {
        LOG.debug("begin to process canal message, msgId:{}, eventType:{}, processor:{}", messageId, eventType, this);
    }

    @Override
    public void afterProcess(String messageId, EventType eventType) {
        LOG.info("process message: {}, eventType:{} finished. processor:{}", messageId, eventType, this);
    }
}
