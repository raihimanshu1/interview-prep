package com.patternwithdepth.phase05_sliding_window.fixed_window;

/**
 * PROBLEM: Maximum Number of Vowels in a Substring of Length K (Problem #90)
 * LEETCODE: 1456
 *
 * DESCRIPTION:
 * Given a string s and an integer k, return the maximum number of vowel letters in any substring of s of length k.
 *
 * Example 1:
 * Input: s = "abciiidef", k = 3
 * Output: 3
 * Explanation: Substring "iii" contains 3 vowels.
 *
 * CONSTRAINTS:
 * - 1 <= s.length <= 10^5
 * - 'a', 'e', 'i', 'o', 'u' are vowels
 *
 * APPROACH:
 * Fixed sliding window with vowel check.
 * Time: O(n), Space: O(1)
 */
public class SW03_MaxVowelsInSubstring {}
