import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinationSumII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return unique combinations that add to target. Each candidate can be used
     * at most once.
     *
     * Sample Input: candidates = [10,1,2,7,6,1,5], target = 8
     * Sample Output: [[1,1,6],[1,2,5],[1,7],[2,6]]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * We are filling a basket to exactly target. Every number can be picked once,
     * and duplicate values should not create duplicate baskets.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try every subset, keep the ones whose sum is target, and use a set to
     * remove duplicate combinations.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Sort candidates so each combination has a stable order.
     * 2. For each index, choose it or skip it.
     * 3. When the end is reached, save the path if the sum is target.
     * 4. Use a set to avoid duplicate paths.
     *
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(2^n * n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * target = 8
     * Choose 1, 1, and 6. Sum is 8, save [1,1,6].
     * Choose 1, 2, and 5. Sum is 8, save [1,2,5].
     */
    public List<List<Integer>> bruteForce(int[] candidates, int target) {
        Arrays.sort(candidates);
        Set<List<Integer>> unique = new HashSet<>();
        brute(candidates, 0, target, new ArrayList<>(), unique);
        return new ArrayList<>(unique);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Sorting groups duplicates together. At the same recursion level, if we
     * already tried one value, trying the same value again would duplicate work.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Sort candidates.
     * 2. Use start index so each number is used once.
     * 3. Skip duplicate values at the same level.
     * 4. Stop early when the current value exceeds remaining target.
     *
     * Time Complexity: O(2^n)
     * Space Complexity: O(n) recursion besides the answer
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sorted candidates = [1,1,2,5,6,7,10]
     * Starting with the first 1 can produce [1,1,6].
     * Starting with the second 1 at the same level is skipped.
     */
    public List<List<Integer>> optimized(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> answer = new ArrayList<>();
        choose(candidates, 0, target, new ArrayList<>(), answer);
        return answer;
    }

    private void brute(int[] candidates, int index, int remaining, List<Integer> current, Set<List<Integer>> unique) {
        if (index == candidates.length) {
            if (remaining == 0) {
                // A full take/skip path hit the target exactly.
                unique.add(new ArrayList<>(current));
            }
            return;
        }

        // Take this index once.
        current.add(candidates[index]);
        brute(candidates, index + 1, remaining - candidates[index], current, unique);
        current.remove(current.size() - 1);

        // Or skip it and move on.
        brute(candidates, index + 1, remaining, current, unique);
    }

    private void choose(int[] candidates, int start, int remaining, List<Integer> current, List<List<Integer>> answer) {
        if (remaining == 0) {
            // Current combination reaches target; all values were used at most once.
            answer.add(new ArrayList<>(current));
            return;
        }

        for (int index = start; index < candidates.length; index++) {
            if (index > start && candidates[index] == candidates[index - 1]) {
                // Avoid making the same combination from an equal value at this depth.
                continue;
            }

            if (candidates[index] > remaining) {
                // Sorted order means all later numbers are too large as well.
                break;
            }

            // Move to index + 1 because each candidate can be used only once.
            current.add(candidates[index]);
            choose(candidates, index + 1, remaining - candidates[index], current, answer);
            current.remove(current.size() - 1);
        }
    }
}
