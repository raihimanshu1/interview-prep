package com.patternwithdepth.phase05_sliding_window.k_distinct_count;

/**
 * PROBLEM: Longest Substring With At Most K Distinct Characters (Problem #107)
 * LEETCODE: 340
 *
 * DESCRIPTION:
 * Given a string s and an integer k, return the length of the longest substring
 * that contains at most k distinct characters.
 *
 * Example 1:
 * Input: s = "eceba", k = 2
 * Output: 3
 * Explanation: "ece" has length 3 with 2 distinct characters.
 *
 * CONSTRAINTS:
 * - 1 <= s.length <= 5*10^4
 *
 * APPROACH:
 * Sliding window with HashMap tracking character count.
 * Time: O(n), Space: O(k)
 */
public class SW20_LongestSubstringKDistinct {}
