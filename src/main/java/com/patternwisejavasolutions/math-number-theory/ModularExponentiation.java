package com.patternwisejavasolutions.mathnumbertheory;

public class ModularExponentiation {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: base = 2, exponent = 10, mod = 1000
     * Sample Output: 24
     *
     * Compute (base^exponent) % mod without creating a huge number.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Remainders can be kept small after every multiplication.
     * This avoids overflow and giant numbers.
     * The key rule is (a * b) % mod equals ((a % mod) * (b % mod)) % mod.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Multiply base exponent times.
     * Take modulo after every multiplication.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Start answer = 1.
     * 2. Repeat exponent times: answer = answer * base % mod.
     * 3. Return answer.
     * Time Complexity: O(exponent)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * 2^10 % 1000
     * Repeated multiplication reaches 1024, remainder 24.
     */
    public long bruteForce(long base, long exponent, long mod) {
        long answer = 1 % mod;
        base %= mod;
        for (long i = 0; i < exponent; i++) {
            answer = (answer * base) % mod;
        }
        return answer;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Use exponent bits.
     * If the current bit is 1, multiply the answer by the current base.
     * Square the base each step.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. While exponent > 0, check the last bit.
     * 2. If odd, multiply answer by base.
     * 3. Square base and halve exponent.
     * Time Complexity: O(log exponent)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * 2^10 uses powers 2, 4, 16, 256.
     * Binary 10 chooses 2^8 and 2^2, giving 1024 % 1000 = 24.
     */
    public long optimized(long base, long exponent, long mod) {
        long answer = 1 % mod;
        base %= mod;

        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                // A 1 bit means this power of base belongs in the answer.
                answer = (answer * base) % mod;
            }
            // Squaring moves from base^(1), base^(2), base^(4), ...
            base = (base * base) % mod;
            exponent >>= 1;
        }

        return answer;
    }
}
