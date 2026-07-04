

package com.companywisejavasolutions.ebay.solutions;
public class SubtreeOfAnotherTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Check whether subRoot appears inside root with the exact same structure
     * and values.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Visit each node of the big tree. Whenever a node has the same value as
     * subRoot, compare both trees from that point.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. If subRoot is empty, it is a subtree.
     * 2. If root is empty, subRoot cannot be found.
     * 3. Check if root and subRoot are identical.
     * 4. Otherwise search in root.left and root.right.
     *
     * Time Complexity: O(m * n) in the worst case
     * Space Complexity: O(h)
     */
    public boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (subRoot == null) return true;
        if (root == null) return false;

        return sameTree(root, subRoot)
                || isSubtree(root.left, subRoot)
                || isSubtree(root.right, subRoot);
    }

    private boolean sameTree(TreeNode first, TreeNode second) {
        if (first == null || second == null) {
            return first == second;
        }

        return first.val == second.val
                && sameTree(first.left, second.left)
                && sameTree(first.right, second.right);
    }
}
