package com.patternwisejavasolutions.bitmanipulation;

public class NumberOf1Bits {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Count how many 1 bits are in the binary form of an integer.
     *
     * Sample Input: n = 11
     * Sample Output: 3
     * Why? 11 is binary 1011.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Binary is made of 0s and 1s. To count 1s, inspect bits one by one.
     * The bit pattern works because n & 1 tells whether the last bit is 1.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Check all 32 bit positions of the integer.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Start count at 0.
     * 2. Repeat 32 times.
     * 3. Add one if the last bit is 1.
     * 4. Shift right to inspect the next bit.
     *
     * Time Complexity: O(32)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * 1011 has last bit 1, then 1, then 0, then 1. Count is 3.
     */
    public int bruteForce(int n) {
        int count = 0;
        for (int i = 0; i < 32; i++) {
            if ((n & 1) == 1) {
                count++;
            }
            n >>>= 1;
        }
        return count;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force always checks 32 positions. Brian Kernighan's trick removes
     * the lowest 1 bit each time using n & (n - 1). So the loop runs only once
     * per 1 bit.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. While n is not 0, set n = n & (n - 1).
     * 2. Increase count each time.
     *
     * Time Complexity: O(number of 1 bits)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * 1011 -> 1010 removes one 1.
     * 1010 -> 1000 removes one 1.
     * 1000 -> 0000 removes one 1.
     */
    public int optimized(int n) {
        int count = 0;
        while (n != 0) {
            // This removes exactly the lowest set bit.
            n &= (n - 1);
            count++;
        }
        return count;
    }
}

