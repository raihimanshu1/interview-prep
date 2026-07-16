package com.patternwisejavasolutions.mathnumbertheory;

public class SieveOfEratosthenes {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: n = 10
     * Sample Output: [2, 3, 5, 7]
     *
     * Return all prime numbers up to n.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A prime number has only two divisors: 1 and itself.
     * The sieve removes multiples of known primes.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Check every number one by one.
     * For each number, test whether any smaller number divides it.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For every number from 2 to n, check primality.
     * 2. Try divisors from 2 to number - 1.
     * 3. Add the number if no divisor works.
     * Time Complexity: O(n^2)
     * Space Complexity: O(1) besides output
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * n = 10
     * 2 prime, 3 prime, 4 not prime, 5 prime, 6 not prime, 7 prime.
     */
    public java.util.List<Integer> bruteForce(int n) {
        java.util.List<Integer> primes = new java.util.ArrayList<>();
        for (int number = 2; number <= n; number++) {
            if (isPrimeSlow(number)) {
                primes.add(number);
            }
        }
        return primes;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Once we know 2 is prime, all multiples of 2 are not prime.
     * Do the same for 3, 5, and so on.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Mark every number as prime at first.
     * 2. For each prime p, mark p * p, p * p + p, ... as not prime.
     * 3. Collect the numbers still marked prime.
     * Time Complexity: O(n log log n)
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * n = 10
     * Mark 4, 6, 8, 10 from prime 2.
     * Mark 9 from prime 3.
     * Remaining: 2, 3, 5, 7.
     */
    public java.util.List<Integer> optimized(int n) {
        java.util.List<Integer> primes = new java.util.ArrayList<>();
        if (n < 2) {
            return primes;
        }

        boolean[] isPrime = new boolean[n + 1];
        java.util.Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;

        for (int number = 2; number * number <= n; number++) {
            if (isPrime[number]) {
                for (int multiple = number * number; multiple <= n; multiple += number) {
                    // number * 2, number * 3, ... were already crossed out earlier.
                    isPrime[multiple] = false;
                }
            }
        }

        for (int number = 2; number <= n; number++) {
            if (isPrime[number]) {
                primes.add(number);
            }
        }
        return primes;
    }

    private boolean isPrimeSlow(int number) {
        if (number < 2) {
            return false;
        }
        for (int divisor = 2; divisor < number; divisor++) {
            if (number % divisor == 0) {
                // Finding any divisor proves the number is composite.
                return false;
            }
        }
        return true;
    }
}
