package cn.javastudy.springboot.simulator.netconf.datastore.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulatorConfigKey implements Serializable {

    private static final long serialVersionUID = 6090655604764428786L;

    // 模拟模拟设备ID，主要用于存储
    private String deviceId;

    // yang模型中的节点名称
    private String nodeName;
}
