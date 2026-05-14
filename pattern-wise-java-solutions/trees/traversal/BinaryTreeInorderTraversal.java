import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryTreeInorderTraversal {

    /* PROBLEM IN SIMPLE WORDS
     * Return the values of a binary tree in inorder order: left, root, right.
     *
     * Sample Input:  root = [1,null,2,3]
     * Sample Output: [1,3,2]
     */

    /* SCHOOL-LEVEL INTUITION
     * Visit the whole left side first, then write the current value, then visit the right side.
     * For a BST this would read numbers in sorted order, but for any binary tree it simply follows
     * the left-root-right visiting rule.
     */

    /* BRUTE FORCE INTUITION
     * Recursion naturally follows the rule: left, node, right.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. If node is null, stop.
     * 2. Traverse left subtree.
     * 3. Add current node value.
     * 4. Traverse right subtree.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */

    /* BRUTE FORCE DRY RUN
     * For [1,null,2,3], visit 1, then go right to 2, visit its left 3, then 2.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public List<Integer> bruteForce(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    /* OPTIMIZED INTUITION
     * Recursion uses the call stack to remember unfinished parent nodes.
     * The optimized version makes that memory visible with our own stack.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Push nodes while moving left.
     * 2. Pop a node, add its value.
     * 3. Move to its right child.
     * 4. Repeat until no node or stack remains.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */

    /* OPTIMIZED DRY RUN
     * Push 1, pop 1, move right to 2, push 2 then 3, pop 3, pop 2.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public List<Integer> optimized(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                // Keep left nodes waiting until their left subtree is done.
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            result.add(current.val);
            current = current.right;
        }

        return result;
    }

    private void inorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            // Null is the recursion base case: there is no subtree to add.
            return;
        }

        inorder(node.left, result);
        result.add(node.val);
        inorder(node.right, result);
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
