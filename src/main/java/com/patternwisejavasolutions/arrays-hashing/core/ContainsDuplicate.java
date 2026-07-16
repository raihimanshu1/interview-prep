package com.patternwisejavasolutions.arrayshashing.core;

import java.util.HashSet;
import java.util.Set;

public class ContainsDuplicate {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We are given an integer array. We must tell whether any value appears
     * at least two times.
     *
     * Sample Input:
     * nums = [1, 2, 3, 1]
     *
     * Sample Output:
     * true
     *
     * Why? The value 1 appears at index 0 and index 3.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Imagine checking a classroom attendance list. If the same name appears
     * twice, the list has a duplicate. The most direct way is to compare every
     * name with every name after it.
     *
     * The pattern is hashing because the optimized solution keeps a memory box
     * of values we have already seen. If a value is already in that box, it is
     * a duplicate.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Pick one number and compare it with all numbers to its right. If any pair
     * is equal, we found a duplicate.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Let i choose the first number.
     * 2. Let j choose every later number.
     * 3. If nums[i] equals nums[j], return true.
     * 4. If no equal pair is found, return false.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1, 2, 3, 1]
     * i = 0 -> nums[i] = 1
     * Compare with 2, then 3, then 1.
     * The last comparison matches, so return true.
     */
    public boolean bruteForce(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            // i is the value we are trying to find again later in the array.
            for (int j = i + 1; j < nums.length; j++) {
                // j starts after i so we compare two different positions only once.
                if (nums[i] == nums[j]) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is repeated searching. For every number, we
     * scan many later numbers. A HashSet removes that repeated work.
     *
     * HashSet answers one simple question quickly: "Have I seen this value
     * before?" That is exactly what duplicate checking needs.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Create an empty HashSet.
     * 2. Read each number from left to right.
     * 3. If the number is already in the set, return true.
     * 4. Otherwise, add it to the set.
     * 5. Return false if the loop finishes.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1, 2, 3, 1]
     * seen = {}
     * Read 1 -> not present, add it.
     * Read 2 -> not present, add it.
     * Read 3 -> not present, add it.
     * Read 1 -> already present, return true.
     */
    public boolean optimized(int[] nums) {
        Set<Integer> seen = new HashSet<>();

        for (int num : nums) {
            // contains() checks our memory before we add the current value.
            if (seen.contains(num)) {
                return true;
            }

            // Store this value so future numbers can be compared against it fast.
            seen.add(num);
        }

        return false;
    }
}

