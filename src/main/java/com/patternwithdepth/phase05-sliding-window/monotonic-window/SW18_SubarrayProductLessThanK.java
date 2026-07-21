package com.patternwithdepth.phase05_sliding_window.monotonic_window;

/**
 * PROBLEM: Subarray Product Less Than K (Problem #105)
 * LEETCODE: 713
 *
 * DESCRIPTION:
 * Given an array of positive integers nums and an integer k, return the number of contiguous
 * subarrays where the product of all elements in the subarray is less than k.
 *
 * Example 1:
 * Input: nums = [10,5,2,6], k = 100
 * Output: 8
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 3*10^4
 *
 * APPROACH:
 * Sliding window. Expand right, shrink left when product >= k.
 * Time: O(n), Space: O(1)
 */
public class SW18_SubarrayProductLessThanK {}
