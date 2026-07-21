package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Second Largest
 *
 * DESCRIPTION:
 * Given an array of integers, find the second largest element.
 *
 * Example 1:
 * Input: nums = [3, 7, 2, 9, 5]
 * Output: 7
 * Explanation: 9 is largest, 7 is second largest.
 *
 * Example 2:
 * Input: nums = [1, 1, 1]
 * Output: -1 (or Integer.MIN_VALUE)
 * Explanation: No distinct second largest element.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * 1. Initialize first = max, second = min
 * 2. Iterate: if num > first, update second=first, first=num
 * 3. Else if num > second and num != first, update second
 * Time: O(n), Space: O(1)
 */
public class A13_SecondLargest {

    public static void main(String[] args) {
        // 1. Create our array containing a mix of numbers.
        int[] nums = {12, 35, 1, 10, 34, 1};

        // 2. Initialize our leaderboard placeholders to the lowest possible integers.
        int largest = Integer.MIN_VALUE;
        int secondLargest = Integer.MIN_VALUE;

        // 3. Loop through every element in the array one by one.
        for (int i = 0; i < nums.length; i++) {

            // 4. Case A: Current number is strictly greater than our 1st place champion.
            if (nums[i] > largest) {
                // The old champion drops down to become the runner-up.
                secondLargest = largest;
                // The current number becomes the new 1st place champion.
                largest = nums[i];

            }
            // 5. Case B: Current number doesn't beat 1st place, but beats 2nd place.
            // We also make sure it's not a duplicate of the 1st place champion (nums[i] != largest).
            else if (nums[i] > secondLargest && nums[i] != largest) {
                // The current number claims the 2nd place runner-up spot.
                secondLargest = nums[i];
            }
        }

        // 6. Check if we actually found a valid second largest element.
        // If the array only contained duplicate values (e.g., {10, 10, 10}), secondLargest would never change.
        if (secondLargest == Integer.MIN_VALUE) {
            System.out.println("There is no second largest element.");
        } else {
            System.out.println("The second largest element is: " + secondLargest);
        }
    }
}