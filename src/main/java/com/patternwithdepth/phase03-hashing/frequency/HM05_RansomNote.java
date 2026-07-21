package com.patternwithdepth.phase03_hashing.frequency;

/**
 * PROBLEM: Ransom Note
 * LEETCODE: 383
 *
 * DESCRIPTION:
 * Given two strings ransomNote and magazine, return true if ransomNote can be
 * constructed by using the letters from magazine.
 *
 * Example 1:
 * Input: ransomNote = "a", magazine = "b"
 * Output: false
 *
 * Example 2:
 * Input: ransomNote = "aa", magazine = "ab"
 * Output: false
 *
 * Example 3:
 * Input: ransomNote = "aa", magazine = "aab"
 * Output: true
 *
 * CONSTRAINTS:
 * - 1 <= ransomNote.length, magazine.length <= 10^5
 *
 * APPROACH:
 * Count letters in magazine, decrement for ransomNote.
 * Time: O(n), Space: O(1)
 */
public class HM05_RansomNote {}
