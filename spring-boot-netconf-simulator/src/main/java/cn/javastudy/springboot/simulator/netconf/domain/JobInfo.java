package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.util.concurrent.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobInfo {

    @JsonProperty("job-key")
    private String jobKey;

    @JsonProperty("state")
    private Service.State state;
}
