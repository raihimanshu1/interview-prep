package com.patternwithdepth.phase05_sliding_window.fixed_window;

/**
 * PROBLEM: Maximum Sum Subarray of Size K (Problem #88)
 *
 * DESCRIPTION:
 * Given an array of integers and a number k, find the maximum sum of any contiguous subarray of size k.
 *
 * Example 1:
 * Input: nums = [2,1,5,1,3,2], k = 3
 * Output: 9
 * Explanation: Subarray [5,1,3] has max sum 9.
 *
 * Example 2:
 * Input: nums = [1,2], k = 1
 * Output: 2
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - 1 <= k <= nums.length
 *
 * APPROACH:
 * Fixed sliding window. Compute sum of first k, then slide.
 * Time: O(n), Space: O(1)
 */
public class SW01_MaxSumSubarrayOfSizeK {}
