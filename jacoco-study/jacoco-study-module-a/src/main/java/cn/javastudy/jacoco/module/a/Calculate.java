package cn.javastudy.jacoco.module.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calculate {

    public int add(int a, int b) {

        return a + b;
    }

    public double divide(double a, double b) {
        return a / b;
    }

    public String getName(String name) {

        return name;
    }

    public List<String> getList(String item) {
        List<String> list = new ArrayList<>();
        list.add(item);
        return list;
    }

    public Map<String, String> getMap(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
