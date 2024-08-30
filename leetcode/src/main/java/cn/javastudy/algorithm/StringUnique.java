package cn.javastudy.algorithm;

import java.util.Scanner;

public class StringUnique {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String str = scanner.nextLine();
            System.out.println(isUnique(str));
        }
    }

    public static boolean isUnique(String astr) {
        for (int i = 0; i < astr.length(); i++) {
            char temp = astr.charAt(i);
            for (int j = i + 1; j < astr.length(); j++) {
                char current = astr.charAt(j);
                if (current == temp) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isUnique2(String astr) {
        if (astr.length() > 26) {
            return false;
        }

        for (int i = 0; i < astr.length(); i++) {
            char temp = astr.charAt(i);
            for (int j = i + 1; j < astr.length(); j++) {
                char current = astr.charAt(j);
                if (current == temp) {
                    return false;
                }
            }
        }

        return true;
    }

}
