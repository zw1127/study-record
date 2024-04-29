package cn.javastudy.algorithm;

import java.util.Scanner;

public class TlvSolution {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String tag = scanner.nextLine();
            String source = scanner.nextLine();
            String solution = solution(tag, source);
            if (solution != null) {
                System.out.println(solution);
            }
        }
    }

    private static String solution(String tag, String source) {
        int index = 0;
        while (index < source.length()) {
            // 取出Tag
            String currentTag = source.substring(index, index + 2);

            // 取出length的字符串，需要反转
            String lengthStr = source.substring(index + 6, index + 8) + source.substring(index + 3, index + 5);

            // 通过16进制转换为int类型的值 
            int length = Integer.parseInt(lengthStr, 16);

            if (tag.equals(currentTag)) {
                // 这里需要去掉最后一个空格的Index，防止超过source的最大长度
                return source.substring(index + 9, index + 9 + length * 3 - 1);
            }

            index = index + 9 + length * 3;
        }

        return null;
    }
}
