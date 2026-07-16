package com.patternwisejavasolutions.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermutationsII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return all unique permutations of nums, even when nums contains duplicates.
     *
     * Sample Input: nums = [1,1,2]
     * Sample Output: [[1,1,2],[1,2,1],[2,1,1]]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A permutation is one full arrangement. Duplicate numbers can create the
     * same arrangement more than once, so we must keep only unique results.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Generate every index-based permutation and put each completed list into a
     * set to remove duplicates.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Track used indices.
     * 2. Try every unused index at each position.
     * 3. When the path is full, add a copy to a set.
     * 4. Return the set as a list.
     *
     * Time Complexity: O(n! * n)
     * Space Complexity: O(n! * n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,1,2]
     * Index choices may build [1,1,2] twice, but the set stores it once.
     */
    public List<List<Integer>> bruteForce(int[] nums) {
        Set<List<Integer>> unique = new HashSet<>();
        boolean[] used = new boolean[nums.length];
        brute(nums, used, new ArrayList<>(), unique);
        return new ArrayList<>(unique);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Sort nums. If two equal numbers sit next to each other, use them in a
     * fixed order. That prevents duplicate branches before they are created.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Sort nums.
     * 2. Track used indices.
     * 3. Skip nums[i] if it equals nums[i - 1] and nums[i - 1] is not used.
     * 4. Save a path when it reaches length n.
     *
     * Time Complexity: O(number of unique permutations * n)
     * Space Complexity: O(n) recursion besides the answer
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,1,2]
     * If the first 1 is not used, we do not start with the second 1.
     * This avoids building duplicate trees.
     */
    public List<List<Integer>> optimized(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> answer = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        choose(nums, used, new ArrayList<>(), answer);
        return answer;
    }

    private void brute(int[] nums, boolean[] used, List<Integer> current, Set<List<Integer>> unique) {
        if (current.size() == nums.length) {
            // Index-based brute force may reach the same values in different ways; set removes copies.
            unique.add(new ArrayList<>(current));
            return;
        }

        for (int index = 0; index < nums.length; index++) {
            if (used[index]) {
                continue;
            }

            // Choose this physical index, not just this value.
            used[index] = true;
            current.add(nums[index]);
            brute(nums, used, current, unique);
            // Undo so this index can be used in a different position later.
            current.remove(current.size() - 1);
            used[index] = false;
        }
    }

    private void choose(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> answer) {
        if (current.size() == nums.length) {
            // Full unique permutation built.
            answer.add(new ArrayList<>(current));
            return;
        }

        for (int index = 0; index < nums.length; index++) {
            if (used[index]) {
                continue;
            }

            if (index > 0 && nums[index] == nums[index - 1] && !used[index - 1]) {
                // At this depth, use the first equal value before the second to avoid duplicate branches.
                continue;
            }

            // Choose, explore, undo.
            used[index] = true;
            current.add(nums[index]);
            choose(nums, used, current, answer);
            current.remove(current.size() - 1);
            used[index] = false;
        }
    }
}
