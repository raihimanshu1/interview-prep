import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BinaryTreeZigzagLevelOrderTraversal {

    /* PROBLEM IN SIMPLE WORDS
     * Return level order traversal, but alternate directions: left-to-right, then right-to-left.
     *
     * Sample Input:  root = [3,9,20,null,null,15,7]
     * Sample Output: [[3],[20,9],[15,7]]
     */

    /* SCHOOL-LEVEL INTUITION
     * Read one row at a time. The first row reads normally, the next row reads backwards, and then
     * the direction keeps switching.
     */

    /* BRUTE FORCE INTUITION
     * Do normal level order traversal. For every other level, reverse the list for that level.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Use a queue to process nodes level by level.
     * 2. Store each level from left to right.
     * 3. Reverse the level list when the direction should be right to left.
     * 4. Add the level to the answer.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /* BRUTE FORCE DRY RUN
     * Level 0 is [3]. Level 1 is [9,20], reverse it to [20,9]. Level 2 stays [15,7].
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public List<List<Integer>> bruteForce(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // Freeze this row; children added below belong to the next row.
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left != null) {
                    // Children are still enqueued left-to-right; only the output direction changes.
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    // This child waits for the next level, not the current zigzag row.
                    queue.offer(node.right);
                }
            }

            if (!leftToRight) {
                // Brute force records the row normally, then pays a second pass to reverse it.
                reverse(level);
            }
            result.add(level);
            leftToRight = !leftToRight;
        }

        return result;
    }

    /* OPTIMIZED INTUITION
     * Instead of reversing later, place values directly at the front or back of a linked list.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Use BFS with a queue.
     * 2. For each level, use a LinkedList for values.
     * 3. Add to the end for left-to-right levels.
     * 4. Add to the front for right-to-left levels.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /* OPTIMIZED DRY RUN
     * Level [9,20] while reversed: add 9 to front -> [9], add 20 to front -> [20,9].
     */

    /* OPTIMIZED IMPLEMENTATION */
    public List<List<Integer>> optimized(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // Process exactly one tree level before flipping direction.
            LinkedList<Integer> level = new LinkedList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (leftToRight) {
                    // Normal row: values appear in the same order the queue gives them.
                    level.addLast(node.val);
                } else {
                    // Adding to the front gives the reversed order without a second pass.
                    level.addFirst(node.val);
                }

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            result.add(level);
            leftToRight = !leftToRight;
        }

        return result;
    }

    private void reverse(List<Integer> values) {
        int left = 0;
        int right = values.size() - 1;
        while (left < right) {
            int temp = values.get(left);
            values.set(left, values.get(right));
            values.set(right, temp);
            left++;
            right--;
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
