package com.patternwithdepth.phase05_sliding_window.frequency_window;

/**
 * PROBLEM: Find All Anagrams in a String (Problem #100)
 * LEETCODE: 438
 *
 * DESCRIPTION:
 * Given two strings s and p, return an array of all start indices of p's anagrams in s.
 *
 * Example 1:
 * Input: s = "cbaebabacd", p = "abc"
 * Output: [0,6]
 * Explanation: "cba" at 0, "bac" at 6.
 *
 * CONSTRAINTS:
 * - 1 <= s.length, p.length <= 3*10^4
 *
 * APPROACH:
 * Fixed sliding window with frequency array, sliding character by character.
 * Time: O(n), Space: O(1)
 */
public class SW13_FindAllAnagrams {}
