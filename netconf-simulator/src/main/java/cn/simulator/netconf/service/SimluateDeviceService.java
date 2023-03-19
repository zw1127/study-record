package cn.simulator.netconf.service;

import cn.simulator.netconf.domain.SimulateDeviceInfo;
import com.google.common.util.concurrent.ListenableFuture;

public interface SimluateDeviceService {

    Integer DEFAULT_CALLHOME_PORT = 4334;

    void startSimulateDevice(SimulateDeviceInfo deviceInfo);

    void stopSimulateDevice(String uniqueKey);

    ListenableFuture<Boolean> callhomeConnect(String uniqueKey, String callhomeIp, Integer callhomePort);

    default ListenableFuture<Boolean> callhomeConnect(String uniqueKey, String callhomeIp) {
        return callhomeConnect(uniqueKey, callhomeIp, DEFAULT_CALLHOME_PORT);
    }

    void callhomeDisconnect(String uniqueKey, String callhomeIp, Integer callhomePort);

    default void callhomeDisconnect(String uniqueKey, String callhomeIp) {
        callhomeDisconnect(uniqueKey, callhomeIp, DEFAULT_CALLHOME_PORT);
    }
}
