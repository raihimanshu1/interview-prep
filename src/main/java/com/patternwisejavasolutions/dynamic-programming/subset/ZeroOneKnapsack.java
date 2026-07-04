
package com.patternwisejavasolutions.dynamicProgramming.subset;
public class ZeroOneKnapsack {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: weights = [1, 3, 4], values = [15, 20, 30], capacity = 4
     * Sample Output: 35
     *
     * Each item can be taken once or skipped. Maximize total value without
     * crossing the bag capacity.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For every item, we ask: should I put this in the bag or leave it?
     * If it fits, we compare both choices.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try all subsets of items.
     * Keep the best total value among subsets whose weight fits.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. At each item, try skip.
     * 2. If it fits, try take.
     * 3. Return the better result.
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * capacity = 4
     * Item weight 1 value 15: take it or skip it.
     * If taken, remaining capacity becomes 3.
     */
    public int bruteForce(int[] weights, int[] values, int capacity) {
        return choose(weights, values, capacity, 0);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The same item index and remaining capacity appear many times.
     * DP stores the best answer for each capacity as items are processed, so a
     * state like "best with capacity 3 after these items" is not recomputed.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. dp[c] means best value using processed items with capacity c.
     * 2. For each item, scan capacity backward.
     * 3. Backward scan prevents using the same item more than once.
     * Time Complexity: O(n * capacity)
     * Space Complexity: O(capacity)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Item weight 1 value 15 updates capacities 4 down to 1.
     * Later item weight 3 value 20 can combine with capacity 1's saved value.
     */
    public int optimized(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];

        for (int i = 0; i < weights.length; i++) {
            for (int currentCapacity = capacity; currentCapacity >= weights[i]; currentCapacity--) {
                // Take item i: use its value plus the best previous value for leftover capacity.
                int take = values[i] + dp[currentCapacity - weights[i]];
                // Or skip item i and keep the old dp[currentCapacity].
                dp[currentCapacity] = Math.max(dp[currentCapacity], take);
            }
        }

        return dp[capacity];
    }

    private int choose(int[] weights, int[] values, int capacity, int index) {
        if (index == weights.length) {
            // No items remain, so no more value can be added.
            return 0;
        }

        // Choice 1: leave this item out.
        int skip = choose(weights, values, capacity, index + 1);
        int take = 0;
        if (weights[index] <= capacity) {
            // Choice 2: take this item once and reduce remaining capacity.
            take = values[index] + choose(weights, values, capacity - weights[index], index + 1);
        }

        return Math.max(skip, take);
    }
}
