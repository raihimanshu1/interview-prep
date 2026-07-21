package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Kadane's Algorithm (Maximum Subarray Sum)
 * (Problem number: 25 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an integer array nums, find the contiguous subarray (containing at least
 * one number) which has the largest sum and return its sum.
 *
 * Example 1:
 * Input: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
 * Output: 6
 * Explanation: Subarray [4, -1, 2, 1] has the largest sum = 6.
 *
 * Example 2:
 * Input: nums = [1]
 * Output: 1
 * Explanation: Single element.
 *
 * Example 3:
 * Input: nums = [5, 4, -1, 7, 8]
 * Output: 23
 * Explanation: Entire array is the max subarray.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - -10^5 <= nums[i] <= 10^5
 *
 * APPROACH:
 * BRUTE FORCE: O(n^3) - Try all subarrays and track max sum
 * OPTIMIZED:   O(n)   - Use Kadane's algorithm (current sum or restart)
 */

public class LS08_KadanesAlgorithm {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};

        // 2. --- BRUTE FORCE APPROACH ---
        // Call brute force method.
        int bruteResult = maxSubArrayBruteForce(nums.clone());
        System.out.println("Brute Force Max Subarray Sum: " + bruteResult); // Expected: 6

        // 3. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int optimizedResult = maxSubArrayKadane(nums.clone());
        System.out.println("Kadane Max Subarray Sum: " + optimizedResult); // Expected: 6

        // 4. Test all positive.
        int[] nums2 = {5, 4, -1, 7, 8};
        System.out.println("All Positive - Kadane: " + maxSubArrayKadane(nums2)); // Expected: 23
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: Try all possible subarrays, calculate their sum, track the maximum.
    // Time:  O(n^3)  |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int maxSubArrayBruteForce(int[] nums) {
        int n = nums.length;
        int maxSum = Integer.MIN_VALUE; // 1. Track the maximum sum found.

        // 2. Iterate over each possible starting index.

        //    int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        for (int i = 0; i < n; i++) {
            // 3. Iterate over each possible ending index (from i to n-1).
            for (int j = i; j < n; j++) {
                int currentSum = 0; // 4. Calculate sum of subarray [i, j].

                // 5. Sum all elements from i to j.
                for (int k = i; k <= j; k++) {
                    currentSum += nums[k];
                }

                // 6. Update maxSum if current subarray sum is better.
                if (currentSum > maxSum) {
                    maxSum = currentSum;
                }
            }
        }

        return maxSum;
    }

//    with two loops

    public static int maxSubArrayBetter(int[] nums) {

        int n = nums.length;
        int maxSum = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {

//       Note -     imp note not adding arr[i] here instead startig other loop from start its imp notes
            int currentSum = 0;

            // Extend the subarray one element at a time
            for (int j = i; j < n; j++) {

                currentSum += nums[j];

                maxSum = Math.max(maxSum, currentSum);
            }
        }

        return maxSum;
    }

    // -------------------------------------------------------------------------
    // KADANE'S ALGORITHM (OPTIMIZED)
    // Idea: As we scan, keep adding elements to currentSum. If currentSum becomes
    //       negative, restart from current element (since negative sum hurts future).
    //       At each step, track the maximum of currentSum and global max.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int maxSubArrayKadane(int[] nums) {
        int n = nums.length;
        int maxSum = nums[0]; // 1. Track the global maximum (start with first element).
        int currentSum = nums[0]; // 2. Track the current subarray sum.

        // 3. Start from the second element.
        //    int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        for (int i = 1; i < n; i++) {
            // 4. Decide: either extend current subarray or start fresh at nums[i].
            //    We pick whichever is better: (currentSum + nums[i]) vs nums[i] alone.
            if (currentSum + nums[i] > nums[i]) {
                currentSum = currentSum + nums[i]; // Extend the subarray.
            } else {
                currentSum = nums[i]; // Start a new subarray from here.
            }

            // 5. Update maxSum if current running sum is better.
            if (currentSum > maxSum) {
                maxSum = currentSum;
            }
        }

        return maxSum;
    }
}