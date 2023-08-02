package cn.javastudy.springboot.simulator.netconf.utils;

import cn.javastudy.springboot.simulator.netconf.domain.DeviceBatchBaseInfo;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testGenerateDeviceIdList() {
        DeviceBatchBaseInfo baseInfo = new DeviceBatchBaseInfo("test-", 0, 10, 0);
        List<String> strings = Utils.generateDeviceIdList(baseInfo);
        Assert.assertNotNull(strings);
        Assert.assertEquals("test-9", strings.get(9));

        baseInfo.setDeviceIdLength(8);
        List<String> strings1 = Utils.generateDeviceIdList(baseInfo);
        Assert.assertNotNull(strings1);
        Assert.assertEquals("test-009", strings1.get(9));

        DeviceBatchBaseInfo baseInfo1 = new DeviceBatchBaseInfo("S", 1, 1000, 21);
        List<String> strings2 = Utils.generateDeviceIdList(baseInfo1);
        Assert.assertNotNull(strings2);
    }
}
