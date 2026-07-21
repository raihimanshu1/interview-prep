package com.patternwithdepth.phase05_sliding_window.monotonic_window;

/**
 * PROBLEM: Sliding Window Maximum (Problem #103)
 * LEETCODE: 239
 *
 * DESCRIPTION:
 * Given an array nums and a sliding window of size k moving from left to right,
 * return an array of the maximum element in each window.
 *
 * Example 1:
 * Input: nums = [1,3,-1,-3,5,3,6,7], k = 3
 * Output: [3,3,5,5,6,7]
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5, 1 <= k <= nums.length
 *
 * APPROACH:
 * Deque (monotonic queue) to store indices of elements in decreasing order.
 * Time: O(n), Space: O(k)
 */
public class SW16_SlidingWindowMaximum {}
