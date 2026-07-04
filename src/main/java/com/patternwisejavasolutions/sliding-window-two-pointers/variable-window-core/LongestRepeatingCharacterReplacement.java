
package com.patternwisejavasolutions.slidingWindowTwoPointers.variableWindowCore;
public class LongestRepeatingCharacterReplacement {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input:
     * s = "AABABBA", k = 1
     *
     * Sample Output:
     * 4
     *
     * We can replace at most k characters.
     * We need the longest substring that can become all the same character.
     *
     * Example:
     * s = "AABABBA"
     * k = 1
     *
     * We can make "AABA" into "AAAA" by replacing B.
     *
     * Answer:
     * 4
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try every substring.
     *
     * For each substring:
     * - Find the most frequent character.
     * - Replace all other characters.
     *
     * If replacements needed <= k, it is valid.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Try each substring and ask which character we would keep unchanged.
     * The best choice is the most frequent character; every other character in
     * that substring would need replacement.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "AAB"
     * k = 1
     *
     * substring "AAB":
     * A appears 2 times
     * B appears 1 time
     *
     * To make all same:
     * replace B with A
     *
     * replacements needed = 1
     * valid length = 3
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Try every substring.
     * 2. Count character frequencies.
     * 3. Find max frequency.
     * 4. replacements needed = length - maxFrequency.
     * 5. If replacements <= k, update best.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(String s, int k) {
        int bestLength = 0;

        for (int start = 0; start < s.length(); start++) {
            int[] frequency = new int[26];
            int maxFrequency = 0;

            for (int end = start; end < s.length(); end++) {
                int index = s.charAt(end) - 'A';
                frequency[index]++;

                maxFrequency = Math.max(maxFrequency, frequency[index]);

                int length = end - start + 1;
                int replacementsNeeded = length - maxFrequency;

                if (replacementsNeeded <= k) {
                    bestLength = Math.max(bestLength, length);
                }
            }
        }

        return bestLength;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Use sliding window.
     *
     * For a window:
     * window length - count of most frequent character
     * tells how many replacements are needed.
     *
     * If replacements needed > k,
     * window is invalid, so shrink from left.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "AABABBA"
     * k = 1
     *
     * window "AABA":
     * length = 4
     * most frequent = A appears 3
     * replacements needed = 4 - 3 = 1
     *
     * valid because k = 1
     * best = 4
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Expand right pointer.
     * 2. Count characters in window.
     * 3. Track maxFrequency.
     * 4. If window length - maxFrequency > k, shrink left.
     * 5. Update best length.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * You can try each target character from A to Z and find the longest window
     * that can become only that character. Tracking the most frequent character
     * inside one window avoids repeating the scan 26 times.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(String s, int k) {
        int[] frequency = new int[26];

        int left = 0;
        int maxFrequency = 0;
        int bestLength = 0;

        for (int right = 0; right < s.length(); right++) {
            int rightIndex = s.charAt(right) - 'A';
            frequency[rightIndex]++;

            /*
             * maxFrequency tells us the count of the character
             * we would keep unchanged in this window.
             */
            maxFrequency = Math.max(maxFrequency, frequency[rightIndex]);

            int windowLength = right - left + 1;

            /*
             * If replacements needed is more than k,
             * the window is too large and must shrink.
             */
            while (windowLength - maxFrequency > k) {
                int leftIndex = s.charAt(left) - 'A';
                // Too many characters would need replacement, so shrink from the left.
                frequency[leftIndex]--;
                left++;
                windowLength = right - left + 1;
            }

            bestLength = Math.max(bestLength, windowLength);
        }

        return bestLength;
    }
}
