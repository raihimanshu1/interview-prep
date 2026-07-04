
package com.patternwisejavasolutions.trees.traversal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class BinaryTreePostorderTraversal {

    /* PROBLEM IN SIMPLE WORDS
     * Return the values of a binary tree in postorder order: left, right, root.
     *
     * Sample Input:  root = [1,null,2,3]
     * Sample Output: [3,2,1]
     */

    /* SCHOOL-LEVEL INTUITION
     * Finish both children first. Only after the children are done do we write the parent.
     * That is why postorder often fits problems where a parent needs answers from its children.
     */

    /* BRUTE FORCE INTUITION
     * Recursion is the clearest way: left child, right child, then current node.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. If node is null, stop.
     * 2. Traverse left subtree.
     * 3. Traverse right subtree.
     * 4. Add current node value.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */

    /* BRUTE FORCE DRY RUN
     * For [1,null,2,3], visit 3 first, then 2, then 1.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public List<Integer> bruteForce(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorder(root, result);
        return result;
    }

    /* OPTIMIZED INTUITION
     * True left-right-root is awkward with one stack.
     * But root-right-left is easy with one stack, and reversing that list gives left-right-root.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Push root.
     * 2. Pop node and add it.
     * 3. Push left child, then right child.
     * 4. Reverse the collected order.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /* OPTIMIZED DRY RUN
     * Collect [1,2,3] as root-right-left. Reverse it into [3,2,1].
     */

    /* OPTIMIZED IMPLEMENTATION */
    public List<Integer> optimized(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);

            // This builds root-right-left after reversal because left is pushed first.
            if (node.left != null) {
                stack.push(node.left);
            }
            if (node.right != null) {
                stack.push(node.right);
            }
        }

        Collections.reverse(result);
        return result;
    }

    private void postorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            // Base case: no node means no value to append.
            return;
        }

        postorder(node.left, result);
        postorder(node.right, result);
        result.add(node.val);
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
