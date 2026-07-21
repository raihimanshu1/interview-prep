package com.patternwithdepth.phase05_sliding_window.k_distinct_count;

/**
 * PROBLEM: Count Substrings With K Distinct Characters (Problem #109)
 *
 * DESCRIPTION:
 * Given a string s and an integer k, count the number of substrings that contain exactly k distinct characters.
 *
 * Example 1:
 * Input: s = "pqpqs", k = 2
 * Output: 7
 *
 * CONSTRAINTS:
 * - 1 <= s.length <= 10^4
 *
 * APPROACH:
 * Sliding window with atMost(k) - atMost(k-1).
 * Time: O(n), Space: O(n)
 */
public class SW22_CountSubstringsKDistinct {}
