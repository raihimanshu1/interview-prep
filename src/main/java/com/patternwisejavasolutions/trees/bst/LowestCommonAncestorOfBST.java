package com.patternwisejavasolutions.trees.bst;

import java.util.ArrayList;
import java.util.List;

public class LowestCommonAncestorOfBST {

    /* PROBLEM IN SIMPLE WORDS
     * Find the lowest node in a BST that has both p and q as descendants. A node can be its own
     * descendant for this problem.
     *
     * Sample Input:  root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
     * Sample Output: 6
     */

    /* SCHOOL-LEVEL INTUITION
     * In a BST, small values go left and large values go right. The first node where p and q split
     * into different sides is their common meeting point.
     */

    /* BRUTE FORCE INTUITION
     * Store the path from root to p and the path from root to q. The last same node in both paths is
     * the answer.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Find and store path from root to p.
     * 2. Find and store path from root to q.
     * 3. Walk both paths while nodes are the same.
     * 4. Return the last same node.
     *
     * Time Complexity: O(h)
     * Space Complexity: O(h)
     */

    /* BRUTE FORCE DRY RUN
     * Path to 2 is [6,2]. Path to 8 is [6,8]. Last same node is 6.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> pathP = new ArrayList<>();
        List<TreeNode> pathQ = new ArrayList<>();
        findPath(root, p.val, pathP);
        findPath(root, q.val, pathQ);

        TreeNode answer = null;
        int index = 0;
        while (index < pathP.size() && index < pathQ.size() && pathP.get(index) == pathQ.get(index)) {
            answer = pathP.get(index);
            index++;
        }

        return answer;
    }

    /* OPTIMIZED INTUITION
     * Move down using the BST rule until the current node sits between p and q.
     */

    /* OPTIMIZED ALGORITHM
     * 1. If both p and q are smaller than current, move left.
     * 2. If both p and q are larger than current, move right.
     * 3. Otherwise current is where their paths split, so return it.
     *
     * Time Complexity: O(h)
     * Space Complexity: O(1)
     */

    /* OPTIMIZED DRY RUN
     * At 6, p=2 is smaller and q=8 is larger, so 6 is the LCA.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode current = root;

        while (current != null) {
            if (p.val < current.val && q.val < current.val) {
                current = current.left;
            } else if (p.val > current.val && q.val > current.val) {
                current = current.right;
            } else {
                return current;
            }
        }

        return null;
    }

    private boolean findPath(TreeNode node, int target, List<TreeNode> path) {
        if (node == null) {
            return false;
        }

        path.add(node);
        if (node.val == target) {
            return true;
        }

        // Even the brute force path search uses the BST direction to keep it simple and fast.
        if (target < node.val && findPath(node.left, target, path)) {
            return true;
        }
        if (target > node.val && findPath(node.right, target, path)) {
            return true;
        }

        path.remove(path.size() - 1);
        return false;
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
