package cn.javastudy.tree;

import java.util.Stack;

public class BinaryTreeTraversalStack {

    public static void main(String[] args) {
        TreeNode head = new TreeNode(1);

        head.left = new TreeNode(2);
        head.right = new TreeNode(3);
        head.left.left = new TreeNode(4);
        head.left.right = new TreeNode(5);
        head.right.left = new TreeNode(6);
        head.right.right = new TreeNode(7);

//        print(head);

        test1();
    }

    //递归遍历
    private static void print(TreeNode head) {
        preOrder(head);
        System.out.println();
        System.out.println("先序遍历单栈版");

        inOrder(head);
        System.out.println();
        System.out.println("中序遍历单栈版");

        postOrder(head);
        System.out.println();
        System.out.println("后序遍历单栈版");
    }

    private static void test1() {
        // 90, 17, 83, 24, 85, 71
        TreeNode head = new TreeNode(90);
        head.left = new TreeNode(17);
        head.left.right = new TreeNode(83);
        head.left.right.left = new TreeNode(24);
        head.left.right.right = new TreeNode(85);
        head.left.right.right.left = new TreeNode(71);

        //先序遍历
        preOrder(head);

        System.out.println("中序遍历一下，看看是否是有序的");
        //中序遍历一下，看看是否是有序的
        inOrder(head);

        System.out.println();
        System.out.println(isValidBST(head));
    }

    public static boolean isValidBST(TreeNode root) {
        return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public static boolean isValidBST(TreeNode node, long min, long max) {
        // 如果节点为空，返回true，因为空树也是BST
        if (node == null) {
            return true;
        }

        // 如果当前节点的值不满足BST的条件，返回false
        if (node.val <= min || node.val >= max) {
            return false;
        }
        // 递归检查左子树和右子树，更新min和max的范围
        return isValidBST(node.left, min, node.val) && isValidBST(node.right, node.val, max);
    }

    //先序遍历：中 -> 左 -> 右
    private static void preOrder(TreeNode root) {
        if (root == null) {
            return;
        }

        //栈是先进后出的原则
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            root = stack.pop();
            System.out.print(root.val + " ");

            if (root.right != null) {
                stack.push(root.right);
            }

            if (root.left != null) {
                stack.push(root.left);
            }
        }
    }

    //中序遍历：左 -> 中 -> 右
    private static void inOrder(TreeNode head) {
        if (head == null) {
            return;
        }

        Stack<TreeNode> stack = new Stack<>();
        while (!stack.isEmpty() || head != null) {
            if (head != null) {
                stack.push(head);
                head = head.left;
            } else {
                head = stack.pop();
                System.out.print(head.val + " ");
                head = head.right;
            }
        }
    }

    //后序遍历：左 -> 右 -> 中
    private static void postOrder(TreeNode head) {
        if (head == null) {
            return;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(head);

        // 如果始终没有打印过节点，head就一直是头节点
        // 打印过节点，head就是打印节点
        // 之后head是上一次打印的节点
        while (!stack.isEmpty()) {
            TreeNode current = stack.peek();
            if (current.left != null
                && head != current.left
                && head != current.right) {
                stack.push(current.left);
                continue;
            }

            if (current.right != null && head != current.right) {
                stack.push(current.right);
                continue;
            }

            System.out.print(current.val + " ");
            head = stack.pop();
        }

    }
}
