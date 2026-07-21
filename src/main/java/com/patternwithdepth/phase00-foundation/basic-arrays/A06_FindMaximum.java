package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Find Maximum
 *
 * DESCRIPTION:
 * Given an array of integers, find the maximum element.
 *
 * Example 1:
 * Input: nums = [3, 7, 2, 9, 5]
 * Output: 9
 * Explanation: 9 is the largest number in the array.
 *
 * Example 2:
 * Input: nums = [-5, -2, -8, -1]
 * Output: -1
 * Explanation: -1 is the largest (least negative) number.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 *
 * APPROACH:
 * 1. Initialize max = Integer.MIN_VALUE (or nums[0])
 * 2. Iterate through array
 * 3. If current element > max, update max
 * Time: O(n), Space: O(1)
 *
 * Follow-up:
 * Handle empty array edge case.
 */
public class A06_FindMaximum {

    public static void main(String[] args) {
        // 1. Create a basket (array) containing our list of negative numbers.
        int [] nums = {-5, -2, -8, -1};

        // 2. Initialize our "champion" tracker.
        // We start with Integer.MIN_VALUE, which is the smallest possible integer in Java (-2,147,483,648).
        // Why? Because ANY number in our array will be larger than this, forcing the very first
        // number we look at to become our initial maximum.
        int max = Integer.MIN_VALUE;

        // 3. Start a loop to visit every single index in the array, one by one.
        // We start at index 0 (the first item) and go up to the last item (nums.length - 1).
        for (int i = 0; i < nums.length; i++) {

            // 4. Compare the current number we are looking at (nums[i]) with our current champion (max).
            // "Is the number I am looking at right now strictly greater than my current maximum?"
            if (nums[i] > max) {

                // 5. If yes, the current number takes the crown!
                // We overwrite the old 'max' value with this new, larger number.
                max = nums[i];
            }
            // 6. If no, we do absolutely nothing and just move to the next iteration of the loop.
        }

        // 7. Once the loop finishes, 'max' holds the true maximum value of the entire array.
        // We print it out to the console.
        System.out.println("max = " + max);
    }
}