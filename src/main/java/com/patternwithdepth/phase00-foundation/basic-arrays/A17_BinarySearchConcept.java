package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Binary Search (Concept Only)
 *
 * DESCRIPTION:
 * Given a sorted array and a target value, find the target using binary search.
 * Binary search repeatedly divides the search range in half.
 *
 * Example 1:
 * Input: nums = [1, 3, 5, 7, 9, 11], target = 7
 * Output: 3
 * Explanation: Binary search finds 7 at index 3.
 *
 * Example 2:
 * Input: nums = [1, 3, 5, 7, 9, 11], target = 2
 * Output: -1
 * Explanation: 2 is not in the array.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - nums is sorted in ascending order
 *
 * APPROACH:
 * 1. Initialize lo = 0, hi = n-1
 * 2. While lo <= hi: mid = lo + (hi-lo)/2
 * 3. If nums[mid] == target, return mid
 * 4. If nums[mid] < target, lo = mid + 1
 * 5. Else hi = mid - 1
 * 6. Return -1 if not found
 * Time: O(log n), Space: O(1)
 */
public class A17_BinarySearchConcept {

    public static void main(String[] args) {
        // 1. Create a strictly SORTED array to search through.
        int[] nums = {10, 20, 30, 40, 50, 60, 70};
        int target = 60;

        // 2. Call our binary search utility.
        int resultIndex = binarySearch(nums, target);

        // Print the result.
        if (resultIndex == -1) {
            System.out.println("Target not found.");
        } else {
            System.out.println("Target found at index: " + resultIndex);
        }
    }

    public static int binarySearch(int[] nums, int target) {
        // 3. Establish the search boundaries using two pointers.
        // 'low' points to the first possible box; 'high' points to the last possible box.
        int low = 0;
        int high = nums.length - 1;

        // 4. Run a loop that keeps shrinking the search space.
        // As long as low doesn't cross high, there are still boxes left to check.
        while (low <= high) {

            // 5. Calculate the exact middle index of our current boundary.
            // (low + high) / 2 is the basic way.
            // Writing it as low + (high - low) / 2 prevents memory overflow for massive arrays.
            int mid = low + (high - low) / 2;

            // GATE 1: Did we get lucky and find the target right in the middle?
            if (nums[mid] == target) {
                return mid; // Match found! Exit immediately and return the index.
            }

            // GATE 2: Is the number in the middle strictly larger than our target?
            // If the middle number is 50 and our target is 20, then 20 MUST be on the left side.
            if (nums[mid] > target) {
                high = mid - 1; // Throw away the right half by shifting the 'high' wall left.
            }

            // GATE 3: The middle number must be strictly smaller than our target.
            // If the middle number is 50 and our target is 60, then 60 MUST be on the right side.
            else {
                low = mid + 1; // Throw away the left half by shifting the 'low' wall right.
            }
        }

        // 6. If low and high cross each other, the target is definitely not in the array.
        return -1;
    }
}