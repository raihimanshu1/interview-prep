package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Maximum Sum Circular Subarray
 * (Problem number: 26 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given a circular integer array nums, return the maximum possible sum of a
 * subarray in nums. The subarray can wrap around from end to beginning.
 *
 * Example 1:
 * Input: nums = [1, -2, 3, -2]
 * Output: 3
 * Explanation: Subarray [3] has maximum sum 3.
 *
 * Example 2:
 * Input: nums = [5, -3, 5]
 * Output: 10
 * Explanation: Circular subarray [5, 5] has maximum sum 10.
 *
 * Example 3:
 * Input: nums = [-3, -2, -1]
 * Output: -1
 * Explanation: Single element -1 is the best.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 3 * 10^4
 * - -3 * 10^4 <= nums[i] <= 3 * 10^4
 *
 * APPROACH:
 * OPTIMIZED: O(n) - Max of: (Kadane for normal) and (totalSum - minSubarraySum)
 */

public class LS09_MaximumCircularSubarray {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] nums = {1, -2, 3, -2};

        // 2. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int result = maxSubarraySumCircular(nums);
        System.out.println("Max Circular Subarray Sum: " + result); // Expected: 3

        // 3. Test all positive.
        int[] nums2 = {5, -3, 5};
        System.out.println("Wrap-around: " + maxSubarraySumCircular(nums2)); // Expected: 10

        // 4. Test all negative.
        int[] nums3 = {-3, -2, -1};
        System.out.println("All Negative: " + maxSubarraySumCircular(nums3)); // Expected: -1
    }

    // -------------------------------------------------------------------------
    // KADANE'S ALGORITHM HELPER
    // Returns maximum subarray sum for the given array.
    // -------------------------------------------------------------------------
    public static int kadane(int[] nums) {
        int n = nums.length;
        int maxSum = nums[0];
        int currentSum = nums[0];

        // 1. Start from the second element.
        for (int i = 1; i < n; i++) {
            // 2. Either extend the subarray or start fresh at current element.
            currentSum = Math.max(nums[i], currentSum + nums[i]);

            // 3. Update global max.
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: The maximum circular subarray sum is the max of:
    //       Case A: Max subarray (Kadane on original array)
    //       Case B: Wrap-around subarray = totalSum - minSubarraySum
    //       Special case: If all numbers are negative, Case B equals totalSum
    //       which is less than Case A. So we just take Case A.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int maxSubarraySumCircular(int[] nums) {
        int n = nums.length;
        int totalSum = 0;

        // 1. Calculate total sum of the array.
        for (int i = 0; i < n; i++) {
            totalSum += nums[i];
        }

        // 2. Find maximum subarray sum using Kadane (non-circular case).
        int maxSum = kadane(nums);

        // 3. Find minimum subarray sum.
        //    To get min subarray sum, we can flip signs and use Kadane,
        //    OR we can run a Kadane-like pass tracking min sum.
        int minSum = nums[0];
        int currentMin = nums[0];
        for (int i = 1; i < n; i++) {
            currentMin = Math.min(nums[i], currentMin + nums[i]);
            minSum = Math.min(minSum, currentMin);
        }

        // 4. The wrap-around sum is totalSum - minSum.
        //    But if all numbers are negative, minSum == totalSum, which gives 0.
        //    We must not return 0 for all-negative arrays; return maxSum instead.
        if (minSum == totalSum) {
            return maxSum; // All numbers are negative.
        }

        // 5. Return the maximum of non-circular and circular cases.
        return Math.max(maxSum, totalSum - minSum);
    }
}