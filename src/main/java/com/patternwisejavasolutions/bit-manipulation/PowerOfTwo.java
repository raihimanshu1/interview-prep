package com.patternwisejavasolutions.bitmanipulation;

public class PowerOfTwo {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: n = 16
     * Sample Output: true
     *
     * Return whether n is exactly 1, 2, 4, 8, 16, and so on.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Powers of two are made by multiplying by 2 again and again.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Start at 1 and keep multiplying by 2 until we reach or pass n.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. If n <= 0, return false.
     * 2. Keep value = 1.
     * 3. Multiply by 2 until value >= n.
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * n = 16
     * 1 -> 2 -> 4 -> 8 -> 16, so true.
     */
    public boolean bruteForce(int n) {
        if (n <= 0) {
            return false;
        }

        long value = 1;
        while (value < n) {
            value *= 2;
        }
        return value == n;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * A positive power of two has exactly one 1-bit.
     * n & (n - 1) removes the lowest 1-bit.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Check n is positive.
     * 2. Return whether n & (n - 1) is zero.
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * 16 = 10000
     * 15 = 01111
     * AND = 00000, so true.
     */
    public boolean optimized(int n) {
        // Positive powers of two have one set bit, so removing the lowest set bit leaves zero.
        return n > 0 && (n & (n - 1)) == 0;
    }
}
