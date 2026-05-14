import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BSTIterator {

    /* PROBLEM IN SIMPLE WORDS
     * Build an iterator for a BST. next() returns the next smallest value, and hasNext() tells if a
     * next value exists.
     *
     * Sample Input:  ["BSTIterator","next","next","hasNext"] with root = [7,3,15,null,null,9,20]
     * Sample Output: [null,3,7,true]
     */

    /* SCHOOL-LEVEL INTUITION
     * A BST's inorder traversal gives values in sorted order. The iterator just returns that sorted
     * order one value at a time.
     */

    /* BRUTE FORCE INTUITION
     * Store the full inorder list first. Then next() simply moves through the list by index.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. During construction, do inorder traversal and store all values.
     * 2. Keep an index starting before the first value.
     * 3. next() moves the index and returns that value.
     * 4. hasNext() checks whether another index exists.
     *
     * Time Complexity: Constructor O(n), next O(1), hasNext O(1)
     * Space Complexity: O(n)
     */

    /* BRUTE FORCE DRY RUN
     * Inorder of [7,3,15,null,null,9,20] is [3,7,9,15,20]. next returns 3, then 7.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    static class BruteForce {
        private final List<Integer> values = new ArrayList<>();
        private int index = -1;

        BruteForce(TreeNode root) {
            inorder(root);
        }

        public int next() {
            index++;
            return values.get(index);
        }

        public boolean hasNext() {
            return index + 1 < values.size();
        }

        private void inorder(TreeNode node) {
            if (node == null) {
                return;
            }

            inorder(node.left);
            values.add(node.val);
            inorder(node.right);
        }
    }

    /* OPTIMIZED INTUITION
     * The brute force list stores values that may never be requested.
     * We do not need to store all values. Keep only the left path to the next smallest node.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Constructor pushes root and all left children.
     * 2. next() pops the top node, which is the next smallest.
     * 3. If that node has a right child, push that right child and all its left children.
     * 4. hasNext() checks if the stack has nodes.
     *
     * Time Complexity: Constructor O(h), next average O(1), hasNext O(1)
     * Space Complexity: O(h)
     */

    /* OPTIMIZED DRY RUN
     * Push 7 then 3. next pops 3. next pops 7, then pushes 15 and 9.
     */

    /* OPTIMIZED IMPLEMENTATION */
    static class Optimized {
        private final Stack<TreeNode> stack = new Stack<>();

        Optimized(TreeNode root) {
            pushLeftPath(root);
        }

        public int next() {
            TreeNode node = stack.pop();

            // The right subtree may contain the next larger value, so prepare its left path.
            if (node.right != null) {
                pushLeftPath(node.right);
            }

            return node.val;
        }

        public boolean hasNext() {
            return !stack.isEmpty();
        }

        private void pushLeftPath(TreeNode node) {
            while (node != null) {
                // Left descendants are smaller, so they must be returned before this node's right side.
                stack.push(node);
                node = node.left;
            }
        }
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
