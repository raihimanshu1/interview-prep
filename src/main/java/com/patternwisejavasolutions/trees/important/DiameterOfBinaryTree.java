
package com.patternwisejavasolutions.trees.important;
public class DiameterOfBinaryTree {

    /* PROBLEM IN SIMPLE WORDS
     * Find the longest path between any two nodes in a binary tree.
     * The path length is counted in edges, not nodes.
     *
     * Sample Input:  root = [1,2,3,4,5]
     * Sample Output: 3
     *
     * The longest path is 4 -> 2 -> 1 -> 3, which has 3 edges.
     */

    /* SCHOOL-LEVEL INTUITION
     * The longest path may pass through the root, or it may be completely inside one subtree.
     * At any node, the best path through that node uses the deepest left branch plus the deepest right branch.
     * So height information helps us measure possible diameters.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * At every node, a path can go through left subtree, current node, and right subtree. Directly compute height for every node.
     */

    /* BRUTE FORCE DRY RUN
     * At node 2, left height 1 and right height 1, path through node is 2 edges. At root, left height 2 and right 1, path is 3.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. For the current node, calculate the height of its left subtree.
     * 2. Calculate the height of its right subtree.
     * 3. left height + right height gives the path length through the current node.
     * 4. Recursively find the best diameter in the left and right subtrees.
     * 5. Return the largest of those three choices.
     *
     * Time Complexity:
     * Height is recalculated many times. In a skewed tree this can become O(n^2).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the height of the tree.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(TreeNode root) {
        if (root == null) {
            return 0;
        }

        // A diameter passing through root uses the deepest left path and deepest right path.
        int throughRoot = height(root.left) + height(root.right);
        int leftDiameter = bruteForce(root.left);
        int rightDiameter = bruteForce(root.right);

        return Math.max(throughRoot, Math.max(leftDiameter, rightDiameter));
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain is recomputing heights for the same nodes.
     * Each DFS call can return height while also updating the best diameter seen so far.
     * This avoids recalculating height again and again.
     */

    /* OPTIMIZED DRY RUN
     * At node 2, DFS gets heights 1 and 1, updates diameter to 2, returns height 2. Root uses that height.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Use postorder DFS so children are solved before their parent.
     * 2. Each call returns the height of its subtree.
     * 3. At each node, left depth + right depth is a possible diameter.
     * 4. Keep the largest diameter found so far.
     *
     * Time Complexity:
     * Every node is visited once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the tree height.
     */

    /* OPTIMIZED IMPLEMENTATION */
    private int bestDiameter;

    public int optimized(TreeNode root) {
        bestDiameter = 0;
        depth(root);
        return bestDiameter;
    }

    private int height(TreeNode node) {
        if (node == null) {
            // Empty child contributes height 0 to its parent.
            return 0;
        }

        return 1 + Math.max(height(node.left), height(node.right));
    }

    private int depth(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int leftDepth = depth(node.left);
        int rightDepth = depth(node.right);

        // A path through this node has leftDepth edges down one side and rightDepth down the other.
        bestDiameter = Math.max(bestDiameter, leftDepth + rightDepth);
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
