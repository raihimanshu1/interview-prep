package com.patternwithdepth.phase05_sliding_window.k_distinct_count;

/**
 * PROBLEM: Subarrays With K Different Integers (Problem #108)
 * LEETCODE: 992
 *
 * DESCRIPTION:
 * Given an integer array nums and an integer k, return the number of good subarrays
 * where there are exactly k different integers.
 *
 * Example 1:
 * Input: nums = [1,2,1,2,3], k = 2
 * Output: 7
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 2*10^4
 *
 * APPROACH:
 * Sliding window: exactly(k) = atMost(k) - atMost(k-1).
 * Time: O(n), Space: O(n)
 */
public class SW21_SubarraysWithKDifferentIntegers {}
