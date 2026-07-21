package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Prefix Minimum
 * (Problem number: 23 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array arr, return an array where result[i] is the minimum of all
 * elements from index 0 to i (inclusive) in the original array.
 *
 * Example 1:
 * Input: arr = [4, 1, 3, 2, 7]
 * Output: [4, 1, 1, 1, 1]
 * Explanation:
 * - index 0: min of [4] is 4
 * - index 1: min of [4, 1] is 1
 * - index 2: min of [4, 1, 3] is 1
 * - index 3: min of [4, 1, 3, 2] is 1
 * - index 4: min of [4, 1, 3, 2, 7] is 1
 *
 * Example 2:
 * Input: arr = [1, 2, 3, 4, 5]
 * Output: [1, 1, 1, 1, 1]
 * Explanation: The first element is the global minimum.
 *
 * CONSTRAINTS:
 * - 1 <= arr.length <= 10^4
 * - 1 <= arr[i] <= 10^5
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each index, scan 0 to i to find min
 * OPTIMIZED:   O(n)   - Track prefix minimum so far while scanning left to right
 */

public class LS06_PrefixMinimum {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] arr = {4, 1, 3, 2, 7};

        // 2. --- BRUTE FORCE APPROACH ---
        // Call brute force method.
        int[] bruteResult = prefixMinimumBruteForce(arr.clone());
        System.out.print("Brute Force Result: ");
        printArray(bruteResult); // Expected: [4, 1, 1, 1, 1]

        // 3. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int[] optimizedResult = prefixMinimumOptimized(arr.clone());
        System.out.print("Optimized Result: ");
        printArray(optimizedResult); // Expected: [4, 1, 1, 1, 1]
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each index i, scan all elements from 0 to i and find the minimum.
    // Time:  O(n^2)  |  Space: O(1) extra
    // -------------------------------------------------------------------------
    public static int[] prefixMinimumBruteForce(int[] arr) {
        int n = arr.length;

        // 1. Iterate through each index i.
        for (int i = 0; i < n; i++) {
            int minPrefix = arr[0]; // Start with first element.

            // 2. Scan ALL elements from index 0 to i to find the true minimum.
            for (int j = 1; j <= i; j++) {
                if (arr[j] < minPrefix) {
                    minPrefix = arr[j]; // Update if we found a smaller number.
                }
            }

            // 3. Replace the current element with the minimum found in the prefix.
            arr[i] = minPrefix;
        }

        return arr;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: Track the prefix minimum so far while scanning left to right.
    //       The prefix minimum at index i is the min(prefix_min[i-1], arr[i]).
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int[] prefixMinimumOptimized(int[] arr) {
        int n = arr.length;

        // 1. Iterate from index 1 because index 0 is just arr[0].
        for (int i = 1; i < n; i++) {
            // 2. Update current element with the minimum of previous prefix minimum
            //    (stored at arr[i-1]) and the current element arr[i].
            //    This way, arr[i] becomes the minimum of all elements from 0 to i.
            if (arr[i - 1] < arr[i]) {
                arr[i] = arr[i - 1];
            }
        }

        return arr;
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