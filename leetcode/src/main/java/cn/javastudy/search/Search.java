package cn.javastudy.search;

public class Search {

    public int search(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        int start = 0;
        int end = nums.length - 1;
        while (start <= end) {
            int middle = (start + end) / 2;
            if (nums[middle] == target) {
                return middle;
            }

            if (nums[middle] < target) {
                start = middle + 1;
            } else if (nums[middle] > target) {
                end = middle - 1;
            }

        }
        return -1;
    }

    public int minNumberInRotateArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        int start = 0;
        int end = nums.length - 1;
        while (start < end) {
            int middle = (start + end) / 2;

            if (nums[middle] > nums[end]) {
                start = middle + 1;
            } else if (nums[middle] < nums[end]) {
                end = middle;
            } else {
                end--;
            }
        }

        return nums[start];
    }
}
