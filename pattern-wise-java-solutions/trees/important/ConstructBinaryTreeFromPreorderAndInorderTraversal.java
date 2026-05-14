import java.util.HashMap;
import java.util.Map;

public class ConstructBinaryTreeFromPreorderAndInorderTraversal {

    /* PROBLEM IN SIMPLE WORDS
     * Build a binary tree from its preorder and inorder traversal arrays.
     *
     * Sample Input:  preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
     * Sample Output: [3,9,20,null,null,15,7]
     */

    /* SCHOOL-LEVEL INTUITION
     * Preorder gives the root first.
     * Inorder shows which values sit on the left of that root and which values sit on the right.
     * Once we split those two sides, each side becomes the same smaller building problem.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * Preorder tells root first. Inorder tells what belongs to left and right of root.
     * The direct method scans the inorder array each time to find the root position.
     */

    /* BRUTE FORCE DRY RUN
     * Preorder root is 3. In inorder, 3 splits [9] as left and [15,20,7] as right.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. The first value in the current preorder range is the root.
     * 2. Scan the current inorder range to find that root value.
     * 3. Values left of the root in inorder form the left subtree.
     * 4. Values right of the root in inorder form the right subtree.
     * 5. Recursively build both subtrees.
     *
     * Time Complexity:
     * In the worst case, scanning inorder for every node can take O(n^2) time.
     *
     * Space Complexity:
     * Recursion uses O(h) stack space, where h is the tree height.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(int[] preorder, int[] inorder) {
        return buildSlow(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1);
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * Scanning inorder every time is repeated work. Store value -> inorder index in a map so each root split is O(1).
     */

    /* OPTIMIZED DRY RUN
     * Root 3 lookup gives index 1 immediately. Left size is 1, so preorder ranges are easy to split.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Store each inorder value and its index in a map.
     * 2. Use preorder to choose the root.
     * 3. Use the map to find the root index in O(1).
     * 4. Use the left subtree size to split preorder ranges correctly.
     *
     * Time Complexity:
     * The map lets us find each root position once, so the time is O(n).
     *
     * Space Complexity:
     * The map stores n values and recursion uses stack space, so the space is O(n).
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(int[] preorder, int[] inorder) {
        Map<Integer, Integer> inorderIndex = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            // Save where each value appears so we do not scan again later.
            inorderIndex.put(inorder[i], i);
        }

        return buildFast(preorder, 0, preorder.length - 1, 0, inorder.length - 1, inorderIndex);
    }

    private TreeNode buildSlow(int[] preorder, int preStart, int preEnd, int[] inorder, int inStart, int inEnd) {
        if (preStart > preEnd) {
            return null;
        }

        // Preorder always gives the root before its left and right subtrees.
        TreeNode root = new TreeNode(preorder[preStart]);
        int rootIndex = inStart;
        while (inorder[rootIndex] != root.val) {
            rootIndex++;
        }

        // Everything before rootIndex in inorder belongs to the left subtree.
        int leftSize = rootIndex - inStart;
        root.left = buildSlow(preorder, preStart + 1, preStart + leftSize, inorder, inStart, rootIndex - 1);
        root.right = buildSlow(preorder, preStart + leftSize + 1, preEnd, inorder, rootIndex + 1, inEnd);
        return root;
    }

    private TreeNode buildFast(int[] preorder, int preStart, int preEnd, int inStart, int inEnd, Map<Integer, Integer> inorderIndex) {
        if (preStart > preEnd) {
            // No preorder values remain for this subtree range.
            return null;
        }

        TreeNode root = new TreeNode(preorder[preStart]);
        int rootIndex = inorderIndex.get(root.val);

        // leftSize tells us how many preorder values belong to the left subtree.
        int leftSize = rootIndex - inStart;
        /*
         * Preorder layout inside this range:
         * [root][left subtree values][right subtree values]
         *
         * leftSize is what lets us cut the preorder range without guessing.
         */
        root.left = buildFast(preorder, preStart + 1, preStart + leftSize, inStart, rootIndex - 1, inorderIndex);
        root.right = buildFast(preorder, preStart + leftSize + 1, preEnd, rootIndex + 1, inEnd, inorderIndex);
        return root;
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
