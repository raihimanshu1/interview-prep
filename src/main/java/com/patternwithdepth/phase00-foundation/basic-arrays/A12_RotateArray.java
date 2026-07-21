package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Rotate Array
 *
 * DESCRIPTION:
 * Given an array, rotate the array to the right by k steps.
 *
 * Example 1:
 * Input: nums = [1,2,3,4,5,6,7], k = 3
 * Output: [5,6,7,1,2,3,4]
 * Explanation: Rotated right by 3 steps.
 *
 * Example 2:
 * Input: nums = [-1,-100,3,99], k = 2
 * Output: [3,99,-1,-100]
 * Explanation: Rotated right by 2 steps.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - 0 <= k <= 10^5
 *
 * APPROACH:
 * 1. k = k % n (handle k > n)
 * 2. Reverse entire array
 * 3. Reverse first k elements
 * 4. Reverse remaining n-k elements
 * Time: O(n), Space: O(1)
 */
import java.util.Arrays;

public class A12_RotateArray {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5};
        int k = 2; // Number of steps to rotate to the right

        // 1. Handle edge cases where k is greater than the array size.
        // For example, if an array has length 5, rotating it 5 times brings it back to the start.
        // Rotating it 7 times is exactly the same as rotating it 2 times (7 % 5 = 2).
        k = k % nums.length;

        // Step 1: Reverse the entire array from start to finish.
        reverse(nums, 0, nums.length - 1);

        // Step 2: Reverse the first 'k' elements (from index 0 to k - 1).
        reverse(nums, 0, k - 1);

        // Step 3: Reverse the rest of the elements (from index k to the very end).
        reverse(nums, k, nums.length - 1);

        // Print out the final rotated array.
        System.out.println("Rotated Array: " + Arrays.toString(nums));
    }

    // A simple, reusable helper function to reverse a specific segment of an array in-place.
    // It uses the classic two-pointer approach.
    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            // Swap elements at 'start' and 'end' using a temporary holding variable
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;

            // Move the pointers closer to each other
            start++;
            end--;
        }
    }
}

