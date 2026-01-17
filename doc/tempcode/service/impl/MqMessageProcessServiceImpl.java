import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class MqMessageProcessServiceImpl implements MqMessageProcessService {

    private static final Logger LOG = LoggerFactory.getLogger(MqMessageProcessServiceImpl.class);

    private static final long MESSAGE_OVER_TIME = 2 * 60 * 1000;
    private static final List<EventType> SUPPORTED_EVENT_TYPES =
        List.of(EventType.INSERT, EventType.DELETE, EventType.UPDATE);
    @Resource
    private TableProcessorRegisterService tableProcessorRegisterService;

    @Resource
    private CanalProperties canalProperties;

    @Resource
    private SnmpMasterShipService snmpMasterShipService;

    private final ThreadPoolExecutor executorService;

    public MqMessageProcessServiceImpl() {
        int theadPoolSize = Optional.ofNullable(canalProperties)
            .map(CanalProperties::getCanalTheadPoolSize)
            .orElse(DEFAULT_THEAD_POOL_SIZE);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setThreadNamePrefix("snmp-plugin-mq-message-consumer-");
        taskExecutor.setCorePoolSize(theadPoolSize);
        taskExecutor.setMaxPoolSize(theadPoolSize);
        taskExecutor.initialize();
        this.executorService = taskExecutor.getThreadPoolExecutor();
    }

    @PostConstruct
    public void init() {
        LOG.info("{} init.", getClass());
    }

    @PreDestroy
    public void stop() {
        executorService.shutdownNow();
    }

    @Override
    public void process(MessageExt message) {
        executorService.execute(() -> doProcess(message));
    }

    @SuppressWarnings("IllegalCatch")
    public void doProcess(MessageExt message) {
        byte[] body = message.getBody();
        if (ArrayUtils.isEmpty(body)) {
            LOG.info("message body is null");
            return;
        }

        try {
            FlatMessage flatMessage = JsonUtils.readValue(body, FlatMessage.class);

            String database = canalProperties != null ? canalProperties.getDatabase() : DEFAULT_DATABASE;
            if (!StringUtils.equals(database, flatMessage.getDatabase())) {
                LOG.trace("database： {} is ignore.", flatMessage.getDatabase());
                return;
            }

            EventType eventType = EventType.valueOf(flatMessage.getType());
            if (!isSupportedEventType(eventType)) {
                LOG.trace("eventType:{} is not suppported.", eventType);
                return;
            }

            String table = flatMessage.getTable();
            TableProcessor tableProcessor = tableProcessorRegisterService.getTableProcessor(table);
            if (tableProcessor == null) {
                LOG.debug("table: {}'s processor does not registed.", table);
                return;
            }

            // 如果当前节点不是master，同时不是修改的switch_info表的时候，直接返回
            if (!"switch_info".equals(tableProcessor.tableName()) && !snmpMasterShipService.isMaster()) {
                LOG.debug("table:{} ignore, because current node is not master.", table);
                return;
            }

            if (tableProcessor.checkMessageOverTime()) {
                long bornTimestamp = message.getBornTimestamp();
                if (System.currentTimeMillis() - bornTimestamp > MESSAGE_OVER_TIME) {
                    LOG.warn("message:{} is over time, throw it. processor:{}", message.getMsgId(), tableProcessor);
                    return;
                }
            }

            Stopwatch stopwatch = Stopwatch.createStarted();
            tableProcessor.doProcess(message.getMsgId(), eventType, flatMessage);
            stopwatch.stop();
            long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            if (cost > 500) {
                LOG.warn("processor:{} deal message:{} cost:{}", tableProcessor, message.getMsgId(), cost);
            }
        } catch (Throwable e) {
            LOG.warn("process message:{} error.", message.getMsgId(), e);
//            throw new IllegalArgumentException("process message " + message + " error.", e);
        }
    }

    private boolean isSupportedEventType(EventType eventType) {
        return SUPPORTED_EVENT_TYPES.contains(eventType);
    }
}
