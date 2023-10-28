/*
 * Copyright (c) 2023 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
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
