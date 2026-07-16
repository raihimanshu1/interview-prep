package com.companywisejavasolutions.ebay.solutions;

import java.util.Arrays;

public class MinimizeMaximumPairSumInArray {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Pair all numbers so the largest pair sum is as small as possible.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Match the smallest number with the largest number. This balances pairs,
     * because large numbers get help from small partners.
     */

    /*
     * Time Complexity: O(n log n)
     * Space Complexity: O(1) besides sorting internals
     */
    public int minPairSum(int[] nums) {
        Arrays.sort(nums);
        int left = 0;
        int right = nums.length - 1;
        int answer = 0;

        while (left < right) {
            answer = Math.max(answer, nums[left] + nums[right]);
            left++;
            right--;
        }

        return answer;
    }
}
