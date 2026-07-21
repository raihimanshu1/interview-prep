package com.patternwithdepth.phase05_sliding_window.minimum_window;

/**
 * PROBLEM: Minimum Window Substring (Problem #101)
 * LEETCODE: 76
 *
 * DESCRIPTION:
 * Given two strings s and t, return the minimum window substring of s such that every character in t
 * (including duplicates) is included in the window.
 *
 * Example 1:
 * Input: s = "ADOBECODEBANC", t = "ABC"
 * Output: "BANC"
 *
 * CONSTRAINTS:
 * - m == s.length, n == t.length, 1 <= m,n <= 10^5
 *
 * APPROACH:
 * Sliding window with two frequency arrays. Expand right, shrink left.
 * Time: O(m + n), Space: O(1)
 */
public class SW14_MinimumWindowSubstring {}
