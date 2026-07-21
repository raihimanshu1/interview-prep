package com.patternwithdepth.phase06_binary_search.classic;

/**
 * PROBLEM: Sqrt(x) (Problem #113)
 * LEETCODE: 69
 *
 * DESCRIPTION:
 * Given a non-negative integer x, return the square root rounded down to the nearest integer.
 *
 * Example 1:
 * Input: x = 8
 * Output: 2
 * Explanation: sqrt(8) = 2.828..., floor = 2.
 *
 * CONSTRAINTS:
 * - 0 <= x <= 2^31 - 1
 *
 * APPROACH:
 * Binary search on answer from 0 to x.
 * Time: O(log x), Space: O(1)
 */
public class BS04_SqrtX {}
