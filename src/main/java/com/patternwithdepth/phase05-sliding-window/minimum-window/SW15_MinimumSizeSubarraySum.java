package com.patternwithdepth.phase05_sliding_window.minimum_window;

/**
 * PROBLEM: Minimum Size Subarray Sum (Problem #102)
 * LEETCODE: 209
 *
 * DESCRIPTION:
 * Given an array of positive integers nums and a positive integer target, return the minimal length
 * of a contiguous subarray whose sum >= target. If none, return 0.
 *
 * Example 1:
 * Input: target = 7, nums = [2,3,1,2,4,3]
 * Output: 2
 * Explanation: Subarray [4,3] has length 2.
 *
 * CONSTRAINTS:
 * - 1 <= target <= 10^9, 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * Sliding window. Expand right, shrink left when sum >= target.
 * Time: O(n), Space: O(1)
 */
public class SW15_MinimumSizeSubarraySum {}
