package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUniqueInfo {

    // 设备唯一标识，可以是sn/mac等
    @JsonProperty("device-id")
    private String uniqueKey;

    @JsonProperty("manage-ip")
    private String manageIp;
}
