package com.patternwisejavasolutions.trees.important;

public class BinaryTreeMaximumPathSum {

    private int bestSum;

    /* PROBLEM IN SIMPLE WORDS
     * Find the largest sum of any path in a binary tree. A path can start and end at any nodes, but
     * it must follow parent-child links.
     *
     * Sample Input:  root = [-10,9,20,null,null,15,7]
     * Sample Output: 42
     */

    /* SCHOOL-LEVEL INTUITION
     * Each node can be the highest point of a path. At that node, the best path may use the best
     * left branch, the node itself, and the best right branch.
     * A negative branch should be ignored because adding it would only lower the sum.
     */

    /* BRUTE FORCE INTUITION
     * Try every node as the path's highest turning point, then calculate the best downward branch
     * from its left and right children.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Visit every node.
     * 2. For that node, compute the best downward path from the left child.
     * 3. Compute the best downward path from the right child.
     * 4. Combine left + node + right and update the answer.
     * 5. Repeat for all nodes.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(h)
     */

    /* BRUTE FORCE DRY RUN
     * At node 20, best left branch is 15 and best right branch is 7, so path sum is 42.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(TreeNode root) {
        bestSum = Integer.MIN_VALUE;
        checkEveryNode(root);
        return bestSum;
    }

    /* OPTIMIZED INTUITION
     * The brute force pain is recomputing downward branches for many turning points.
     * While returning the best one-side branch to the parent, also update the full path answer at
     * each node. This avoids recalculating branches.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Use DFS.
     * 2. Get best gain from left and right children.
     * 3. Ignore negative gains because they make a path worse.
     * 4. Update answer with left + node + right.
     * 5. Return node + max(left, right) to the parent.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */

    /* OPTIMIZED DRY RUN
     * Node 20 sees left gain 15 and right gain 7, updates best to 42, and returns 35 upward.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(TreeNode root) {
        bestSum = Integer.MIN_VALUE;
        bestGain(root);
        return bestSum;
    }

    private void checkEveryNode(TreeNode node) {
        if (node == null) {
            return;
        }

        int leftGain = Math.max(0, downwardGain(node.left));
        int rightGain = Math.max(0, downwardGain(node.right));
        bestSum = Math.max(bestSum, node.val + leftGain + rightGain);

        checkEveryNode(node.left);
        checkEveryNode(node.right);
    }

    private int downwardGain(TreeNode node) {
        if (node == null) {
            // A missing child contributes no gain.
            return 0;
        }

        // A path sent upward can choose only one side, otherwise it would split into two paths.
        int left = downwardGain(node.left);
        int right = downwardGain(node.right);
        return node.val + Math.max(0, Math.max(left, right));
    }

    private int bestGain(TreeNode node) {
        if (node == null) {
            return 0;
        }

        int leftGain = Math.max(0, bestGain(node.left));
        int rightGain = Math.max(0, bestGain(node.right));

        // This is the best path that turns at this node and uses both sides.
        bestSum = Math.max(bestSum, node.val + leftGain + rightGain);
        return node.val + Math.max(leftGain, rightGain);
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
