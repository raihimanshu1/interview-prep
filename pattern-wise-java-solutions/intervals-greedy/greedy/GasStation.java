public class GasStation {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: gas = [1,2,3,4,5], cost = [3,4,5,1,2]
     * Sample Output: 3
     *
     * Input gas = [1,2,3,4,5], cost = [3,4,5,1,2]
     * Output: 3
     * Start at station 3 to complete the circle.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try every station as the start.
     * Simulate driving around the full circle.
     * If gas tank never becomes negative, that start works.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to stand at each station and test whether a
     * full trip works from there. If the tank ever drops below zero, that start
     * cannot finish the circle.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * Start at 0: tank becomes negative quickly.
     * Start at 3: gain 4, spend 1, then continue and never go negative.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. For each start station, simulate n moves.
     * 2. Add gas, subtract cost.
     * 3. If tank goes negative, start fails.
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * For each start, the inner loop simulates exactly one full lap.
     */
    public int bruteForce(int[] gas, int[] cost) {
        int n = gas.length;

        for (int start = 0; start < n; start++) {
            int tank = 0;
            boolean possible = true;

            for (int step = 0; step < n; step++) {
                int station = (start + step) % n;
                tank += gas[station] - cost[station];

                if (tank < 0) {
                    possible = false;
                    break;
                }
            }

            if (possible) {
                return start;
            }
        }

        return -1;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * If total gas is less than total cost, no answer exists.
     * If tank becomes negative at station i, any start between previous start and i also fails.
     * So next possible start is i + 1.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * When tank drops below 0, it means the current start cannot reach this station.
     * Starting even later inside this failed segment would have even less gas support.
     * So skip the whole segment.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Track total balance and current tank.
     * 2. If tank < 0, move start to next station and reset tank.
     * 3. At end, return start if total balance >= 0.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * The loop keeps one candidate start and skips whole failed segments.
     */
    public int optimized(int[] gas, int[] cost) {
        int totalBalance = 0;
        int tank = 0;
        int start = 0;

        for (int station = 0; station < gas.length; station++) {
            int gain = gas[station] - cost[station];
            totalBalance += gain;
            tank += gain;

            if (tank < 0) {
                // This start cannot reach station + 1; no station inside this
                // failed segment can do better, because it starts with less help.
                start = station + 1;
                tank = 0;
            }
        }

        return totalBalance >= 0 ? start : -1;
    }
}
