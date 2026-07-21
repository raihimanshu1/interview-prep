package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Running Maximum (Replace Elements with Greatest on Right)
 * (Problem number: 18 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array arr, replace every element in that array with the greatest element
 * among the elements to its right, and replace the last element with -1.
 * After doing so, return the array.
 *
 * Example 1:
 * Input: arr = [17,18,5,4,6,1]
 * Output: [18,6,6,6,1,-1]
 * Explanation:
 * - index 0: greatest on right is 18 -> 18
 * - index 1: greatest on right is 6 -> 6
 * - index 2: greatest on right is 6 -> 6
 * - index 3: greatest on right is 6 -> 6
 * - index 4: greatest on right is 1 -> 1
 * - index 5: no elements on right -> -1
 *
 * Example 2:
 * Input: arr = [400]
 * Output: [-1]
 * Explanation: There is only one element, so the last (and only) element becomes -1.
 *
 * CONSTRAINTS:
 * - 1 <= arr.length <= 10^4
 * - 1 <= arr[i] <= 10^5
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each element, scan all elements on its right
 * OPTIMIZED:   O(n)   - Scan from right, keep track of max so far
 */

public class LS01_RunningMaximum {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] arr = {17, 18, 5, 4, 6, 1};

        // 2. --- BRUTE FORCE APPROACH ---
        // Call brute force method.
        int[] bruteResult = runningMaximumBruteForce(arr.clone());
        System.out.print("Brute Force Result: ");
        printArray(bruteResult); // Expected: [18, 6, 6, 6, 1, -1]

        // 3. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int[] optimizedResult = runningMaximumOptimized(arr.clone());
        System.out.print("Optimized Result: ");
        printArray(optimizedResult); // Expected: [18, 6, 6, 6, 1, -1]
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each element, look at every element to its right and pick the max.
    // Time:  O(n^2)  |  Space: O(1) extra (output array reuses input)
    // -------------------------------------------------------------------------
    public static int[] runningMaximumBruteForce(int[] arr) {
        int n = arr.length;

        // 1. Iterate over every index except the last one.
        for (int i = 0; i < n - 1; i++) {
            int maxRight = arr[i + 1]; // Start with immediate neighbor.

            // 2. Scan ALL elements to the right of index i to find the true maximum.
            for (int j = i + 2; j < n; j++) {
                if (arr[j] > maxRight) {
                    maxRight = arr[j]; // Update if we found a bigger number.
                }
            }

            // 3. Replace current element with the max found on its right.
            arr[i] = maxRight;
        }

        // 4. The last element always becomes -1 (nothing on its right).
        arr[n - 1] = -1;

        return arr;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: Scan from RIGHT to LEFT. Keep a running maximum (maxSoFar).
    //       When we move left, we already know the max on the right of current.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int[] runningMaximumOptimized(int[] arr) {
        int n = arr.length;

        // 1. Track the greatest element seen so far.
        //    For the last element, the "greatest on right" is nothing -> -1.
        int maxSoFar = -1;

        // 2. Traverse from the LAST element to the FIRST element (right loop).
        for (int i = n - 1; i >= 0; i--) {
            // 3. Temporarily store the current value before overwriting it.
            int current = arr[i];

            // 4. Overwrite the current position with maxSoFar (which represents
            //    the greatest element among all elements to the right of i).
            arr[i] = maxSoFar;

            // 5. Update maxSoFar for the next iteration (moving left).
            //    The next element to the left will consider current as a candidate
            //    for the greatest element on its right.
            if (current > maxSoFar) {
                maxSoFar = current;
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