package cn.javastudy.springboot.simulator.netconf.service;

import cn.javastudy.springboot.simulator.netconf.device.NetconfSimulateDevice;
import cn.javastudy.springboot.simulator.netconf.domain.CallhomeBatchInfo;
import cn.javastudy.springboot.simulator.netconf.domain.CallhomeInfo;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceBatchBaseInfo;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceBatchInfo;
import cn.javastudy.springboot.simulator.netconf.domain.Result;
import cn.javastudy.springboot.simulator.netconf.domain.SimulateDeviceInfo;
import cn.javastudy.springboot.simulator.netconf.utils.Utils;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.w3c.dom.Document;

public interface SimluateDeviceService {

    Integer DEFAULT_CALLHOME_PORT = 4334;

    String DEFAULT_ID_PREFIX = "sim-";
    Integer DEFAULT_ID_START = 1;

    Integer DEFAULT_PORT_START = 17831;
    Integer DEFAULT_BATCH_SIZE = 1;

    ListenableFuture<Result<SimulateDeviceInfo>> startSimulateDevice(SimulateDeviceInfo deviceInfo);

    default ListenableFuture<List<Result<SimulateDeviceInfo>>> startSimulateBatch(DeviceBatchInfo deviceBatchInfo) {
        String deviceIdPrefix = Optional.ofNullable(deviceBatchInfo.getDeviceIdPrefix()).orElse(DEFAULT_ID_PREFIX);
        int deviceIdStart = Optional.ofNullable(deviceBatchInfo.getDeviceIdStart()).orElse(DEFAULT_ID_START);
        int portStart = Optional.ofNullable(deviceBatchInfo.getPortStart()).orElse(DEFAULT_PORT_START);
        int batchSize = Optional.ofNullable(deviceBatchInfo.getBatchSize()).orElse(DEFAULT_BATCH_SIZE);

        List<ListenableFuture<Result<SimulateDeviceInfo>>> result = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            int port = portStart + i;
            int deviceIdNum = deviceIdStart + i;

            String deviceId = deviceIdPrefix + deviceIdNum;
            SimulateDeviceInfo deviceInfo = new SimulateDeviceInfo(port);

            deviceInfo.setUniqueKey(deviceId);
            result.add(startSimulateDevice(deviceInfo));
        }

        return Futures.successfulAsList(result);
    }

    void stopSimulateDevice(String uniqueKey);

    default void stopSimulateDeviceBatch(DeviceBatchBaseInfo baseInfo) {
        Optional.ofNullable(Utils.generateDeviceIdList(baseInfo))
            .orElse(Collections.emptyList())
            .forEach(this::stopSimulateDevice);
    }

    ListenableFuture<Result<CallhomeInfo>> callhomeConnect(String uniqueKey, String callhomeIp, Integer callhomePort);

    default ListenableFuture<Result<CallhomeInfo>> callhomeConnect(String uniqueKey, String callhomeIp) {
        return callhomeConnect(uniqueKey, callhomeIp, DEFAULT_CALLHOME_PORT);
    }

    default ListenableFuture<List<Result<CallhomeInfo>>> callhomeConnectBatch(CallhomeBatchInfo batchInfo) {
        return Futures.successfulAsList(Optional.ofNullable(Utils.generateDeviceIdList(batchInfo))
            .orElse(Collections.emptyList())
            .stream()
            .map(deviceId -> callhomeConnect(deviceId, batchInfo.getCallhomeIp(), batchInfo.getCallhomePort()))
            .collect(Collectors.toList()));
    }

    void callhomeDisconnect(String uniqueKey, String callhomeIp, Integer callhomePort);

    default void callhomeDisconnect(String uniqueKey, String callhomeIp) {
        callhomeDisconnect(uniqueKey, callhomeIp, DEFAULT_CALLHOME_PORT);
    }

    default void callhomeDisconnectBatch(CallhomeBatchInfo batchInfo) {
        Optional.ofNullable(Utils.generateDeviceIdList(batchInfo))
            .orElse(Collections.emptyList())
            .forEach(deviceId -> callhomeDisconnect(deviceId, batchInfo.getCallhomeIp(), batchInfo.getCallhomePort()));
    }

    Map<String, NetconfSimulateDevice> startedDevices();

    ListenableFuture<Boolean> sendNotification(Document notificationContent,
                                               String deviceId,
                                               String targetIp,
                                               Integer targetPort);
}
