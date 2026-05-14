public class TargetSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1,1,1,1,1], target = 3
     * Sample Output: 5
     *
     * Input: nums = [1,1,1,1,1], target = 3
     * Output: 5 ways to put + or - signs to reach 3.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For every number, choose either plus or minus.
     * At the end, check whether the final sum equals target.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Walk through nums and branch twice at every number: put a plus sign in
     * front of it or put a minus sign. A full path is one sign assignment; only
     * paths whose final sum equals target are counted.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * For first 1:
     * Use +1, then solve rest.
     * Use -1, then solve rest.
     * Every number doubles the number of choices.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively move index by index.
     * 2. Add nums[index] in one branch.
     * 3. Subtract nums[index] in another branch.
     * 4. Count paths where final sum equals target.
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This counts every plus/minus sign path directly.
     */
    public int bruteForce(int[] nums, int target) {
        return countWays(nums, 0, 0, target);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Convert to subset-sum idea.
     * Let P be positive group and N be negative group.
     * P - N = target, P + N = total.
     * So P = (target + total) / 2.
     * Now count subsets with sum P. Repeated states become subset sums:
     * dp[sum] stores how many processed-number subsets make that sum.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * nums total = 5, target = 3
     * positiveSum = (5 + 3) / 2 = 4
     * Count subsets that sum to 4 -> 5 ways.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Compute total.
     * 2. If target impossible or parity odd, return 0.
     * 3. Count subsets with sum positiveSum using 1D DP.
     * Time Complexity: O(n * sum)
     * Space Complexity: O(sum)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * 1D DP counts subsets instead of rebuilding the sign tree.
     */
    public int optimized(int[] nums, int target) {
        int total = 0;

        for (int num : nums) {
            total += num;
        }

        if (Math.abs(target) > total || (target + total) % 2 == 1) {
            return 0;
        }

        int positiveSum = (target + total) / 2;
        int[] dp = new int[positiveSum + 1];
        // Empty subset makes sum 0 in exactly one way.
        dp[0] = 1;

        for (int num : nums) {
            for (int sum = positiveSum; sum >= num; sum--) {
                // Add num to every older subset that made sum - num.
                dp[sum] += dp[sum - num];
            }
        }

        return dp[positiveSum];
    }


    private int countWays(int[] nums, int index, int currentSum, int target) {
        if (index == nums.length) {
            // Count this sign assignment only if it lands exactly on target.
            return currentSum == target ? 1 : 0;
        }

        int plus = countWays(nums, index + 1, currentSum + nums[index], target);
        int minus = countWays(nums, index + 1, currentSum - nums[index], target);
        return plus + minus;
    }
}
