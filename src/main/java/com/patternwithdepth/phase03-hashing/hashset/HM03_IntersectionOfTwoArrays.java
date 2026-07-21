package com.patternwithdepth.phase03_hashing.hashset;

/**
 * PROBLEM: Intersection of Two Arrays
 * LEETCODE: 349
 *
 * DESCRIPTION:
 * Given two integer arrays nums1 and nums2, return an array of their intersection.
 * Each element in the result must be unique.
 *
 * Example 1:
 * Input: nums1 = [1,2,2,1], nums2 = [2,2]
 * Output: [2]
 *
 * Example 2:
 * Input: nums1 = [4,9,5], nums2 = [9,4,9,8,4]
 * Output: [9,4]
 *
 * CONSTRAINTS:
 * - 1 <= nums1.length, nums2.length <= 1000
 *
 * APPROACH:
 * Use HashSet for nums1, then check nums2 against it.
 * Time: O(n+m), Space: O(n)
 */
public class HM03_IntersectionOfTwoArrays {}
