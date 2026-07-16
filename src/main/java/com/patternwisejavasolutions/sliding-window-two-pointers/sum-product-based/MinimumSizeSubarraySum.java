package com.patternwisejavasolutions.slidingwindowtwopointers.sumproductbased;

public class MinimumSizeSubarraySum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Find the length of the smallest continuous subarray whose sum is at least target.
     *
     * Sample Input:
     * target = 7
     * nums = [2, 3, 1, 2, 4, 3]
     *
     * Sample Output:
     * 2
     *
     * Why?
     * [4, 3] has sum 7 and length 2.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * We need a continuous group.
     * The simple way is to try every starting point and keep adding numbers until
     * the sum reaches target.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Start from each index and keep adding numbers until the sum reaches the
     * target. The first time it reaches target for that start is the shortest
     * subarray beginning there.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * start = 0:
     * [2] sum = 2
     * [2, 3] sum = 5
     * [2, 3, 1] sum = 6
     * [2, 3, 1, 2] sum = 8, length = 4
     *
     * start = 4:
     * [4] sum = 4
     * [4, 3] sum = 7, length = 2
     *
     * Best length becomes 2.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Choose every start index.
     * 2. Keep adding numbers to create subarrays.
     * 3. Once sum >= target, update best length.
     * 4. Break because adding more numbers only makes this subarray longer.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int target, int[] nums) {
        int bestLength = Integer.MAX_VALUE;

        for (int start = 0; start < nums.length; start++) {
            int sum = 0;

            for (int end = start; end < nums.length; end++) {
                sum += nums[end];

                if (sum >= target) {
                    bestLength = Math.min(bestLength, end - start + 1);
                    break;
                }
            }
        }

        return bestLength == Integer.MAX_VALUE ? 0 : bestLength;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * All numbers are positive in this classic problem.
     *
     * That matters because:
     * - When we move right, sum only increases.
     * - When sum is already enough, moving left can make the window smaller.
     *
     * So we do not restart from every index. We slide one window.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [2, 3, 1, 2, 4, 3], target = 7
     *
     * Expand to [2,3,1,2], sum = 8, length = 4.
     * Shrink left: remove 2, sum = 6.
     *
     * Expand to include 4: window [3,1,2,4], sum = 10.
     * Shrink until sum drops below 7.
     *
     * Later window [4,3] has sum 7, best = 2.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Move right across the array and add nums[right] to sum.
     * 2. While sum >= target, update answer.
     * 3. Remove nums[left] and move left to try a smaller valid window.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Prefix sums plus binary search can find the earliest end index for each
     * start when all numbers are positive. The sliding window reaches the same
     * answer in one pass.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int target, int[] nums) {
        int left = 0;
        int sum = 0;
        int bestLength = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];

            while (sum >= target) {
                bestLength = Math.min(bestLength, right - left + 1);
                // Already valid, so remove left to see if a shorter valid window exists.
                sum -= nums[left];
                left++;
            }
        }

        return bestLength == Integer.MAX_VALUE ? 0 : bestLength;
    }
}
