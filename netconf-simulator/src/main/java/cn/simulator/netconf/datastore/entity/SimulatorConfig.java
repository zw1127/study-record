package cn.simulator.netconf.datastore.entity;

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

    private Integer id;

    // 节点值
    private String nodeValue;
}
