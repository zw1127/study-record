package cn.javastudy.algorithm;

import java.util.Arrays;

public final class SortAlgorithm {

    private SortAlgorithm() {
    }

    public static void main(String[] args) {
        int[] arry = {5, 4, 1, 3, 2, 10, 6, 7, 8, 9};
        mergeSort1(arry);
        System.out.println(Arrays.toString(arry));
    }

    // 冒泡排序
    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        for (int i = arr.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    /**
     * 快速排序
     * 入口函数（递归方法），算法的调用从这里开始。
     */
    public static void quickSort(int[] arr, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return;
        }

        // 核心算法部分：分别介绍 双边指针（交换法），双边指针（挖坑法），单边指针
        int pivotIndex = doublePointerSwap(arr, startIndex, endIndex);

        // 用分界值下标区分出左右区间，进行递归调用
        quickSort(arr, startIndex, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, endIndex);
    }


    /**
     * 双边指针（交换法）
     * 思路：
     * <pre>
     * 记录分界值 pivot，创建左右指针（记录下标）。
     * （分界值选择方式有：首元素，随机选取，三数取中法）
     *
     * 首先从右向左找出比pivot小的数据，
     * 然后从左向右找出比pivot大的数据，
     * 左右指针数据交换，进入下次循环。
     *
     * 结束循环后将当前指针数据与分界值互换，
     * 返回当前指针下标（即分界值下标）
     * </pre>
     */
    private static int doublePointerSwap(int[] arr, int startIndex, int endIndex) {
        int pivot = arr[startIndex];
        int leftPoint = startIndex;
        int rightPoint = endIndex;

        while (leftPoint < rightPoint) {
            // 从右向左找出比pivot小的数据
            while (leftPoint < rightPoint
                && arr[rightPoint] > pivot) {
                rightPoint--;
            }
            // 从左向右找出比pivot大的数据
            while (leftPoint < rightPoint
                && arr[leftPoint] <= pivot) {
                leftPoint++;
            }
            // 没有过界则交换
            if (leftPoint < rightPoint) {
                int temp = arr[leftPoint];
                arr[leftPoint] = arr[rightPoint];
                arr[rightPoint] = temp;
            }
        }
        // 最终将分界值与当前指针数据交换
        arr[startIndex] = arr[rightPoint];
        arr[rightPoint] = pivot;
        // 返回分界值所在下标
        return rightPoint;
    }

    /**
     * 二分查找
     */
    public static int binarySearch(Integer[] srcArray, int des) {
        //定义初始最小、最大索引
        int start = 0;
        int end = srcArray.length - 1;
        //确保不会出现重复查找，越界
        while (start <= end) {
            //计算出中间索引值
            int middle = (end + start) >>> 1;//防止溢出。或者 int middle = start + (end - start) / 2;
            if (des == srcArray[middle]) {
                return middle;
                //判断下限
            } else if (des < srcArray[middle]) {
                end = middle - 1;
                //判断上限
            } else {
                start = middle + 1;
            }
        }

        //若没有，则返回-1
        return -1;
    }

