package com.patternwithdepth.phase05_sliding_window.expand_shrink;

/**
 * PROBLEM: Longest Repeating Character Replacement (Problem #97)
 * LEETCODE: 424
 *
 * DESCRIPTION:
 * Given a string s and an integer k, you can choose any character and change it to any other at most k times.
 * Return the length of the longest substring containing the same letter after k replacements.
 *
 * Example 1:
 * Input: s = "ABAB", k = 2
 * Output: 4
 *
 * CONSTRAINTS:
 * - 1 <= s.length <= 10^5
 *
 * APPROACH:
 * Sliding window. Track max frequency in window. Shrink if window - maxFreq > k.
 * Time: O(n), Space: O(1)
 */
public class SW10_LongestRepeatingCharReplacement {}
