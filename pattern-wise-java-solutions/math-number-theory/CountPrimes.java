public class CountPrimes {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: n = 10
     * Sample Output: 4
     *
     * Count prime numbers strictly less than n.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Prime numbers have no divisors except 1 and themselves.
     * For n = 10, primes less than it are 2, 3, 5, 7.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Check every number below n and test if it is prime.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Loop number from 2 to n - 1.
     * 2. Test every possible divisor.
     * 3. Count numbers that are prime.
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * n = 10
     * 2, 3, 5, 7 are prime.
     * Count is 4.
     */
    public int bruteForce(int n) {
        int count = 0;
        for (int number = 2; number < n; number++) {
            if (isPrimeSlow(number)) {
                count++;
            }
        }
        return count;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Sieve marks composite numbers in groups.
     * This avoids checking divisors for every number separately.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Create prime marks for 0 to n - 1.
     * 2. For each prime p, mark multiples from p * p.
     * 3. Count remaining prime marks.
     * Time Complexity: O(n log log n)
     * Space Complexity: O(n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * n = 10
     * Mark 4, 6, 8 from 2 and 9 from 3.
     * Remaining primes are 2, 3, 5, 7.
     */
    public int optimized(int n) {
        if (n <= 2) {
            return 0;
        }

        boolean[] isPrime = new boolean[n];
        java.util.Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;

        for (int number = 2; number * number < n; number++) {
            if (isPrime[number]) {
                for (int multiple = number * number; multiple < n; multiple += number) {
                    // Smaller multiples were already marked by smaller prime factors.
                    isPrime[multiple] = false;
                }
            }
        }

        int count = 0;
        for (boolean prime : isPrime) {
            if (prime) {
                count++;
            }
        }
        return count;
    }

    private boolean isPrimeSlow(int number) {
        for (int divisor = 2; divisor < number; divisor++) {
            if (number % divisor == 0) {
                // A clean division means number has a factor other than 1 and itself.
                return false;
            }
        }
        return number >= 2;
    }
}