    public static void selectionSort1(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        for (int i = 0; i < arr.length; i++) {
            // 从左至右，找出最小数：
            // 找最小数的方法，从j = i + 1开始循环，j小于arr.length结束，
            // 比较arr[i]和arr[j]的值，如果arr[i] 大于 arr[j],
            // 则交换，保证arr[i]小于后面的值
//            for (int j = i + 1; j < arr.length; j++) {
//                if (arr[i] > arr[j]) {
//                    swap(arr, i, j);
//                }
//            }
            // 这种写法交换次数过多，可以先暂存最小值的索引，内层循环完成后，可以得到最小值的索引，
            // 最后与arr[i]交换，这样的话，交换次数最多只有arr.length次
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[minIndex] > arr[j]) {
                    minIndex = j;
                }
            }
            swap(arr, i, minIndex);
        }
    }

    /**
     * 选择排序
     * <pre>
     * for i = 0
     * for j = i + 1
     *
     * 选择排序的步骤如下。
     *   (1) 从左至右检查数组的每个格子，找出值最小的那个。
     *   (2) 知道哪个格子的值最小之后，将该格与本次检查的起点交换。
     *   (3) 重复第(1) (2)步，直至数组排好序。
     * </pre>
     */
    public static void selectionSort(int[] arr) {
        /*判断数组为空或为一个元素的情况，即边界检查*/
        if (arr == null || arr.length < 2) {
            return;
        }

        /*每次要进行比较的两个数，的前面那个数的下标*/
        for (int i = 0; i < arr.length - 1; i++) {
            //minIndex变量保存该趟比较过程中，最小元素所对应的索引，
            //先假设前面的元素为最小元素
            int minIndex = i;
            /*每趟比较，将前面的元素与其后的元素逐个比较*/
            for (int j = i + 1; j < arr.length; j++) {
                //如果后面的元素小，将后面元素的索引赋值为最小值的索引
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            //然后交换原始最小值和此次查找到的最小值
            swap(arr, i, minIndex);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static void insertionSort1(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        //0-0 有序
        //0-i 想有序
        for (int i = 1; i < array.length; i++) {
            for (int j = i - 1; j >= 0; j--) {
                // 当前数为i，即 j+1，当前数比左边大，或者左边没有数了，就停
                if (array[j] > array[j + 1]) {
                    // 当i=1时，内层循环第一次只循环一次，相当于只比较数组的arry[0] 和 array[1]
                    swap(array, j, j + 1);
                }
            }
        }
    }

    /**
     * 插入排序
     * <pre>
     *     插入排序包含 4 种步骤：移除、比较、平移和插入。
     *      for i = 1
     *      while position >= 0 &&  array[position - 1] > temp_value
     *
     *  (1) 在第一轮里，暂时将索引 1（第 2 格）的值移走，并用一个临时变量来保存它。
     *  (2) 接着便是平移阶段，我们会拿空隙左侧的每一个值与临时变量的值进行比较。
     *      如果空隙左侧的值大于临时变量的值，则将该值右移一格。随着值右移，空隙会左移。
     *      如果遇到比临时变量小的值，或者空隙已经到了数组的最左端，就结束平移阶段。
     *  (3) 将临时移走的值插入当前空隙。
     *  (4) 重复第(1)至(3)步，直至数组完全有序。
     * </pre>
     */
    public static void insertionSort(int[] array) {
        for (int i = 1; i < array.length; i++) {
            int position = i;
            int temp_value = array[i];
            while (position >= 0 && array[position - 1] > temp_value) {
                array[position] = array[position - 1];
                position = position - 1;
            }
            array[position] = temp_value;
        }
    }

    // 归并排序，采用递归的方式
    public static void mergeSort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        process(array, 0, array.length - 1);
    }

    // 非递归方式
    public static void mergeSort1(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        int n = array.length;
        // 步长为 1 2 4 8 16...指数级增长，每次merge步长个数的数据
        for (int step = 1; step < n; step *= 2) {
            int left = 0;
            while (left < n) {
                int middle = left + step - 1;
                // 当 middle 值大于或者等于数组长度时，表示右边已经没有数据了
                if (middle >= n - 1) {
                    break;
                }

                // 有右侧，求右侧的边界。右侧的索引值为左侧的索引 * 步长 - 1，如果这个值超过了数组长度，则取数组长度。
                int right = Math.min(left + step * 2 - 1, n - 1);

                // 合并左右数据
                merge(array, left, middle, right);

                // 下一步归并
                left = right + 1;
            }
        }
    }

    private static void process(int[] array, int left, int right) {
        // 如果数组的左右下标相等，说明已经排好序了
        if (left >= right) {
            return;
        }
        // 通过递归来实现归并排序
        // 求中间位置，等同于 (right + left)/2，下面这种写法可以预防数据大时，越界的问题
        // (right + left)/2 = (2left + right - left) /2 = left + (right - left) / 2
        // 除以2，就是将该数右移一位
        // 移位运算符的优先级比+ -还低
        int middle = left + ((right - left) >> 1);

        // 左边的排好序
        process(array, left, middle);
        // 右边的排好序
        process(array, middle + 1, right);
        // 合并
        merge(array, left, middle, right);
    }

    private static void merge(int[] array, int left, int middle, int right) {
        // 谁小拷贝谁
        int[] temp = new int[right - left + 1];

        int index = 0;
        int leftIndex = left;
        int rightIndex = middle + 1;
        // 都没有越界时，谁小拷贝谁
        while (leftIndex <= middle && rightIndex <= right) {
            if (array[leftIndex] <= array[rightIndex]) {
                temp[index++] = array[leftIndex++];
            } else {
                temp[index++] = array[rightIndex++];
            }
        }

        // 下面两个一定会有一个越界的
        // 右边越界时，拷贝左侧剩余的
        while (leftIndex <= middle) {
            temp[index++] = array[leftIndex++];
        }

        // 左边越界时，拷贝右侧剩余的
        while (rightIndex <= right) {
            temp[index++] = array[rightIndex++];
        }

        // 拷贝回原数组
        for (int i = 0; i < temp.length; i++) {
            array[left + i] = temp[i];
        }
    }
}
