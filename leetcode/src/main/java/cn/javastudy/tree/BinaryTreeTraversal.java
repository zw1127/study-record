package cn.javastudy.tree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryTreeTraversal {

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * 先序遍历  中 -> 左 -> 右
     *
     * @param root TreeNode类
     * @return int整型一维数组
     */
    public int[] preorderTraversal(TreeNode root) {
        // write code here
        if (root == null) {
            return new int[0];
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        List<Integer> list = new ArrayList<>();
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            list.add(node.val);

            if (node.right != null) {
                stack.push(node.right);
            }

            if (node.left != null) {
                stack.push(node.left);
            }
        }

        return list.stream().mapToInt(i -> i).toArray();
    }

    /**
     * 中序遍历  左 -> 中 -> 右
     *
     * @param root TreeNode类
     * @return int整型一维数组
     */
    public int[] inorderTraversal(TreeNode root) {
        if (root == null) {
            return new int[0];
        }

        Stack<TreeNode> stack = new Stack<>();
        List<Integer> list = new ArrayList<>();
        while (root != null || !stack.isEmpty()) {
            if (root != null) {
                stack.push(root);
                root = root.left;
            } else {
                root = stack.pop();
                list.add(root.val);
                root = root.right;
            }
        }

        return list.stream().mapToInt(i -> i).toArray();
    }

    /**
     * 后序遍历  左 -> 右 -> 中
     *
     * @param root TreeNode类
     * @return int整型一维数组
     */
    public int[] postorderTraversal(TreeNode root) {
        if (root == null) {
            return new int[0];
        }

        List<Integer> list = new ArrayList<>();
        postorderRecursion(root, list);

        return list.stream().mapToInt(i -> i).toArray();
    }

    //递归实现
    public void postorderRecursion(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }

        postorderRecursion(root.left, result);
        postorderRecursion(root.right, result);
        result.add(root.val);
    }

    // 非递归，单栈实现
    public void postorder(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode current = stack.peek();

            if (current.left != null && root != current.left && root != current.right) {
                stack.push(current.left);
            } else if (current.right != null && root != current.right) {
                stack.push(current.right);
            } else {
                root = stack.pop();
                result.add(root.val);
            }
        }
    }

    // 求二叉树的最大深度
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        return Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }
}
