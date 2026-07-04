
package com.patternwisejavasolutions.trees.bfs;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BinaryTreeRightSideView {

    /* PROBLEM IN SIMPLE WORDS
     * Return the values visible when we look at the binary tree from its right side.
     *
     * Sample Input:  root = [1,2,3,null,5,null,4]
     * Sample Output: [1,3,4]
     *
     * The rightmost visible node on level 1 is 1.
     * The rightmost visible node on level 2 is 3.
     * The rightmost visible node on level 3 is 4.
     */

    /* SCHOOL-LEVEL INTUITION
     * Imagine standing on the right side of the tree.
     * For each level, only the node farthest to the right blocks the nodes behind it.
     * So we inspect the tree level by level and keep the last value from each level.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * From the right side, we see the last node of each level. So level order traversal can solve it.
     * The simple approach is to first collect every level completely, then pick the last value from each level.
     */

    /* BRUTE FORCE DRY RUN
     * Levels are [1], [2,3], [5,4]. Last values are 1, 3, 4.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Traverse the tree level by level using BFS.
     * 2. Store each level as a list of values.
     * 3. For every stored level, add its last value to the answer.
     *
     * Time Complexity:
     * We visit every node once, so the time is O(n), where n is the number of nodes.
     *
     * Space Complexity:
     * We store all levels and also use a queue, so the space is O(n).
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public List<Integer> bruteForce(TreeNode root) {
        List<Integer> answer = new ArrayList<>();
        List<List<Integer>> levels = collectLevels(root);

        for (List<Integer> level : levels) {
            // The right side view wants the last value seen on this level.
            answer.add(level.get(level.size() - 1));
        }

        return answer;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * We do not need to store all levels. While processing a level, when we reach its last node, add it to answer.
     */

    /* OPTIMIZED DRY RUN
     * For level [2,3], levelSize is 2. The second node processed is 3, so add 3.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Traverse the tree level by level using a queue.
     * 2. Before processing a level, save its size.
     * 3. When processing the last node in that level, add it to the answer.
     *
     * Time Complexity:
     * Every node enters and leaves the queue once, so the time is O(n).
     *
     * Space Complexity:
     * The queue can hold many nodes from one level, so the space is O(n) in the worst case.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public List<Integer> optimized(TreeNode root) {
        List<Integer> answer = new ArrayList<>();

        if (root == null) {
            return answer;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                if (i == levelSize - 1) {
                    // This is the last node of the current level, so it is visible from the right.
                    answer.add(node.val);
                }

                if (node.left != null) {
                    // Children are added for the next level.
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    // Right child is also added for the next level.
                    queue.offer(node.right);
                }
            }
        }

        return answer;
    }

    private List<List<Integer>> collectLevels(TreeNode root) {
        List<List<Integer>> levels = new ArrayList<>();
        if (root == null) {
            return levels;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            levels.add(level);
        }
        return levels;
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
