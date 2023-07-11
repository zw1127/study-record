package cn.javastudy.springboot.simulator.netconf.datastore.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulatorConfig extends SimulatorConfigKey {

    private static final long serialVersionUID = -6243823607496574762L;

    @Schema(description = "数据库中的Id")
    private Integer id;

    // 节点值
    @Schema(description = "yang模型中对应节点的xml数据")
    private String nodeValue;
}
