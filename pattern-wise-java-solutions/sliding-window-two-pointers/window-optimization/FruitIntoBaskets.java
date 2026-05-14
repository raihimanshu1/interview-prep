import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FruitIntoBaskets {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We have fruit trees in a row.
     * We can carry only two baskets, so we can collect at most two fruit types.
     * Find the longest continuous group with at most two different numbers.
     *
     * Sample Input:
     * fruits = [1, 2, 1]
     *
     * Sample Output:
     * 3
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * This is the same shape as:
     * longest subarray with at most 2 distinct values.
     *
     * First try every subarray and check how many fruit types it has.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Choose a tree as the starting point and keep walking right while the
     * fruits fit into two baskets. The moment a third fruit type appears, that
     * starting point cannot be extended further.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * fruits = [1,2,3,2,2]
     *
     * start = 1:
     * [2] valid
     * [2,3] valid
     * [2,3,2] valid, length = 3
     * [2,3,2,2] valid, length = 4
     *
     * Best becomes 4.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Try every start.
     * 2. Expand end.
     * 3. Track fruit types in a set.
     * 4. If more than 2 types appear, stop.
     * 5. Otherwise update best length.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1), because the set holds at most 3 types before break
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] fruits) {
        int bestLength = 0;

        for (int start = 0; start < fruits.length; start++) {
            Set<Integer> types = new HashSet<>();

            for (int end = start; end < fruits.length; end++) {
                types.add(fruits[end]);

                if (types.size() > 2) {
                    break;
                }

                bestLength = Math.max(bestLength, end - start + 1);
            }
        }

        return bestLength;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Use a sliding window that always has at most two fruit types.
     *
     * A frequency map helps us know:
     * - how many of each fruit type are inside the window
     * - when a fruit type completely leaves the window
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * fruits = [1,2,3,2,2]
     *
     * Add 1 -> types {1}, valid
     * Add 2 -> types {1,2}, valid
     * Add 3 -> types {1,2,3}, invalid
     * Remove from left until only two types remain.
     * Window becomes [2,3], then grows to [2,3,2,2].
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Move right and add fruits[right] to map.
     * 2. While map has more than 2 types, remove fruits[left].
     * 3. If a fruit count becomes 0, remove that type from map.
     * 4. Update best length after the window is valid.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1), because there are at most 3 tracked types briefly
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Since only two fruit types are allowed, another solution tracks the last
     * fruit type, the second-last fruit type, and the length of the current run.
     * The map version is easier to extend to k fruit types.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] fruits) {
        Map<Integer, Integer> frequency = new HashMap<>();
        int left = 0;
        int bestLength = 0;

        for (int right = 0; right < fruits.length; right++) {
            int rightFruit = fruits[right];
            frequency.put(rightFruit, frequency.getOrDefault(rightFruit, 0) + 1);

            while (frequency.size() > 2) {
                int leftFruit = fruits[left];
                // A third type entered, so remove fruit from the left until two types remain.
                frequency.put(leftFruit, frequency.get(leftFruit) - 1);

                if (frequency.get(leftFruit) == 0) {
                    frequency.remove(leftFruit);
                }

                left++;
            }

            bestLength = Math.max(bestLength, right - left + 1);
        }

        return bestLength;
    }
}
