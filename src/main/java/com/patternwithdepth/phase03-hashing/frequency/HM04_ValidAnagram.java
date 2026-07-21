package com.patternwithdepth.phase03_hashing.frequency;

/**
 * PROBLEM: Valid Anagram
 * LEETCODE: 242
 *
 * DESCRIPTION:
 * Given two strings s and t, return true if t is an anagram of s.
 *
 * Example 1:
 * Input: s = "anagram", t = "nagaram"
 * Output: true
 *
 * Example 2:
 * Input: s = "rat", t = "car"
 * Output: false
 *
 * CONSTRAINTS:
 * - 1 <= s.length, t.length <= 5*10^4
 *
 * APPROACH:
 * Use int[26] frequency array, increment for s, decrement for t.
 * Time: O(n), Space: O(1)
 */
public class HM04_ValidAnagram {}
