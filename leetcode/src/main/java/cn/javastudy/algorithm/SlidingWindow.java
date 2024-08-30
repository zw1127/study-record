package cn.javastudy.algorithm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;


/**
 * <pre>
 *    //外层循环扩展右边界，内层循环扩展左边界
 *    for (int l = 0, r = 0 ; r < n ; r++) {
 * 	      //当前考虑的元素
 * 	      while (l <= r && check()) {//区间[left,right]不符合题意
 *            //扩展左边界
 *        }
 *        //区间[left,right]符合题意，统计相关信息
 *    }
 * </pre>
 */
public class SlidingWindow {

    public static void main(String[] args) {
        String s = "abcabcbb";
        System.out.println(lengthOfLongestSubstring(s));

        int[] num1 = new int[]{1, 2};
        int[] num2 = new int[]{3, 4};
        findMedianSortedArrays(num1, num2);
    }

    public static int lengthOfLongestSubstring(String s) {
        int left = 0;
        int result = 0;
        Set<Character> set = new HashSet<>();
        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);
            while (set.contains(current)) {
                set.remove(s.charAt(left));
                left++;
            }
            set.add(current);
            result = Math.max(result, right - left + 1);
        }

        return result;
    }

    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int[] help = new int[nums1.length + nums2.length];

        int index = 0;
        int left = 0;
        int right = 0;

        // 如果2个数组区间有交叉
        if (nums1[0] < nums2[0] && nums1[nums1.length - 1] >= nums2[0]) {}

        // 如果nums1的最大数比nums2的最小数还要小
        if (nums1[nums1.length - 1] <= nums2[0]) {
            System.arraycopy(nums1, 0, help, 0, nums1.length);
            System.arraycopy(nums2, 0, help, nums1.length, nums2.length);
        }

        // 如果nums1的最小数比nums2的最大数还要大
        if (nums1[0] > nums2[nums2.length - 1]) {
            System.arraycopy(nums2, 0, help, 0, nums2.length);
            System.arraycopy(nums1, 0, help, nums2.length, nums1.length);
        }

        while (index < help.length && (left < nums1.length || right < nums2.length)) {
            if (nums1[left] <= nums2[right]) {
                help[index++] = nums1[left++];
            } else {
                help[index++] = nums2[right++];
            }
        }

        int sum = 0;
        if (help.length % 2 == 0) {
            sum = help[help.length / 2 - 1] + help[help.length / 2];
        } else {
            sum = help[help.length / 2] + help[help.length / 2];
        }

        return new BigDecimal(sum).divide(new BigDecimal(2), 5, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
