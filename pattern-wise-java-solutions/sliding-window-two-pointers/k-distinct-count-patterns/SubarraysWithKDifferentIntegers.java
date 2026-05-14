import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SubarraysWithKDifferentIntegers {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Count subarrays that contain exactly k different integers.
     *
     * Sample Input:
     * nums = [1, 2, 1, 2, 3], k = 2
     *
     * Sample Output:
     * 7
     *
     * Some valid subarrays:
     * [1, 2], [2, 1], [1, 2], [2, 3], [1, 2, 1]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A subarray must be continuous.
     *
     * The direct idea is:
     * - Choose every possible start.
     * - Expand the end one by one.
     * - Track unique numbers in a set.
     * - Count the subarray when the set size is exactly k.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Choose each start and extend right while adding numbers to a set. Count
     * the subarray when the set has exactly k different numbers, and stop when
     * it has more than k.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1, 2, 1], k = 2
     *
     * start = 0:
     * [1]       -> 1 different, not counted
     * [1, 2]    -> 2 different, count = 1
     * [1, 2, 1] -> 2 different, count = 2
     *
     * start = 1:
     * [2]    -> 1 different
     * [2, 1] -> 2 different, count = 3
     *
     * start = 2:
     * [1] -> 1 different
     *
     * Answer is 3.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Try every starting index.
     * 2. Extend the ending index.
     * 3. Add each number to a set.
     * 4. If set size is exactly k, count the subarray.
     * 5. If set size becomes greater than k, stop this start.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(k) or O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] nums, int k) {
        int answer = 0;

        for (int start = 0; start < nums.length; start++) {
            Set<Integer> differentNumbers = new HashSet<>();

            for (int end = start; end < nums.length; end++) {
                differentNumbers.add(nums[end]);

                if (differentNumbers.size() == k) {
                    answer++;
                }

                if (differentNumbers.size() > k) {
                    break;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Exactly k is hard to count directly.
     *
     * But "at most k" is easy with sliding window.
     *
     * Formula:
     * exactly(k) = atMost(k) - atMost(k - 1)
     *
     * Example:
     * If we count all subarrays with at most 2 different integers,
     * that includes subarrays with 1 or 2 different integers.
     *
     * If we subtract all subarrays with at most 1 different integer,
     * only subarrays with exactly 2 different integers remain.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1, 2, 1], k = 2
     *
     * atMost(2) = 6
     * All subarrays are allowed because none has more than 2 different integers.
     *
     * atMost(1) = 3
     * Only [1], [2], [1]
     *
     * exactly(2) = 6 - 3 = 3
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Create helper countAtMost(nums, k).
     * 2. Use left and right pointers to maintain a valid window.
     * 3. Use a frequency map to know how many different numbers are inside.
     * 4. When different count is too large, shrink from left.
     * 5. Add right - left + 1 for every valid right.
     * 6. Return countAtMost(k) - countAtMost(k - 1).
     *
     * Time Complexity: O(n)
     * Space Complexity: O(k) or O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * You can count subarrays with at most k distinct values and subtract
     * subarrays with at most k - 1 distinct values. That transform is the main
     * trick for turning exact-k counting into sliding-window work.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] nums, int k) {
        return countAtMostKDifferent(nums, k) - countAtMostKDifferent(nums, k - 1);
    }

    private int countAtMostKDifferent(int[] nums, int k) {
        if (k < 0) {
            return 0;
        }

        Map<Integer, Integer> frequency = new HashMap<>();
        int left = 0;
        int answer = 0;

        for (int right = 0; right < nums.length; right++) {
            int rightNumber = nums[right];
            frequency.put(rightNumber, frequency.getOrDefault(rightNumber, 0) + 1);

            while (frequency.size() > k) {
                int leftNumber = nums[left];
                // Remove numbers from the left until the window is at most k-different again.
                frequency.put(leftNumber, frequency.get(leftNumber) - 1);

                if (frequency.get(leftNumber) == 0) {
                    frequency.remove(leftNumber);
                }

                left++;
            }

            // Every subarray ending at right and starting inside this window has at most k types.
            answer += right - left + 1;
        }

        return answer;
    }
}
