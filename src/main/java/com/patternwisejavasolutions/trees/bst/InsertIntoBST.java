package com.patternwisejavasolutions.trees.bst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InsertIntoBST {

    /* PROBLEM IN SIMPLE WORDS
     * Insert a new value into a BST and return the root.
     *
     * Sample Input:  root = [4,2,7,1,3], val = 5
     * Sample Output: [4,2,7,1,3,5]
     */

    /* SCHOOL-LEVEL INTUITION
     * Compare the new value with each node. Smaller values go left, larger values go right. When we
     * find an empty place, put the new node there.
     */

    /* BRUTE FORCE INTUITION
     * Collect all old values, add the new one, sort them, and rebuild a valid balanced BST.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Traverse the tree and collect all values.
     * 2. Add the new value.
     * 3. Sort the values.
     * 4. Build a balanced BST from the sorted list.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */

    /* BRUTE FORCE DRY RUN
     * Values [4,2,7,1,3] plus 5 become sorted [1,2,3,4,5,7], then rebuild a BST.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(TreeNode root, int val) {
        List<Integer> values = new ArrayList<>();
        collect(root, values);
        values.add(val);
        Collections.sort(values);
        return buildBalanced(values, 0, values.size() - 1);
    }

    /* OPTIMIZED INTUITION
     * The original tree is already a BST. Walk down to the one empty child spot where the value
     * belongs and attach it there.
     */

    /* OPTIMIZED ALGORITHM
     * 1. If root is null, return a new node.
     * 2. Start from root.
     * 3. Move left if val is smaller, otherwise move right.
     * 4. When the needed child is null, attach the new node.
     * 5. Return the original root.
     *
     * Time Complexity: O(h)
     * Space Complexity: O(1)
     */

    /* OPTIMIZED DRY RUN
     * 5 > 4, move right to 7. 5 < 7 and left is empty, so attach 5 as 7.left.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        TreeNode current = root;
        while (true) {
            if (val < current.val) {
                if (current.left == null) {
                    current.left = new TreeNode(val);
                    break;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new TreeNode(val);
                    break;
                }
                current = current.right;
            }
        }

        return root;
    }

    private void collect(TreeNode node, List<Integer> values) {
        if (node == null) {
            return;
        }

        values.add(node.val);
        collect(node.left, values);
        collect(node.right, values);
    }

    private TreeNode buildBalanced(List<Integer> values, int left, int right) {
        if (left > right) {
            return null;
        }

        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(values.get(mid));
        root.left = buildBalanced(values, left, mid - 1);
        root.right = buildBalanced(values, mid + 1, right);
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
