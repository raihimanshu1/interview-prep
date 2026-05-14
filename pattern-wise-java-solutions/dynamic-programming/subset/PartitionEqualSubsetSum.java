import java.util.HashSet;
import java.util.Set;

public class PartitionEqualSubsetSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1,5,11,5]
     * Sample Output: true
     *
     * Input: nums = [1,5,11,5]
     * Output: true because [11] and [1,5,5] both sum to 11.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If total sum is odd, equal partition is impossible.
     * If total is even, we only need to know whether some subset sums to total / 2.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Once the target is total / 2, each number has a simple choice: put it in
     * the target subset or leave it out. Brute force tries both choices until
     * the remaining target becomes 0 or all numbers are used.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * total = 22, target = 11
     * Choose 11 directly -> target becomes 0.
     * So partition is possible.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Calculate total sum.
     * 2. If odd, return false.
     * 3. Recursively choose or skip numbers to make target.
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This directly explores include/exclude choices for the target subset.
     */
    public boolean bruteForce(int[] nums) {
        int total = 0;

        for (int num : nums) {
            total += num;
        }

        if (total % 2 == 1) {
            return false;
        }

        return canMake(nums, 0, total / 2);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Instead of exploring all branches repeatedly, store all sums we can make so far.
     * For every number, a new possible sum is oldSum + number. This is DP
     * because "can we make sum x?" is remembered instead of rediscovered.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * nums [1,5,11,5]
     * Possible after 1: {0,1}
     * After 5: {0,1,5,6}
     * After 11 includes 11, so true.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. target = total / 2.
     * 2. Start possible sums with 0.
     * 3. For each num, add num to previous sums.
     * 4. If target appears, return true.
     * Time Complexity: O(n * target)
     * Space Complexity: O(target)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * The set represents reachable subset sums after the numbers processed so far.
     */
    public boolean optimized(int[] nums) {
        int total = 0;

        for (int num : nums) {
            total += num;
        }

        if (total % 2 == 1) {
            return false;
        }

        int target = total / 2;
        Set<Integer> possible = new HashSet<>();
        // Sum 0 is always possible by choosing nothing.
        possible.add(0);

        for (int num : nums) {
            Set<Integer> next = new HashSet<>(possible);

            for (int sum : possible) {
                // Either keep old sum, or include num to create a new sum.
                next.add(sum + num);
            }

            if (next.contains(target)) {
                return true;
            }

            possible = next;
        }

        return possible.contains(target);
    }


    private boolean canMake(int[] nums, int index, int remaining) {
        if (remaining == 0) {
            // Found a subset that reaches half the total.
            return true;
        }

        if (index == nums.length || remaining < 0) {
            return false;
        }

        // Include nums[index], or skip it.
        return canMake(nums, index + 1, remaining - nums[index])
            || canMake(nums, index + 1, remaining);
    }
}
