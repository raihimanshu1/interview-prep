package com.patternwithdepth.phase03_hashing.frequency;

/**
 * PROBLEM: First Unique Character in a String
 * LEETCODE: 387
 *
 * DESCRIPTION:
 * Given a string s, find the first non-repeating character and return its index.
 * If it does not exist, return -1.
 *
 * Example 1:
 * Input: s = "leetcode"
 * Output: 0
 *
 * Example 2:
 * Input: s = "loveleetcode"
 * Output: 2
 *
 * Example 3:
 * Input: s = "aabb"
 * Output: -1
 *
 * CONSTRAINTS:
 * - 1 <= s.length <= 10^5
 *
 * APPROACH:
 * Count frequencies in first pass, find first with count=1 in second pass.
 * Time: O(n), Space: O(1)
 */
public class HM06_FirstUniqueCharacter {}
