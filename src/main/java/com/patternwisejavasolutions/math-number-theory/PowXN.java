
package com.patternwisejavasolutions.mathNumberTheory;
public class PowXN {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: x = 2.0, n = 10
     * Sample Output: 1024.0
     *
     * Compute x raised to power n.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * x^n means multiply x by itself n times.
     * Negative powers mean take the reciprocal.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Multiply one step at a time.
     * If n is negative, compute the positive power and divide 1 by it.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Use long exponent to safely handle Integer.MIN_VALUE.
     * 2. Multiply abs(n) times.
     * 3. Invert the result for negative n.
     * Time Complexity: O(abs(n))
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * x = 2, n = 3
     * answer = 1 -> 2 -> 4 -> 8.
     */
    public double bruteForce(double x, int n) {
        long exponent = Math.abs((long) n);
        double answer = 1.0;
        for (long i = 0; i < exponent; i++) {
            answer *= x;
        }
        return n < 0 ? 1.0 / answer : answer;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Fast power uses the binary form of the exponent.
     * Square the base each step and only multiply answer when the current bit is 1.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Convert n to a non-negative long exponent.
     * 2. While exponent > 0, multiply answer when exponent is odd.
     * 3. Square x and halve exponent.
     * Time Complexity: O(log abs(n))
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * 2^10: exponent binary is 1010.
     * Use squared bases 4 and 256 to reach 1024.
     */
    public double optimized(double x, int n) {
        long exponent = n;
        if (exponent < 0) {
            x = 1.0 / x;
            exponent = -exponent;
        }

        double answer = 1.0;
        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                // Current exponent bit is 1, so include this squared base.
                answer *= x;
            }
            // Square base to represent the next power of two.
            x *= x;
            exponent >>= 1;
        }

        return answer;
    }
}
