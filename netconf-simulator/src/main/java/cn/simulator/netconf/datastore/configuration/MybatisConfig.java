package cn.simulator.netconf.datastore.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"cn.simulator.netconf.datastore.mapper"})
public class MybatisConfig {
}
