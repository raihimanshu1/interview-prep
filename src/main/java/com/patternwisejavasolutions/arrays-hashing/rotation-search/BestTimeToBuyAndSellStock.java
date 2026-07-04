
package com.patternwisejavasolutions.arraysHashing.rotationSearch;
public class BestTimeToBuyAndSellStock {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Choose one day to buy and a later day to sell for maximum profit.
 *
 * Sample Input:
 * prices = [7, 1, 5, 3, 6, 4]
 *
 * Sample Output:
 * 5
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Profit is sell price minus an earlier buy price. For each selling day, the
 * best buy is simply the cheapest price seen before or on that day.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to try every buy day with every later sell day and
 * calculate the profit for that pair.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Choose buy index.
 * 2. Choose sell index after buy.
 * 3. Compute prices[sell] - prices[buy].
 * 4. Keep the maximum profit.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * prices = [7, 1, 5, 3, 6, 4]
 * Buy at price 1 and sell later at price 6.
 * Profit = 6 - 1 = 5.
 */

public int bruteForce(int[] prices) {
        int best = 0;

        for (int buy = 0; buy < prices.length; buy++) {
            for (int sell = buy + 1; sell < prices.length; sell++) {
                best = Math.max(best, prices[sell] - prices[buy]);
            }
        }

        return best;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is trying all earlier buy days for every sell
 * day. While scanning once, remember the lowest price seen so far. Today's best
 * profit uses that lowest earlier price.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Set minPrice to a large value.
 * 2. For each price, update minPrice.
 * 3. Compute price - minPrice as profit.
 * 4. Track best profit.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * See 7 -> minPrice = 7.
 * See 1 -> minPrice = 1.
 * See 6 later -> profit = 6 - 1 = 5, so bestProfit becomes 5.
 */

public int optimized(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int bestProfit = 0;

        for (int price : prices) {
            // The cheapest price so far is the best possible buy for today.
            minPrice = Math.min(minPrice, price);
            // Selling today after the cheapest earlier price gives today's best profit.
            bestProfit = Math.max(bestProfit, price - minPrice);
        }

        return bestProfit;
    }
}
