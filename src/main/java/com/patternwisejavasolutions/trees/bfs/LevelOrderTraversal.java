
package com.patternwisejavasolutions.trees.bfs;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LevelOrderTraversal {

    /* PROBLEM IN SIMPLE WORDS
     * Return the binary tree values level by level, from top to bottom.
     *
     * Sample Input:  root = [3,9,20,null,null,15,7]
     * Sample Output: [[3],[9,20],[15,7]]
     */

    /* SCHOOL-LEVEL INTUITION
     * Level order means reading the tree like rows in a classroom seating chart.
     * Finish every node in the current row before moving to the next row.
     * A queue helps because children wait their turn after the nodes already in line.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * Before learning queues, a very direct school-style idea is:
     * "First print level 1, then level 2, then level 3..."
     *
     * To do that, we can first find the height of the tree.
     * Then for each level number, we do a DFS from the root and collect only
     * nodes that are exactly on that level.
     *
     * This is real brute force because the DFS starts again from the root for
     * every level, so upper nodes are visited many times.
     */

    /* BRUTE FORCE DRY RUN
     * root = [3,9,20,null,null,15,7]
     *
     * height = 3
     * level 1: DFS collects [3]
     * level 2: DFS starts again and collects [9,20]
     * level 3: DFS starts again and collects [15,7]
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Compute tree height.
     * 2. For level from 1 to height:
     *    - start DFS from root
     *    - when remaining level becomes 1, add that node value
     * 3. Add each collected level to the answer.
     *
     * Time Complexity:
     * In the worst case, DFS repeats work for each level, so the time is O(n * h).
     *
     * Space Complexity:
     * Recursion can go as deep as the tree height, so extra space is O(h),
     * ignoring the output list.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public List<List<Integer>> bruteForce(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        int height = height(root);

        for (int levelNumber = 1; levelNumber <= height; levelNumber++) {
            List<Integer> level = new ArrayList<>();
            /*
             * This intentionally starts from root again for every level.
             * That repeated work is why this is the brute force version.
             */
            collectGivenLevel(root, levelNumber, level);
            result.add(level);
        }

        return result;
    }

    private int height(TreeNode node) {
        if (node == null) {
            return 0;
        }

        return 1 + Math.max(height(node.left), height(node.right));
    }

    private void collectGivenLevel(TreeNode node, int levelNumber, List<Integer> level) {
        if (node == null) {
            return;
        }

        if (levelNumber == 1) {
            level.add(node.val);
            return;
        }

        collectGivenLevel(node.left, levelNumber - 1, level);
        collectGivenLevel(node.right, levelNumber - 1, level);
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * BFS is already the natural optimized solution because it visits each node once and groups by level.
     */

    /* OPTIMIZED DRY RUN
     * At each while loop, queue contains exactly one level. We read levelSize first so new children wait for next level.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Keep a queue of nodes waiting to be processed.
     * 2. At the start of each loop, queue.size() tells us how many nodes are on the current level.
     * 3. Process exactly those nodes, then store that level.
     *
     * Time Complexity:
     * Each node is added and removed from the queue one time, so the time is O(n).
     *
     * Space Complexity:
     * The result stores all node values and the queue stores one level at a time, so the space is O(n).
     */

    /* OPTIMIZED IMPLEMENTATION */
    public List<List<Integer>> optimized(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                // Add the current node to the list for this exact level.
                level.add(node.val);

                if (node.left != null) {
                    // Left child belongs to the next level, so it waits in the queue.
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    // Right child also waits for the next level.
                    queue.offer(node.right);
                }
            }

            // After processing levelSize nodes, this level is complete.
            result.add(level);
        }

        return result;
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
