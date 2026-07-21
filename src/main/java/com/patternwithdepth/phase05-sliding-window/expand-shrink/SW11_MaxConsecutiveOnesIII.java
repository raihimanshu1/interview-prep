package com.patternwithdepth.phase05_sliding_window.expand_shrink;

/**
 * PROBLEM: Max Consecutive Ones III (Problem #98)
 * LEETCODE: 1004
 *
 * DESCRIPTION:
 * Given a binary array nums and an integer k, return the max number of consecutive 1's you can get
 * by flipping at most k 0's.
 *
 * Example 1:
 * Input: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
 * Output: 6
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * Sliding window. Track zeros in window. Shrink if zeros > k.
 * Time: O(n), Space: O(1)
 */
public class SW11_MaxConsecutiveOnesIII {}
