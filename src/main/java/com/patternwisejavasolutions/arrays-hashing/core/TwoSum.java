package com.patternwisejavasolutions.arrayshashing.core;

import java.util.HashMap;
import java.util.Map;

public class TwoSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We are given numbers and a target. Return the indices of two different
     * numbers whose sum equals the target.
     *
     * Sample Input:
     * nums = [2, 7, 11, 15], target = 9
     *
     * Sample Output:
     * [0, 1]
     *
     * Why? nums[0] + nums[1] = 2 + 7 = 9.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If two numbers must make a target, then choosing one number immediately
     * tells us the other number we need.
     *
     * Example: target is 9 and current number is 7. The partner must be 2.
     * This is why hashing fits: we can remember earlier numbers and ask whether
     * the needed partner has already appeared.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Try every possible pair. This is like checking every pair of students to
     * see whether their marks add to a required total.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Use i for the first number.
     * 2. Use j for the second number, starting at i + 1.
     * 3. If nums[i] + nums[j] equals target, return [i, j].
     * 4. Return an empty array if no pair exists.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [2, 7, 11, 15], target = 9
     * i = 0, nums[i] = 2
     * j = 1, nums[j] = 7
     * 2 + 7 = 9, so return [0, 1].
     */
    public int[] bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            // nums[i] is the first number of the pair.
            for (int j = i + 1; j < nums.length; j++) {
                // j starts after i so the same element is never used twice.
                if (nums[i] + nums[j] == target) {
                    return new int[] { i, j };
                }
            }
        }

        return new int[] {};
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is searching for the partner again and again.
     * A HashMap stores number -> index for numbers already seen.
     *
     * For each current number, compute needed = target - current. If needed is
     * already in the map, we have found the pair.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Create a HashMap from number to index.
     * 2. For each index i, calculate needed = target - nums[i].
     * 3. If needed is in the map, return [map.get(needed), i].
     * 4. Otherwise, store nums[i] with index i.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [2, 7, 11, 15], target = 9
     * map = {}
     * i = 0, current = 2, needed = 7 -> not found, store 2 -> 0.
     * i = 1, current = 7, needed = 2 -> found at index 0.
     * Return [0, 1].
     */
    public int[] optimized(int[] nums, int target) {
        Map<Integer, Integer> numberToIndex = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int current = nums[i];
            int needed = target - current;

            // If needed was seen earlier, that earlier value plus current makes target.
            if (numberToIndex.containsKey(needed)) {
                return new int[] { numberToIndex.get(needed), i };
            }

            // Store after checking so one element cannot pair with itself.
            numberToIndex.put(current, i);
        }

        return new int[] {};
    }
}

