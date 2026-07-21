package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Find Minimum
 *
 * DESCRIPTION:
 * Given an array of integers, find the minimum element.
 *
 * Example 1:
 * Input: nums = [3, 7, 2, 9, 5]
 * Output: 2
 * Explanation: 2 is the smallest number in the array.
 *
 * Example 2:
 * Input: nums = [-5, -2, -8, -1]
 * Output: -8
 * Explanation: -8 is the smallest number.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 *
 * APPROACH:
 * 1. Initialize min = Integer.MAX_VALUE (or nums[0])
 * 2. Iterate through array
 * 3. If current element < min, update min
 * Time: O(n), Space: O(1)
 */
public class A07_FindMinimum {

    public static void main(String[] args) {
        // 1. Create our array containing the list of numbers.
        int [] nums = {-5, -2, -8, -1};

        // 2. Initialize our "record holder" tracker for the minimum value.
        // We start with Integer.MAX_VALUE, which is the largest possible integer in Java (2,147,483,647).
        // Why? Because ANY number in our array will be smaller than this, forcing the very first
        // number we look at to become our initial minimum.
        int min = Integer.MAX_VALUE;

        // 3. Start a loop to visit every single index in the array, from index 0 to the end.
        for (int i = 0; i < nums.length; i++) {

            // 4. Compare the current number (nums[i]) with our current minimum (min).
            // "Is the number I am looking at right now strictly smaller than my current minimum?"
            if (nums[i] < min) {

                // 5. If yes, we found a new lowest number!
                // We overwrite 'min' with this smaller value.
                min = nums[i];
            }
            // 6. If no, we ignore it and move to the next item.
        }

        // 7. Print the final absolute minimum value found.
        System.out.println("min = " + min);
    }
}