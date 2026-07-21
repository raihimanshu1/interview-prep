package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Maximum Product Subarray
 * (Problem number: 30 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an integer array nums, find the maximum product of a contiguous subarray.
 *
 * Example 1:
 * Input: nums = [2, 3, -2, 4]
 * Output: 6
 * Explanation: Subarray [2, 3] has max product 6.
 *
 * Example 2:
 * Input: nums = [-2, 0, -1]
 * Output: 0
 * Explanation: No product greater than 0.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 2 * 10^4
 * - -10 <= nums[i] <= 10
 *
 * APPROACH:
 * OPTIMIZED: O(n) - Track max and min so far (flip on negative)
 */

public class LS13_MaximumProductSubarray {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] nums = {2, 3, -2, 4};

        // 2. --- OPTIMIZED APPROACH ---
        int result = maxProduct(nums);
        System.out.println("Max Product: " + result); // Expected: 6

        // 3. Test with zero and negatives.
        int[] nums2 = {-2, 0, -1};
        System.out.println("With Zero: " + maxProduct(nums2)); // Expected: 0

        // 4. Test all negatives.
        int[] nums3 = {-2, -3, -1, -4};
        System.out.println("All Negative: " + maxProduct(nums3)); // Expected: 12
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: Track both maxProduct and minProduct at each step.
    //       Since multiplying by a negative flips max/min, we keep both.
    //       The minProduct can become maxProduct if we encounter another negative.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int maxProduct(int[] nums) {
        int n = nums.length;
        int maxProduct = nums[0]; // 1. Track the overall maximum product.
        int minProduct = nums[0]; // 2. Track the minimum product (for negative flips).
        int result = nums[0]; // 3. Store the best result so far.

        // 4. Start from the second element.
        for (int i = 1; i < n; i++) {
            int current = nums[i];

            // 5. If current is negative, max and min swap roles.
            //    Store current maxProduct before updating.
            if (current < 0) {
                int temp = maxProduct;
                maxProduct = minProduct;
                minProduct = temp;
            }

            // 6. Update maxProduct: either start fresh at current or extend previous.
            maxProduct = Math.max(current, maxProduct * current);

            // 7. Update minProduct: either start fresh at current or extend previous.
            minProduct = Math.min(current, minProduct * current);

            // 8. Update result if maxProduct is better.
            result = Math.max(result, maxProduct);
        }

        return result;
    }
}