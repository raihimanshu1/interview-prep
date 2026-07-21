package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Prefix Maximum
 * (Problem number: 22 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array arr, return an array where result[i] is the maximum of all
 * elements from index 0 to i (inclusive) in the original array.
 *
 * Example 1:
 * Input: arr = [1, 3, 2, 5, 4]
 * Output: [1, 3, 3, 5, 5]
 * Explanation:
 * - index 0: max of [1] is 1
 * - index 1: max of [1, 3] is 3
 * - index 2: max of [1, 3, 2] is 3
 * - index 3: max of [1, 3, 2, 5] is 5
 * - index 4: max of [1, 3, 2, 5, 4] is 5
 *
 * Example 2:
 * Input: arr = [5, 4, 3, 2, 1]
 * Output: [5, 5, 5, 5, 5]
 * Explanation: The first element is the global maximum.
 *
 * CONSTRAINTS:
 * - 1 <= arr.length <= 10^4
 * - 1 <= arr[i] <= 10^5
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each index, scan 0 to i to find max
 * OPTIMIZED:   O(n)   - Track prefix maximum so far while scanning left to right
 */

public class LS05_PrefixMaximum {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] arr = {1, 3, 2, 5, 4};

        // 2. --- BRUTE FORCE APPROACH ---
        // Call brute force method.
        int[] bruteResult = prefixMaximumBruteForce(arr.clone());
        System.out.print("Brute Force Result: ");
        printArray(bruteResult); // Expected: [1, 3, 3, 5, 5]

        // 3. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int[] optimizedResult = prefixMaximumOptimized(arr.clone());
        System.out.print("Optimized Result: ");
        printArray(optimizedResult); // Expected: [1, 3, 3, 5, 5]
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each index i, scan all elements from 0 to i and find the max.
    // Time:  O(n^2)  |  Space: O(1) extra
    // -------------------------------------------------------------------------
    public static int[] prefixMaximumBruteForce(int[] arr) {
        int n = arr.length;

        // 1. Iterate through each index i.
        for (int i = 0; i < n; i++) {
            int maxPrefix = arr[0]; // Start with first element.

            // 2. Scan ALL elements from index 0 to i to find the true maximum.
            for (int j = 1; j <= i; j++) {
                if (arr[j] > maxPrefix) {
                    maxPrefix = arr[j]; // Update if we found a bigger number.
                }
            }

            // 3. Replace the current element with the maximum found in the prefix.
            arr[i] = maxPrefix;
        }

        return arr;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: Track the prefix maximum so far while scanning left to right.
    //       The prefix maximum at index i is the max(prefix_max[i-1], arr[i]).
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int[] prefixMaximumOptimized(int[] arr) {
        int n = arr.length;

        // 1. Iterate from index 1 because index 0 is just arr[0].
        for (int i = 1; i < n; i++) {
            // 2. Update current element with the maximum of previous prefix maximum
            //    (stored at arr[i-1]) and the current element arr[i].
            //    This way, arr[i] becomes the maximum of all elements from 0 to i.
            if (arr[i - 1] > arr[i]) {
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