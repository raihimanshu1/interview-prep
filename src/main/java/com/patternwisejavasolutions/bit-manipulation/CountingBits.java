package com.patternwisejavasolutions.bitmanipulation;

public class CountingBits {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * For every number from 0 to n, return how many 1 bits it has.
     *
     * Sample Input: n = 5
     * Sample Output: [0, 1, 1, 2, 1, 2]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Each number's bit count can be counted directly, but nearby numbers share
     * patterns. Removing the lowest 1 bit gives a smaller number whose answer we
     * already know.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For every number, count its bits independently.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create answer array of size n + 1.
     * 2. For every value from 0 to n, count its 1 bits.
     * 3. Store that count.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * 0 has 0 ones. 1 has 1. 2 is 10, so 1. 3 is 11, so 2.
     */
    public int[] bruteForce(int n) {
        int[] answer = new int[n + 1];
        for (int value = 0; value <= n; value++) {
            answer[value] = countOnes(value);
        }
        return answer;
    }

    private int countOnes(int value) {
        int count = 0;
        while (value != 0) {
            // value & 1 reads the current lowest bit: add 1 only when that bit is set.
            count += value & 1;
            // Unsigned shift moves the next bit into the lowest position.
            value >>>= 1;
        }
        return count;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force repeats bit counting. For any number x, x & (x - 1)
     * removes one 1 bit. So bits[x] = bits[x & (x - 1)] + 1.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. answer[0] = 0.
     * 2. For value from 1 to n, compute answer[value & (value - 1)] + 1.
     * 3. Store it.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * value 5 is 101. 5 & 4 is 100, so answer[5] = answer[4] + 1 = 2.
     */
    public int[] optimized(int n) {
        int[] answer = new int[n + 1];
        for (int value = 1; value <= n; value++) {
            // Drop the lowest 1 bit, then add it back to the known smaller answer.
            answer[value] = answer[value & (value - 1)] + 1;
        }
        return answer;
    }
}
