
package com.patternwisejavasolutions.trees.traversal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryTreePreorderTraversal {

    /* PROBLEM IN SIMPLE WORDS
     * Return the values of a binary tree in preorder order: root, left, right.
     *
     * Sample Input:  root = [1,null,2,3]
     * Sample Output: [1,2,3]
     */

    /* SCHOOL-LEVEL INTUITION
     * Write the current node first, then explore the left side, then the right side.
     * This is useful when the parent should be handled before its children.
     */

    /* BRUTE FORCE INTUITION
     * Recursion directly matches preorder: visit now, then left, then right.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. If node is null, stop.
     * 2. Add current node value.
     * 3. Traverse left subtree.
     * 4. Traverse right subtree.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */

    /* BRUTE FORCE DRY RUN
     * For [1,null,2,3], visit 1, then right node 2, then 2's left child 3.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public List<Integer> bruteForce(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorder(root, result);
        return result;
    }

    /* OPTIMIZED INTUITION
     * Recursion automatically remembers the next subtree to visit.
     * A stack lets us do that ourselves. Push right before left so left is processed first.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Push root.
     * 2. Pop a node and add its value.
     * 3. Push right child first.
     * 4. Push left child second.
     * 5. Repeat until stack is empty.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */

    /* OPTIMIZED DRY RUN
     * Pop 1, push 2. Pop 2, push nothing right, push 3. Pop 3.
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

            // Stack is last-in-first-out, so right must wait below left.
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }

        return result;
    }

    private void preorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            // Base case: an empty child contributes no values.
            return;
        }

        result.add(node.val);
        preorder(node.left, result);
        preorder(node.right, result);
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
