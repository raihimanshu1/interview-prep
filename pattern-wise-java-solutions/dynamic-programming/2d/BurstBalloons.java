public class BurstBalloons {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [3, 1, 5, 8]
     * Sample Output: 167
     *
     * When a balloon is burst, coins = left value * current value * right value.
     * Find the maximum coins possible.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Bursting one balloon changes its neighbors.
     * Instead of asking which balloon to burst first, it is easier to ask which
     * balloon will be the last one left inside a smaller range.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try every balloon as the next balloon to burst.
     * Recursively continue until no balloons remain.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Keep a list of remaining balloons.
     * 2. Try bursting each index.
     * 3. Add gained coins and solve the smaller list.
     * Time Complexity: O(n!)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [3, 1, 5]
     * Burst 1 first gives 3 * 1 * 5 = 15, then solve [3, 5].
     * Try every first burst and keep the best total.
     */
    public int bruteForce(int[] nums) {
        java.util.List<Integer> balloons = new java.util.ArrayList<>();
        for (int num : nums) {
            balloons.add(num);
        }
        return burstAnyNext(balloons);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Add fake balloons with value 1 at both ends.
     * For every range, choose the balloon that bursts last in that range.
     * Then its left and right neighbors are already known. dp[left][right]
     * reuses the best answer for smaller open intervals instead of replaying
     * every burst order.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build values = [1] + nums + [1].
     * 2. dp[left][right] means best coins from open interval (left, right).
     * 3. Try each last balloon inside that interval.
     * 4. Combine left range + last burst + right range.
     * Time Complexity: O(n^3)
     * Space Complexity: O(n^2)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * values = [1, 3, 1, 5, 8, 1]
     * Small intervals are solved first.
     * Bigger intervals reuse their smaller answers until the full interval is solved.
     */
    public int optimized(int[] nums) {
        int n = nums.length;
        int[] values = new int[n + 2];
        values[0] = 1;
        values[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            values[i + 1] = nums[i];
        }

        int[][] dp = new int[n + 2][n + 2];

        for (int length = 2; length < n + 2; length++) {
            for (int left = 0; left + length < n + 2; left++) {
                int right = left + length;
                for (int last = left + 1; last < right; last++) {
                    // If last bursts last inside (left, right), left and right are its neighbors.
                    int coins = values[left] * values[last] * values[right];
                    // Add best coins from the two smaller independent intervals.
                    coins += dp[left][last] + dp[last][right];
                    dp[left][right] = Math.max(dp[left][right], coins);
                }
            }
        }

        return dp[0][n + 1];
    }

    private int burstAnyNext(java.util.List<Integer> balloons) {
        if (balloons.isEmpty()) {
            return 0;
        }

        int best = 0;
        for (int i = 0; i < balloons.size(); i++) {
            int left = i == 0 ? 1 : balloons.get(i - 1);
            int right = i == balloons.size() - 1 ? 1 : balloons.get(i + 1);
            // Choose balloon i as the next burst.
            int current = balloons.remove(i);
            int coins = left * current * right + burstAnyNext(balloons);
            best = Math.max(best, coins);
            // Undo by putting it back before trying a different next burst.
            balloons.add(i, current);
        }

        return best;
    }
}
