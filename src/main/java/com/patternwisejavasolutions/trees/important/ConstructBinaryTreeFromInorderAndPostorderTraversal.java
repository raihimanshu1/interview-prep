
package com.patternwisejavasolutions.trees.important;
import java.util.HashMap;
import java.util.Map;

public class ConstructBinaryTreeFromInorderAndPostorderTraversal {

    /* PROBLEM IN SIMPLE WORDS
     * Build a binary tree from its inorder and postorder traversal arrays.
     *
     * Sample Input:  inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
     * Sample Output: [3,9,20,null,null,15,7]
     */

    /* SCHOOL-LEVEL INTUITION
     * Postorder gives the root at the end. Inorder tells which values belong to the left and right
     * sides of that root.
     */

    /* BRUTE FORCE INTUITION
     * Use the last postorder value as root, then scan inorder to split left and right parts.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. The last value in the current postorder range is the root.
     * 2. Scan inorder to find the root index.
     * 3. Values left of that index form the left subtree.
     * 4. Values right of that index form the right subtree.
     * 5. Recursively build both subtrees.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(h)
     */

    /* BRUTE FORCE DRY RUN
     * Postorder ends at 3. Inorder splits into [9] and [15,20,7], so 3 is the root.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(int[] inorder, int[] postorder) {
        return buildSlow(inorder, 0, inorder.length - 1, postorder, 0, postorder.length - 1);
    }

    /* OPTIMIZED INTUITION
     * The slow part is searching inorder. A value-to-index map finds every root position in O(1).
     */

    /* OPTIMIZED ALGORITHM
     * 1. Store every inorder value with its index in a map.
     * 2. Use postorder end as root.
     * 3. Lookup root index in inorder.
     * 4. Use left subtree size to split postorder ranges.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /* OPTIMIZED DRY RUN
     * Root 3 maps to inorder index 1. Left size is 1, so postorder left range is [9].
     *
     * Range picture:
     * inorder   = [left subtree] root [right subtree]
     * postorder = [left subtree] [right subtree] root
     *
     * If leftSize = number of nodes in the left subtree:
     * - postStart to postStart + leftSize - 1 belongs to the left subtree
     * - postStart + leftSize to postEnd - 1 belongs to the right subtree
     * - postEnd is the root
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(int[] inorder, int[] postorder) {
        Map<Integer, Integer> inorderIndex = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderIndex.put(inorder[i], i);
        }

        return buildFast(inorder, 0, inorder.length - 1, postorder, 0, postorder.length - 1, inorderIndex);
    }

    private TreeNode buildSlow(int[] inorder, int inStart, int inEnd, int[] postorder, int postStart, int postEnd) {
        if (inStart > inEnd) {
            return null;
        }

        TreeNode root = new TreeNode(postorder[postEnd]);
        int rootIndex = inStart;
        while (inorder[rootIndex] != root.val) {
            rootIndex++;
        }

        int leftSize = rootIndex - inStart;
        root.left = buildSlow(inorder, inStart, rootIndex - 1, postorder, postStart, postStart + leftSize - 1);
        root.right = buildSlow(inorder, rootIndex + 1, inEnd, postorder, postStart + leftSize, postEnd - 1);
        return root;
    }

    private TreeNode buildFast(int[] inorder, int inStart, int inEnd, int[] postorder, int postStart, int postEnd, Map<Integer, Integer> inorderIndex) {
        if (inStart > inEnd) {
            return null;
        }

        TreeNode root = new TreeNode(postorder[postEnd]);
        int rootIndex = inorderIndex.get(root.val);
        int leftSize = rootIndex - inStart;

        /*
         * Postorder layout inside this range:
         * [left subtree values][right subtree values][root]
         *
         * leftSize tells exactly how many values belong to the left subtree.
         * That is why left postorder ends at postStart + leftSize - 1,
         * and right postorder starts at postStart + leftSize.
         */
        root.left = buildFast(inorder, inStart, rootIndex - 1, postorder, postStart, postStart + leftSize - 1, inorderIndex);
        root.right = buildFast(inorder, rootIndex + 1, inEnd, postorder, postStart + leftSize, postEnd - 1, inorderIndex);
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
