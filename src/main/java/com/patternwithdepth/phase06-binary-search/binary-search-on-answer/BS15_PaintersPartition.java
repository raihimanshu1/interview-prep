package com.patternwithdepth.phase06_binary_search.binary_search_on_answer;

/**
 * PROBLEM: Painter's Partition Problem (Problem #124)
 *
 * DESCRIPTION:
 * Given k painters and n boards, each board of length L[i]. Each painter paints contiguous boards.
 * Minimize the time to paint all boards (each unit takes 1 time).
 *
 * Example 1:
 * Input: boards = [5,10,30,20,15], k = 3
 * Output: 35
 *
 * CONSTRAINTS:
 * - 1 <= k <= boards.length <= 10^5
 *
 * APPROACH:
 * Binary search on answer (max time per painter).
 * Time: O(n log sum), Space: O(1)
 */
public class BS15_PaintersPartition {}
