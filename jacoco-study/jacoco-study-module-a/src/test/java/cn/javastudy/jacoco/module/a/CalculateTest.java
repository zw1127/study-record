/*
 * Copyright (c) 2023 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
package cn.javastudy.jacoco.module.a;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;

import java.util.List;
import java.util.Map;
import org.junit.Test;

public class CalculateTest {

    @Test
    public void testAdd() {

        //一般匹配符
        int s = new Calculate().add(1, 1);
        //allOf：所有条件必须都成立，测试才通过
        assertThat(s, allOf(greaterThan(1), lessThan(3)));
        //anyOf：只要有一个条件成立，测试就通过
        assertThat(s, anyOf(greaterThan(1), lessThan(1)));
        //anything：无论什么条件，测试都通过
        assertThat(s, anything());
        //is：变量的值等于指定值时，测试通过
        assertThat(s, is(2));
        //not：和is相反，变量的值不等于指定值时，测试通过
        assertThat(s, not(1));

        //数值匹配符
        double d = new Calculate().divide(10, 3);
        //closeTo：浮点型变量的值在3.0±0.5范围内，测试通过
        assertThat(d, closeTo(3.0, 0.5));
        //greaterThan：变量的值大于指定值时，测试通过
        assertThat(d, greaterThan(3.0));
        //lessThan：变量的值小于指定值时，测试通过
        assertThat(d, lessThan(3.5));
        //greaterThanOrEuqalTo：变量的值大于等于指定值时，测试通过
        assertThat(d, greaterThanOrEqualTo(3.3));
        //lessThanOrEqualTo：变量的值小于等于指定值时，测试通过
        assertThat(d, lessThanOrEqualTo(3.4));

        //字符串匹配符
        String n = new Calculate().getName("Magci");
        //containsString：字符串变量中包含指定字符串时，测试通过
        assertThat(n, containsString("ci"));
        //startsWith：字符串变量以指定字符串开头时，测试通过
        assertThat(n, startsWith("Ma"));
        //endsWith：字符串变量以指定字符串结尾时，测试通过
        assertThat(n, endsWith("i"));
        //euqalTo：字符串变量等于指定字符串时，测试通过
        assertThat(n, equalTo("Magci"));
        //equalToIgnoringCase：字符串变量在忽略大小写的情况下等于指定字符串时，测试通过
        assertThat(n, equalToIgnoringCase("magci"));
        //equalToIgnoringWhiteSpace：字符串变量在忽略头尾任意空格的情况下等于指定字符串时，测试通过
        assertThat(n, equalToCompressingWhiteSpace(" Magci   "));

        //集合匹配符
        List<String> l = new Calculate().getList("Magci");
        //hasItem：Iterable变量中含有指定元素时，测试通过
        assertThat(l, hasItem("Magci"));

        Map<String, String> m = new Calculate().getMap("mgc", "Magci");
        //hasEntry：Map变量中含有指定键值对时，测试通过
        assertThat(m, hasEntry("mgc", "Magci"));
        //hasKey：Map变量中含有指定键时，测试通过
        assertThat(m, hasKey("mgc"));
        //hasValue：Map变量中含有指定值时，测试通过
        assertThat(m, hasValue("Magci"));
    }
}
