package cn.javastudy.algorithm;

public class SmallSum {

    public static void main(String[] args) {
        int[] arr = {1, 3, 4, 2, 5};
        int sum = smallSum1(arr);
        System.out.println(sum);
    }

    private static int smallSum(int[] array) {
        if (array == null || array.length < 2) {
            return 0;
        }

        return process(array, 0, array.length - 1);
    }

    // 不使用递归方式来计算
    private static int smallSum1(int[] array) {
        if (array == null || array.length < 2) {
            return 0;
        }

        int sum = 0;
        int length = array.length;
        for (int step = 1; step < length; step = step * 2) {
            int left = 0;

            while (left < length) {
                int middle = left + step - 1;
                if (middle > length - 1) {
                    break;
                }

                int right = Math.min(middle + step, length - 1);
                sum += merge(array, left, middle, right);
                left = right + 1;
            }
        }

        return sum;
    }

    private static int process(int[] array, int left, int right) {
        if (left == right) {
            return 0;
        }

        int middle = left + ((right - left) >> 1);
        return process(array, left, middle)
            + process(array, middle + 1, right)
            + merge(array, left, middle, right);
    }

    private static int merge(int[] array, int left, int middle, int right) {
        int[] temp = new int[right - left + 1];

        int index = 0;
        int leftIndex = left;
        int rightIndex = middle + 1;

        int result = 0;
        while (leftIndex <= middle && rightIndex <= right) {
            // 都没有越界
            if (array[leftIndex] < array[rightIndex]) {
                // 判断右边是否比左边的大，
                // 如果右边比左边的大，则从右边当前位置开始所有的数都比左边的大，都需要将左边的值求小和
                result += (right - rightIndex + 1) * array[leftIndex];
                temp[index++] = array[leftIndex++];
            } else {
                temp[index++] = array[rightIndex++];
            }
        }

        while (leftIndex <= middle) {
            temp[index++] = array[leftIndex++];
        }

        while (rightIndex <= right) {
            temp[index++] = array[rightIndex++];
        }

        for (int i = 0; i < temp.length; i++) {
            array[left + i] = temp[i];
        }

        return result;
    }
}
