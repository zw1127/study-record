package cn.javastudy.algorithm;

public class SortTest {

    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        // 0 ~ 0-1
        // 0 ~ 0-2
        // 0 ~ 0-3
        // 0 ~ i--
        for (int end = arr.length - 1; end > 0; end--) {
            for (int i = 0; i < end; i++) {
                // 0 ~ end
                // 0 1
                // 1 2
                // 2 3
                // 3 4
                // ...
                if (arr[i] > arr[i + 1]) {
                    swap(arr, i, i + 1);
                }
            }
        }
    }

    public static void selectionSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            swap(arr, i, minIndex);
        }
    }

    public static void insertionSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        // 0 ~ 0
        // 0 ~ 1
        // 0 ~ 2
        // ...
        for (int i = 1; i < arr.length; i++) {
            // 0 ~ i-1 已经有序了！新来的数是[i]， 向左看！
            for (int j = i - 1; j >= 0 && arr[j] > arr[j + 1]; j--) {
                swap(arr, j, j + 1);
            }

        }
    }

    // 交换数组中两个位置的数
    public static void swap(int[] arr, int left, int right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
    }
}
