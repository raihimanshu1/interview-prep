package com.patternwithdepth.phase02_two_pointer.opposite_ends;

/**
 * PROBLEM: Container With Most Water
 * LEETCODE: 11
 *
 * DESCRIPTION:
 * Given n non-negative integers representing heights of vertical lines,
 * find two lines that together with x-axis form a container that holds the most water.
 *
 * Example 1:
 * Input: height = [1,8,6,2,5,4,8,3,7]
 * Output: 49
 * Explanation: Max area = min(8,7) * 7 = 49
 *
 * Example 2:
 * Input: height = [1,1]
 * Output: 1
 *
 * CONSTRAINTS:
 * - 2 <= n <= 10^5
 * - 0 <= height[i] <= 10^4
 *
 * APPROACH:
 * Two pointers from ends. Move the pointer with the smaller height.
 * Time: O(n), Space: O(1)
 */
public class PT04_ContainerWithMostWater {}
