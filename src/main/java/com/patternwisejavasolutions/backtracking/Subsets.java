package com.patternwisejavasolutions.backtracking;

import java.util.ArrayList;
import java.util.List;

public class Subsets {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1,2,3]
     * Sample Output: 8 subsets, including [] and [1,2,3]
     *
     * Input: nums = [1, 2, 3]
     * Output contains every possible subset:
     * [], [1], [2], [3], [1,2], [1,3], [2,3], [1,2,3]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each number, we have two choices:
     * 1. Do not take it.
     * 2. Take it.
     * Backtracking is just trying both choices in a clean order.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Stand at each number and make the yes/no decision: leave it out, then
     * try the rest; put it in, then try the rest. The recursion tree has two
     * branches per number, so it naturally lists every possible subset.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * At 1: skip 1 -> later subsets without 1.
     * At 1: take 1 -> later subsets with 1.
     * Repeating this for 2 and 3 creates all subsets.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Use recursion with an index.
     * 2. At every index, first skip nums[index].
     * 3. Then take nums[index].
     * 4. When index reaches nums.length, save the current subset.
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(n) recursion, ignoring answer
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This version makes the two branches, skip and take, very explicit.
     */
    public List<List<Integer>> bruteForce(int[] nums) {
        List<List<Integer>> answer = new ArrayList<>();
        buildByTakeOrSkip(nums, 0, new ArrayList<>(), answer);
        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * For generating all subsets, backtracking is already optimal because the output itself has 2^n subsets.
     * The clean version focuses on readable choose/explore/undo steps.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * current = []
     * Choose 1 -> [1]
     * Choose 2 -> [1,2]
     * Undo 2 -> [1]
     * Choose 3 -> [1,3]
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Add a copy of current subset to answer at every recursion call.
     * 2. Try each next number.
     * 3. Add it, recurse, then remove it.
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * This version saves every prefix as a subset and then extends it forward.
     */
    public List<List<Integer>> optimized(int[] nums) {
        List<List<Integer>> answer = new ArrayList<>();
        buildSubsets(nums, 0, new ArrayList<>(), answer);
        return answer;
    }


    private void buildByTakeOrSkip(int[] nums, int index, List<Integer> current, List<List<Integer>> answer) {
        if (index == nums.length) {
            // Reached a leaf in the take/skip tree; current is one finished subset.
            answer.add(new ArrayList<>(current));
            return;
        }

        // Skip nums[index].
        buildByTakeOrSkip(nums, index + 1, current, answer);

        // Take nums[index], explore, then remove it so the caller's list is restored.
        current.add(nums[index]);
        buildByTakeOrSkip(nums, index + 1, current, answer);
        current.remove(current.size() - 1);
    }

    private void buildSubsets(int[] nums, int start, List<Integer> current, List<List<Integer>> answer) {
        // Every current prefix is a valid subset, including the empty subset.
        answer.add(new ArrayList<>(current));

        for (int index = start; index < nums.length; index++) {
            // Only move forward so the same subset is not produced in a different order.
            current.add(nums[index]);
            buildSubsets(nums, index + 1, current, answer);
            current.remove(current.size() - 1);
        }
    }
}
