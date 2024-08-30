package cn.javastudy.tree;

import java.util.StringJoiner;

class TreeNode {

    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int index) {
        val = index;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TreeNode.class.getSimpleName() + "[", "]")
            .add("val=" + val)
            .toString();
    }
}
