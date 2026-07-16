package com.patternwisejavasolutions.trees.dfscore;

import java.util.LinkedList;
import java.util.Queue;

public class SameTree {

    /* PROBLEM IN SIMPLE WORDS
     * Check whether two binary trees have exactly the same structure and the same values.
     *
     * Sample Input:  p = [1,2,3], q = [1,2,3]
     * Sample Output: true
     */

    /* SCHOOL-LEVEL INTUITION
     * Two trees are the same only if every matching spot tells the same story.
     * The values must match, and missing children must also be missing in both trees.
     * After checking one pair of nodes, we check their left pair and right pair the same way.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * Two trees are same if every matching position has the same value. We can compare them level by level.
     * To keep positions aligned, we put nodes from both trees into two queues at the same time.
     */

    /* BRUTE FORCE DRY RUN
     * Compare roots 1 and 1. Compare left children 2 and 2. Compare right children 3 and 3. Everything matches.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Put the root of p in one queue and the root of q in another queue.
     * 2. Remove one node from each queue together.
     * 3. If both are null, that position matches.
     * 4. If only one is null, the structure is different.
     * 5. If values differ, the trees are different.
     * 6. Add left and right children in the same order.
     *
     * Time Complexity:
     * We compare each pair of matching positions once, so the time is O(n).
     *
     * Space Complexity:
     * The queues can store many nodes from a level, so the space is O(n).
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public boolean bruteForce(TreeNode p, TreeNode q) {
        Queue<TreeNode> first = new LinkedList<>();
        Queue<TreeNode> second = new LinkedList<>();
        first.offer(p);
        second.offer(q);

        while (!first.isEmpty()) {
            TreeNode a = first.poll();
            TreeNode b = second.poll();

            if (a == null && b == null) {
                continue;
            }
            if (a == null || b == null) {
                return false;
            }
            if (a.val != b.val) {
                return false;
            }

            // Add children in the same order so matching positions are compared together.
            first.offer(a.left);
            first.offer(a.right);
            second.offer(b.left);
            second.offer(b.right);
        }

        return true;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * Recursion matches the definition nicely: roots must match, left subtrees must match, and right subtrees must match.
     */

    /* OPTIMIZED DRY RUN
     * At node 1, values match. Then recursion checks 2 with 2 and 3 with 3. All true, so whole tree is true.
     */

    /* OPTIMIZED ALGORITHM
     * 1. If both nodes are null, this position matches.
     * 2. If only one node is null, the trees are not the same.
     * 3. If both nodes exist but values differ, the trees are not the same.
     * 4. Recursively compare left with left and right with right.
     *
     * Time Complexity:
     * Every node is checked once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the height of the tree.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public boolean optimized(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }
        if (p == null || q == null) {
            return false;
        }
        if (p.val != q.val) {
            return false;
        }

        // Both current values match, so the answer depends on both child pairs.
        return optimized(p.left, q.left) && optimized(p.right, q.right);
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
