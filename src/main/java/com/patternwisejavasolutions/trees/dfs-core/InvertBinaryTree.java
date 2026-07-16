package com.patternwisejavasolutions.trees.dfscore;

import java.util.LinkedList;
import java.util.Queue;

public class InvertBinaryTree {

    /* PROBLEM IN SIMPLE WORDS
     * Swap the left and right child of every node in the tree.
     *
     * Sample Input:  root = [4,2,7,1,3,6,9]
     * Sample Output: [4,7,2,9,6,3,1]
     */

    /* SCHOOL-LEVEL INTUITION
     * Inverting a tree means every node swaps its left and right child.
     * After swapping the root, the same swap must happen inside both smaller subtrees.
     * If every node does this once, the whole tree becomes a mirror image.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * To invert a tree, every node must swap its children. We can visit nodes level by level and swap each one.
     */

    /* BRUTE FORCE DRY RUN
     * At 4, swap 2 and 7. At 7, swap 6 and 9. At 2, swap 1 and 3.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. If the root is null, return null.
     * 2. Use a queue to visit every node.
     * 3. For each node, swap its left and right child.
     * 4. Add the swapped children to the queue so their children can also be swapped.
     *
     * Time Complexity:
     * Every node is visited once, so the time is O(n).
     *
     * Space Complexity:
     * The queue can hold many nodes from a level, so the space is O(n) in the worst case.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(TreeNode root) {
        if (root == null) {
            return null;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            // Swapping these two pointers is the actual inversion step for this node.
            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;

            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }

        return root;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * Recursion says: invert left subtree, invert right subtree, then swap them at current node.
     */

    /* OPTIMIZED DRY RUN
     * For root 4, recursively invert 2 subtree and 7 subtree, then place inverted 7 on left and inverted 2 on right.
     */

    /* OPTIMIZED ALGORITHM
     * 1. If the node is null, return null.
     * 2. Recursively invert the left subtree.
     * 3. Recursively invert the right subtree.
     * 4. Put the inverted right subtree on the left and the inverted left subtree on the right.
     *
     * Time Complexity:
     * Every node is visited once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses the call stack. The stack height is O(h), where h is the height of the tree.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(TreeNode root) {
        if (root == null) {
            return null;
        }

        // First invert both smaller subtrees.
        TreeNode invertedLeft = optimized(root.left);
        TreeNode invertedRight = optimized(root.right);

        // Then swap their positions at the current node.
        root.left = invertedRight;
        root.right = invertedLeft;

        return root;
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
