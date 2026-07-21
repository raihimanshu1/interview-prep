package com.patternwithdepth.phase03_hashing.frequency;

/**
 * PROBLEM: Find All Numbers Disappeared in an Array
 * LEETCODE: 448
 *
 * DESCRIPTION:
 * Given an array nums of n integers where nums[i] is in the range [1, n],
 * return an array of all integers in [1, n] that do not appear.
 *
 * Example 1:
 * Input: nums = [4,3,2,7,8,2,3,1]
 * Output: [5,6]
 *
 * Example 2:
 * Input: nums = [1,1]
 * Output: [2]
 *
 * CONSTRAINTS:
 * - 1 <= n <= 10^5
 *
 * APPROACH:
 * Mark visited by negating nums[abs(value)-1].
 * Time: O(n), Space: O(1)
 */
public class HM08_FindAllNumbersDisappeared {}
