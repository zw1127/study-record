import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TableProcessorRegisterServiceImpl implements TableProcessorRegisterService {

    private static final Logger LOG = LoggerFactory.getLogger(TableProcessorRegisterServiceImpl.class);

    private static final Map<String, TableProcessor> PROCESSOR_MAP = new ConcurrentHashMap<>();

    @Override
    public void registerTableProcessor(TableProcessor tableProcessor) {
        if (tableProcessor == null || StringUtils.isEmpty(tableProcessor.tableName())) {
            LOG.info("TableProcessor is null or tableName is empty.");
            return;
        }

        if (PROCESSOR_MAP.containsKey(tableProcessor.tableName())) {
            LOG.info("table:{} TableProcessor: {} regited already.", tableProcessor.tableName(), tableProcessor);
            return;
        }

        PROCESSOR_MAP.put(tableProcessor.tableName(), tableProcessor);
        LOG.info("register TableProcessor:{} successful.", tableProcessor);
    }

    @Override
    public void unRegisterTableProcessor(String tableName) {
        TableProcessor tableProcessor = PROCESSOR_MAP.remove(tableName);
        LOG.info("unregister table:{} processor, TableProcessor is {}", tableName, tableProcessor);
    }

    @Override
    public List<TableProcessor> registedTableProcessList() {
        return List.copyOf(new ArrayList<>(PROCESSOR_MAP.values()));
    }

    @Override
    public TableProcessor getTableProcessor(String tableName) {
        return PROCESSOR_MAP.get(tableName);
    }

}
