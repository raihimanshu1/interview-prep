
package com.patternwisejavasolutions.trees.bst;
import java.util.Arrays;

public class ConvertSortedArrayToBST {

    /* PROBLEM IN SIMPLE WORDS
     * Convert a sorted array into a height-balanced Binary Search Tree.
     *
     * Sample Input:  nums = [-10,-3,0,5,9]
     * Sample Output: [0,-10,5,null,-3,null,9]
     */

    /* SCHOOL-LEVEL INTUITION
     * The middle number should become the root, because numbers on its left are smaller and numbers
     * on its right are larger. Choosing the middle keeps both sides balanced.
     */

    /* BRUTE FORCE INTUITION
     * Keep copying the left and right parts into smaller arrays, then solve each smaller array.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. If the array is empty, return null.
     * 2. Pick the middle value as root.
     * 3. Copy values before middle into a left array.
     * 4. Copy values after middle into a right array.
     * 5. Recursively build both sides.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(n log n)
     */

    /* BRUTE FORCE DRY RUN
     * [-10,-3,0,5,9] picks 0. Left array is [-10,-3], right array is [5,9].
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public TreeNode bruteForce(int[] nums) {
        if (nums.length == 0) {
            return null;
        }

        int mid = nums.length / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = bruteForce(Arrays.copyOfRange(nums, 0, mid));
        root.right = bruteForce(Arrays.copyOfRange(nums, mid + 1, nums.length));
        return root;
    }

    /* OPTIMIZED INTUITION
     * We do not need to copy arrays. Use start and end indexes to describe the current part.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Build using a start and end index.
     * 2. Pick middle index as root.
     * 3. Recursively build start to mid - 1 as left subtree.
     * 4. Recursively build mid + 1 to end as right subtree.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(log n)
     */

    /* OPTIMIZED DRY RUN
     * start=0,end=4 gives mid=2 and root 0. Then build 0..1 and 3..4.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public TreeNode optimized(int[] nums) {
        return build(nums, 0, nums.length - 1);
    }

    private TreeNode build(int[] nums, int left, int right) {
        if (left > right) {
            // Empty index range means this child branch has no node.
            return null;
        }

        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        // Values before mid are smaller, so they must form the left subtree.
        root.left = build(nums, left, mid - 1);
        // Values after mid are larger, so they must form the right subtree.
        root.right = build(nums, mid + 1, right);
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
