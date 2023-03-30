package cn.javastudy.springboot.simulator.netconf.service;

import cn.javastudy.springboot.simulator.netconf.device.NetconfSimulateDevice;
import cn.javastudy.springboot.simulator.netconf.domain.SimulateDeviceInfo;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Map;
import org.w3c.dom.Document;

public interface SimluateDeviceService {

    Integer DEFAULT_CALLHOME_PORT = 4334;

    ListenableFuture<Boolean> startSimulateDevice(SimulateDeviceInfo deviceInfo);

    void stopSimulateDevice(String uniqueKey);

    ListenableFuture<Boolean> callhomeConnect(String uniqueKey, String callhomeIp, Integer callhomePort);

    default ListenableFuture<Boolean> callhomeConnect(String uniqueKey, String callhomeIp) {
        return callhomeConnect(uniqueKey, callhomeIp, DEFAULT_CALLHOME_PORT);
    }

    void callhomeDisconnect(String uniqueKey, String callhomeIp, Integer callhomePort);

    default void callhomeDisconnect(String uniqueKey, String callhomeIp) {
        callhomeDisconnect(uniqueKey, callhomeIp, DEFAULT_CALLHOME_PORT);
    }

    Map<String, NetconfSimulateDevice> startedDevices();

    void sendNotification(Document notificationContent, String deviceId, String targetIp, Integer targetPort);
}
