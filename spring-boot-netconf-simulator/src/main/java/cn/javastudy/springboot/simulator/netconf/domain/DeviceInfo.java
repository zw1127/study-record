package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

    @JsonProperty("device-id")
    private String deviceId;

    @JsonProperty("device-port")
    private Integer port;

    @JsonProperty("connected-sessions")
    private List<String> connectedList;
}
