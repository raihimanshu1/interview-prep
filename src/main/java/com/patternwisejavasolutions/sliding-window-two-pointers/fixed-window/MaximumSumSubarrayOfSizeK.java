package com.patternwisejavasolutions.slidingwindowtwopointers.fixedwindow;

public class MaximumSumSubarrayOfSizeK {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input:
     * nums = [2, 1, 5, 1, 3, 2], k = 3
     *
     * Sample Output:
     * 9
     *
     * We need to find the maximum sum of any continuous subarray of size k.
     *
     * Example:
     * nums = [2, 1, 5, 1, 3, 2]
     * k = 3
     *
     * Windows of size 3:
     * [2, 1, 5] sum = 8
     * [1, 5, 1] sum = 7
     * [5, 1, 3] sum = 9
     * [1, 3, 2] sum = 6
     *
     * Answer:
     * 9
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Since window size is fixed at k,
     * try every group of exactly k elements and calculate its sum.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Because the window must have exactly k numbers, the first idea is to
     * place the window at every possible start and add those k numbers fresh.
     * Then compare that sum with the best sum seen so far.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [2, 1, 5, 1]
     * k = 3
     *
     * window starting at 0:
     * [2, 1, 5] sum = 8
     *
     * window starting at 1:
     * [1, 5, 1] sum = 7
     *
     * best = 8
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Start each window at index start.
     * 2. Add exactly k numbers.
     * 3. Update best sum.
     *
     * Time Complexity: O(n * k)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] nums, int k) {
        int bestSum = Integer.MIN_VALUE;

        for (int start = 0; start + k <= nums.length; start++) {
            int windowSum = 0;

            for (int i = start; i < start + k; i++) {
                windowSum += nums[i];
            }

            bestSum = Math.max(bestSum, windowSum);
        }

        return bestSum;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Consecutive windows share most elements.
     *
     * Example:
     * [2, 1, 5]
     * next window is [1, 5, 1]
     *
     * We remove 2 and add 1.
     *
     * So instead of recalculating the whole sum,
     * we slide the window:
     * - add new right element
     * - remove old left element
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [2, 1, 5, 1]
     * k = 3
     *
     * Add 2 -> sum = 2
     * Add 1 -> sum = 3
     * Add 5 -> sum = 8
     * window size is 3, best = 8
     * remove 2 -> sum = 6
     *
     * Add 1 -> sum = 7
     * best still 8
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep windowSum.
     * 2. Add nums[right].
     * 3. Once window size reaches k:
     *    - update best
     *    - remove leftmost element
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Prefix sums can compute each fixed-window sum as prefix[end] - prefix[start].
     * The rolling-sum window uses less extra space because each new window only
     * changes by one entering value and one leaving value.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] nums, int k) {
        int windowSum = 0;
        int bestSum = Integer.MIN_VALUE;

        for (int right = 0; right < nums.length; right++) {
            /*
             * Add the new element entering the window.
             */
            windowSum += nums[right];

            /*
             * If right >= k - 1, the window has exactly k elements.
             */
            if (right >= k - 1) {
                bestSum = Math.max(bestSum, windowSum);

                /*
                 * Remove the leftmost element before moving to next window.
                 */
                int left = right - k + 1;
                windowSum -= nums[left];
            }
        }

        return bestSum;
    }
}
