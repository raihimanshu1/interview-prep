
package com.patternwisejavasolutions.dynamicProgramming.1d;
public class FibonacciNumber {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: n = 4
     * Sample Output: 3
     *
     * Fibonacci starts as 0, 1, 1, 2, 3...
     * Return the nth Fibonacci number.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Every number after the first two is made by adding the previous two numbers.
     * So fib(4) = fib(3) + fib(2) = 2 + 1 = 3.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Directly follow the definition.
     * To find fib(n), ask for fib(n - 1) and fib(n - 2).
     * This creates repeated states: fib(2) is needed inside fib(4) more than once.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. If n is 0 or 1, return n.
     * 2. Otherwise return fib(n - 1) + fib(n - 2).
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * n = 4
     * fib(4) = fib(3) + fib(2)
     * fib(3) = fib(2) + fib(1)
     * Many smaller answers are recalculated.
     */
    public int bruteForce(int n) {
        if (n <= 1) {
            return n;
        }
        return bruteForce(n - 1) + bruteForce(n - 2);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * We only need the last two Fibonacci numbers to make the next one.
     * Carry those two values forward in a loop, so each Fibonacci state is
     * computed once and then reused.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep previousTwo = 0 and previousOne = 1.
     * 2. Build each next value by adding them.
     * 3. Shift the two saved values forward.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * n = 4
     * Start 0, 1.
     * next = 1, then 2, then 3.
     * Return 3.
     */
    public int optimized(int n) {
        if (n <= 1) {
            return n;
        }

        int previousTwo = 0;
        int previousOne = 1;

        for (int i = 2; i <= n; i++) {
            // fib(i) = fib(i - 2) + fib(i - 1).
            int current = previousTwo + previousOne;
            previousTwo = previousOne;
            previousOne = current;
        }

        return previousOne;
    }
}
