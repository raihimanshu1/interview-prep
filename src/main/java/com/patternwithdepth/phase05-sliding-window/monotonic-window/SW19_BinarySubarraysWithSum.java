package com.patternwithdepth.phase05_sliding_window.monotonic_window;

/**
 * PROBLEM: Binary Subarrays With Sum (Problem #106)
 * LEETCODE: 930
 *
 * DESCRIPTION:
 * Given a binary array nums and an integer goal, return the number of subarrays with sum equal to goal.
 *
 * Example 1:
 * Input: nums = [1,0,1,0,1], goal = 2
 * Output: 4
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 3*10^4
 *
 * APPROACH:
 * Sliding window with atMost(goal) - atMost(goal-1).
 * Time: O(n), Space: O(1)
 */
public class SW19_BinarySubarraysWithSum {}
