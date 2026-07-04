
package com.patternwisejavasolutions.arraysHashing.core;
import java.util.HashSet;
import java.util.Set;

public class LongestConsecutiveSequence {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Find the length of the longest chain of consecutive numbers in an unsorted
     * array.
     *
     * Sample Input:
     * nums = [100, 4, 200, 1, 3, 2]
     *
     * Sample Output:
     * 4
     *
     * Why? The longest chain is [1, 2, 3, 4].
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Consecutive means numbers come one after another, like 5, 6, 7. The array
     * is not sorted, so nearby values may be far apart in the array.
     *
     * Hashing fits because we need quick membership checks: "Does x + 1 exist?"
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Start from every number and repeatedly search the array for the next
     * number. Count how long the chain can grow.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each number, set current = number and length = 1.
     * 2. While current + 1 exists in the array, move to it and increase length.
     * 3. Track the maximum length.
     *
     * Time Complexity: O(n^3) in the simple repeated-search version
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [100, 4, 200, 1, 3, 2]
     * Start at 1 -> find 2 -> find 3 -> find 4 -> cannot find 5.
     * Chain length is 4.
     */
    public int bruteForce(int[] nums) {
        int best = 0;

        for (int num : nums) {
            int current = num;
            int length = 1;

            while (exists(nums, current + 1)) {
                current++;
                length++;
            }

            best = Math.max(best, length);
        }

        return best;
    }

    private boolean exists(int[] nums, int target) {
        for (int num : nums) {
            if (num == target) {
                return true;
            }
        }
        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is repeatedly searching the array. Put every
     * value in a HashSet so "does this value exist?" becomes fast.
     *
     * Also, only start counting from numbers that are true starts. A number is a
     * start if num - 1 does not exist. This prevents recounting the same chain.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Add all numbers to a HashSet.
     * 2. For each number, check whether num - 1 is absent.
     * 3. If it is absent, num starts a chain.
     * 4. Count num, num + 1, num + 2, ... while present.
     * 5. Track the longest length.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * set = {100, 4, 200, 1, 3, 2}
     * 1 has no 0, so start chain: 1, 2, 3, 4 -> length 4.
     * 2, 3, 4 are skipped as starts because each has a previous number.
     */
    public int optimized(int[] nums) {
        Set<Integer> values = new HashSet<>();
        for (int num : nums) {
            values.add(num);
        }

        int best = 0;

        for (int num : values) {
            // Only begin at the first number of a chain to avoid duplicate work.
            if (!values.contains(num - 1)) {
                int current = num;
                int length = 1;

                while (values.contains(current + 1)) {
                    current++;
                    length++;
                }

                best = Math.max(best, length);
            }
        }

        return best;
    }
}

