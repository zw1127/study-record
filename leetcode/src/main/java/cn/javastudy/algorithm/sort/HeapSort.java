package cn.javastudy.algorithm.sort;

import java.util.Arrays;

public class HeapSort {

    public static void main(String[] args) {
        int[] arry = {5, 4, 1, 3, 2, 10, 6, 7, 8, 9};
        heapSort(arry);
        System.out.println(Arrays.toString(arry));

    }

    public static void heapSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        // 创建一个大根堆
//        for (int i = 0; i < arr.length; i++) {
//            heapInsert(arr, i);
//        }

        // 复杂度不变，这样写更快
        for (int i = arr.length - 1; i > 0; i--) {
            heapInsert(arr, i);
        }

        int heapSize = arr.length;

        // 将第0个节点和最后一个节点交换
        swap(arr, 0, --heapSize);
        while (heapSize > 0) {
            heapify(arr, 0, heapSize);
            swap(arr, 0, --heapSize);
        }
    }

    private static void heapify(int[] arr, int index, int heapSize) {
        // 重新构建大根堆
        // 左孩子的下标
        int left = index * 2 + 1;
        //下方还有孩子时
        while (left < heapSize) {
            // 两个孩子中，谁的值大，把下标给 largest
            int largest = left + 1 < heapSize && arr[left + 1] > arr[left] ? left + 1 : left;

            // 比较当前节点和较大孩子，谁的值大，把下标给largest
            largest = arr[largest] > arr[index] ? largest : index;
            if (largest == index) {
                break;
            }

            // 交换当前节点和largest对应的下标
            swap(arr, largest, index);

            // 修改当前节点和其左子节点的下标值
            index = largest;
            left = index * 2 + 1;
        }
    }

    private static void heapInsert(int[] arr, int index) {
        // 插入到堆中
        // 比较当前节点和其父节点的大小，如果当前节点的值大于其父节点，则交换
        // 更新index为其夫节点，重复上面的步骤
        while (arr[index] > arr[(index - 1) / 2]) {
            swap(arr, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
