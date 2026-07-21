package com.patternwithdepth.phase05_sliding_window.expand_window;

/**
 * PROBLEM: Max Consecutive Ones (Problem #93)
 * LEETCODE: 485
 *
 * DESCRIPTION:
 * Given a binary array nums, return the maximum number of consecutive 1's in the array.
 *
 * Example 1:
 * Input: nums = [1,1,0,1,1,1]
 * Output: 3
 * Explanation: The longest consecutive sequence of 1s is 3.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * Expand window while seeing 1, reset on 0.
 * Time: O(n), Space: O(1)
 */
public class SW06_MaxConsecutiveOnes {}
