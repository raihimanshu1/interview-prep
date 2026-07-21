package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Linear Search
 *
 * DESCRIPTION:
 * Given an array and a target value, return the index of the target if found.
 * If not found, return -1.
 *
 * Example 1:
 * Input: nums = [4, 2, 7, 1, 9], target = 7
 * Output: 2
 * Explanation: 7 is at index 2.
 *
 * Example 2:
 * Input: nums = [4, 2, 7, 1, 9], target = 5
 * Output: -1
 * Explanation: 5 is not in the array.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * 1. Iterate through array from index 0 to n-1
 * 2. If nums[i] == target, return i
 * 3. Return -1 if not found
 * Time: O(n), Space: O(1)
 */
public class A16_LinearSearch {

    public static void main(String[] args) {
        // 1. Create our array (our row of boxes) and define the target we are searching for.
        int[] nums = {12, 45, 78, 23, 56};
        int target = 23;

        // 2. Call our search helper method to find the target's index.
        int resultIndex = linearSearch(nums, target);

        // 7. Print the result based on what index was returned.
        if (resultIndex == -1) {
            System.out.println("Target " + target + " not found in the array.");
        } else {
            System.out.println("Target " + target + " found at index: " + resultIndex);
        }
    }

    // A clean, production-grade search utility.
    // It returns the index if found, or -1 if the target does not exist.
    public static int linearSearch(int[] nums, int target) {

        // 3. Start a loop to visit every single index from 0 to the end of the array.
        for (int i = 0; i < nums.length; i++) {

            // 4. The Match Gate: Check if the current element matches our target.
            if (nums[i] == target) {

                // 5. Match found! We immediately exit the entire method and return the index.
                // There is no need to waste time scanning the rest of the array.
                return i;
            }
        }

        // 6. If the loop completes entirely, it means we opened every single box
        // and never found a match. We return -1 (a standard signal in programming meaning "not found").
        return -1;
    }
}