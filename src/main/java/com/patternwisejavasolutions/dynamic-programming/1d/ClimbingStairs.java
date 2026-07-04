
package com.patternwisejavasolutions.dynamicProgramming.1d;
public class ClimbingStairs {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: n = 5
     * Sample Output: 8
     *
     * Input: n = 5 stairs
     * You can climb 1 or 2 steps at a time.
     * Output: 8 different ways to reach the top.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * To reach stair n, your last move came from either stair n - 1 or stair n - 2.
     * So ways(n) = ways(n - 1) + ways(n - 2).
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Imagine the last move onto stair n. It was either a 1-step jump from
     * n - 1 or a 2-step jump from n - 2. Brute force asks both smaller
     * questions every time, so ways(3), ways(4), etc. get recalculated often.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * n = 3
     * Ways to reach 3:
     * From stair 2, take 1 step.
     * From stair 1, take 2 steps.
     * ways(3) = ways(2) + ways(1) = 2 + 1 = 3.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively ask ways(n - 1) and ways(n - 2).
     * 2. Base: n <= 2 has n ways.
     * Time Complexity: O(2^n)
     * Space Complexity: O(n) recursion
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This directly follows the two possible last moves.
     */
    public int bruteForce(int n) {
        if (n <= 2) {
            return n;
        }

        return bruteForce(n - 1) + bruteForce(n - 2);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force repeats the same smaller answers many times.
     * Store previous two answers and build upward like Fibonacci: once ways(3)
     * is known, reuse it to compute ways(4) instead of calling recursion again.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * ways(1)=1, ways(2)=2
     * ways(3)=3
     * ways(4)=5
     * ways(5)=8
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Handle n <= 2.
     * 2. Keep oneStepBack and twoStepsBack.
     * 3. current = oneStepBack + twoStepsBack.
     * 4. Move forward until n.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Only the previous two DP states are needed at any moment.
     */
    public int optimized(int n) {
        if (n <= 2) {
            return n;
        }

        int twoStepsBack = 1;
        int oneStepBack = 2;

        for (int stair = 3; stair <= n; stair++) {
            // ways(stair) = ways(stair - 1) + ways(stair - 2).
            int current = oneStepBack + twoStepsBack;
            twoStepsBack = oneStepBack;
            oneStepBack = current;
        }

        return oneStepBack;
    }
}
