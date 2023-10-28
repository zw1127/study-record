package cn.javastudy.springboot.simulator.netconf.utils;

import cn.javastudy.springboot.simulator.netconf.domain.DeviceBatchBaseInfo;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        //打印： 2023-10-23T15:37:01+08:00
        System.out.println(dtf.format(LocalDateTime.now().atZone(Clock.systemDefaultZone().getZone())));
    }
}
