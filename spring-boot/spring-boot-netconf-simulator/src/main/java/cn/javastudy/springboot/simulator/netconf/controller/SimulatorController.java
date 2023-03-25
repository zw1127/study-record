package cn.javastudy.springboot.simulator.netconf.controller;

import cn.javastudy.springboot.simulator.netconf.device.NetconfSimulateDevice;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceInfo;
import cn.javastudy.springboot.simulator.netconf.domain.SimulateDeviceInfo;
import cn.javastudy.springboot.simulator.netconf.service.SimluateDeviceService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.util.concurrent.ListenableFuture;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String startDevice(@RequestBody SimulateDeviceInfo deviceInfo) {
        try {
            ListenableFuture<Boolean> future = simluateDeviceService.startSimulateDevice(deviceInfo);
            Boolean result = future.get(TIME_OUT, TimeUnit.SECONDS);
            LOG.info("start netconf simulator:{} result:{}", deviceInfo, result);
            return Boolean.TRUE.equals(result) ? "successful" : "failed";
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return "failed";
        }
    }

    @ApiOperationSupport(order = 1)
    @PostMapping("/stop")
    @Operation(summary = "停止模拟器")
    public String stopDevice(String uniqueKey) {
        simluateDeviceService.stopSimulateDevice(uniqueKey);
        return "stop simulator: " + uniqueKey + " successful.";
    }

    @ApiOperationSupport(order = 2)
    @PostMapping("/callhome-connect")
    @Operation(summary = "callhome连接控制器")
    public String callhomeConnect(String uniqueKey, String callhomeIp) {
        try {
            ListenableFuture<Boolean> future = simluateDeviceService.callhomeConnect(uniqueKey, callhomeIp);
            Boolean result = future.get(TIME_OUT, TimeUnit.SECONDS);
            LOG.info("start netconf simulator result:{}", result);
            return Boolean.TRUE.equals(result) ? "successful" : "failed";
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return "failed";
        }
    }

    @ApiOperationSupport(order = 3)
    @PostMapping("/callhome-disconnect")
    @Operation(summary = "断开控制器的callhome连接")
    public String callhomeDisconnect(String uniqueKey, String callhomeIp) {
        simluateDeviceService.callhomeDisconnect(uniqueKey, callhomeIp);
        return "disconnect device: " + uniqueKey + " callhome connectd controller:" + callhomeIp + " successful.";
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

            List<String> connectedList = Optional.ofNullable(device.getSessions())
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
}
