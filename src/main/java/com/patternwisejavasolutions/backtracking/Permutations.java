package com.patternwisejavasolutions.backtracking;

import java.util.ArrayList;
import java.util.List;

public class Permutations {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1,2,3]
     * Sample Output: all 6 orderings
     *
     * Input: nums = [1,2,3]
     * Output contains all orderings such as [1,2,3], [1,3,2], [2,1,3], etc.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A permutation is an ordering.
     * For the first position, try every number.
     * For the next position, try every unused number.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Build the ordering one slot at a time. For each empty slot, scan the
     * whole nums array and pick a number only if it is not already in current.
     * This is real brute force because current.contains(...) rechecks the
     * partial list again and again instead of remembering used indices.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * Start []
     * Choose 1 -> [1]
     * Then choose 2 -> [1,2]
     * Then choose 3 -> [1,2,3]
     * Undo and try [1,3,2].
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Keep a current list.
     * 2. Try each number not already in current.
     * 3. When current length equals nums length, save it.
     * Time Complexity: O(n! * n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * The partial list itself is used as memory, so membership checks are slow.
     */
    public List<List<Integer>> bruteForce(int[] nums) {
        List<List<Integer>> answer = new ArrayList<>();
        brute(nums, new ArrayList<>(), answer);
        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Checking current.contains every time costs extra.
     * Use a boolean used[] array so we instantly know whether a number is already chosen.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * used[0] = true after choosing nums[0].
     * That prevents choosing the same value again in the current permutation.
     * After recursion, set it back to false.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Create used array.
     * 2. For each position, try all unused indices.
     * 3. Mark used, recurse, unmark used.
     * 4. Save full permutations.
     * Time Complexity: O(n! * n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * The used array gives each index a quick chosen/not-chosen flag.
     */
    public List<List<Integer>> optimized(int[] nums) {
        List<List<Integer>> answer = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        build(nums, used, new ArrayList<>(), answer);
        return answer;
    }


    private void brute(int[] nums, List<Integer> current, List<List<Integer>> answer) {
        if (current.size() == nums.length) {
            // A full path is one complete ordering; copy it before undoing later choices.
            answer.add(new ArrayList<>(current));
            return;
        }

        for (int num : nums) {
            if (current.contains(num)) {
                continue;
            }

            // Choose num, explore all endings after it, then undo the choice.
            current.add(num);
            brute(nums, current, answer);
            current.remove(current.size() - 1);
        }
    }

    private void build(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> answer) {
        if (current.size() == nums.length) {
            // Base case: every index has been used exactly once.
            answer.add(new ArrayList<>(current));
            return;
        }

        for (int index = 0; index < nums.length; index++) {
            if (used[index]) {
                continue;
            }

            // Choose this index so the same array position cannot appear twice.
            used[index] = true;
            current.add(nums[index]);
            build(nums, used, current, answer);
            // Undo both the list and the used flag before trying the next index.
            current.remove(current.size() - 1);
            used[index] = false;
        }
    }
}
