package com.patternwisejavasolutions.trees.dfscore;

import java.util.LinkedList;
import java.util.Queue;

public class MaximumDepthOfBinaryTree {

    /* PROBLEM IN SIMPLE WORDS
     * Find how many levels the binary tree has.
     *
     * Sample Input:  root = [3,9,20,null,null,15,7]
     * Sample Output: 3
     */

    /* SCHOOL-LEVEL INTUITION
     * The depth is the number of levels from the root down to the deepest leaf.
     * If we count one level for the current node, the remaining depth is whichever child side is deeper.
     * Empty children add zero levels.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * Depth means level count. A beginner-friendly way is BFS level by level and count how many levels we process.
     */

    /* BRUTE FORCE DRY RUN
     * Level 1 has 3. Level 2 has 9 and 20. Level 3 has 15 and 7. Total depth is 3.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Put the root in a queue.
     * 2. Process one full level at a time.
     * 3. After finishing each level, increase depth by 1.
     *
     * Time Complexity:
     * Every node is removed from the queue once, so the time is O(n).
     *
     * Space Complexity:
     * The queue can hold one wide level of the tree, so the space is O(n) in the worst case.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                if (node.left != null) {
                    // Add children so the next loop can count the next level.
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    // This child also belongs to the next level.
                    queue.offer(node.right);
                }
            }

            // One full level has been processed.
            depth++;
        }

        return depth;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * A tree depth can be defined recursively: current node contributes 1 level plus the deeper depth of its left or right subtree.
     */

    /* OPTIMIZED DRY RUN
     * For root 3, left depth is 1 from node 9, right depth is 2 from 20->15/7. Return 1 + max(1,2) = 3.
     */

    /* OPTIMIZED ALGORITHM
     * 1. If the node is null, its depth is 0.
     * 2. Find the depth of the left subtree.
     * 3. Find the depth of the right subtree.
     * 4. Return 1 for the current node plus the larger subtree depth.
     *
     * Time Complexity:
     * Every node is visited once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the tree height.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int leftDepth = optimized(root.left);
        int rightDepth = optimized(root.right);

        // Current node adds one level above the deeper child subtree.
        return 1 + Math.max(leftDepth, rightDepth);
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
