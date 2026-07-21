package com.patternwithdepth.phase02_two_pointer.opposite_ends;

/**
 * PROBLEM: Two Sum II - Input Array Is Sorted
 * LEETCODE: 167
 *
 * DESCRIPTION:
 * Given a 1-indexed sorted array and a target, find two numbers that sum to target.
 * Return the indices (1-indexed) of the two numbers.
 *
 * Example 1:
 * Input: numbers = [2,7,11,15], target = 9
 * Output: [1,2]
 * Explanation: 2 + 7 = 9
 *
 * Example 2:
 * Input: numbers = [2,3,4], target = 6
 * Output: [1,3]
 *
 * CONSTRAINTS:
 * - 2 <= numbers.length <= 3*10^4
 * - Exactly one solution exists
 *
 * APPROACH:
 * Two pointers: left=0, right=n-1. If sum < target, left++; else right--.
 * Time: O(n), Space: O(1)
 */
public class PT03_TwoSumII {}
