package com.patternwithdepth.phase06_binary_search.bounds;

/**
 * PROBLEM: Peak Index in a Mountain Array (Problem #116)
 * LEETCODE: 852
 *
 * DESCRIPTION:
 * Given a mountain array (strictly increasing then decreasing), find the peak index.
 *
 * Example 1:
 * Input: arr = [0,1,0]
 * Output: 1
 *
 * CONSTRAINTS:
 * - 3 <= arr.length <= 10^5
 *
 * APPROACH:
 * Binary search. If mid < mid+1, peak is on right, else left.
 * Time: O(log n), Space: O(1)
 */
public class BS07_PeakIndexInMountainArray {}
