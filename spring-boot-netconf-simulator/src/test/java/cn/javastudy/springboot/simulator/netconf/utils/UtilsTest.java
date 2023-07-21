package cn.javastudy.springboot.simulator.netconf.utils;

import cn.javastudy.springboot.simulator.netconf.domain.DeviceBatchBaseInfo;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testGenerateDeviceIdList() {
        DeviceBatchBaseInfo baseInfo = new DeviceBatchBaseInfo("test-", 100, 10);
        List<String> strings = Utils.generateDeviceIdList(baseInfo);
        Assert.assertNotNull(strings);
        Assert.assertEquals("test-109", strings.get(9));
    }
}
