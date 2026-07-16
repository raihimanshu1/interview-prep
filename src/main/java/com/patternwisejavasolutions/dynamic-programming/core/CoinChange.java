package com.patternwisejavasolutions.dynamicprogramming.core;

import java.util.Arrays;

public class CoinChange {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: coins = [1,2,5], amount = 11
     * Sample Output: 3
     *
     * Input: coins = [1,2,5], amount = 11
     * Output: 3 because 5 + 5 + 1 uses 3 coins.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try every coin as the first coin.
     * After choosing a coin, solve the smaller remaining amount.
     * Pick the minimum answer.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Pick a coin as the next coin, subtract it from the amount, and ask the
     * same question for the remaining amount. The same remainder, like amount
     * 6, can be reached after many different first choices, so it is recomputed.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * amount 11:
     * Choose 1 -> solve 10
     * Choose 2 -> solve 9
     * Choose 5 -> solve 6
     * The best path becomes 5 + 5 + 1.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. If amount is 0, return 0.
     * 2. Try each coin.
     * 3. Recursively solve amount - coin.
     * 4. Return 1 + minimum subanswer.
     * Time Complexity: exponential
     * Space Complexity: O(amount)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This directly tries every coin as the next choice.
     */
    public int bruteForce(int[] coins, int amount) {
        int answer = brute(coins, amount);
        return answer == Integer.MAX_VALUE ? -1 : answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The same remaining amount repeats again and again.
     * Let dp[x] mean minimum coins needed to make amount x.
     * Build dp from 0 to amount.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * dp[0] = 0
     * For amount 1, coin 1 works -> dp[1] = 1
     * For amount 2, coin 2 works -> dp[2] = 1
     * For amount 5, coin 5 works -> dp[5] = 1
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Fill dp with a large number.
     * 2. dp[0] = 0.
     * 3. For each amount, try each coin.
     * 4. dp[current] = min(dp[current], dp[current - coin] + 1).
     * Time Complexity: O(amount * coins)
     * Space Complexity: O(amount)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[currentAmount] stores the best answer for that amount once.
     */
    public int optimized(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        // Zero coins are needed to make amount 0.
        dp[0] = 0;

        for (int currentAmount = 1; currentAmount <= amount; currentAmount++) {
            for (int coin : coins) {
                if (coin <= currentAmount) {
                    // If coin is last, the previous state is currentAmount - coin.
                    dp[currentAmount] = Math.min(dp[currentAmount], dp[currentAmount - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }


    private int brute(int[] coins, int amount) {
        if (amount == 0) {
            // No remaining amount means no more coins are needed.
            return 0;
        }

        if (amount < 0) {
            return Integer.MAX_VALUE;
        }

        int best = Integer.MAX_VALUE;

        for (int coin : coins) {
            // Try this coin next and solve the smaller remainder.
            int smaller = brute(coins, amount - coin);

            if (smaller != Integer.MAX_VALUE) {
                best = Math.min(best, smaller + 1);
            }
        }

        return best;
    }
}
