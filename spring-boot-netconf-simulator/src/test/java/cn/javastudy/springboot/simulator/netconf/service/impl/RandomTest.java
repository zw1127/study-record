package cn.javastudy.springboot.simulator.netconf.service.impl;

import static cn.javastudy.springboot.simulator.netconf.utils.Utils.getRandomDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

@SuppressWarnings("RegexpSinglelineJava")
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

    @Test
    public void testIntStream() {
        String prefix = "test-";
        int size = 5;
        int start = 8;

        // 使用IntStream.rangeClosed和mapToObj生成指定前缀和大小的List
        List<String> myList = IntStream.range(start, start + size)
            .mapToObj(i -> prefix + i)
            .collect(Collectors.toList());

        // 输出生成的List
        System.out.println(myList);
    }

}
