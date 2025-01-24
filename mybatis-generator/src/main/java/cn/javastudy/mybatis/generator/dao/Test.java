package cn.javastudy.mybatis.generator.dao;

import java.util.Arrays;
import java.util.Stack;

/**
 * Function Description:  <br>
 * Writer: zw_1127 . <br>
 * Creating Time: 2024-12-17 15:58 <br>
 * Version: 1.0.0 <br>
 */
public class Test {

    public static void main(String[] args) {
        int[] nums = {1, 3, 6, 2, 4, 5, 9, 7, 8};
//        int[] smallest = findSmallestOnLeft(nums);
        int[] smallest = findSmallestOnLeft(nums);
//        int[] smallest = new int[nums.length];
//        findNearestSmaller(nums, nums.length, smallest);
        System.out.println(Arrays.toString(smallest));
    }

    /**
     * 创建一个名为findSmallestOnLeft的方法，它接受一个整数数组nums作为参数，并返回一个新的整数数组result。
     * 初始化一个result数组，其长度与输入数组相同，所有元素都初始化为-1。
     * 创建一个Stack<Integer>类型的栈stack，用于存储数组元素的索引。
     * 遍历输入数组：
     * 对于每个元素，检查栈是否为空或者当前元素是否小于栈顶元素对应的数组值。如果是，弹出栈顶元素，直到栈为空或者栈顶元素对应的数组值小于当前元素。
     * 如果栈为空，则将当前元素的索引对应的result数组元素设置为-1。
     * 如果栈不为空，则将当前元素的索引对应的result数组元素设置为栈顶元素对应的数组值。
     * 将当前元素的索引压入栈中。
     * 返回填充好的result数组。
     *
     * @param nums
     * @return
     */
    public static int[] findSmallestOnLeft(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] >= nums[i]) {
                stack.pop();
            }

            if (stack.isEmpty()) {
                result[i] = -1;
            } else {
                result[i] = nums[stack.peek()];
            }
            stack.push(i);
        }

        return result;
    }

    private static void findNearestSmaller(int arr[], int size, int result[]) {

        int i = 0, j = 0;
        int nearest_smaller = -1;
        for (i = 0; i < size; i++) {

            for (j = 0; j < i; j++) {
                if (arr[j] < arr[i]) {
                    nearest_smaller = arr[j];
                }
            }
            result[i] = nearest_smaller;
        }
    }
}
