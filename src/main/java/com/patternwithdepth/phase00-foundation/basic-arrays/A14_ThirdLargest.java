package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Third Largest
 *
 * DESCRIPTION:
 * Given an array of integers, find the third largest distinct element.
 *
 * Example 1:
 * Input: nums = [3, 7, 2, 9, 5]
 * Output: 5
 * Explanation: Distinct sorted: 9, 7, 5, 3, 2. Third is 5.
 *
 * Example 2:
 * Input: nums = [1, 2]
 * Output: Integer.MIN_VALUE (or largest element)
 * Explanation: Not enough distinct elements.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * 1. Track first, second, third largest distinct values
 * 2. Update accordingly as we iterate
 * Time: O(n), Space: O(1)
 */

public class A14_ThirdLargest {

    public static void main(String[] args) {
        // 1. Create our array containing a mix of numbers with duplicates.
        int[] nums = {40, 40, 30, 20};

        // 2. Initialize our three leaderboard slots to the lowest possible value.
        int largest = Integer.MIN_VALUE;
        int secondLargest = Integer.MIN_VALUE;
        int thirdLargest = Integer.MIN_VALUE;

        // 3. Loop through every single element in the array one by one.
        for (int i = 0; i < nums.length; i++) {

            // GATE 1: Strictly greater than 1st place
            if (nums[i] > largest) {
                thirdLargest = secondLargest; // 2nd place drops to 3rd
                secondLargest = largest;      // 1st place drops to 2nd
                largest = nums[i];            // Current number takes 1st
            }

            // GATE 2: Potential for 2nd place (Must not be a duplicate of 1st)
            else if (nums[i] > secondLargest && nums[i] != largest) {
                thirdLargest = secondLargest; // Old 2nd place drops to 3rd
                secondLargest = nums[i];      // Current number takes 2nd
            }

            // GATE 3: Potential for 3rd place (Must not be a duplicate of 1st OR 2nd)
            else if (nums[i] > thirdLargest && nums[i] != largest && nums[i] != secondLargest) {
                thirdLargest = nums[i];       // Current number takes 3rd
            }
        }

        // 4. Print out our results safely
        if (thirdLargest == Integer.MIN_VALUE) {
            System.out.println("There is no third largest unique element.");
        } else {
            System.out.println("The third largest element is: " + thirdLargest);
        }
    }
}