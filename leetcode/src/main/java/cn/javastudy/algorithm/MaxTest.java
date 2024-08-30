package cn.javastudy.algorithm;

import java.util.ArrayList;
import java.util.List;

public class MaxTest {

    public static void main(String[] args) {
        int a = Integer.MAX_VALUE;
        int b = Integer.MIN_VALUE;

//        System.out.println(getMax(a, b));

        String s = "abcdddeeeeaabbbcd";
//        String s = "aaa";
        System.out.println(largeGroupPositions(s));
    }

    public static int getMax(int a, int b) {
        int c = a - b;

        // 获取每个值的符号位，值为：0 或 1；1 为非负，0 为负数
        int sa = (a >>> 31) ^ 1;
        int sb = (b >>> 31) ^ 1;
        int sc = (c >>> 31) ^ 1;

        // a, b的符号位是否一样，一样的话，diffAb = 0; 不一样 diffAb = 1;
        int diffAb = sa ^ sb;

        // 1、 当a,b符号位一样时，returnA 为c的符号位，sc 为1 为非负，0 为负数
        // 2、 当a,b符号不一样时，returnA 为a的符号位
        int returnA = diffAb * sa + (diffAb ^ 1) * sc;

        return a * returnA + b * (returnA ^ 1);
    }

    public static List<List<Integer>> largeGroupPositions(String s) {
        int length = s.length();
        List<List<Integer>> result = new ArrayList<>();

        int temp = 1;
        int i = 0;
        while (i < length - 1) {
            char c = s.charAt(i);
            char next = s.charAt(i);
            for (int j = i; j <= length - 1; j++) {
                next = s.charAt(j);
                if (c == next) {
                    if (i != j) {
                        temp++;
                    }
                } else {
                    break;
                }
            }

            // 当前节点和下一节点不相等，或者当前索引已经到最后一个节点，同时temp >= 3
            if (temp >= 3 && (c != next || i + temp >= length - 1)) {
                List<Integer> list = new ArrayList<>();
                list.add(i);
                list.add(i + temp - 1);
                result.add(list);
                i += temp - 1;
            } else {
                i += temp;
            }
            temp = 1;
        }

        return result;
    }
}
