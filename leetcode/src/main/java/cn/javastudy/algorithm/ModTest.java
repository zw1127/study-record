package cn.javastudy.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  描述：
 *      请你计算 a * b mod p 的值。要求只能使用加法和取模运算，且计算过程中的值不能超过 2 * 10^7
 *      一共有 q 次询问.
 *   输入描述：
 *      第一行输入一个正整数 q，代表询问次数。
 *      接下来每行输入三个正整数 a, b, p, 代表一次询问。
 *
 *      数据范围：
 *      1 <= q <= 10^5
 *      1 <= a, b, p <= 10^7
 *
 *   输出描述：
 *     对于每次询问, 输出一个整数,  代表 a * b mod p 的值
 *
 *     示例1
 *       输入： 2
 *             2 2 6
 *             3 4 10
 *       输出： 4
 *             2
 *  </pre>
 */
public class ModTest {

    public static void main(String[] args) throws IOException {
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        StreamTokenizer st = new StreamTokenizer(in);
//        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
//
//        st.nextToken();
//        int q = (int) st.nval;
//        for (int i = 0; i < q; i++) {
//            st.nextToken();
//            int a = (int) st.nval;
//
//            st.nextToken();
//            int b = (int) st.nval;
//            st.nextToken();
//            int p = (int) st.nval;
//
//            out.println(mod(a, b, p));
//        }
//        out.flush();
//        in.close();
//        out.close();

//        ListNode head = new ListNode(2);
//        head.next = new ListNode(4);
//        head.next.next = new ListNode(3);
//
//        ListNode head2 = new ListNode(5);
//        head2.next = new ListNode(6);
//        head2.next.next = new ListNode(4);

        ListNode head = new ListNode(9);

        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(9);
        head2.next.next = new ListNode(9);
        head2.next.next.next = new ListNode(9);
        head2.next.next.next.next = new ListNode(9);
        head2.next.next.next.next.next = new ListNode(9);
        head2.next.next.next.next.next.next = new ListNode(9);
        head2.next.next.next.next.next.next.next = new ListNode(9);
        head2.next.next.next.next.next.next.next.next = new ListNode(9);
        head2.next.next.next.next.next.next.next.next.next = new ListNode(9);

        ListNode listNode = addTwoNumbers3(head, head2);
    }

    private static int mod(int a, int b, int p) {
        // 基本情况：当 b == 0 时，返回 0
        if (b == 0) {
            return 0;
        }

        // 递归求解：将 b 对半分
        int halfProduct = mod(a, b >> 1, p);

        // 合并子问题的结果，并取模
        halfProduct = (halfProduct + halfProduct) % p;

        // 如果 b 是奇数，需要额外加上一个 a
        if (b % 2 != 0) {
            halfProduct = (halfProduct + a) % p;
        }

        return halfProduct;
    }

    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{map.get(target - nums[i]), i};
            }
            map.put(nums[i], i);
        }

        return new int[0];
    }

    public static ListNode addTwoNumbers3(ListNode l1, ListNode l2) {
        int temp = 0;

        ListNode head = new ListNode(0);
        ListNode cur = head;
        while (l1 != null || l2 != null) {
            cur.val = (l1.val + l2.val + temp) % 10;
            temp = (l1.val + l2.val + temp) / 10;

            l1 = l1.next;
            l2 = l2.next;
            if (l1 == null && l2 == null) {
                break;
            }

            if (l1 == null) {
                l1 = new ListNode(0);
            }

            if (l2 == null) {
                l2 = new ListNode(0);
            }

            cur.next = new ListNode(0);
            cur = cur.next;
        }

        if (temp != 0) {
            cur.next = new ListNode(temp);
        }

        return head;
    }

    public static ListNode addTwoNumbers2(ListNode l1, ListNode l2) {
        List<Integer> list1 = listNode2IntArray(l1);
        List<Integer> list2 = listNode2IntArray(l2);

        // 表示进位
        int temp = 0;
        // 两个数组长度一样的，都是101
        List<Integer> result = new ArrayList<>();

        int length = Math.max(list1.size(), list2.size());
        for (int i = 0; i < length; i++) {
            int num1 = i < list1.size() ? list1.get(i) : 0;
            int num2 = i < list2.size() ? list2.get(i) : 0;

            // 都没空，说明都没越界
            int sum = num1 + num2 + temp;

            if (sum <= 9) {
                temp = 0;
                result.add(sum);
            } else {
                temp = 1;
                result.add(sum % 10);
            }
        }

        if (temp != 0) {
            result.add(temp);
        }

        // 将结果转换为ListNode
        List<ListNode> nodes = result.stream().map(ListNode::new).toList();

        // 创建节点之间的关系
        for (int i = 1; i < nodes.size(); i++) {
            nodes.get(i - 1).next = nodes.get(i);
        }

        return nodes.get(0);
    }

    private static List<Integer> listNode2IntArray(ListNode node) {
        if (node == null) {
            return List.of();
        }

        List<Integer> result = new ArrayList<>();
        result.add(node.val);
        while (node.next != null) {
            node = node.next;
            if (result.size() >= 101) {
                break;
            }
            result.add(node.val);
        }

        return result;
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        LinkedList<Integer> stack1 = new LinkedList<>();
        LinkedList<Integer> Stack2 = new LinkedList<>();

        toStack(stack1, l1);
        toStack(Stack2, l2);

        int a = stackToInt(stack1);
        int b = stackToInt(Stack2);

        int sum = a + b;

        List<Integer> list = convertToArray(sum);
        int length = list.size();

        List<ListNode> nodes = list.stream().map(ListNode::new).toList();

        // head 为个位
        ListNode head = nodes.get(0);
        for (int i = 1; i < length; i++) {
            nodes.get(i - 1).next = nodes.get(i);
        }

        return nodes.get(0);
    }

    public static List<Integer> convertToArray(int number) {
        // 将数字转换为倒序字符串
        String numberStr = String.valueOf(number);
        List<Integer> digits = new ArrayList<>(numberStr.length());

        // 遍历字符串，将每个字符转换为整数并存入数组
        for (int i = numberStr.length() - 1; i >= 0; i--) {
            digits.add(Character.getNumericValue(numberStr.charAt(i)));
        }

        return digits;
    }

    private static void toStack(LinkedList<Integer> stack, ListNode node) {
        if (node != null) {
            stack.push(node.val);
            toStack(stack, node.next);
        }
    }

    private static int stackToInt(LinkedList<Integer> stack) {
        int value = 0;
        int size = stack.size();
        for (int i = size - 1; i >= 0; i--) {
            value += stack.pop() * ((int) Math.pow(10, i));
        }

        return value;
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

}
