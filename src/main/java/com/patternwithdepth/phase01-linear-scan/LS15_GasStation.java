package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Gas Station
 * (Problem number: 32 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given two integer arrays gas and cost, return the starting gas station's index
 * if you can travel around the circuit once in the clockwise direction. If not, return -1.
 * If there exists a solution, it is guaranteed to be unique.
 *
 * Example 1:
 * Input: gas = [1, 2, 3, 4, 5], cost = [3, 4, 5, 1, 2]
 * Output: 3
 * Explanation: Start at station 3 (index 3) and fill up with 4 gas.
 * Travel to station 4 (index 4) using 2 gas, tank = 6.
 * Travel to station 0 (index 0) using 3 gas, tank = 8.
 * Travel to station 1 (index 1) using 4 gas, tank = 7.
 * Travel to station 2 (index 2) using 5 gas, tank = 6.
 * Travel to station 3 (index 3) using 1 gas, tank = 9.
 *
 * Example 2:
 * Input: gas = [2, 3, 4], cost = [3, 4, 3]
 * Output: -1
 * Explanation: Cannot complete the circuit.
 *
 * CONSTRAINTS:
 * - gas.length == n
 * - 1 <= n <= 10^5
 * - 0 <= gas[i], cost[i] <= 10^4
 *
 * APPROACH:
 * OPTIMIZED: O(n) - If total gas < total cost, impossible. Otherwise, find start.
 */

public class LS15_GasStation {

    public static void main(String[] args) {
        // 1. Create our input arrays.
        int[] gas = {1, 2, 3, 4, 5};
        int[] cost = {3, 4, 5, 1, 2};

        // 2. --- OPTIMIZED APPROACH ---
        int result = canCompleteCircuit(gas, cost);
        System.out.println("Starting Station: " + result); // Expected: 3

        // 3. Test impossible case.
        int[] gas2 = {2, 3, 4};
        int[] cost2 = {3, 4, 3};
        System.out.println("Impossible: " + canCompleteCircuit(gas2, cost2)); // Expected: -1
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: If total gas < total cost, it's impossible to complete the circuit.
    //       Otherwise, there MUST be a valid starting point. We simulate from
    //       potential start points: if tank becomes negative, the next station
    //       is the only possible start (all previous stations can't be start).
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int canCompleteCircuit(int[] gas, int[] cost) {
        int n = gas.length;
        int totalTank = 0; // 1. Track total gas - total cost.
        int currentTank = 0; // 2. Track current trip's tank.
        int start = 0; // 3. Track the candidate starting station.

        // 4. Iterate through each station.
        for (int i = 0; i < n; i++) {
            // 5. Calculate net gain at station i.
            int diff = gas[i] - cost[i];
            totalTank += diff; // 6. Accumulate total gas vs cost.

            // 7. Add diff to current tank.
            currentTank += diff;

            // 8. If current tank goes negative, we can't reach station i+1 from start.
            //    So start must be at least i+1. Reset current tank.
            if (currentTank < 0) {
                start = i + 1; // 9. Next station becomes new start candidate.
                currentTank = 0; // 10. Reset tank for new start.
            }
        }

        // 11. If total gas < total cost, no solution exists.
        if (totalTank < 0) {
            return -1;
        }

        // 12. Otherwise, start is the unique valid starting station.
        return start;
    }
}