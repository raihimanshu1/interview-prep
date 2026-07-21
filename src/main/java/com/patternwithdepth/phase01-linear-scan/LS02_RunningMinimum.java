package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Running Minimum (Replace Elements with Smallest on Right)
 * (Problem number: 19 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array arr, replace every element in that array with the smallest element
 * among the elements to its right, and replace the last element with -1.
 * After doing so, return the array.
 *
 * Example 1:
 * Input: arr = [5, 2, 8, 3, 9, 1]
 * Output: [2, 1, 1, 1, 1, -1]
 * Explanation:
 * - index 0: smallest on right is 2 -> 2
 * - index 1: smallest on right is 1 -> 1
 * - index 2: smallest on right is 1 -> 1
 * - index 3: smallest on right is 1 -> 1
 * - index 4: smallest on right is 1 -> 1
 * - index 5: no elements on right -> -1
 *
 * Example 2:
 * Input: arr = [100]
 * Output: [-1]
 * Explanation: There is only one element, so the last element becomes -1.
 *
 * CONSTRAINTS:
 * - 1 <= arr.length <= 10^4
 * - 1 <= arr[i] <= 10^5
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each element, scan all elements on its right to find min
 * OPTIMIZED:   O(n)   - Scan from right to left, track min so far
 */

public class LS02_RunningMinimum {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] arr = {5, 2, 8, 3, 9, 1};

        // 2. --- BRUTE FORCE APPROACH ---
        // Call brute force method.
        int[] bruteResult = runningMinimumBruteForce(arr.clone());
        System.out.print("Brute Force Result: ");
        printArray(bruteResult); // Expected: [2, 1, 1, 1, 1, -1]

        // 3. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int[] optimizedResult = runningMinimumOptimized(arr.clone());
        System.out.print("Optimized Result: ");
        printArray(optimizedResult); // Expected: [2, 1, 1, 1, 1, -1]
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each element at index i, scan all elements from i+1 to n-1
    //       and find the minimum. Replace arr[i] with that minimum.
    // Time:  O(n^2)  |  Space: O(1) extra
    // -------------------------------------------------------------------------
    public static int[] runningMinimumBruteForce(int[] arr) {
        int n = arr.length;

        // 1. Iterate through each index except the last one.
        for (int i = 0; i < n - 1; i++) {
            int minRight = arr[i + 1]; // Start with immediate right neighbor.

            // 2. Scan ALL elements to the right of index i to find the true minimum.
            for (int j = i + 2; j < n; j++) {
                if (arr[j] < minRight) {
                    minRight = arr[j]; // Update if we found a smaller number.
                }
            }

            // 3. Replace current element with the minimum found on its right.
            arr[i] = minRight;
        }

        // 4. The last element always becomes -1 (no elements to its right).
        arr[n - 1] = -1;

        return arr;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: Scan from RIGHT to LEFT. Keep track of the minimum element seen so far.
    //       When moving left, we already know the min on the right of the current element.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int[] runningMinimumOptimized(int[] arr) {
        int n = arr.length;

        // 1. Track the smallest element seen so far.
        //    For the last element, the "smallest on right" is nothing -> -1.
        int minSoFar = -1;

        // 2. Traverse from the LAST element to the FIRST element.
        for (int i = n - 1; i >= 0; i--) {
            // 3. Temporarily store the current value before overwriting it.
            int current = arr[i];

            // 4. Overwrite the current position with minSoFar (which represents
            //    the smallest element among all elements to the right of i).
            arr[i] = minSoFar;

            // 5. Update minSoFar for the next iteration (moving left).
            //    The next element to the left will consider current as a candidate
            //    for the smallest element on its right.
            if (minSoFar == -1 || current < minSoFar) {
                minSoFar = current;
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