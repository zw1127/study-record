
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIgnoreTableDeleteProcessor<O extends MibModel, T, S>
    extends AbstractTableProcessor<O, T, S> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractIgnoreTableDeleteProcessor.class);

    protected AbstractIgnoreTableDeleteProcessor(CanalModelTransform<O, T, S> modelTransform) {
        super(modelTransform);
    }

    @Override
    protected void delete(Map<String, String> dataMap) {
        LOG.info("table:{} delete event should ignore. dataMap:{}", tableName(), dataMap);
    }
}
