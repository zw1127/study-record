package cn.javastudy.springboot.simulator.netconf.service;

import java.util.Set;
import org.opendaylight.netconf.api.capability.Capability;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactoryListener;
import org.springframework.stereotype.Component;

/**
 * NetconfOperationServiceFactory管理类.
 * 用于创建NetconfOperationServiceFactory以及初始化配置存储实现.
 */
@Component
public class OperationServiceFactoryManager {

    public NetconfOperationServiceFactory createDatabrokerFactory(
        final Set<Capability> capabilities,
        final SchemaContextService schemaContextService,
        final NetconfOperationServiceFactoryListener netconfOperationServiceFactoryListener) {
        return null;
    }
}
