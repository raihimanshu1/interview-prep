package com.patternwisejavasolutions.dynamicprogramming.core;

public class LongestIncreasingSubsequence {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [10,9,2,5,3,7,101,18]
     * Sample Output: 4
     *
     * Input: nums = [10,9,2,5,3,7,101,18]
     * Output: 4 because one LIS is [2,3,7,101].
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each number, ask:
     * What increasing subsequence can end here?
     * Look at all previous smaller numbers and extend the best one.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * At each index, either skip the number or take it if it is larger than the
     * last number we took. This creates all increasing subsequences and returns
     * the longest length among those branches.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * At number 7:
     * Previous smaller numbers include 2,5,3.
     * Best subsequence ending before it has length 2.
     * So length ending at 7 is 3.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively choose or skip each number.
     * 2. Only choose current if it is greater than previous chosen.
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This method explicitly explores take and skip choices.
     */
    public int bruteForce(int[] nums) {
        return choose(nums, 0, Integer.MIN_VALUE);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Let dp[i] mean LIS length ending exactly at i.
     * Every element alone has length 1.
     * If nums[j] < nums[i], we can extend dp[j]. This reuses the best answer
     * ending at j instead of rebuilding every earlier subsequence.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * nums = [2,5,3,7]
     * dp[0]=1
     * 5 extends 2 -> dp[1]=2
     * 3 extends 2 -> dp[2]=2
     * 7 extends best of 5 or 3 -> dp[3]=3
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Fill dp with 1.
     * 2. For every i, check all j before i.
     * 3. If nums[j] < nums[i], update dp[i].
     * 4. Return max dp.
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[i] records the best increasing subsequence that must end at i.
     */
    public int optimized(int[] nums) {
        int[] dp = new int[nums.length];
        int answer = 0;

        for (int i = 0; i < nums.length; i++) {
            // A single number is an increasing subsequence of length 1.
            dp[i] = 1;

            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    // Extend a valid smaller ending number.
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }

            answer = Math.max(answer, dp[i]);
        }

        return answer;
    }


    private int choose(int[] nums, int index, int previous) {
        if (index == nums.length) {
            // No numbers left to add.
            return 0;
        }

        // Option 1: ignore nums[index].
        int skip = choose(nums, index + 1, previous);
        int take = 0;

        if (nums[index] > previous) {
            // Option 2: take it only if increasing order is preserved.
            take = 1 + choose(nums, index + 1, nums[index]);
        }

        return Math.max(skip, take);
    }
}
