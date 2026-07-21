package com.patternwithdepth.phase05_sliding_window.expand_shrink;

/**
 * PROBLEM: Longest Substring Without Repeating Characters (Problem #95)
 * LEETCODE: 3
 *
 * DESCRIPTION:
 * Given a string s, find the length of the longest substring without repeating characters.
 *
 * Example 1:
 * Input: s = "abcabcbb"
 * Output: 3
 * Explanation: "abc" has length 3.
 *
 * CONSTRAINTS:
 * - 0 <= s.length <= 5*10^4
 *
 * APPROACH:
 * Sliding window with HashMap to track last index of each character.
 * Time: O(n), Space: O(min(m, n))
 */
public class SW08_LongestSubstringWithoutRepeating {}
