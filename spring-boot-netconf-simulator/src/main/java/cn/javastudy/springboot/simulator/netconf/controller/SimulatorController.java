package cn.javastudy.springboot.simulator.netconf.controller;

import cn.javastudy.springboot.simulator.netconf.device.DeviceSessionManager;
import cn.javastudy.springboot.simulator.netconf.device.NetconfSimulateDevice;
import cn.javastudy.springboot.simulator.netconf.domain.CallhomeBatchInfo;
import cn.javastudy.springboot.simulator.netconf.domain.CallhomeInfo;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceBatchBaseInfo;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceBatchInfo;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceInfo;
import cn.javastudy.springboot.simulator.netconf.domain.Result;
import cn.javastudy.springboot.simulator.netconf.domain.ResultBuilder;
import cn.javastudy.springboot.simulator.netconf.domain.SimulateDeviceInfo;
import cn.javastudy.springboot.simulator.netconf.service.SimluateDeviceService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.util.concurrent.ListenableFuture;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@RestController
@RequestMapping("/simulator/device")
@Tag(name = "模拟器管理")
public class SimulatorController {

    private static final Logger LOG = LoggerFactory.getLogger(SimulatorController.class);

    private static final int TIME_OUT = 20;

    @Resource
    private SimluateDeviceService simluateDeviceService;

    @ApiOperationSupport
    @PostMapping("/start")
    @Operation(summary = "启动模拟器")
    public Result<SimulateDeviceInfo> startDevice(@RequestBody SimulateDeviceInfo deviceInfo) {
        try {
            ListenableFuture<Result<SimulateDeviceInfo>> future =
                simluateDeviceService.startSimulateDevice(deviceInfo);
            Result<SimulateDeviceInfo> result = future.get(TIME_OUT, TimeUnit.SECONDS);
            LOG.info("start netconf simulator:{} result:{}", deviceInfo, result);

            return result;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return ResultBuilder.failed(deviceInfo, e).build();
        }
    }

    @ApiOperationSupport
    @PostMapping("/start-batch")
    @Operation(summary = "批量启动模拟器")
    public List<Result<SimulateDeviceInfo>> startDevices(@RequestBody DeviceBatchInfo deviceBatchInfo) {
        try {
            int timeout = Optional.ofNullable(deviceBatchInfo.getBatchSize()).orElse(1) * TIME_OUT;
            ListenableFuture<List<Result<SimulateDeviceInfo>>> future =
                simluateDeviceService.startSimulateBatch(deviceBatchInfo);
            List<Result<SimulateDeviceInfo>> result = future.get(timeout, TimeUnit.SECONDS);
            LOG.info("start netconf simulator:{} result:{}", deviceBatchInfo, result);
            return result.stream().filter(Objects::nonNull).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("start batch:" + deviceBatchInfo + " error.", e);
        }
    }

    @ApiOperationSupport(order = 1)
    @PostMapping("/stop")
    @Operation(summary = "停止模拟器")
    public String stopDevice(String uniqueKey) {
        simluateDeviceService.stopSimulateDevice(uniqueKey);
        return "stop simulator: " + uniqueKey + " successful.";
    }

    @PostMapping("/stop-batch")
    @Operation(summary = "批量停止模拟器")
    public String stopDeviceBatch(@RequestBody DeviceBatchBaseInfo batchBaseInfo) {
        simluateDeviceService.stopSimulateDeviceBatch(batchBaseInfo);
        return "stop simulator batch: " + batchBaseInfo + " successful.";
    }

    @PostMapping("/stop-all")
    @Operation(summary = "停止所有模拟器")
    public String stopAllDevices() {
        simluateDeviceService.stopAllSimulateDevices();
        return "stop all simulators successful.";
    }

    @ApiOperationSupport(order = 2)
    @PostMapping("/callhome-connect")
    @Operation(summary = "callhome连接控制器")
    public Result<CallhomeInfo> callhomeConnect(String uniqueKey, String callhomeIp) {
        CallhomeInfo callhomeInfo = new CallhomeInfo();
        callhomeInfo.setCallhomeIp(callhomeIp);
        callhomeInfo.setUniqueKey(uniqueKey);
        try {
            ListenableFuture<Result<CallhomeInfo>> future =
                simluateDeviceService.callhomeConnect(uniqueKey, callhomeIp);
            Result<CallhomeInfo> result = future.get(TIME_OUT, TimeUnit.SECONDS);
            LOG.info("start netconf simulator result:{}", result);
            return result;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return ResultBuilder.failed(callhomeInfo, e).build();
        }
    }

