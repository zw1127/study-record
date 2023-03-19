package cn.simulator.netconf.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUniqueInfo {

    // 设备唯一标识，可以是sn/mac等
    private String uniqueKey;

    private String manageIp;
}
