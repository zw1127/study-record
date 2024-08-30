package cn.javastudy.algorithm;

public class BinaryTest {

    public static void printBinary(int num) {
        for (int i = 31; i >= 0; i--) {
            System.out.print((num & (1 << i)) == 0 ? '0' : '1');
        }
        System.out.println();
    }

    public static void main(String[] args) {
        printBinary(1);
        printBinary(10);
        printBinary(100);
        printBinary(1000);
    }
}
