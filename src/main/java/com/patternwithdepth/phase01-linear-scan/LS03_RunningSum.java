package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Running Sum
 * (Problem number: 20 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array nums, return the running sum of the array.
 * The running sum at index i is the sum of all elements from index 0 to i.
 *
 * Example 1:
 * Input: nums = [1, 2, 3, 4]
 * Output: [1, 3, 6, 10]
 * Explanation: Running sum is [1, 1+2, 1+2+3, 1+2+3+4].
 *
 * Example 2:
 * Input: nums = [1, 1, 1, 1, 1]
 * Output: [1, 2, 3, 4, 5]
 * Explanation: Running sum is [1, 1+1, 1+1+1, 1+1+1+1, 1+1+1+1+1].
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^4
 * - -10^5 <= nums[i] <= 10^5
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each element, sum all elements from 0 to i
 * OPTIMIZED:   O(n)   - Use previous running sum, add current element
 */

public class LS03_RunningSum {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] nums = {1, 2, 3, 4};

        // 2. --- BRUTE FORCE APPROACH ---
        // Call brute force method.
        int[] bruteResult = runningSumBruteForce(nums.clone());
        System.out.print("Brute Force Result: ");
        printArray(bruteResult); // Expected: [1, 3, 6, 10]

        // 3. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int[] optimizedResult = runningSumOptimized(nums.clone());
        System.out.print("Optimized Result: ");
        printArray(optimizedResult); // Expected: [1, 3, 6, 10]
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each index i, compute the sum of all elements from index 0 to i.
    // Time:  O(n^2)  |  Space: O(1) extra
    // -------------------------------------------------------------------------
    public static int[] runningSumBruteForce(int[] nums) {
        int n = nums.length;

        // 1. Iterate through each index i.
        for (int i = 0; i < n; i++) {
            int sum = 0; // Initialize sum for current index.

            // 2. Sum all elements from index 0 to i (inclusive).
            for (int j = 0; j <= i; j++) {
                sum += nums[j]; // Accumulate each element.
            }

            // 3. Replace the current element with its running sum.
            nums[i] = sum;
        }

        return nums;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: The running sum at index i = running sum at index i-1 + nums[i].
    //       We just keep adding the current element to the previous running sum.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int[] runningSumOptimized(int[] nums) {
        int n = nums.length;

        // 1. Start from index 1 because index 0 is just nums[0] itself.
        for (int i = 1; i < n; i++) {
            // 2. Add the current element to the previous running sum.
            //    nums[i-1] already stores the sum of elements from 0 to i-1.
            //    After this update, nums[i] becomes sum from 0 to i.
            // index 0 is first element so need to worry about that
            nums[i] =  nums[i] + nums[i - 1];
        }

        return nums;
    }

    // -------------------------------------------------------------------------
    // Helper method to print array elements nicely.
    // -------------------------------------------------------------------------
    public static void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}