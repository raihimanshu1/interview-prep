import java.util.HashMap;
import java.util.Map;

public class MajorityElement {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Find the number that appears more than n / 2 times in the array. The
     * problem guarantees such a number exists.
     *
     * Sample Input:
     * nums = [3, 2, 3]
     *
     * Sample Output:
     * 3
     */

    /*
     * WHAT TO NOTICE FIRST
     *
     * In [3, 2, 3], the value 3 appears twice out of three positions. Since
     * more than n / 2 means more than 1 here, 3 is already the majority.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Majority means "more than half." If a class has 5 students, a majority
     * group must have at least 3 students. Since the majority value appears more
     * than all other values combined, it has a special strength.
     *
     * This problem first fits counting with a map. It also has a voting pattern:
     * different values can cancel each other, and the majority survives.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Count each value by scanning the array. If one value's count becomes more
     * than n / 2, it is the answer.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick nums[i] as the value to test.
     * 2. Count how many times that value appears by scanning the full array.
     * 3. If count is greater than n / 2, return that value.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [3, 2, 3]
     * test value 3 -> count is 2
     * n / 2 is 1, and 2 > 1, so return 3.
     */
    public int bruteForce(int[] nums) {
        for (int valueToTest : nums) {
            int count = 0;

            for (int num : nums) {
                if (num == valueToTest) {
                    count++;
                }
            }

            if (count > nums.length / 2) {
                return valueToTest;
            }
        }

        return -1;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is recounting the same values. A map can count
     * in one pass, but Boyer-Moore Voting uses constant space.
     *
     * Think of pairing one majority value with one non-majority value and
     * removing both. Because the majority has more than half, it cannot be fully
     * removed. The remaining value with support is the majority.
     *
     * Other useful approach: HashMap counting in O(n) time and O(n) space.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep a current leader and votes.
     * 2. If votes is 0, choose the current number as leader.
     * 3. If current equals leader, add one vote.
     * 4. Otherwise, remove one vote.
     * 5. Return candidate.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [3, 2, 3]
     * votes = 0 -> leader = 3, votes = 1
     * current 2 differs -> votes = 0
     * votes = 0 -> leader = 3, votes = 1
     * return 3.
     */
    public int optimized(int[] nums) {
        int leader = 0;
        int votes = 0;

        for (int num : nums) {
            if (votes == 0) {
                // A zero vote count means previous groups canceled out.
                leader = num;
            }

            if (num == leader) {
                // Same value supports the current leader.
                votes++;
            } else {
                // Different value cancels one support vote.
                votes--;
            }
        }

        return leader;
    }

    public int countingWithMap(int[] nums) {
        Map<Integer, Integer> counts = new HashMap<>();

        for (int num : nums) {
            int newCount = counts.getOrDefault(num, 0) + 1;
            if (newCount > nums.length / 2) {
                return num;
            }
            counts.put(num, newCount);
        }

        return -1;
    }
}
