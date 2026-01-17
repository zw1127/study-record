
import com.alibaba.otter.canal.protocol.CanalEntry;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public abstract class AbstractBaseTableProcessor extends AbstractMonitorTableProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseTableProcessor.class);

    @Override
    public void process(CanalEntry.EventType eventType, FlatMessage flatMessage) {
        // old里只有修改过的属性值
        List<Map<String, String>> before = flatMessage.getOld();
        // data里是该行数据的所有属性的值
        List<Map<String, String>> data = flatMessage.getData();
        LOG.trace("eventType:{}, before:{}, data: {}", eventType, before, data);

        if (CanalEntry.EventType.UPDATE.equals(eventType)) {
            if (before.size() != data.size()) {
                LOG.info("before size:{}, dataList size:{} is not same.", before.size(), data.size());
                return;
            }

            for (int i = 0; i < data.size(); i++) {
                update(data.get(i), before.get(i));
            }
            return;
        }

        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        switch (eventType) {
            case INSERT:
                data.forEach(this::insert);
                break;
            case DELETE:
                data.forEach(this::delete);
                return;
            default:
                LOG.debug("eventType:{} do not care.", eventType);
        }
    }

    protected abstract void update(Map<String, String> dataMap, Map<String, String> oldData);

    protected abstract void insert(Map<String, String> dataMap);

    protected abstract void delete(Map<String, String> dataMap);

}
