package com.patternwisejavasolutions.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubsetsII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1,2,2]
     * Sample Output: unique subsets only, so [2] appears once
     *
     * Input: nums = [1, 2, 2]
     * Output should contain unique subsets only.
     * [2] should appear once, not twice.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Duplicates in the input can create duplicate subsets.
     * A simple way is to generate everything first and use a set to remove repeated subsets.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Use the same take/skip tree as normal subsets, even though duplicate
     * values can lead to the same subset more than once. Sorting makes equal
     * subsets have the same list order, and the set removes repeated results.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * nums = [1,2,2]
     * One path picks the first 2 -> [2]
     * Another path picks the second 2 -> [2]
     * They look the same, so keep only one.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Generate all subsets using take/skip.
     * 2. Store each subset in a set to avoid duplicates.
     * 3. Return the unique subsets.
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(2^n * n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This generates duplicates first, then lets the set filter them.
     */
    public List<List<Integer>> bruteForce(int[] nums) {
        Set<List<Integer>> unique = new HashSet<>();
        Arrays.sort(nums);
        generateAll(nums, 0, new ArrayList<>(), unique);
        return new ArrayList<>(unique);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Sort the array first.
     * Then duplicate values sit next to each other.
     * At the same recursion level, if we already tried one value, skip its duplicate.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Sorted nums = [1,2,2]
     * At the same level, after using the first 2, skip the second 2.
     * This prevents duplicate branches before they are created.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Sort nums.
     * 2. Add current subset to answer.
     * 3. Loop from start to end.
     * 4. If nums[i] equals nums[i-1] at the same level, skip it.
     * 5. Choose, recurse, undo.
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(n) excluding answer
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Duplicate branches are skipped before they are created.
     */
    public List<List<Integer>> optimized(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> answer = new ArrayList<>();
        buildUnique(nums, 0, new ArrayList<>(), answer);
        return answer;
    }


    private void generateAll(int[] nums, int index, List<Integer> current, Set<List<Integer>> unique) {
        if (index == nums.length) {
            // Leaf of the take/skip tree; the set keeps only one copy of equal subsets.
            unique.add(new ArrayList<>(current));
            return;
        }

        // Skip nums[index].
        generateAll(nums, index + 1, current, unique);
        // Take nums[index], explore, then undo.
        current.add(nums[index]);
        generateAll(nums, index + 1, current, unique);
        current.remove(current.size() - 1);
    }

    private void buildUnique(int[] nums, int start, List<Integer> current, List<List<Integer>> answer) {
        // Current prefix is a valid unique subset.
        answer.add(new ArrayList<>(current));

        for (int index = start; index < nums.length; index++) {
            if (index > start && nums[index] == nums[index - 1]) {
                // Same value was already tried at this depth, so this branch would duplicate it.
                continue;
            }

            // Choose this value and only extend with later indices.
            current.add(nums[index]);
            buildUnique(nums, index + 1, current, answer);
            current.remove(current.size() - 1);
        }
    }
}
