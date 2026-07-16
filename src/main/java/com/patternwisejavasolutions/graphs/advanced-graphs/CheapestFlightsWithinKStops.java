package com.patternwisejavasolutions.graphs.advancedgraphs;

import java.util.Arrays;

public class CheapestFlightsWithinKStops {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input flights [from,to,price], source, destination, and k stops.
     * Return cheapest price using at most k stops, or -1.
     *
     * Sample Input:
     * n = 4, flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]],
     * src = 0, dst = 3, k = 1
     *
     * Sample Output:
     * 700
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A stop limit means the route can use at most k + 1 flights.
     * One way is to try possible routes from the source and stop searching when the route gets too long.
     * The challenge is that a cheap-looking early flight may not be best if it uses too many steps.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Start from the source city and try every outgoing flight.
     * Keep walking flight by flight until we reach the destination or run out of allowed flights.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * With k = 1, we can take at most 2 flights.
     * From 0, try 0 -> 1 with cost 100.
     * From 1, try 1 -> 3 with total cost 700 and record it.
     * Also try other routes within 2 flights; the smallest destination cost is the answer.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. DFS all routes from source up to k + 1 edges.
     * 2. Track cheapest route reaching destination.
     * Time Complexity: exponential
     * Space Complexity: O(k)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(int n, int[][] flights, int src, int dst, int k) {
        int answer = dfs(flights, src, dst, k + 1, 0, new boolean[n]);
        return answer >= Integer.MAX_VALUE / 2 ? -1 : answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * DFS repeats many route prefixes.
     * Bellman-Ford style works well with edge limits because each round means "allow one more flight."
     * Use a copy so one round does not accidentally chain multiple new flights in the same round.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * prices[src] = 0.
     * Round 1 relaxes direct flights.
     * Round 2 relaxes routes with two flights using previous round prices.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Initialize prices with infinity except source.
     * 2. Repeat k + 1 times.
     * 3. Copy previous prices.
     * 4. Relax every flight from previous prices into copy.
     * 5. Return destination price.
     * Time Complexity: O(kE)
     * Space Complexity: O(V)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(int n, int[][] flights, int src, int dst, int k) {
        int[] prices = new int[n];
        Arrays.fill(prices, Integer.MAX_VALUE / 2);
        prices[src] = 0;

        for (int edges = 0; edges <= k; edges++) {
            int[] nextPrices = prices.clone();

            for (int[] flight : flights) {
                int from = flight[0];
                int to = flight[1];
                int price = flight[2];

                if (prices[from] + price < nextPrices[to]) {
                    // Relax this flight using only prices from the previous edge-count round.
                    nextPrices[to] = prices[from] + price;
                }
            }

            prices = nextPrices;
        }

        return prices[dst] >= Integer.MAX_VALUE / 2 ? -1 : prices[dst];
    }


    private int dfs(int[][] flights, int city, int dst, int edgesLeft, int cost, boolean[] visiting) {
        if (city == dst) {
            return cost;
        }

        if (edgesLeft == 0) {
            return Integer.MAX_VALUE / 2;
        }

        visiting[city] = true; // Avoid cycles inside the current route being tried.
        int best = Integer.MAX_VALUE / 2;

        for (int[] flight : flights) {
            if (flight[0] == city && !visiting[flight[1]]) {
                best = Math.min(best, dfs(flights, flight[1], dst, edgesLeft - 1, cost + flight[2], visiting));
            }
        }

        visiting[city] = false; // Backtrack so another route can use this city.
        return best;
    }
}
