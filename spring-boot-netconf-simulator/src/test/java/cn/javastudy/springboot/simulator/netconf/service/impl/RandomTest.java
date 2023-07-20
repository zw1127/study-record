package cn.javastudy.springboot.simulator.netconf.service.impl;

import static cn.javastudy.springboot.simulator.netconf.utils.Utils.getRandomDecimal;

import java.math.BigDecimal;
import org.junit.Test;

public class RandomTest {

    @Test
    public void randomTest() {
        BigDecimal min = new BigDecimal(10);
        BigDecimal max = new BigDecimal(100);
        System.out.println(getRandomDecimal(min, max, 0));
        System.out.println(getRandomDecimal(min, max, 1));
        System.out.println(getRandomDecimal(min, max, 2));
        System.out.println(getRandomDecimal(min, max, 3));
    }

}
