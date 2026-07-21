package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Longest Mountain in Array
 * (Problem number: 29 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array of integers arr, return the length of the longest subarray
 * which is a mountain. A mountain subarray has length >= 3 and there exists
 * some index i (0 < i < n-1) such that:
 * arr[0] < arr[1] < ... < arr[i-1] < arr[i]
 * arr[i] > arr[i+1] > ... > arr[n-1]
 *
 * Example 1:
 * Input: arr = [2, 1, 4, 7, 3, 2, 5]
 * Output: 5
 * Explanation: The largest mountain is [1, 4, 7, 3, 2].
 *
 * Example 2:
 * Input: arr = [2, 2, 2]
 * Output: 0
 * Explanation: No mountain exists.
 *
 * CONSTRAINTS:
 * - 1 <= arr.length <= 10^4
 * - 0 <= arr[i] <= 10^4
 *
 * APPROACH:
 * OPTIMIZED: O(n) - Scan for peaks, expand left and right from each peak
 */

public class LS12_LongestMountainInArray {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] arr = {2, 1, 4, 7, 3, 2, 5};

        // 2. --- OPTIMIZED APPROACH ---
        int result = longestMountain(arr);
        System.out.println("Longest Mountain Length: " + result); // Expected: 5

        // 3. Test no mountain.
        int[] arr2 = {2, 2, 2};
        System.out.println("No Mountain: " + longestMountain(arr2)); // Expected: 0

        // 4. Test multiple mountains.
        int[] arr3 = {0, 2, 2, 2, 2, 1, 0};
        System.out.println("Multiple Peaks: " + longestMountain(arr3)); // Expected: 3
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: Scan the array for "peaks" (elements greater than both neighbors).
    //       For each peak, expand left while strictly increasing, expand right
    //       while strictly decreasing. Track the longest mountain found.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int longestMountain(int[] arr) {
        int n = arr.length;
        int maxLen = 0; // 1. Track the longest mountain length.

        // 2. A peak must have elements on both sides, so iterate from 1 to n-2.
        for (int i = 1; i < n - 1; i++) {
            // 3. Check if current element is a peak.
            if (arr[i] > arr[i - 1] && arr[i] > arr[i + 1]) {
                int len = 1; // 4. Start counting from the peak.

                // 5. Expand LEFT while strictly increasing.
                int left = i - 1;
                while (left >= 0 && arr[left] < arr[left + 1]) {
                    len++;
                    left--;
                }

                // 6. Expand RIGHT while strictly decreasing.
                int right = i + 1;
                while (right < n && arr[right] < arr[right - 1]) {
                    len++;
                    right++;
                }

                // 7. Update maxLen if this mountain is longer.
                if (len > maxLen) {
                    maxLen = len;
                }
            }
        }

        return maxLen;
    }
}