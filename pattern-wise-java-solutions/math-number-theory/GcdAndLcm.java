public class GcdAndLcm {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: a = 12, b = 18
     * Sample Output: gcd = 6, lcm = 36
     *
     * GCD is the largest number that divides both.
     * LCM is the smallest positive number divisible by both.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * GCD is the biggest common factor.
     * LCM is the first common multiple.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Check possible divisors from small to big and remember the largest valid one.
     * For LCM, try multiples until both numbers divide it.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For gcd, scan 1 to min(a, b).
     * 2. For lcm, scan from max(a, b) upward.
     * 3. Return both results.
     * Time Complexity: O(min(a,b) + lcm)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * a = 12, b = 18
     * Common divisors include 1, 2, 3, 6, so gcd is 6.
     * First common multiple is 36.
     */
    public long[] bruteForce(long a, long b) {
        if (a == 0 || b == 0) {
            return new long[] {Math.max(Math.abs(a), Math.abs(b)), 0};
        }

        long gcd = 1;
        for (long divisor = 1; divisor <= Math.min(Math.abs(a), Math.abs(b)); divisor++) {
            if (a % divisor == 0 && b % divisor == 0) {
                gcd = divisor;
            }
        }

        long lcm = Math.max(Math.abs(a), Math.abs(b));
        while (lcm % Math.abs(a) != 0 || lcm % Math.abs(b) != 0) {
            lcm++;
        }
        return new long[] {gcd, lcm};
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Euclid's rule says gcd(a, b) is the same as gcd(b, a % b).
     * Once gcd is known, lcm = abs(a * b) / gcd.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Repeatedly replace a, b with b, a % b.
     * 2. When b becomes 0, a is gcd.
     * 3. Compute lcm using gcd.
     * Time Complexity: O(log min(a,b))
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * gcd(18, 12) -> gcd(12, 6) -> gcd(6, 0) = 6.
     * lcm = 12 * 18 / 6 = 36.
     */
    public long[] optimized(long a, long b) {
        long gcd = gcd(a, b);
        long lcm = (a == 0 || b == 0) ? 0 : Math.abs(a / gcd * b);
        return new long[] {gcd, lcm};
    }

    private long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            // Euclid removes full groups of b; the leftover remainder has the same gcd.
            long remainder = a % b;
            a = b;
            b = remainder;
        }
        return a;
    }
}
