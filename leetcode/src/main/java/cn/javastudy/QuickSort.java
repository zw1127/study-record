package cn.javastudy;

import java.util.Arrays;

public class QuickSort {

    public static void main(String[] args) {
        int[] arr = {10, 3, 6, 2, 1, 9};
        quickSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }

    public static void quickSort(int[] arr, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return;
        }

        int pivotIndex = dobulePointSwap(arr, startIndex, endIndex);

        quickSort(arr, startIndex, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, endIndex);
    }

    private static int dobulePointSwap(int[] arr, int startIndex, int endIndex) {
        int pivot = arr[startIndex];
        int leftPoint = startIndex;
        int rightPoint = endIndex;

        while (leftPoint < rightPoint) {
            while (leftPoint < rightPoint && arr[rightPoint] > pivot) {
                rightPoint--;
            }

            while (leftPoint < rightPoint && arr[leftPoint] <= pivot) {
                leftPoint++;
            }

            if (leftPoint < rightPoint) {
                int temp = arr[leftPoint];
                arr[leftPoint] = arr[rightPoint];
                arr[rightPoint] = temp;
            }
        }

        arr[startIndex] = arr[rightPoint];
        arr[rightPoint] = pivot;

        return rightPoint;
    }
}
