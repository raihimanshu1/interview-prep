package com.patternwisejavasolutions.bitmanipulation;

public class ReverseBits {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Reverse the 32 bits of an integer.
     *
     * Sample Input: binary 00000010100101000001111010011100
     * Sample Output: binary 00111001011110000010100101000000
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Reversing bits is like moving beads from one side of a string to the other.
     * Take the last bit of n and place it at the end of the answer being built.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Store all 32 bits in an array, then rebuild them in reverse order.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Extract 32 bits into an array.
     * 2. Read the array from front to back while building reversed result.
     * 3. Shift result left before adding each bit.
     *
     * Time Complexity: O(32)
     * Space Complexity: O(32)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * If the last bits are 1, 0, 1, they become the first bits of the answer.
     */
    public int bruteForce(int n) {
        int[] bits = new int[32];
        for (int i = 0; i < 32; i++) {
            bits[i] = (n >>> i) & 1;
        }

        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | bits[i];
        }
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The array is not needed. We can take one bit from n and immediately place
     * it into result, repeating exactly 32 times.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start result as 0.
     * 2. Repeat 32 times.
     * 3. Shift result left to make room.
     * 4. Copy n's last bit into result.
     * 5. Shift n right unsigned.
     *
     * Time Complexity: O(32)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * result starts 0. If n's last bit is 1, result becomes 1. Next bit gets
     * placed after shifting result left.
     */
    public int optimized(int n) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            // Make room, then copy n's current lowest bit into that room.
            result <<= 1;
            result |= (n & 1);
            // Unsigned shift keeps filling with 0s even when n is negative.
            n >>>= 1;
        }
        return result;
    }
}
