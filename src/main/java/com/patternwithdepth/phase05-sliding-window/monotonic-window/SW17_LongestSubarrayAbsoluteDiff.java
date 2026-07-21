package com.patternwithdepth.phase05_sliding_window.monotonic_window;

/**
 * PROBLEM: Longest Continuous Subarray With Absolute Diff ≤ Limit (Problem #104)
 * LEETCODE: 1438
 *
 * DESCRIPTION:
 * Given an array and a limit, return the size of the longest subarray such that
 * the absolute difference between any two elements is ≤ limit.
 *
 * Example 1:
 * Input: nums = [8,2,4,7], limit = 4
 * Output: 2
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * Two deques (min and max) to track window extremes.
 * Time: O(n), Space: O(n)
 */
public class SW17_LongestSubarrayAbsoluteDiff {}
