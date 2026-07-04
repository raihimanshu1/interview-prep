
package com.patternwisejavasolutions.binarySearch.core;
public class GuessNumberHigherOrLower {

    private int pickedNumber;

    public GuessNumberHigherOrLower(int pickedNumber) {
        this.pickedNumber = pickedNumber;
    }

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * A hidden number is picked from 1 to n. The guess API returns:
     * -1 if your guess is too high, 1 if your guess is too low, and 0 if correct.
     *
     * Sample Input: n = 10, pick = 6
     * Sample Output: 6
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * This is like someone saying "higher" or "lower" after each guess. Those
     * clues let us throw away half the remaining numbers each time.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Guess every number from 1 to n until the API says correct. This works because the hidden
     * number must be somewhere in that full range.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For number from 1 to n, call guess(number).
     * 2. If the response is 0, return number.
     * 3. Return -1 only if no number matches.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * n = 10, pick = 6
     * Guess 1, 2, 3, 4, 5 are too low. Guess 6 is correct.
     */
    public int bruteForce(int n) {
        for (int number = 1; number <= n; number++) {
            if (guess(number) == 0) {
                return number;
            }
        }

        return -1;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The brute force waste is ignoring the API's "higher/lower" clue. If the API says a guess is
     * too high, every larger guess is impossible. If it says too low, every smaller guess is
     * impossible. That lets binary search remove half the numbers each round.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Search from 1 to n.
     * 2. Guess mid.
     * 3. If correct, return mid.
     * 4. If too high, move right to mid - 1.
     * 5. If too low, move left to mid + 1.
     *
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * n = 10, pick = 6
     * Guess 5, too low, search 6..10.
     * Guess 8, too high, search 6..7.
     * Guess 6, correct.
     */
    public int optimized(int n) {
        int left = 1;
        int right = n;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int response = guess(mid);

            if (response == 0) {
                return mid;
            }

            if (response < 0) {
                // Guess was too high, so discard mid and every number larger than it.
                right = mid - 1;
            } else {
                // Guess was too low, so discard mid and every number smaller than it.
                left = mid + 1;
            }
        }

        return -1;
    }

    private int guess(int number) {
        if (number == pickedNumber) {
            return 0;
        }

        return number > pickedNumber ? -1 : 1;
    }
}
