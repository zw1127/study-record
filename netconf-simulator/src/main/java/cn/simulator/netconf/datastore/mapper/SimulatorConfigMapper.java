package cn.simulator.netconf.datastore.mapper;

import cn.simulator.netconf.datastore.entity.SimulatorConfig;
import cn.simulator.netconf.datastore.entity.SimulatorConfigKey;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulatorConfigMapper {

    SimulatorConfig selectByPrimaryKey(Integer id);

    SimulatorConfig selectByKey(SimulatorConfigKey key);

    List<SimulatorConfig> selectByDeviceId(String deviceId);

    // 由于性能问题，查询所有时，nodeValue对应的xml过大，不查询nodeValue的值
    List<SimulatorConfig> selectAll();

    int deleteByPrimaryKey(Integer id);

    int deleteByKey(SimulatorConfigKey key);

    int deleteByDeviceId(String deviceId);

    int deleteAll();

    int insert(SimulatorConfig config);

    int insertBatch(List<SimulatorConfig> configList);

    int updateByPrimaryKey(SimulatorConfig config);

    int updateValueByPrimaryKey(SimulatorConfig config);

    int updateValueByKey(SimulatorConfig config);

}
