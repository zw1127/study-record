package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallhomeInfo {

    @JsonProperty("callhome-ip")
    private String callhomeIp;

    @JsonProperty("callhome-port")
    private Integer callhomePort;
}
