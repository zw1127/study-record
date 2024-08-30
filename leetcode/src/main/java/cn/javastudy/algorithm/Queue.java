package cn.javastudy.algorithm;

public class Queue {

    private int[] array;

    private int left;
    private int right;

    public Queue(int capacity) {
        array = new int[capacity];
        left = 0;
        right = 0;
    }

    public void push(int num) {
        array[right++] = num;
    }

    public int pop() {
        return array[left++];
    }

    public int peek() {
        return array[left];
    }

    public int size() {
        return right - left;
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
