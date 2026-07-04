
package com.patternwisejavasolutions.trees.bst;
public class SearchInBST {

    /* PROBLEM IN SIMPLE WORDS
     * Find the node with the given value in a BST. Return that node, or null if it does not exist.
     *
     * Sample Input:  root = [4,2,7,1,3], val = 2
     * Sample Output: [2,1,3]
     */

    /* SCHOOL-LEVEL INTUITION
     * In a BST, smaller values are on the left and bigger values are on the right. So each comparison
     * tells us which half to ignore.
     */

    /* BRUTE FORCE INTUITION
     * Ignore the BST property and search the whole tree like a normal binary tree.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. If root is null, return null.
     * 2. If root value matches, return root.
     * 3. Search left subtree.
     * 4. If not found, search right subtree.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */

    /* BRUTE FORCE DRY RUN
     * Check 4, then search left. Check 2 and return that subtree.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(TreeNode root, int val) {
        if (root == null || root.val == val) {
            return root;
        }

        TreeNode leftAnswer = bruteForce(root.left, val);
        if (leftAnswer != null) {
            return leftAnswer;
        }
        return bruteForce(root.right, val);
    }

    /* OPTIMIZED INTUITION
     * Use the BST rule to move only left or only right at each step.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Start at root.
     * 2. If value matches, return current node.
     * 3. If val is smaller, move left.
     * 4. If val is larger, move right.
     * 5. Return null if the search falls off the tree.
     *
     * Time Complexity: O(h)
     * Space Complexity: O(1)
     */

    /* OPTIMIZED DRY RUN
     * 2 is smaller than 4, move left. Current is 2, so return it.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(TreeNode root, int val) {
        TreeNode current = root;

        while (current != null) {
            if (current.val == val) {
                return current;
            }

            // The BST ordering lets us discard one whole side each time.
            if (val < current.val) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
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
