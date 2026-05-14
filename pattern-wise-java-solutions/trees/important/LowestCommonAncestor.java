import java.util.ArrayList;
import java.util.List;

public class LowestCommonAncestor {

    /* PROBLEM IN SIMPLE WORDS
     * Find the lowest node that has both p and q as descendants.
     * A node can also be a descendant of itself.
     *
     * Sample Input:  root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
     * Sample Output: 3
     */

    /* SCHOOL-LEVEL INTUITION
     * The lowest common ancestor is the deepest meeting point on the routes from root to p and root to q.
     * If both nodes go through the same ancestor and then split, that ancestor is the answer.
     * We can think in terms of comparing root-to-node paths or asking each subtree what it contains.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * Find path from root to p and path from root to q. The last same node in both paths is LCA.
     */

    /* BRUTE FORCE DRY RUN
     * Path to 5 is [3,5]. Path to 1 is [3,1]. Last common node is 3.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Build the path from root to p.
     * 2. Build the path from root to q.
     * 3. Compare both paths from the beginning.
     * 4. The last equal node before the paths split is the answer.
     *
     * Time Complexity:
     * Finding both paths can visit the tree, so the time is O(n).
     *
     * Space Complexity:
     * The two paths and recursion stack can store nodes, so the space is O(n).
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> pathToP = new ArrayList<>();
        List<TreeNode> pathToQ = new ArrayList<>();

        findPath(root, p, pathToP);
        findPath(root, q, pathToQ);

        TreeNode answer = null;
        int index = 0;

        while (index < pathToP.size() && index < pathToQ.size() && pathToP.get(index) == pathToQ.get(index)) {
            // Keep moving while both paths share the same ancestor.
            answer = pathToP.get(index);
            index++;
        }

        return answer;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * In recursion, if current subtree contains p or q, it reports that upward. If left side finds one and right side finds the other, current node is the LCA.
     */

    /* OPTIMIZED DRY RUN
     * At root 3, left recursion finds 5 and right recursion finds 1, so 3 is the answer.
     */

    /* OPTIMIZED ALGORITHM
     * 1. If the current node is null, return null.
     * 2. If the current node is p or q, return it upward.
     * 3. Search the left subtree and the right subtree.
     * 4. If both sides return a node, current node is where p and q meet.
     * 5. If only one side returns a node, pass that node upward.
     *
     * Time Complexity:
     * Every node may be visited once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the tree height.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }
        if (root == p || root == q) {
            return root;
        }

        TreeNode left = optimized(root.left, p, q);
        TreeNode right = optimized(root.right, p, q);

        if (left != null && right != null) {
            // p and q were found on different sides, so this root is their lowest meeting point.
            return root;
        }

        if (left != null) {
            // Left side found p or q, so pass that node upward.
            return left;
        }

        // If right is null, this also correctly returns null.
        return right;
    }

    private boolean findPath(TreeNode node, TreeNode target, List<TreeNode> path) {
        if (node == null) {
            return false;
        }

        // Try including this node in the current root-to-target path.
        path.add(node);
        if (node == target) {
            return true;
        }
        if (findPath(node.left, target, path) || findPath(node.right, target, path)) {
            return true;
        }

        // This branch did not reach the target, so remove the node before backtracking.
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
