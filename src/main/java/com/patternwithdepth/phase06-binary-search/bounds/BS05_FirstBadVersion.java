package com.patternwithdepth.phase06_binary_search.bounds;

/**
 * PROBLEM: First Bad Version (Problem #114)
 * LEETCODE: 278
 *
 * DESCRIPTION:
 * Given n versions [1,2,...,n], find the first bad version using isBadVersion API.
 *
 * Example 1:
 * Input: n = 5, bad = 4
 * Output: 4
 *
 * CONSTRAINTS:
 * - 1 <= bad <= n <= 2^31 - 1
 *
 * APPROACH:
 * Binary search for leftmost true.
 * Time: O(log n), Space: O(1)
 */
public class BS05_FirstBadVersion {}
