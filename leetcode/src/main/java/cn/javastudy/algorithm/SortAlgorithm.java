package cn.javastudy.algorithm;

public final class SortAlgorithm {

    private SortAlgorithm() {
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
}
