package cn.javastudy.node;

public class ReverseList {

    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode prev = null;
        ListNode current = head;
        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }

        return prev;
    }

    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode prev = dummy;
        ListNode start = head;
        for (int i = 1; i < m; i++) {
            prev = start;
            start = start.next;
        }

        for (int i = 0; i < n - m; i++) {
            // 在范围内的才需要反转
            ListNode next = start.next;
            start.next = prev;
            prev = start;
            start = next;
        }

        int index = 1;
        while (start != null) {
            ListNode next = start.next;
            if (index < m || index > n) {
                prev = start;
                start = next;
            } else {

            }

            index++;
        }

        head = prev;
        return dummy.next;
    }
}
