package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Sum of Array
 *
 * DESCRIPTION:
 * Given an array of integers, return the sum of all elements.
 *
 * Example 1:
 * Input: nums = [1, 2, 3, 4, 5]
 * Output: 15
 * Explanation: 1 + 2 + 3 + 4 + 5 = 15
 *
 * Example 2:
 * Input: nums = [-1, 0, 1]
 * Output: 0
 * Explanation: -1 + 0 + 1 = 0
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * - Sum may overflow 32-bit integer (use long)
 *
 * APPROACH:
 * 1. Initialize sum = 0 (as long)
 * 2. Iterate through array, add each element
 * Time: O(n), Space: O(1)
 */
public class A08_SumOfArray {

    public static void main(String[] args) {
        // 1. Create our array (our basket of numbers to add up).
        int[] nums = {4, 2, 7, 1};

        // 2. Initialize our accumulator variable (our cash register).
        // We start at 0 because we haven't added any numbers to it yet.
        int sum = 0;

        // 3. Start a loop to visit every single index in the array, one by one.
        // We start at index 0 and move forward until we hit the end of the array.
        for (int i = 0; i < nums.length; i++) {

            // 4. Take the current number (nums[i]) and add it to our existing sum.
            // The '+=' operator is shorthand for: sum = sum + nums[i];
            sum += nums[i];

            // 5. The loop finishes this step and goes to the next index.
        }

        // 6. Once the loop has visited every number, 'sum' holds the final total.
        // We print it to the screen.
        System.out.println("sum = " + sum);
    }
}