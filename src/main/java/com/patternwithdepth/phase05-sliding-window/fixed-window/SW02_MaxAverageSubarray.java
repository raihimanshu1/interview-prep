package com.patternwithdepth.phase05_sliding_window.fixed_window;

/**
 * PROBLEM: Maximum Average Subarray I (Problem #89)
 * LEETCODE: 643
 *
 * DESCRIPTION:
 * Given an array and an integer k, find the maximum average value of any contiguous subarray of size k.
 *
 * Example 1:
 * Input: nums = [1,12,-5,-6,50,3], k = 4
 * Output: 12.75
 * Explanation: Max average is 12.75 from subarray [12,-5,-6,50]
 *
 * CONSTRAINTS:
 * - 1 <= k <= nums.length <= 10^5
 *
 * APPROACH:
 * Fixed sliding window. Track max sum, divide by k at end.
 * Time: O(n), Space: O(1)
 */
public class SW02_MaxAverageSubarray {}
