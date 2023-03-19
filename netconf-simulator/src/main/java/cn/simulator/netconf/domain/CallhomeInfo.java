package cn.simulator.netconf.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallhomeInfo {

    private String callhomeIp;

    private Integer callhomePort;
}
