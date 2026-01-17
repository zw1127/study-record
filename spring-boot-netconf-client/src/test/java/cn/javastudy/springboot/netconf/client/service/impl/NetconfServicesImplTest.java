package cn.javastudy.springboot.netconf.client.service.impl;

import cn.javastudy.springboot.netconf.client.service.NetconfServices;
import cn.javastudy.springboot.netconf.client.utils.YangParserUtils;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opendaylight.yangtools.yang.common.QNameModule;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.api.stmt.ModuleEffectiveStatement;

@Ignore
public class NetconfServicesImplTest {

    private NetconfServices netconfServices;

    @Before
    public void setUp() throws Exception {
        EffectiveModelContext effectiveModelContext = YangParserUtils.parseYangResourceDirectory("/yang");

        Map<QNameModule, ModuleEffectiveStatement> moduleStatements = effectiveModelContext.getModuleStatements();
    }

    @Test
    public void testResolve() {
        netconfServices.getNotificationService();
    }
}
