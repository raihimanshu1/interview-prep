package com.patternwisejavasolutions.bitmanipulation;

import java.util.HashSet;
import java.util.Set;

public class SingleNumber {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Every number appears twice except one number. Return the number that
     * appears once.
     *
     * Sample Input: nums = [2, 2, 1]
     * Sample Output: 1
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Pairs cancel out. If two identical cards are removed from a pile, only the
     * unpaired card remains. XOR has the same behavior for bits: x ^ x = 0 and
     * x ^ 0 = x.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Count each number by scanning the whole array. The number with count 1 is
     * the answer.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick a candidate number.
     * 2. Count how many times it appears.
     * 3. Return the candidate whose count is 1.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [2, 2, 1]
     * Count 2 -> two times. Count 1 -> one time, return 1.
     */
    public int bruteForce(int[] nums) {
        for (int candidate : nums) {
            int count = 0;
            for (int num : nums) {
                if (num == candidate) {
                    count++;
                }
            }
            if (count == 1) {
                return candidate;
            }
        }
        return -1;
    }

    public int hashingApproach(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            if (set.contains(num)) {
                set.remove(num);
            } else {
                set.add(num);
            }
        }
        return set.iterator().next();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is repeated counting. XOR cancels duplicate
     * pairs automatically, so one pass is enough.
     *
     * Other useful approach: use a HashSet to add first sightings and remove
     * second sightings, but XOR uses constant space.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start answer as 0.
     * 2. XOR answer with every number.
     * 3. Duplicate numbers cancel to 0.
     * 4. Return the remaining value.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * 0 ^ 2 = 2
     * 2 ^ 2 = 0
     * 0 ^ 1 = 1
     */
    public int optimized(int[] nums) {
        int answer = 0;
        for (int num : nums) {
            // XOR removes pairs and keeps the unpaired value.
            answer ^= num;
        }
        return answer;
    }
}

