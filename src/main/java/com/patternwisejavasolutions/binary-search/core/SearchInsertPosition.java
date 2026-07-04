
package com.patternwisejavasolutions.binarySearch.core;
public class SearchInsertPosition {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given a sorted array, return the index of target. If target is missing,
     * return the index where it should be inserted to keep the array sorted.
     *
     * Sample Input: nums = [1,3,5,6], target = 5
     * Sample Output: 2
     *
     * Sample Input: nums = [1,3,5,6], target = 2
     * Sample Output: 1
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Think about placing a book on a sorted shelf. The correct spot is the first place where
     * the shelf value is not smaller than our book. Everything before that spot is too small.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Scan the array from left to right. The first number that is greater than or equal to target
     * is the answer position. This is natural because insertion is about finding the first value
     * that target should stand before.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Check each index from left to right.
     * 2. If nums[index] >= target, return index.
     * 3. If no such value exists, target belongs at the end.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,3,5,6], target = 2
     * 1 is smaller, 3 is the first bigger value, so return index 1.
     */
    public int bruteForce(int[] nums, int target) {
        for (int index = 0; index < nums.length; index++) {
            if (nums[index] >= target) {
                return index;
            }
        }

        return nums.length;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The brute force waste is checking many small values one by one. Because the array is sorted,
     * one middle check tells us whether all earlier values are too small. Binary search can find
     * the first position where nums[position] is at least target.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep left at 0 and right at nums.length.
     * 2. Check middle.
     * 3. If nums[mid] is smaller than target, search right.
     * 4. Otherwise mid could be the answer, so search left including mid.
     *
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,3,5,6], target = 2
     * mid = 2 gives 5, search left. mid = 1 gives 3, search left.
     * mid = 0 gives 1, search right. answer is 1.
     */
    public int optimized(int[] nums, int target) {
        int left = 0;
        int right = nums.length;

        while (left < right) {
            // [left, right) is the possible insertion window; right can equal nums.length.
            int mid = left + (right - left) / 2;

            if (nums[mid] < target) {
                // nums[mid] is too small, so target must be inserted after mid.
                left = mid + 1;
            } else {
                // mid is valid, but there may be an earlier valid position.
                right = mid;
            }
        }

        return left;
    }
}
