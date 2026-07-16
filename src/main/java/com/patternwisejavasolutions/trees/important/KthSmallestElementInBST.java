package com.patternwisejavasolutions.trees.important;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class KthSmallestElementInBST {

    /* PROBLEM IN SIMPLE WORDS
     * Find the kth smallest value in a Binary Search Tree.
     *
     * Sample Input:  root = [3,1,4,null,2], k = 1
     * Sample Output: 1
     */

    /* SCHOOL-LEVEL INTUITION
     * A BST keeps smaller values to the left and larger values to the right.
     * If we read it left, node, right, the values come out sorted.
     * The kth value in that sorted reading is the kth smallest.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * BST inorder traversal gives values in sorted order. So collect inorder values and pick k-1.
     */

    /* BRUTE FORCE DRY RUN
     * Inorder of [3,1,4,null,2] is [1,2,3,4]. k=1 gives value 1.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Traverse the BST using inorder: left, node, right.
     * 2. Store every visited value in a list.
     * 3. Since the list is sorted, return the value at index k - 1.
     *
     * Time Complexity:
     * We visit every node, so the time is O(n).
     *
     * Space Complexity:
     * The list stores all n values, so the space is O(n).
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(TreeNode root, int k) {
        List<Integer> values = new ArrayList<>();
        inorder(root, values);

        // k is 1-based, but list indexes are 0-based.
        return values.get(k - 1);
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * We do not need all values. Stop inorder traversal as soon as we visit the kth node.
     */

    /* OPTIMIZED DRY RUN
     * Visit 1 first. If k=1, return 1 immediately.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Use a stack to perform inorder traversal without recursion.
     * 2. Keep moving left because smaller values are on the left.
     * 3. When a node is visited, decrease k.
     * 4. When k becomes 0, that node is the kth smallest.
     *
     * Time Complexity:
     * We stop after reaching the kth visited node, so the time is O(h + k).
     * Here h is the tree height, because we may first walk down the left side.
     *
     * Space Complexity:
     * The stack stores nodes on the path from root to current, so the space is O(h).
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                // Go left first because inorder visits smaller BST values first.
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            // We have now visited one more value in sorted order.
            k--;

            if (k == 0) {
                return current.val;
            }

            current = current.right;
        }

        return -1;
    }

    private void inorder(TreeNode node, List<Integer> values) {
        if (node == null) {
            return;
        }

        inorder(node.left, values);
        // In a BST, this visit happens after smaller values and before larger values.
        values.add(node.val);
        inorder(node.right, values);
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
