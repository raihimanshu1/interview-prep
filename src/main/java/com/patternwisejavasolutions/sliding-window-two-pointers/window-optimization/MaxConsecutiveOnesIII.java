
package com.patternwisejavasolutions.slidingWindowTwoPointers.windowOptimization;
public class MaxConsecutiveOnesIII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We can flip at most k zeroes into ones.
     * Find the longest continuous subarray that can become all 1s.
     *
     * Sample Input:
     * nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
     *
     * Sample Output:
     * 6
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A window is valid if it contains at most k zeroes.
     * Brute force tries every window and counts zeroes.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Pick a start and extend right while counting zeroes. Each zero is one
     * flip. If the count becomes more than k, that start cannot create a valid
     * longer all-ones block.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,1,0,0,1], k = 1
     *
     * start = 0:
     * [1] valid
     * [1,1] valid
     * [1,1,0] valid, length = 3
     * [1,1,0,0] has 2 zeroes, invalid
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Choose each start.
     * 2. Expand end and count zeroes.
     * 3. If zeroes <= k, update best length.
     * 4. If zeroes > k, stop that start.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] nums, int k) {
        int bestLength = 0;

        for (int start = 0; start < nums.length; start++) {
            int zeroes = 0;

            for (int end = start; end < nums.length; end++) {
                if (nums[end] == 0) {
                    zeroes++;
                }

                if (zeroes > k) {
                    break;
                }

                bestLength = Math.max(bestLength, end - start + 1);
            }
        }

        return bestLength;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Instead of recounting zeroes for every start, keep one moving window.
     *
     * Add nums[right].
     * If the window has too many zeroes, move left until it becomes valid again.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,1,0,0,1], k = 1
     *
     * Window [1,1,0] has one zero, valid, best = 3.
     * Add next 0 -> two zeroes, invalid.
     * Move left until one zero is removed.
     * Continue with a valid window.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep left pointer and zero count.
     * 2. Move right and add zero if nums[right] is 0.
     * 3. While zeroes > k, remove nums[left] and move left.
     * 4. Update best valid window length.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Prefix sums of zero counts can answer how many zeroes are in any range,
     * then binary search can find the longest valid range for each start. The
     * sliding window is simpler because the range only grows and shrinks.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] nums, int k) {
        int left = 0;
        int zeroes = 0;
        int bestLength = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeroes++;
            }

            while (zeroes > k) {
                if (nums[left] == 0) {
                    // Removing this zero gives one flip back to the window.
                    zeroes--;
                }

                left++;
            }

            bestLength = Math.max(bestLength, right - left + 1);
        }

        return bestLength;
    }
}
