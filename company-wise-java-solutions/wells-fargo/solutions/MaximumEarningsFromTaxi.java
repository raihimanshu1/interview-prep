package wellsfargo.solutions;

import java.util.Arrays;

public class MaximumEarningsFromTaxi {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given taxi rides with start, end, and tip, choose non-overlapping rides to maximize earnings.
     *
     * INPUT
     * rides[i] = [start, end, tip].
     *
     * OUTPUT
     * Maximum total earnings.
     *
     * EXAMPLE
     * n = 5, rides = [[2,5,4],[1,5,1]] -> 7.
     *
     * WHAT IT MEANS
     * Each ride is a time interval with profit end - start + tip. You can only take rides that do not overlap.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1: [[2,5,4],[1,5,1]] -> 7
     * Example 2: [[1,6,1],[3,10,2],[10,12,3]] -> can take compatible rides ending before next start
     * Example 3: No rides -> 0
     *
     * EDGE CASES
     * - Ride ending at time x is compatible with ride starting at x.
     * - Sort by end time for DP.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Sort rides and recursively decide: skip this ride, or take it if it starts after the time we are available.
     *
     * Time Complexity: O(2^n). Space Complexity: O(n) recursion depth.
     */

    /*
     * OPTIMIZED APPROACH
     *
     * Weighted interval DP. Sort by end time. For each ride, compare skipping it with taking it plus the best compatible earlier ride found by binary search.
     *
     * Time Complexity: O(n log n). Space Complexity: O(n).
     */
public long bruteForce(int n, int[][] rides) {
        Arrays.sort(rides, (a, b) -> Integer.compare(a[0], b[0]));
        return choose(rides, 0, 0);
    }

    private long choose(int[][] rides, int index, int availableAt) {
        if (index == rides.length) {
            return 0;
        }

        long skip = choose(rides, index + 1, availableAt);
        long take = 0;

        if (rides[index][0] >= availableAt) {
            long profit = rides[index][1] - rides[index][0] + rides[index][2];
            take = profit + choose(rides, index + 1, rides[index][1]);
        }

        return Math.max(skip, take);
    }

    public long optimized(int n, int[][] rides) {
        Arrays.sort(rides, (a, b) -> Integer.compare(a[1], b[1]));
        long[] dp = new long[rides.length + 1];

        for (int i = 1; i <= rides.length; i++) {
            int[] ride = rides[i - 1];
            long profit = ride[1] - ride[0] + ride[2];
            int previous = lastRideEndingBefore(rides, i - 2, ride[0]);
            dp[i] = Math.max(dp[i - 1], profit + dp[previous + 1]);
        }

        return dp[rides.length];
    }

    private int lastRideEndingBefore(int[][] rides, int right, int start) {
        int left = 0;
        int answer = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (rides[mid][1] <= start) {
                answer = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        MaximumEarningsFromTaxi solver = new MaximumEarningsFromTaxi();
        System.out.println("Use bruteForce and optimized with the examples in MORE INPUTS TO PRACTICE.");
    }
}
