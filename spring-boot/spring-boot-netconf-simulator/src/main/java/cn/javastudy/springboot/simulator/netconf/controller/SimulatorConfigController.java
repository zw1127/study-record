package cn.javastudy.springboot.simulator.netconf.controller;

import cn.javastudy.springboot.simulator.netconf.datastore.entity.SimulatorConfig;
import cn.javastudy.springboot.simulator.netconf.datastore.entity.SimulatorConfigKey;
import cn.javastudy.springboot.simulator.netconf.datastore.mapper.SimulatorConfigMapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulator/config")
@Tag(name = "模拟器配置数据")
public class SimulatorConfigController {

    @Resource
    private SimulatorConfigMapper simulatorConfigMapper;

    @ApiOperationSupport
    @GetMapping("/list")
    @Operation(summary = "查询所有配置列表")
    public List<SimulatorConfig> list() {
        return simulatorConfigMapper.selectAll();
    }

    @ApiOperationSupport(order = 2)
    @GetMapping("/list/{device-id}")
    @Operation(summary = "查询指定设备的所有配置列表")
    public List<SimulatorConfig> listByDevice(@PathVariable("device-id") String deviceId) {
        return simulatorConfigMapper.selectByDeviceId(deviceId);
    }

    @ApiOperationSupport(order = 3)
    @GetMapping("/list/{device-id}/{node-name}")
    @Operation(summary = "查询指定设备指定node配置")
    public SimulatorConfig listByDeviceAndNodeName(@PathVariable("device-id") String deviceId,
                                                   @PathVariable("node-name") String nodeName) {
        return simulatorConfigMapper.selectByKey(new SimulatorConfigKey(deviceId, nodeName));
    }

    @ApiOperationSupport(order = 4)
    @PutMapping("/update")
    @Operation(summary = "修改设备配置")
    public Boolean update(@RequestBody SimulatorConfig simulatorConfig) {
        Objects.requireNonNull(simulatorConfig);
        Objects.requireNonNull(simulatorConfig.getId());

        return simulatorConfigMapper.updateValueByKey(simulatorConfig) > 0;
    }

    @ApiOperationSupport(order = 1)
    @DeleteMapping("/delete")
    @Operation(summary = "删除所有配置")
    public String deleteAll() {
        int deleted = simulatorConfigMapper.deleteAll();

        return "[" + deleted + "] records deleted.";
    }

    @ApiOperationSupport(order = 5)
    @DeleteMapping("/delete/{device-id}")
    @Operation(summary = "删除指定设备所有配置")
    public String deleteByDeviceId(@PathVariable("device-id") String deviceId) {
        int deleted = simulatorConfigMapper.deleteByDeviceId(deviceId);

        return "[" + deleted + "] records deleted.";
    }
}
