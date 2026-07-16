package com.companywisejavasolutions.ebay.solutions;

public class BestTimeToBuyAndSellStockII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We are given daily stock prices. We may buy and sell many times, but we
     * can hold only one stock at a time. Return the maximum total profit.
     *
     * Sample Input: prices = [7, 1, 5, 3, 6, 4]
     * Sample Output: 7
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Every upward step is profit we can collect. If price rises from 1 to 5,
     * that is the same total gain as buying at 1 and selling at 5. If it rises
     * again from 3 to 6, collect that rise too.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start profit at 0.
     * 2. For each day, compare today's price with yesterday's price.
     * 3. If today is higher, add the difference to profit.
     * 4. Return profit.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public int maxProfit(int[] prices) {
        int profit = 0;

        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }

        return profit;
    }
}
