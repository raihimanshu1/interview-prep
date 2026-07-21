package com.patternwithdepth.phase05_sliding_window.fixed_window;

/**
 * PROBLEM: K Radius Subarray Average (Problem #91)
 * LEETCODE: 2090
 *
 * DESCRIPTION:
 * Given a 0-indexed array nums and an integer k, return an array avgs where avgs[i] is the k-radius average.
 * The k-radius average is the average of all elements in range [i-k, i+k].
 *
 * Example 1:
 * Input: nums = [7,4,3,9,1,8,5,2,6], k = 3
 * Output: [-1,-1,-1,5,4,4,-1,-1,-1]
 *
 * CONSTRAINTS:
 * - 1 <= n <= 10^5
 *
 * APPROACH:
 * Prefix sum to compute window sums efficiently.
 * Time: O(n), Space: O(n)
 */
public class SW04_KRadiusSubarrayAverage {}
