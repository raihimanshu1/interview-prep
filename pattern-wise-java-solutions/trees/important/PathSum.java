public class PathSum {

    /* PROBLEM IN SIMPLE WORDS
     * Check if any path from the root to a leaf adds up to targetSum.
     *
     * Sample Input:  root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
     * Sample Output: true
     *
     * The path 5 -> 4 -> 11 -> 2 adds up to 22.
     */

    /* SCHOOL-LEVEL INTUITION
     * A valid path must start at the root and end at a leaf.
     * As we walk downward, we keep subtracting or adding the values we have used.
     * When we reach a leaf, the running total tells us whether that path matched the target.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * The most literal way is to write down every full root-to-leaf path first.
     * After we have all paths, we calculate the sum of each one.
     *
     * This matches how we might solve it on paper:
     * list all possible roads from root to leaf, then check their totals.
     */

    /* BRUTE FORCE DRY RUN
     * Paths include:
     * 5 -> 4 -> 11 -> 7  = 27
     * 5 -> 4 -> 11 -> 2  = 22
     * 5 -> 8 -> 13       = 26
     * 5 -> 8 -> 4 -> 1   = 18
     *
     * One path sums to 22, so answer is true.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Build a list of all root-to-leaf paths.
     * 2. For each path, add all values in that path.
     * 3. If any path sum equals targetSum, return true.
     * 4. If every path fails, return false.
     *
     * Time Complexity:
     * Copying paths can cost O(n * h) in the worst case.
     *
     * Space Complexity:
     * Storing all paths can cost O(n * h) in the worst case.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public boolean bruteForce(TreeNode root, int targetSum) {
        java.util.List<java.util.List<Integer>> paths = new java.util.ArrayList<>();
        collectRootToLeafPaths(root, new java.util.ArrayList<>(), paths);

        for (java.util.List<Integer> path : paths) {
            int sum = 0;

            for (int value : path) {
                sum = sum + value;
            }

            if (sum == targetSum) {
                return true;
            }
        }

        return false;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * Instead of carrying sum from top, subtract current value from target. At a leaf, we only need to check if remaining target equals leaf value.
     */

    /* OPTIMIZED DRY RUN
     * At 5, remaining becomes 17. At 4, remaining 13. At 11, remaining 2. Leaf 2 matches.
     */

    /* OPTIMIZED ALGORITHM
     * 1. If the node is null, return false.
     * 2. If the node is a leaf, check whether targetSum equals node.val.
     * 3. Otherwise subtract node.val from targetSum.
     * 4. Search the left and right children with the smaller remaining target.
     *
     * Time Complexity:
     * Every node is visited once, so the time is O(n).
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the tree height.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public boolean optimized(TreeNode root, int targetSum) {
        if (root == null) {
            return false;
        }

        if (root.left == null && root.right == null) {
            // At a leaf, the path is complete, so the remaining target must equal this value.
            return targetSum == root.val;
        }

        // Subtract current value so children only need to complete the remaining sum.
        int remaining = targetSum - root.val;
        return optimized(root.left, remaining) || optimized(root.right, remaining);
    }

    private void collectRootToLeafPaths(
        TreeNode node,
        java.util.List<Integer> currentPath,
        java.util.List<java.util.List<Integer>> paths
    ) {
        if (node == null) {
            return;
        }

        /*
         * Add this node because every path we are building must include the
         * current root-to-leaf route.
         */
        currentPath.add(node.val);

        if (node.left == null && node.right == null) {
            /*
             * We copy the path at a leaf because this is one complete road from
             * root to leaf.
             */
            paths.add(new java.util.ArrayList<>(currentPath));
        } else {
            collectRootToLeafPaths(node.left, currentPath, paths);
            collectRootToLeafPaths(node.right, currentPath, paths);
        }

        /*
         * Backtrack so the parent can try the other child path cleanly.
         */
        currentPath.remove(currentPath.size() - 1);
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
