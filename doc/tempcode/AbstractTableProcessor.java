
import static com.fiberhome.sdn.fitview.plugins.snmp.api.utils.SnmpUtils.requireNonNull;

import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTableProcessor<O extends MibModel, T, S> extends AbstractBaseTableProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTableProcessor.class);

    private static final String TIMESTAMP_KEY = "timestamp";
    // 阈值配置表里用的是time_stamp
    private static final String TIME_STAMP_KEY = "time_stamp";

    @Resource
    protected SnmpConfigService snmpConfigService;

    @Resource
    protected JobCoordinatorService jobCoordinatorService;

    @Resource
    protected DeviceDaoService deviceDaoService;

    @Resource
    protected SnmpDeviceVersionService snmpDeviceVersionService;

    @Resource
    private DeviceAdapterFactoryService deviceAdapterFactoryService;

    protected DataMapTransform<T> dataMapTransform;
    protected ModelTransform<O, T, S> modelTransform;

    protected AbstractTableProcessor(DataMapTransform<T> dataMapTransform, ModelTransform<O, T, S> modelTransform) {
        this.dataMapTransform = dataMapTransform;
        this.modelTransform = modelTransform;
    }

    protected AbstractTableProcessor(CanalModelTransform<O, T, S> modelTransform) {
        this.dataMapTransform = modelTransform;
        this.modelTransform = modelTransform;
    }

    @Override
    protected void insert(Map<String, String> dataMap) {
        String timestamp = fetchTimeStamp(dataMap);
        if (StringUtils.isNotEmpty(timestamp)) {
            LOG.info("inverse operate, timestamp:{} is not empty, do not config to device. processor:{}.",
                timestamp, this);
            return;
        }

        String nodeId = dataMap.get("node_id");
        if (StringUtils.isEmpty(nodeId)) {
            LOG.info("dataMap:{}, resovle nodeId is null.", dataMap);
            return;
        }

//        if (snmpDeviceVersionService.isUsp30(nodeId)) {
//            LOG.info("device:{} is usp 3.0 platform, do not insert config data.", nodeId);
//            return;
//        }

        T convert = dataMapTransform.transformConfigModel(dataMap);

        enqueueJob(fetchNodeId(convert), () -> toListenableFuture(processInsert(convert)));
    }

    @Override
    protected void update(Map<String, String> dataMap, Map<String, String> oldData) {
        String nodeId = dataMap.get("node_id");
        if (StringUtils.isEmpty(nodeId)) {
            LOG.info("dataList:{}, resovle nodeId is null.", dataMap);
            return;
        }

//        if (snmpDeviceVersionService.isUsp30(nodeId)) {
//            LOG.info("device:{} is usp 3.0 platform, do not update config data.", nodeId);
//            return;
//        }

        String timestamp = fetchTimeStamp(dataMap);
        String oldTimestamp = fetchTimeStamp(oldData);
        boolean changeTimestamp = containTimeStamp(oldData);
        // update时，before key不为空，同时与after不相等，表示反算
        if (changeTimestamp && !StringUtils.equals(timestamp, oldTimestamp)) {
            LOG.info("inverse operate, timestamp change:{}, old timestamp:{}, now timestamp:{} is different, "
                + "do not config to device. processor:{}", changeTimestamp, oldTimestamp, timestamp, this);
            return;
        }

        T afterData = dataMapTransform.transformConfigModel(dataMap);
        requireNonNull(afterData, "afterData is null");

        oldData.put("node_id", nodeId);
        T beforeData = dataMapTransform.transformConfigModel(oldData);
        requireNonNull(afterData, "beforeData is null");

        enqueueJob(fetchNodeId(afterData), () -> toListenableFuture(processUpdate(beforeData, afterData)));
    }

    private ConfigAdapter<T> resolveDeviceAdapter(String nodeId, @Nonnull T data) {
        String sysObjectId = Optional.ofNullable(snmpDeviceVersionService)
            .map(service -> service.resolveDeviceTypeOid(nodeId))
            .map(DeviceTypeOid::getTypeOid)
            .orElse(null);
        if (StringUtils.isEmpty(sysObjectId)) {
            return null;
        }

        return Optional.ofNullable(deviceAdapterFactoryService)
            .map(service -> service.getConfigAdapter(sysObjectId, this.tableName(), (Class<T>) data.getClass()))
            .orElse(null);
    }

    private String fetchTimeStamp(Map<String, String> dataMap) {
        return Optional.ofNullable(dataMap.get(TIMESTAMP_KEY)).orElse(dataMap.get(TIME_STAMP_KEY));
    }

    private boolean containTimeStamp(Map<String, String> dataMap) {
        return dataMap.containsKey(TIMESTAMP_KEY) || dataMap.containsKey(TIME_STAMP_KEY);
    }

    @Override
    protected void delete(Map<String, String> dataMap) {
        String nodeId = dataMap.get("node_id");
        if (StringUtils.isEmpty(nodeId)) {
            LOG.info("dataMap:{}, resovle nodeId is null.", dataMap);
            return;
        }

//        if (snmpDeviceVersionService.isUsp30(nodeId)) {
//            LOG.info("device:{} is usp 3.0 platform, do not delete config data.", nodeId);
//            return;
//        }

        T convert = dataMapTransform.transformConfigModel(dataMap);

        enqueueJob(fetchNodeId(convert), () -> toListenableFuture(processDelete(convert)));
    }

    private ListenableFuture<Void> toListenableFuture(CompletableFuture<Boolean> completableFuture) {
        return JdkFutureAdapters.listenInPoolThread(
            completableFuture.thenApply(input -> null),
            MoreExecutors.directExecutor());
    }

    private void enqueueJob(String nodeId, Callable<ListenableFuture<Void>> mainWorker) {
        String jobKey = buildJobKey(nodeId);
        DeviceSnmpInfo snmpInfo = deviceDaoService.getDeviceSnmpInfoBySn(nodeId);
        if (snmpInfo == null) {
            LOG.warn("node:{} snmpInfo is null, this device may deleted. jobKey:{}", nodeId, jobKey);
            return;
        }
        jobCoordinatorService.enqueueJob(jobKey, mainWorker);
    }

    protected String buildJobKey(String nodeId) {
        return new StringJoiner("-")
            .add(nodeId)
            .add(this.getClass().getSimpleName())
            .toString();
    }

    protected abstract String fetchNodeId(@Nonnull T data);

    public CompletableFuture<Boolean> processInsert(@Nonnull T data) {
        String nodeId = fetchNodeId(data);
        if (StringUtils.isEmpty(nodeId)) {
            LOG.info("table:{} config data:{}, nodeId is null or empty.", tableName(), data);
            return CompletableFuture.completedFuture(Boolean.FALSE);
        }

        ConfigAdapter<T> configAdapter = resolveDeviceAdapter(nodeId, data);
        if (configAdapter != null) {
            return configAdapter.insert(data);
        }

        O mibData = modelTransform.transformMibModel(data);

        return snmpConfigService.insertAsync(nodeId, mibData)
            .whenComplete((result, throwable) -> onInsert(nodeId, data, mibData, result));
    }

    protected void onInsert(String nodeId, T data, O mibData, Boolean result) {
        LOG.trace("node:{} data:{} insertAsync complete, result:{}.", nodeId, data, result);
        if (result) {
            fetchFromDevice(nodeId, mibData)
                .thenAccept(inserted -> {
                    if (inserted != null) {
                        S stateData = modelTransform.transformStateModel(nodeId, inserted);
                        onConfigSuccess(OperateType.INSERT, stateData);
                        LOG.info("node:{} insert data:{} to datastore successful.", nodeId, stateData);
                    } else {
                        LOG.warn("node:{} fetch data from device:{} result is null.", nodeId, mibData.getClass());
                    }
                });
        }
    }

    public CompletableFuture<Boolean> processUpdate(@Nonnull T beforeData, @Nonnull T afterData) {
        String nodeId = fetchNodeId(afterData);
        if (StringUtils.isEmpty(nodeId)) {
            LOG.info("update table:{} before data:{}, after data:{} nodeId is null or empty.",
                tableName(), beforeData, afterData);
            return CompletableFuture.completedFuture(Boolean.FALSE);
        }

        ConfigAdapter<T> configAdapter = resolveDeviceAdapter(nodeId, afterData);
        if (configAdapter != null) {
            return configAdapter.update(beforeData, afterData);
        }

        O before = modelTransform.transformMibModel(beforeData);
        O after = modelTransform.transformMibModel(afterData);

        return snmpConfigService.updateAsync(nodeId, before, after)
            .whenComplete((result, throwable) -> onUpdate(nodeId, before, after, result));
    }

    protected void onUpdate(String nodeId, O before, O after, Boolean result) {
        LOG.trace("node:{} befor data:{}, afterData:{} updateAsync complete, result:{}.",
            nodeId, before, after, result);
        if (result) {
            fetchFromDevice(nodeId, after)
                .thenAccept(updated -> {
                    if (updated != null) {
                        S stateData = modelTransform.transformStateModel(nodeId, updated);
                        onConfigSuccess(OperateType.UPDATE, stateData);
                        LOG.info("node:{} update data:{} to datastore successful.", nodeId, stateData);
                    } else {
                        S stateData = modelTransform.transformStateModel(nodeId, after);
                        onConfigSuccess(OperateType.DELETE, stateData);
                        LOG.warn("node:{} fetch data from device:{} result is null, delete state data from datastore.",
                            nodeId, after.getClass());
                    }
                });
        }
    }

    public CompletableFuture<Boolean> processDelete(@Nonnull T data) {
        String nodeId = fetchNodeId(data);
        if (StringUtils.isEmpty(nodeId)) {
            LOG.info("delete table:{} data:{}, nodeId is null or empty.", tableName(), data);
            return CompletableFuture.completedFuture(Boolean.FALSE);
        }

        ConfigAdapter<T> configAdapter = resolveDeviceAdapter(nodeId, data);
        if (configAdapter != null) {
            return configAdapter.delete(data);
        }

        O mibData = modelTransform.transformMibModel(data);

        return fetchFromDevice(nodeId, mibData)
            .thenApply(device -> {
                if (SnmpMessageUtils.isDeleted(device)) {
                    return Boolean.TRUE;
                }

                return snmpConfigService.deleteAsync(nodeId, mibData, modelTransform.fieldNames()).join();
            }).whenComplete((result, throwable) -> onDelete(nodeId, data, mibData, result));
    }

    protected void onDelete(String nodeId, T data, O mibData, Boolean result) {
        LOG.trace("node:{} data:{} deleteAsync complete, result: {}.", nodeId, data, result);
        if (result) {
            // 注意： 这个 mibData中只有KEY的信息，所以transformStateModel不要发生NPE，以免影响更新数据库逻辑。
            S stateData = modelTransform.transformStateModel(nodeId, mibData);
            fetchFromDevice(nodeId, mibData)
                .thenAccept(deleted -> {
                    if (SnmpMessageUtils.isDeleted(deleted)) {
                        onConfigSuccess(OperateType.DELETE, stateData);
                        LOG.info("node:{} delete data:{} from datastore successful.", nodeId, stateData);
                    }
                });
        }
    }

    private boolean isTable(O mibData) {
        return mibData.getClass().isAnnotationPresent(OidTableProperty.class);
    }

    protected CompletableFuture<O> fetchFromDevice(String nodeId, O mibData) {
        String[] fieldNames = modelTransform.fieldNames().toArray(new String[0]);
        if (isTable(mibData)) {
            return snmpConfigService.getTableEntryAsync(nodeId, mibData, fieldNames);
        } else {
            return (CompletableFuture<O>) snmpConfigService.getAsync(nodeId, mibData.getClass(), fieldNames);
        }

    }

    protected abstract void onConfigSuccess(@Nonnull OperateType operateType, @Nonnull S data);

}
