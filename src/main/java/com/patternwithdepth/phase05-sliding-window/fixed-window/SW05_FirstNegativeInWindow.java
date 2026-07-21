package com.patternwithdepth.phase05_sliding_window.fixed_window;

/**
 * PROBLEM: First Negative Number in Every Window of Size K (Problem #92)
 *
 * DESCRIPTION:
 * Given an array and an integer k, for every contiguous subarray of size k, find the first negative number.
 * Return an array of these values. If no negative exists, store 0.
 *
 * Example 1:
 * Input: nums = [12,-1,-7,8,-15,30,16,28], k = 3
 * Output: [-1,-1,-7,-15,-15,0]
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 *
 * APPROACH:
 * Deque to store indices of negative numbers in current window.
 * Time: O(n), Space: O(k)
 */
public class SW05_FirstNegativeInWindow {}