    @ApiOperationSupport(order = 2)
    @PostMapping("/callhome-connect-batch")
    @Operation(summary = "callhome批量连接控制器")
    public List<Result<CallhomeInfo>> callhomeConnectBatch(@RequestBody CallhomeBatchInfo callhomeBatchInfo) {
        try {
            int timeout = Optional.ofNullable(callhomeBatchInfo.getBatchSize()).orElse(1) * TIME_OUT;
            ListenableFuture<List<Result<CallhomeInfo>>> future =
                simluateDeviceService.callhomeConnectBatch(callhomeBatchInfo);
            List<Result<CallhomeInfo>> result = future.get(timeout, TimeUnit.SECONDS);
            LOG.info("start netconf simulator result:{}", result);
            return result;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("callhome connect batch:" + callhomeBatchInfo + " error.", e);
        }
    }

    @ApiOperationSupport(order = 3)
    @PostMapping("/callhome-disconnect")
    @Operation(summary = "断开控制器的callhome连接")
    public String callhomeDisconnect(String uniqueKey, String callhomeIp) {
        simluateDeviceService.callhomeDisconnect(uniqueKey, callhomeIp);
        return "disconnect device: " + uniqueKey + " callhome connectd controller:" + callhomeIp + " successful.";
    }

    @PostMapping("/callhome-disconnect-batch")
    @Operation(summary = "批量断开控制器的callhome连接")
    public String callhomeDisconnectBatch(@RequestBody CallhomeBatchInfo callhomeBatchInfo) {
        simluateDeviceService.callhomeDisconnectBatch(callhomeBatchInfo);
        return "disconnect device batch: " + callhomeBatchInfo + " callhome connected successful.";
    }

    @ApiOperationSupport(order = 4)
    @GetMapping("/simluate-list/")
    @Operation(summary = "查询所有启动的模拟器列表")
    public List<DeviceInfo> listByDevice() {
        List<DeviceInfo> deviceInfoList = new ArrayList<>();

        Collection<NetconfSimulateDevice> devices = simluateDeviceService.startedDevices().values();
        for (NetconfSimulateDevice device : devices) {
            SimulateDeviceInfo simulateDeviceInfo = device.getDeviceInfo();
            DeviceInfo deviceInfo = new DeviceInfo();

            deviceInfo.setDeviceId(simulateDeviceInfo.getUniqueKey());
            deviceInfo.setPort(simulateDeviceInfo.getPortNumber());

            List<String> connectedList = Optional.ofNullable(device.getSessionManager())
                .map(DeviceSessionManager::getSessions)
                .orElse(Collections.emptyMap())
                .keySet()
                .stream()
                .map(InetSocketAddress::toString)
                .map(ip -> StringUtils.remove(ip, "/"))
                .collect(Collectors.toList());
            deviceInfo.setConnectedList(connectedList);
            deviceInfoList.add(deviceInfo);
        }

        return deviceInfoList;
    }

    @ApiOperationSupport(order = 2)
    @PostMapping(value = "/send-notification/{uniqueKey}/{targetIp}/{targetPort}",
        consumes = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "发送Notification")
    public String sendNotification(@PathVariable("uniqueKey") String uniqueKey,
                                   @PathVariable("targetIp") String targetIp,
                                   @PathVariable("targetPort") Integer targetPort,
                                   @RequestBody String notificationXml) {
        try {
            Document document = XmlUtil.readXmlToDocument(notificationXml);
            ListenableFuture<Boolean> future =
                simluateDeviceService.sendNotification(document, uniqueKey, targetIp, targetPort);
            return future.get() ? "successful" : "failed";
        } catch (SAXException | IOException | InterruptedException | ExecutionException e) {
            return "failed:" + e.getMessage();
        }
    }
}
