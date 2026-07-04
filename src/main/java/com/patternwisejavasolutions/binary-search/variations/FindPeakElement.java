
package com.patternwisejavasolutions.binarySearch.variations;
public class FindPeakElement {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Return any index whose value is greater than its neighbors. Values outside the array are
     * treated as very small.
     *
     * Sample Input: nums = [1, 2, 3, 1]
     * Sample Output: 2
     *
     * SCHOOL-LEVEL INTUITION:
     * A peak is like the top of a hill. If the next value is higher, walking toward it must
     * eventually reach a peak. If the next value is lower, the current side already contains a peak.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Check every index and compare it with its left and right neighbor. The first index bigger
     * than both neighbors is a peak.
     *
     * BRUTE FORCE ALGORITHM
     * 1. For every index, read left neighbor or negative infinity if missing.
     * 2. Read right neighbor or negative infinity if missing.
     * 3. If nums[index] is bigger than both, return index.
     *
     * BRUTE FORCE DRY RUN
     * nums = [1, 2, 3, 1]
     * index 0: 1 is not bigger than 2
     * index 1: 2 is not bigger than 3
     * index 2: 3 is bigger than 2 and 1 -> return 2
     *
     * Time: O(n), Space: O(1)
     */
    public int bruteForce(int[] nums) {
        for (int index = 0; index < nums.length; index++) {
            int left = index == 0 ? Integer.MIN_VALUE : nums[index - 1];
            int right = index == nums.length - 1 ? Integer.MIN_VALUE : nums[index + 1];

            if (nums[index] > left && nums[index] > right) {
                return index;
            }
        }

        return -1;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is checking every hill. Compare nums[mid] and nums[mid + 1].
     * If the slope goes up, a peak must exist on the right. If the slope goes down, a peak must
     * exist at mid or on the left.
     *
     * Pattern used: Binary Search on slope.
     *
     * OPTIMIZED ALGORITHM
     * 1. Set left = 0, right = last index.
     * 2. While left < right, compute mid.
     * 3. If nums[mid] < nums[mid + 1], move left to mid + 1.
     * 4. Otherwise move right to mid.
     * 5. Return left.
     *
     * OPTIMIZED DRY RUN
     * nums = [1, 2, 3, 1]
     * mid = 1, 2 < 3 -> peak is right
     * mid = 2, 3 > 1 -> peak is at mid or left, right = 2
     * return 2
     *
     * Time: O(log n), Space: O(1)
     */
    public int optimized(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] < nums[mid + 1]) {
                // We are climbing upward, so a peak is guaranteed to the right.
                left = mid + 1;
            } else {
                // We are going down, so mid can be a peak or a peak exists on the left side.
                right = mid;
            }
        }

        return left;
    }
}

