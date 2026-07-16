package com.companywisejavasolutions.ebay.solutions;

import java.util.ArrayDeque;
import java.util.Deque;

public class LongestContinuousSubarrayWithAbsoluteDiffLessThanOrEqualToLimit {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Find the longest subarray where max value minus min value is at most limit.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A window is valid only if its biggest and smallest numbers are close enough.
     * Two deques let us know the current window max and min quickly.
     */

    /*
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int longestSubarray(int[] nums, int limit) {
        Deque<Integer> maxDeque = new ArrayDeque<>();
        Deque<Integer> minDeque = new ArrayDeque<>();
        int left = 0;
        int best = 0;

        for (int right = 0; right < nums.length; right++) {
            while (!maxDeque.isEmpty() && nums[maxDeque.peekLast()] < nums[right]) {
                maxDeque.pollLast();
            }
            maxDeque.offerLast(right);

            while (!minDeque.isEmpty() && nums[minDeque.peekLast()] > nums[right]) {
                minDeque.pollLast();
            }
            minDeque.offerLast(right);

            while (nums[maxDeque.peekFirst()] - nums[minDeque.peekFirst()] > limit) {
                if (maxDeque.peekFirst() == left) maxDeque.pollFirst();
                if (minDeque.peekFirst() == left) minDeque.pollFirst();
                left++;
            }

            best = Math.max(best, right - left + 1);
        }

        return best;
    }
}
