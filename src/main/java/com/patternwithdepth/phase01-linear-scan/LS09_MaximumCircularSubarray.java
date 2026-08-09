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
 *
 *
 * "If the maximum subarray doesn't need to wrap from the end to the beginning, normal Kadane already gives the answer.
 * If the maximum subarray does wrap, then we compute it as totalSum - minimumSubarray.
 * Finally, we take the maximum of these two cases."
 *
 *
 */

public class LS09_MaximumCircularSubarray {

        public static void main(String[] args) {

            // Test 1: Maximum subarray does NOT wrap around.
            int[] nums1 = {1, -2, 3, -2};
            System.out.println("Example 1 : " + maxSubarraySumCircular(nums1)); // Expected: 3

            // Test 2: Maximum subarray wraps around.
            int[] nums2 = {5, -3, 5};
            System.out.println("Example 2 : " + maxSubarraySumCircular(nums2)); // Expected: 10

            // Test 3: All numbers are negative.
            int[] nums3 = {-3, -2, -1};
            System.out.println("Example 3 : " + maxSubarraySumCircular(nums3)); // Expected: -1
        }

        /*
         * ============================================================================
         * KADANE'S ALGORITHM (Maximum Subarray Sum)
         * ============================================================================
         *
         * Returns the maximum sum of any contiguous subarray.
         *
         * IDEA
         * ----
         * At every index we ask only one question:
         *
         * "Should I continue the previous subarray
         *  OR
         *  should I start a brand new subarray from the current element?"
         *
         * We choose whichever gives the larger sum.
         *
         * Time  : O(n)
         * Space : O(1)
         * ============================================================================
         */
        public static int maxKadane(int[] nums) {

            int currentSum = nums[0];
            int maxSum = nums[0];

            for (int i = 1; i < nums.length; i++) {

                /*
                 * Option 1:
                 * Continue (extend) the previous subarray.
                 *
                 * Sum = currentSum + nums[i]
                 *
                 * Option 2:
                 * Ignore everything before this index and start a new subarray.
                 *
                 * Sum = nums[i]
                 *
                 * Whichever gives the larger sum becomes the maximum subarray
                 * ending at the current index.
                 */
                if (currentSum + nums[i] > nums[i]) {
                    currentSum = currentSum + nums[i]; // Extend the previous subarray.
                } else {
                    currentSum = nums[i]; // Start a new subarray.
                }

                /*
                 * currentSum represents the maximum subarray ending at index i.
                 *
                 * Compare it with the best answer found anywhere so far.
                 */
                if (currentSum > maxSum) {
                    maxSum = currentSum;
                }
            }

            return maxSum;
        }

        /*
         * ============================================================================
         * MINIMUM KADANE
         * ============================================================================
         *
         * This is exactly the same algorithm as Maximum Kadane.
         *
         * The only difference is:
         *
         * Instead of finding the maximum contiguous subarray,
         * we find the minimum contiguous subarray.
         *
         * Why?
         *
         * Because the wrapped circular subarray can be viewed as:
         *
         *      Whole Array
         *            -
         * Minimum Contiguous Subarray
         *
         * Time  : O(n)
         * Space : O(1)
         * ============================================================================
         */
        public static int minKadane(int[] nums) {

            int currentSum = nums[0];
            int minSum = nums[0];

            for (int i = 1; i < nums.length; i++) {

                /*
                 * Option 1:
                 * Continue (extend) the previous minimum subarray.
                 *
                 * Sum = currentSum + nums[i]
                 *
                 * Option 2:
                 * Ignore everything before this index and start a new minimum
                 * subarray.
                 *
                 * Sum = nums[i]
                 *
                 * Whichever gives the smaller sum becomes the minimum subarray
                 * ending at the current index.
                 */
                if (currentSum + nums[i] < nums[i]) {
                    currentSum = currentSum + nums[i]; // Extend the previous minimum subarray.
                } else {
                    currentSum = nums[i]; // Start a new minimum subarray.
                }

                /*
                 * currentSum represents the minimum subarray ending at index i.
                 *
                 * Compare it with the smallest answer found anywhere so far.
                 */
                if (currentSum < minSum) {
                    minSum = currentSum;
                }
            }

            return minSum;
        }

        /*
         * ============================================================================
         * MAXIMUM SUM CIRCULAR SUBARRAY
         * ============================================================================
         *
         * Since the array is circular, there are only TWO possible answers.
         *
         * --------------------------------------------------------------------------
         * CASE 1 : Maximum subarray DOES NOT wrap.
         * --------------------------------------------------------------------------
         *
         * Example:
         *
         *      [1, -2, 3, -2]
         *
         * Best Subarray:
         *
         *      [3]
         *
         * This is simply the normal Maximum Subarray problem.
         *
         * We solve it using Kadane's Algorithm.
         *
         *
         * --------------------------------------------------------------------------
         * CASE 2 : Maximum subarray WRAPS.
         * --------------------------------------------------------------------------
         *
         * Example:
         *
         *      [5, -3, 5]
         *
         * Best Circular Subarray:
         *
         *      5 | 5
         *
         * Instead of directly finding the wrapped subarray,
         * think about what we are NOT selecting.
         *
         * We skip one continuous middle portion.
         *
         * Therefore,
         *
         *      Circular Sum
         *      =
         *      Total Array Sum
         *             -
         *      Minimum Contiguous Subarray Sum
         *
         * Example:
         *
         *      Total Sum = 7
         *      Minimum Subarray = -3
         *
         *      Circular Sum
         *      = 7 - (-3)
         *      = 10
         *
         *
         * --------------------------------------------------------------------------
         * FINAL ANSWER
         * --------------------------------------------------------------------------
         *
         * We don't know whether the best subarray wraps or not.
         *
         * So compute both:
         *
         *      1. Normal Maximum Subarray
         *      2. Circular Maximum Subarray
         *
         * Return the larger one.
         *
         *
         * --------------------------------------------------------------------------
         * EDGE CASE
         * --------------------------------------------------------------------------
         *
         * nums = [-3, -2, -1]
         *
         * Here,
         *
         * Minimum Subarray = Entire Array
         *
         * Therefore,
         *
         * Total - Minimum = 0
         *
         * This means we removed the entire array,
         * leaving an empty subarray.
         *
         * Since an empty subarray is NOT allowed,
         * return the normal Kadane answer.
         *
         * Time  : O(n)
         * Space : O(1)
         * ============================================================================
         */
        public static int maxSubarraySumCircular(int[] nums) {

            int totalSum = 0;

            // Calculate the total sum of the array.
            for (int num : nums) {
                totalSum += num;
            }

            // Case 1: Maximum subarray without wrapping.
            int maxSum = maxKadane(nums);

            // Case 2: Minimum subarray (used to calculate wrapped answer).
            int minSum = minKadane(nums);

            /*
             * If the minimum subarray is the entire array,
             * removing it leaves an empty subarray.
             *
             * This happens only when all numbers are negative.
             */
            if (minSum == totalSum) {
                return maxSum;
            }

            // Maximum sum obtained by wrapping around the array.
            int circularSum = totalSum - minSum;

            // Return the better of the two possible answers.
            return Math.max(maxSum, circularSum);
        }
}