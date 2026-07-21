package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Best Time to Buy and Sell Stock
 * (Problem number: 24 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array prices where prices[i] is the price of a stock on day i.
 * Find the maximum profit you can achieve. You may buy and sell one stock only.
 *
 * Example 1:
 * Input: prices = [7, 1, 5, 3, 6, 4]
 * Output: 5
 * Explanation: Buy on day 2 (price = 1), sell on day 5 (price = 6), profit = 6 - 1 = 5.
 *
 * Example 2:
 * Input: prices = [7, 6, 4, 3, 1]
 * Output: 0
 * Explanation: No profitable transaction possible, so profit = 0.
 *
 * CONSTRAINTS:
 * - 1 <= prices.length <= 10^5
 * - 0 <= prices[i] <= 10^4
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each pair (buy, sell), calculate profit
 * OPTIMIZED:   O(n)   - Track minimum price so far, calculate profit at each day
 */

public class LS07_BestTimeToBuyAndSellStock {

    public static void main(String[] args) {
        // 1. Create our input array (stock prices).
        int[] prices = {7, 1, 5, 3, 6, 4};

        // 2. --- BRUTE FORCE APPROACH ---
        // Call brute force method.
        int bruteResult = maxProfitBruteForce(prices.clone());
        System.out.println("Brute Force Max Profit: " + bruteResult); // Expected: 5

        // 3. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int optimizedResult = maxProfitOptimized(prices.clone());
        System.out.println("Optimized Max Profit: " + optimizedResult); // Expected: 5

        // 4. Test with no profit scenario.
        int[] prices2 = {7, 6, 4, 3, 1};
        System.out.println("No Profit - Optimized: " + maxProfitOptimized(prices2)); // Expected: 0
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each possible buy day, check all possible sell days and track max profit.
    // Time:  O(n^2)  |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int maxProfitBruteForce(int[] prices) {
        int n = prices.length;
        int maxProfit = 0; // 1. Track the maximum profit found.

        // 2. Iterate over each possible buy day.
        for (int i = 0; i < n; i++) {
            // 3. For each buy day, iterate over all possible sell days after i.
            for (int j = i + 1; j < n; j++) {
                // 4. Calculate profit for this buy-sell pair.
                int profit = prices[j] - prices[i];

                // 5. Update maxProfit if current profit is better.
                if (profit > maxProfit) {
                    maxProfit = profit;
                }
            }
        }

        return maxProfit;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: Track the minimum price seen so far till (ith-1)day before the ith day of sell [as same day we can't sell]. At each day, calculate potential
    //       profit if we sell today (current price - min price so far).
    //       Keep updating the minimum price as we go.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------


    public static int maxProfitOptimized(int[] prices) {

        // If there are less than 2 days, no transaction is possible.
        if (prices == null || prices.length < 2) {
            return 0;
        }

        // Minimum price seen before the current day.
        int runningMin = prices[0];

        // Best profit found so far.
        int maxProfit = 0;

        // Start from Day 1 because Day 0 can only be a buying day.
        for (int i = 1; i < prices.length; i++) {

            // ----------------------------------------------------
            // Step 1:
            // Sell on the current day.
            // Buy at the cheapest price seen BEFORE today.
            // ----------------------------------------------------
            int todayProfit = prices[i] - runningMin;  // runningMin variable reperesents that buy the stoks on the ith-1 dat

            // ----------------------------------------------------
            // Step 2:
            // Update the overall maximum profit.
            // ----------------------------------------------------
            maxProfit = Math.max(maxProfit, todayProfit);

            // ----------------------------------------------------
            // Step 3:
            // Include today's price in the running minimum.
            // This prepares the cheapest buying price for future days.
            // ----------------------------------------------------
            runningMin = Math.min(runningMin, prices[i]);
        }

        return maxProfit;
    }

}