package com.companywisejavasolutions.ebay.solutions;

public class ShortestUnsortedContinuousSubarray {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return the length of the shortest continuous subarray which, if sorted,
     * makes the whole array sorted.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * From the left, track the maximum seen so far. If a number is smaller than
     * that maximum, it belongs inside the unsorted region. From the right, do the
     * same with the minimum.
     */
    public int findUnsortedSubarray(int[] nums) {
        int n = nums.length;
        int right = -1;
        int maxSeen = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            maxSeen = Math.max(maxSeen, nums[i]);
            if (nums[i] < maxSeen) right = i;
        }

        int left = 0;
        int minSeen = Integer.MAX_VALUE;
        for (int i = n - 1; i >= 0; i--) {
            minSeen = Math.min(minSeen, nums[i]);
            if (nums[i] > minSeen) left = i;
        }

        return right == -1 ? 0 : right - left + 1;
    }
}
