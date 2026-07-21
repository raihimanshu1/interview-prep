package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Reverse Array
 *
 * DESCRIPTION:
 * Given an array of integers, reverse it in-place.
 *
 * Example 1:
 * Input: nums = [1, 2, 3, 4, 5]
 * Output: [5, 4, 3, 2, 1]
 * Explanation: The array is reversed.
 *
 * Example 2:
 * Input: nums = [1, 2]
 * Output: [2, 1]
 * Explanation: Two elements swapped.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * 1. Use two pointers: left = 0, right = n-1
 * 2. Swap nums[left] and nums[right]
 * 3. left++, right-- until left >= right
 * Time: O(n), Space: O(1)
 */
import java.util.Arrays;

public class A11_ReverseArray {

    public static void main(String[] args) {
        // 1. Create our initial array of numbers.
        int[] nums = {10, 20, 30, 40, 50};

        // 2. Initialize our two pointers.
        // 'left' starts at the beginning (index 0).
        // 'right' starts at the very last element (index length - 1).
        int left = 0;
        int right = nums.length - 1;

        // 3. Run a loop that continues as long as the left pointer is to the left of the right pointer.
        // Once they meet or cross in the middle, the entire array is reversed.
        while (left < right) {

            // 4. Perform the Swap using a temporary holding variable ('temp').
            // First, protect the value at the 'left' position by saving it in 'temp'.
            int temp = nums[left];

            // 5. Overwrite the value at 'left' with the value from 'right'.
            nums[left] = nums[right];

            // 6. Complete the swap by putting the saved 'temp' value into the 'right' position.
            nums[right] = temp;

            // 7. Move both pointers closer to the middle.
            left++;  // Move the left hand forward
            right--; // Move the right hand backward
        }

        // 8. Print the reversed array using Java's built-in Arrays.toString utility.
        System.out.println("Reversed array: " + Arrays.toString(nums));
    }
}