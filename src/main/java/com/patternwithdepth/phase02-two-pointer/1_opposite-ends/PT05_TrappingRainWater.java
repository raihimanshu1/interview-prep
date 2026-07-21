package com.patternwithdepth.phase02_two_pointer.opposite_ends;

/**
 * PROBLEM: Trapping Rain Water
 * LEETCODE: 42
 *
 * DESCRIPTION:
 * Given n non-negative integers representing an elevation map where the width of
 * each bar is 1, compute how much water it can trap after raining.
 *
 * Example 1:
 * Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
 * Output: 6
 * Explanation: Water trapped = 6 units.
 *
 * Example 2:
 * Input: height = [4,2,0,3,2,5]
 * Output: 9
 *
 * CONSTRAINTS:
 * - 1 <= n <= 2*10^4
 * - 0 <= height[i] <= 10^5
 *
 * APPROACH:
 * Two pointers with leftMax and rightMax tracking.
 * Time: O(n), Space: O(1)
 */
public class PT05_TrappingRainWater {}
