package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Check if Sorted
 *
 * DESCRIPTION:
 * Given an array of integers, check if it is sorted in non-decreasing order.
 *
 * Example 1:
 * Input: nums = [1, 2, 2, 3, 4]
 * Output: true
 * Explanation: Array is sorted in non-decreasing order.
 *
 * Example 2:
 * Input: nums = [1, 3, 2]
 * Output: false
 * Explanation: 3 > 2, not sorted.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * 1. Iterate from index 1 to n-1
 * 2. If nums[i] < nums[i-1], return false
 * 3. Return true if loop completes
 * Time: O(n), Space: O(1)
 */
public class A15_CheckIfSorted {

    public static void main(String[] args) {
        // 1. Create our array to test.
        int[] nums = {10, 20, 30, 25, 40};

        // 2. Call our helper method to check if the array is sorted.
        boolean isSorted = checkIfSorted(nums);

        // 6. Print the final result based on what the method returned.
        System.out.println("Is the array sorted? " + isSorted);
    }

    // A clean, production-grade helper method that returns true or false.
    public static boolean checkIfSorted(int[] nums) {

        // 3. Start a loop from index 1 (the second element) up to the end of the array.
        // Why start at 1? Because we will always look backward and compare it with index (i - 1).
        for (int i = 1; i < nums.length; i++) {

            // 4. The One and Only Gate: Check if the rule is broken.
            // "Is the current number strictly smaller than the previous number?"
            if (nums[i] < nums[i - 1]) {

                // 5. Rule broken! We immediately exit the entire method and return false.
                // There is no need to check the remaining elements.
                return false;
            }
        }

        // If the loop finishes entirely without triggering the 'if' block,
        // it means every single element followed the rule perfectly.
        return true;
    }
}