package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Wiggle Subsequence
 * (Problem number: 31 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an integer array nums, return the length of the longest wiggle subsequence.
 * A wiggle sequence alternates between strictly increasing and strictly decreasing.
 *
 * Example 1:
 * Input: nums = [1, 7, 4, 9, 2, 5]
 * Output: 6
 * Explanation: The entire sequence is a wiggle.
 *
 * Example 2:
 * Input: nums = [1, 17, 5, 10, 13, 15, 10, 5, 16, 8]
 * Output: 7
 * Explanation: One wiggle is [1, 17, 5, 10, 5, 16, 8].
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 1000
 * - 0 <= nums[i] <= 1000
 *
 * APPROACH:
 * OPTIMIZED: O(n) - Greedy: count changes in direction, skip duplicates
 */

public class LS14_WiggleSubsequence {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] nums = {1, 7, 4, 9, 2, 5};

        // 2. --- OPTIMIZED APPROACH ---
        int result = wiggleMaxLength(nums);
        System.out.println("Wiggle Max Length: " + result); // Expected: 6

        // 3. Test another example.
        int[] nums2 = {1, 17, 5, 10, 13, 15, 10, 5, 16, 8};
        System.out.println("Wiggle 2: " + wiggleMaxLength(nums2)); // Expected: 7
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: A wiggle sequence needs alternation between up and down.
    //       We greedily count every change in direction.
    //       Duplicate numbers are ignored (they don't contribute to wiggle).
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int wiggleMaxLength(int[] nums) {
        int n = nums.length;
        if (n < 2) {
            return n; // 1. Edge case: 0 or 1 element.
        }

        int peak = 1; // 2. Count of peaks (up sequences).
        int valley = 1; // 3. Count of valleys (down sequences).

        // 4. Iterate through the array.
        for (int i = 1; i < n; i++) {
            // 5. If current is greater than previous, we have an UP move.
            if (nums[i] > nums[i - 1]) {
                peak = valley + 1; // 6. Extend the valley sequence by going up.
            }
            // 7. If current is less than previous, we have a DOWN move.
            else if (nums[i] < nums[i - 1]) {
                valley = peak + 1; // 8. Extend the peak sequence by going down.
            }
            // 9. If equal, do nothing (skip duplicates).
        }

        // 10. The answer is the max of peak and valley sequences.
        return Math.max(peak, valley);
    }
}