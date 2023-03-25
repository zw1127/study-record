package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControllerInfo {

    @JsonProperty("connected-type")
    private ConnectedType connectedType;

    @JsonProperty("controller-ip")
    private String controllerIp;

    @JsonProperty("controller-port")
    private Integer controllerPort;
}
