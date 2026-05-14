public class BalancedBinaryTree {

    /* PROBLEM IN SIMPLE WORDS
     * Check whether every node has left and right subtree heights differing by at most 1.
     *
     * Sample Input:  root = [3,9,20,null,null,15,7]
     * Sample Output: true
     */

    /* SCHOOL-LEVEL INTUITION
     * A tree is balanced when no node has one side much taller than the other.
     * So every node needs two facts: the height of its left subtree and the height of its right subtree.
     * If any difference is more than 1, the whole tree is not balanced.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * A direct way is: for every node, calculate left height and right height, then check children too.
     */

    /* BRUTE FORCE DRY RUN
     * At root 3, left height 1 and right height 2, difference 1. Then check node 9 and node 20.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. If the node is null, it is balanced.
     * 2. Calculate the height of the left subtree.
     * 3. Calculate the height of the right subtree.
     * 4. If the height difference is more than 1, return false.
     * 5. Recursively check that the left and right subtrees are also balanced.
     *
     * Time Complexity:
     * Height is recalculated for many nodes. In a skewed tree this can become O(n^2).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the height of the tree.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public boolean bruteForce(TreeNode root) {
        if (root == null) {
            return true;
        }

        int leftHeight = height(root.left);
        int rightHeight = height(root.right);

        if (Math.abs(leftHeight - rightHeight) > 1) {
            // This node breaks the balanced-tree rule.
            return false;
        }

        // Current node is balanced, so both child subtrees must also be balanced.
        return bruteForce(root.left) && bruteForce(root.right);
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain is recalculating the same subtree heights again and again.
     * Instead, each subtree can return its height once. If a subtree is already unbalanced,
     * return -1 as a warning signal.
     */

    /* OPTIMIZED DRY RUN
     * Node 20 returns height 2. Node 9 returns height 1. Root sees difference 1 and returns height 3.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Use postorder DFS so children report their heights first.
     * 2. If a child subtree is already unbalanced, return -1.
     * 3. If current left/right height difference is more than 1, return -1.
     * 4. Otherwise return the current subtree height.
     *
     * Time Complexity:
     * Every node computes its height once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the tree height.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public boolean optimized(TreeNode root) {
        return checkHeight(root) != -1;
    }

    private int height(TreeNode node) {
        if (node == null) {
            // Empty subtree has height 0 and is balanced.
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    private int checkHeight(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int leftHeight = checkHeight(node.left);
        if (leftHeight == -1) {
            // Left subtree is already unbalanced, so keep passing the warning upward.
            return -1;
        }
        int rightHeight = checkHeight(node.right);
        if (rightHeight == -1) {
            // Right subtree is already unbalanced.
            return -1;
        }
        if (Math.abs(leftHeight - rightHeight) > 1) {
            // Current node itself is unbalanced.
            return -1;
        }

        return 1 + Math.max(leftHeight, rightHeight);
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
