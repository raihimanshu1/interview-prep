

package com.companywisejavasolutions.ebay.solutions;
public class BinaryTreeCameras {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    private static final int NEEDS_CAMERA = 0;
    private static final int HAS_CAMERA = 1;
    private static final int COVERED = 2;
    private int cameras;

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Put the minimum number of cameras in a binary tree so every node is watched.
     * A camera watches its parent, itself, and its children.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Work from leaves upward. If a child needs a camera, the current node is the
     * best place to put it because it can cover parent and children together.
     */
    public int minCameraCover(TreeNode root) {
        cameras = 0;
        if (dfs(root) == NEEDS_CAMERA) {
            cameras++;
        }
        return cameras;
    }

    private int dfs(TreeNode node) {
        if (node == null) return COVERED;

        int left = dfs(node.left);
        int right = dfs(node.right);

        if (left == NEEDS_CAMERA || right == NEEDS_CAMERA) {
            cameras++;
            return HAS_CAMERA;
        }

        if (left == HAS_CAMERA || right == HAS_CAMERA) {
            return COVERED;
        }

        return NEEDS_CAMERA;
    }
}
