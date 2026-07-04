
package com.patternwisejavasolutions.trees.bst;
import java.util.ArrayList;
import java.util.List;

public class DeleteNodeInBST {

    /* PROBLEM IN SIMPLE WORDS
     * Delete a node with the given key from a BST and return the root of the updated BST.
     *
     * Sample Input:  root = [5,3,6,2,4,null,7], key = 3
     * Sample Output: [5,4,6,2,null,null,7]
     */

    /* SCHOOL-LEVEL INTUITION
     * To remove a value, keep all other values. If the node has two children, replace it with the
     * smallest value from its right side, because that value is just bigger than everything on the left.
     */

    /* BRUTE FORCE INTUITION
     * Collect every value except the key, then rebuild a valid BST from the remaining sorted values.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Traverse the tree in inorder to collect sorted values.
     * 2. Skip the first value equal to key.
     * 3. Build a balanced BST from the remaining values.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /* BRUTE FORCE DRY RUN
     * Inorder [2,3,4,5,6,7], remove 3, rebuild from [2,4,5,6,7].
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(TreeNode root, int key) {
        List<Integer> values = new ArrayList<>();
        collectExceptKey(root, key, values, new boolean[] {false});
        return buildBalanced(values, 0, values.size() - 1);
    }

    /* OPTIMIZED INTUITION
     * The brute force version rebuilds the whole tree even though only one spot changes.
     * Use the BST rule to find the key quickly. When found, fix only the affected links.
     */

    /* OPTIMIZED ALGORITHM
     * 1. If key is smaller than root, delete from left subtree.
     * 2. If key is larger than root, delete from right subtree.
     * 3. If found and it has one or zero children, return the child that remains.
     * 4. If found and it has two children, copy the inorder successor's value.
     * 5. Delete that successor from the right subtree.
     *
     * Time Complexity: O(h)
     * Space Complexity: O(h)
     */

    /* OPTIMIZED DRY RUN
     * Delete 3. Node 3 has children 2 and 4, so replace 3 with successor 4, then remove old 4.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(TreeNode root, int key) {
        if (root == null) {
            // Key was not found in this branch.
            return null;
        }

        if (key < root.val) {
            root.left = optimized(root.left, key);
        } else if (key > root.val) {
            root.right = optimized(root.right, key);
        } else {
            if (root.left == null) {
                // No left child: the right child can move up into this node's place.
                return root.right;
            }
            if (root.right == null) {
                // No right child: the left child can move up into this node's place.
                return root.left;
            }

            TreeNode successor = findMinimum(root.right);
            // Successor is the smallest larger value, so replacing with it keeps BST order valid.
            root.val = successor.val;
            root.right = optimized(root.right, successor.val);
        }

        return root;
    }

    private void collectExceptKey(TreeNode node, int key, List<Integer> values, boolean[] removed) {
        if (node == null) {
            return;
        }

        collectExceptKey(node.left, key, values, removed);
        if (node.val == key && !removed[0]) {
            removed[0] = true;
        } else {
            values.add(node.val);
        }
        collectExceptKey(node.right, key, values, removed);
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

    private TreeNode findMinimum(TreeNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
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
