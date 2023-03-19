package cn.javastudy.springboot.simulator.netconf.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulateDeviceInfo extends DeviceUniqueInfo {

    private Integer portNumber;
}
