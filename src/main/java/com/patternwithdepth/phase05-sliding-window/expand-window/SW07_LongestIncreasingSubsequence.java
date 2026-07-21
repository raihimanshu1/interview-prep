package com.patternwithdepth.phase05_sliding_window.expand_window;

/**
 * PROBLEM: Longest Continuous Increasing Subsequence (Problem #94)
 * LEETCODE: 674
 *
 * DESCRIPTION:
 * Given an unsorted array, find the length of the longest continuous increasing subsequence.
 *
 * Example 1:
 * Input: nums = [1,3,5,4,7]
 * Output: 3
 * Explanation: [1,3,5] is the longest continuous increasing subsequence.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^4
 *
 * APPROACH:
 * Track current run length, reset when order breaks.
 * Time: O(n), Space: O(1)
 */
public class SW07_LongestIncreasingSubsequence {}
