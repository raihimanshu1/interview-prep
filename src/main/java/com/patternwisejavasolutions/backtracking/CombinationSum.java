
package com.patternwisejavasolutions.backtracking;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinationSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: candidates = [2,3,6,7], target = 7
     * Sample Output: [[2,2,3],[7]]
     *
     * Input: candidates = [2,3,6,7], target = 7
     * Output: [[2,2,3], [7]]
     * We can reuse the same candidate multiple times.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * At each candidate, we decide how many times to use it.
     * The running sum must not cross the target.
     * When the remaining target becomes 0, we found one combination.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Try every candidate as the next number, even if that creates the same
     * combination in different orders such as [2,3,2] and [2,2,3]. This shows
     * the raw choice tree first: keep adding numbers until the remaining target
     * becomes 0 or goes negative, then use a set to keep each combination once.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * target = 7
     * Choose 2 -> remaining 5
     * Choose 2 again -> remaining 3
     * Choose 3 -> remaining 0
     * Combination [2,2,3] is valid.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Try every candidate from index 0 each time.
     * 2. Add candidate if it does not exceed remaining target.
     * 3. Recurse with reduced target.
     * 4. Save a sorted copy when remaining target becomes 0, so reordered
     *    duplicates collapse in the set.
     * Time Complexity: exponential
     * Space Complexity: O(target depth)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This version explores the full reuse tree, then filters duplicate orderings.
     */
    public List<List<Integer>> bruteForce(int[] candidates, int target) {
        Set<List<Integer>> unique = new HashSet<>();
        brute(candidates, target, new ArrayList<>(), unique);
        return new ArrayList<>(unique);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * To avoid duplicate orderings like [2,3,2], we only move forward from the current index.
     * Because reuse is allowed, after choosing index i we recurse again with i.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * From candidate 2, we may choose 2 again.
     * From candidate 3, we do not go back to 2.
     * This keeps combinations in one order.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Use start index to control choices.
     * 2. If remaining is 0, save current.
     * 3. Loop from start to end.
     * 4. Choose candidates[i].
     * 5. Recurse with same i because reuse is allowed.
     * 6. Undo choice.
     * Time Complexity: exponential
     * Space Complexity: O(target depth)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * The start index keeps combinations in non-decreasing candidate order.
     */
    public List<List<Integer>> optimized(int[] candidates, int target) {
        List<List<Integer>> answer = new ArrayList<>();
        choose(candidates, 0, target, new ArrayList<>(), answer);
        return answer;
    }


    private void brute(int[] candidates, int remaining, List<Integer> current, Set<List<Integer>> unique) {
        if (remaining == 0) {
            // Exact target reached; sorting lets the set merge [2,3,2] and [2,2,3].
            List<Integer> combination = new ArrayList<>(current);
            Collections.sort(combination);
            unique.add(combination);
            return;
        }

        if (remaining < 0) {
            // This path spent too much, so it cannot become valid.
            return;
        }

        for (int candidate : candidates) {
            // Choose any candidate as the next step; brute force allows repeated orderings.
            current.add(candidate);
            brute(candidates, remaining - candidate, current, unique);
            current.remove(current.size() - 1);
        }
    }

    private void choose(int[] candidates, int start, int remaining, List<Integer> current, List<List<Integer>> answer) {
        if (remaining == 0) {
            // Current numbers add exactly to target.
            answer.add(new ArrayList<>(current));
            return;
        }

        for (int index = start; index < candidates.length; index++) {
            if (candidates[index] > remaining) {
                continue;
            }

            // Choose candidates[index]. Recurse with same index because reuse is allowed.
            current.add(candidates[index]);
            choose(candidates, index, remaining - candidates[index], current, answer);
            // Undo so the next candidate starts from the previous path.
            current.remove(current.size() - 1);
        }
    }
}
