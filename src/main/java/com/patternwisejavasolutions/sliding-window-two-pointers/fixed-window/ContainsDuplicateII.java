
package com.patternwisejavasolutions.slidingWindowTwoPointers.fixedWindow;
import java.util.HashSet;
import java.util.Set;

public class ContainsDuplicateII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1,2,3,1], k = 3
     * Sample Output: true
     *
     * Return true if two equal values have indexes at most k apart.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each number, look near it. If the same number appears within k steps,
     * the answer is true.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try every pair of indexes and check both conditions: same value and
     * distance at most k.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick left index.
     * 2. Pick right index after left.
     * 3. If nums[left] == nums[right] and right - left <= k, return true.
     * 4. If no pair works, return false.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,2,3,1], k = 3
     * Compare index 0 and 3: same value 1 and distance 3.
     * Return true.
     */

    public boolean bruteForce(int[] nums, int k) {
        for (int left = 0; left < nums.length; left++) {
            for (int right = left + 1; right < nums.length; right++) {
                if (nums[left] == nums[right] && right - left <= k) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Brute force repeats pair checks for numbers that are too far away.
     * At index right, only the previous k indexes can form a valid pair with it.
     * Keep just those last k numbers in a set. If nums[right] is already in
     * that set, a close duplicate exists.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep a sliding set of values in the last k indexes.
     * 2. Before adding nums[right], check if it already exists.
     * 3. Add nums[right].
     * 4. If window grows bigger than k, remove nums[right - k].
     *
     * Time Complexity: O(n)
     * Space Complexity: O(k)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,2,3,1], k = 3
     * set before last 1 is {1,2,3}; last 1 already exists.
     * Return true.
     */

    public boolean optimized(int[] nums, int k) {
        Set<Integer> window = new HashSet<>();

        for (int right = 0; right < nums.length; right++) {
            if (window.contains(nums[right])) {
                // Same value already appeared within the active distance-k window.
                return true;
            }

            window.add(nums[right]);

            // Keep only values whose indexes can still be within distance k.
            if (right >= k) {
                window.remove(nums[right - k]);
            }
        }

        return false;
    }
}
