package com.patternwisejavasolutions.trees.dfscore;

import java.util.ArrayList;
import java.util.List;

public class ValidateBST {

    /* PROBLEM IN SIMPLE WORDS
     * Check if a binary tree is a valid Binary Search Tree.
     *
     * Sample Input:  root = [2,1,3]
     * Sample Output: true
     *
     * In a valid BST, all values in the left subtree are smaller than the node,
     * and all values in the right subtree are larger than the node.
     */

    /* SCHOOL-LEVEL INTUITION
     * In a valid BST, smaller values stay on the left side and larger values stay on the right side.
     * This rule is not just for direct children; every node must fit inside the allowed range from its ancestors.
     * Example: a 6 inside the left subtree of 5 is invalid even if it is the right child of 4.
     * Another way to see it: reading a BST inorder should produce strictly increasing values.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * In a BST, inorder traversal gives values in increasing order. The easy method is to collect inorder values and check sorted order.
     */

    /* BRUTE FORCE DRY RUN
     * For [2,1,3], inorder is [1,2,3], strictly increasing, so valid.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Do an inorder traversal and store all values in a list.
     * 2. In a valid BST, this list must be strictly increasing.
     * 3. Compare every value with the value before it.
     *
     * Time Complexity:
     * We visit every node once and scan the list once, so the time is O(n).
     *
     * Space Complexity:
     * The list stores all node values, so the space is O(n).
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public boolean bruteForce(TreeNode root) {
        List<Integer> values = new ArrayList<>();
        inorder(root, values);

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) <= values.get(i - 1)) {
                // Equal or smaller means inorder is not strictly increasing.
                return false;
            }
        }

        return true;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * The inorder list proves the tree after storing every value.
     * The range method catches mistakes while walking: left child must be smaller than current,
     * right child must be bigger, and ancestor limits travel down the tree.
     */

    /* OPTIMIZED DRY RUN
     * Root 2 allows left range (-inf,2) and right range (2,+inf). Node 1 fits left range. Node 3 fits right range.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Give each node an allowed range: low < node value < high.
     * 2. The root can be between negative infinity and positive infinity.
     * 3. For a left child, the high limit becomes the current node value.
     * 4. For a right child, the low limit becomes the current node value.
     *
     * Time Complexity:
     * Every node is checked once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the height of the tree.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public boolean optimized(TreeNode root) {
        return isValid(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private void inorder(TreeNode node, List<Integer> values) {
        if (node == null) {
            return;
        }
        inorder(node.left, values);
        values.add(node.val);
        inorder(node.right, values);
    }

    private boolean isValid(TreeNode node, long low, long high) {
        if (node == null) {
            // Empty subtrees do not violate the BST rule.
            return true;
        }
        if (node.val <= low || node.val >= high) {
            return false;
        }

        // Left side must stay below node.val; right side must stay above node.val.
        return isValid(node.left, low, node.val) && isValid(node.right, node.val, high);
    }


    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }
}
