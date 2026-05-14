import java.util.LinkedList;
import java.util.Queue;

public class SymmetricTree {

    /* PROBLEM IN SIMPLE WORDS
     * Check whether a binary tree is a mirror of itself.
     *
     * Sample Input:  root = [1,2,2,3,4,4,3]
     * Sample Output: true
     */

    /* SCHOOL-LEVEL INTUITION
     * Symmetry means the left side is a mirror copy of the right side.
     * The outside pair must match, and the inside pair must match.
     * We compare nodes in pairs instead of looking at one subtree alone.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * Symmetry means left side and right side match in mirror order. We can compare pairs using a queue.
     */

    /* BRUTE FORCE DRY RUN
     * Compare root.left 2 with root.right 2. Then compare left.left 3 with right.right 3 and left.right 4 with right.left 4.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Put root.left and root.right into a queue as the first pair.
     * 2. Remove two nodes at a time.
     * 3. If both are null, that pair is symmetric.
     * 4. If only one is null or their values differ, return false.
     * 5. Add outer children as a pair and inner children as a pair.
     *
     * Time Complexity:
     * Every node is compared once, so the time is O(n).
     *
     * Space Complexity:
     * The queue can hold many node pairs from a level, so the space is O(n).
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public boolean bruteForce(TreeNode root) {
        if (root == null) {
            return true;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root.left);
        queue.offer(root.right);

        while (!queue.isEmpty()) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();

            if (left == null && right == null) {
                continue;
            }
            if (left == null || right == null || left.val != right.val) {
                return false;
            }

            // Mirror comparison: outside with outside, then inside with inside.
            queue.offer(left.left);
            queue.offer(right.right);
            queue.offer(left.right);
            queue.offer(right.left);
        }

        return true;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * Recursion can ask: are these two subtrees mirrors? Their root values must match, and outer children must match, inner children must match.
     */

    /* OPTIMIZED DRY RUN
     * For two 2 nodes, compare 3 with 3 on outside and 4 with 4 on inside.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Compare the left and right subtrees with a helper.
     * 2. If both nodes are null, they match.
     * 3. If only one is null, they do not match.
     * 4. Values must be equal.
     * 5. Outer children must mirror each other, and inner children must mirror each other.
     *
     * Time Complexity:
     * Every node is visited once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the tree height.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public boolean optimized(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isMirror(root.left, root.right);
    }

    private boolean isMirror(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (left.val != right.val) {
            return false;
        }

        // Outer pair: left.left with right.right. Inner pair: left.right with right.left.
        return isMirror(left.left, right.right) && isMirror(left.right, right.left);
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
