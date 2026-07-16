package com.patternwisejavasolutions.bitmanipulation;

public class SubsetsUsingBitmask {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1, 2]
     * Sample Output: [[], [1], [2], [1, 2]]
     *
     * Return all possible subsets of the array.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each number, we make one yes or no decision:
     * include it or leave it out.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Use recursion to try both choices for every index.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. At each index, first skip the number.
     * 2. Then include the number.
     * 3. At the end, save the built subset.
     * Time Complexity: O(n * 2^n)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1, 2]
     * Decisions are no/no -> [], yes/no -> [1], no/yes -> [2], yes/yes -> [1, 2].
     */
    public java.util.List<java.util.List<Integer>> bruteForce(int[] nums) {
        java.util.List<java.util.List<Integer>> answer = new java.util.ArrayList<>();
        build(nums, 0, new java.util.ArrayList<>(), answer);
        return answer;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The recursive tree is really counting from 0 to 2^n - 1 in disguise.
     * A bitmask stores the same yes/no decisions in one integer: if bit i is 1,
     * include nums[i]; if bit i is 0, leave nums[i] out.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. There are 2^n masks.
     * 2. For each mask, inspect every bit.
     * 3. Build one subset from that mask.
     * Time Complexity: O(n * 2^n)
     * Space Complexity: O(n * 2^n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1, 2]
     * mask 00 -> []
     * mask 01 -> [1]
     * mask 10 -> [2]
     * mask 11 -> [1, 2]
     */
    public java.util.List<java.util.List<Integer>> optimized(int[] nums) {
        java.util.List<java.util.List<Integer>> answer = new java.util.ArrayList<>();
        int totalMasks = 1 << nums.length;

        for (int mask = 0; mask < totalMasks; mask++) {
            java.util.List<Integer> subset = new java.util.ArrayList<>();
            for (int bit = 0; bit < nums.length; bit++) {
                if ((mask & (1 << bit)) != 0) {
                    // A 1 bit means "yes, include nums[bit]" for this subset.
                    subset.add(nums[bit]);
                }
            }
            answer.add(subset);
        }

        return answer;
    }

    private void build(int[] nums, int index, java.util.List<Integer> current,
            java.util.List<java.util.List<Integer>> answer) {
        if (index == nums.length) {
            // Reached one complete yes/no decision path.
            answer.add(new java.util.ArrayList<>(current));
            return;
        }

        // Choice 1: leave nums[index] out of this subset.
        build(nums, index + 1, current, answer);
        // Choice 2: include nums[index], explore the rest, then undo the choice.
        current.add(nums[index]);
        build(nums, index + 1, current, answer);
        current.remove(current.size() - 1);
    }
}
