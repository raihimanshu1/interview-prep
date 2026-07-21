package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Maximum Difference (Largest difference between any two elements)
 * (Problem number: 28 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array nums, find the maximum difference between any two elements
 * such that the larger element appears after the smaller element.
 *
 * Example 1:
 * Input: nums = [7, 1, 5, 3, 6, 4]
 * Output: 5
 * Explanation: 6 - 1 = 5
 *
 * Example 2:
 * Input: nums = [9, 8, 7, 6, 5]
 * Output: -1
 * Explanation: No pair where larger appears after smaller.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - 0 <= nums[i] <= 10^5
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each element, check all elements to its right
 * OPTIMIZED:   O(n)   - Track minimum so far, calculate difference at each step
 */

public class LS11_MaximumDifference {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] nums = {7, 1, 5, 3, 6, 4};

        // 2. --- BRUTE FORCE APPROACH ---
        int bruteResult = maxDifferenceBruteForce(nums.clone());
        System.out.println("Brute Force Max Diff: " + bruteResult); // Expected: 5

        // 3. --- OPTIMIZED APPROACH ---
        int optimizedResult = maxDifferenceOptimized(nums.clone());
        System.out.println("Optimized Max Diff: " + optimizedResult); // Expected: 5

        // 4. Test decreasing array.
        int[] nums2 = {9, 8, 7, 6, 5};
        System.out.println("Decreasing: " + maxDifferenceOptimized(nums2)); // Expected: -1
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each element at index i, check all elements to its right and find max diff.
    // Time:  O(n^2)  |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int maxDifferenceBruteForce(int[] nums) {
        int n = nums.length;
        int maxDiff = -1; // 1. Track the maximum difference.

        // 2. Iterate over each element.
        for (int i = 0; i < n; i++) {
            // 3. Check all elements to the right of i.
            for (int j = i + 1; j < n; j++) {
                // 4. Calculate difference.
                int diff = nums[j] - nums[i];

                // 5. Update maxDiff if this difference is better.
                if (diff > maxDiff) {
                    maxDiff = diff;
                }
            }
        }

        return maxDiff;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: Track minimum value seen so far. At each step, the best difference
    //       we can get is current element - minSoFar. Keep updating minSoFar.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int maxDifferenceOptimized(int[] nums) {
        int n = nums.length;
        int minVal = nums[0]; // 1. Track the minimum value seen so far.
        int maxDiff = -1; // 2. Track the maximum difference.

        // 3. Start from the second element.
        for (int i = 1; i < n; i++) {
            // 4. Calculate difference between current element and minVal.
            int diff = nums[i] - minVal;

            // 5. Update maxDiff if this difference is better.
            if (diff > maxDiff) {
                maxDiff = diff;
            }

            // 6. Update minVal if current element is smaller.
            if (nums[i] < minVal) {
                minVal = nums[i];
            }
        }

        return maxDiff;
    }
}