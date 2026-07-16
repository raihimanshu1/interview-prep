package com.companywisejavasolutions.ebay.solutions;

public class BinaryGap {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * In the binary form of n, return the largest distance between two 1 bits.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Scan bit positions from right to left. Remember where the previous 1 was.
     * Each new 1 can form a gap with the previous 1.
     */

    /*
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public int binaryGap(int n) {
        int previousOne = -1;
        int best = 0;
        int position = 0;

        while (n > 0) {
            if ((n & 1) == 1) {
                if (previousOne != -1) {
                    best = Math.max(best, position - previousOne);
                }
                previousOne = position;
            }
            n >>= 1;
            position++;
        }

        return best;
    }
}
