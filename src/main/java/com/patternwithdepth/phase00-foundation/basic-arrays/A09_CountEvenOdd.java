package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Count Even & Odd
 *
 * DESCRIPTION:
 * Given an array of integers, count how many are even and how many are odd.
 *
 * Example 1:
 * Input: nums = [1, 2, 3, 4, 5]
 * Output: even=2, odd=3
 * Explanation: Even numbers: 2, 4. Odd numbers: 1, 3, 5.
 *
 * Example 2:
 * Input: nums = [2, 4, 6, 8]
 * Output: even=4, odd=0
 * Explanation: All numbers are even.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * 1. Initialize evenCount = 0, oddCount = 0
 * 2. For each num, if num % 2 == 0 increment evenCount else increment oddCount
 * Time: O(n), Space: O(1)
 */
public class A09_CountEvenOdd {

    public static void main(String[] args) {
        // 1. Create our array containing a mix of even and odd numbers.
        int[] nums = {5, 8, 2, 9, 6};

        // 2. Initialize our two tracker buckets to 0.
        int evenCount = 0;
        int oddCount = 0;

        // 3. Start a loop to inspect every single number in the array, one by one.
        for (int i = 0; i < nums.length; i++) {

            // 4. Check if the current number is even.
            // The '%' symbol is the modulo operator. It calculates the REMAINDER of a division.
            // nums[i] % 2 means: "Divide the current number by 2 and give me the remainder."
            if (nums[i] % 2 == 0) {

                // 5. If the remainder is exactly 0, the number is even.
                // Increment our even counter by 1.
                evenCount++;

            } else {

                // 6. If the remainder is not 0, the number must be odd.
                // Increment our odd counter by 1.
                oddCount++;
            }
        }

        // 7. After visiting every number, print out our final tally.
        System.out.println("Even elements = " + evenCount);
        System.out.println("Odd elements = " + oddCount);
    }
}