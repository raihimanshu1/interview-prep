package com.patternwithdepth.phase03_hashing.frequency;

/**
 * PROBLEM: Majority Element
 * LEETCODE: 169
 *
 * DESCRIPTION:
 * Given an array nums of size n, return the majority element.
 * The majority element is the element that appears more than ⌊n/2⌋ times.
 *
 * Example 1:
 * Input: nums = [3,2,3]
 * Output: 3
 *
 * Example 2:
 * Input: nums = [2,2,1,1,1,2,2]
 * Output: 2
 *
 * CONSTRAINTS:
 * - 1 <= n <= 5*10^4
 * - Majority element always exists
 *
 * APPROACH:
 * Boyer-Moore Voting Algorithm: candidate + count.
 * Time: O(n), Space: O(1)
 */
public class HM07_MajorityElement {}
