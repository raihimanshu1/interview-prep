
package com.patternwisejavasolutions.binarySearch.searchOnAnswer;
public class KokoEatingBananas {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Koko eats bananas at a fixed speed k bananas per hour. Each hour she chooses one pile.
     * Return the smallest speed k so she can finish all piles within h hours.
     *
     * Sample Input:
     * piles = [3, 6, 7, 11], h = 8
     *
     * Sample Output:
     * 4
     *
     * SCHOOL-LEVEL INTUITION:
     * A faster speed always helps, never hurts. If speed 4 works, speed 5 also works. If speed 2
     * is too slow, speed 1 is also too slow. This creates a sorted yes/no answer line.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Try speeds from 1 upward. For each speed, calculate how many hours are needed.
     * The first speed that finishes on time is the smallest valid speed.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Find maxPile because Koko never needs a speed larger than the biggest pile.
     * 2. For speed from 1 to maxPile, compute required hours.
     * 3. Return the first speed with hours <= h.
     *
     * BRUTE FORCE DRY RUN
     * piles = [3, 6, 7, 11], h = 8
     * speed 1 needs 27 hours -> too slow
     * speed 2 needs 15 hours -> too slow
     * speed 3 needs 10 hours -> too slow
     * speed 4 needs 8 hours -> works, return 4
     *
     * Time: O(n * maxPile), Space: O(1)
     */
    public int bruteForce(int[] piles, int h) {
        int maxPile = 0;
        for (int pile : piles) {
            maxPile = Math.max(maxPile, pile);
        }

        for (int speed = 1; speed <= maxPile; speed++) {
            if (hoursNeeded(piles, speed) <= h) {
                return speed;
            }
        }

        return maxPile;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is checking every speed. Because speeds have a monotonic pattern
     * (too slow, too slow, works, works, works), binary search can find the first working speed.
     *
     * Pattern used: Binary Search on Answer.
     *
     * OPTIMIZED ALGORITHM
     * 1. Search speeds from 1 to maxPile.
     * 2. Let mid be a candidate speed.
     * 3. If mid finishes within h hours, save it and search smaller speeds.
     * 4. If mid is too slow, search larger speeds.
     * 5. Return the smallest saved working speed.
     *
     * OPTIMIZED DRY RUN
     * piles = [3, 6, 7, 11], h = 8
     * low = 1, high = 11, mid = 6 -> works, answer = 6, try smaller
     * mid = 3 -> needs 10 hours, too slow
     * mid = 4 -> works, answer = 4, try smaller
     * return 4
     *
     * Time: O(n log maxPile), Space: O(1)
     */
    public int optimized(int[] piles, int h) {
        int high = 0;
        for (int pile : piles) {
            high = Math.max(high, pile);
        }

        int low = 1;
        int answer = high;

        while (low <= high) {
            int speed = low + (high - low) / 2;

            if (hoursNeeded(piles, speed) <= h) {
                // This speed works, so we try to prove an even slower speed can also work.
                answer = speed;
                high = speed - 1;
            } else {
                // Too many hours means Koko must eat faster.
                low = speed + 1;
            }
        }

        return answer;
    }

    private long hoursNeeded(int[] piles, int speed) {
        long hours = 0;

        for (int pile : piles) {
            // Ceiling division: a partly eaten final hour still counts as one full hour.
            hours += (pile + speed - 1) / speed;
        }

        return hours;
    }
}

