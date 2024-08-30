package cn.javastudy.algorithm;

public class EorTest {

    public static void main(String[] args) {
        int[] arr = {1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 9, 9, 10};
        selectTwo(arr);
    }

    public static void selectTwo(int[] array) {
        int eor = 0;
        for (int current : array) {
            eor ^= current;
        }

        // 循环完成后，得到两个只有奇数个数的数的异或 eor = a ^ b

        // 取出最右边的1
        // 假设  eor          = 1010 0011 1100
        // 那么 ~eor          = 0101 1100 0011
        // ~eor+ 1           = 0101 1100 0100
        // eor & (~eor + 1)  = 0000 0000 0100
        // 提取一个数，最右侧的1，得到的数就这么写
        int rightOne = eor & (~eor + 1);
        int onlyOne = 0;
        for (int current : array) {
            // 取出数组中的每个数，和rightOne求与，看最右边是否为1，为1则与onlyOne异或
            if ((current & rightOne) == 1) {
                onlyOne ^= current;
            }
        }
        System.out.println(onlyOne + "  " + (eor ^ onlyOne));
    }
}
