
package com.patternwisejavasolutions.slidingWindowTwoPointers.variableWindowCore;
import java.util.Arrays;

public class PermutationInString {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input:
     * s1 = "ab", s2 = "eidbaooo"
     *
     * Sample Output:
     * true
     *
     * We need to check if s2 contains any permutation of s1.
     *
     * Permutation means same characters, different order allowed.
     *
     * Example:
     * s1 = "ab"
     * s2 = "eidbaooo"
     *
     * s2 contains "ba".
     * "ba" is a permutation of "ab".
     *
     * Answer:
     * true
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If s1 length is 2,
     * then we only need to check substrings of s2 with length 2.
     *
     * For every such substring:
     * - Sort it.
     * - Sort s1.
     * - If both are equal, it is a permutation.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * A permutation of s1 must have the same length as s1. So the direct idea
     * is to take every same-length piece of s2, sort it, and compare it with
     * sorted s1.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * s1 = "ab"
     * s2 = "eidbaooo"
     *
     * Check substrings of length 2:
     * "ei" -> sorted "ei", not "ab"
     * "id" -> not
     * "db" -> not
     * "ba" -> sorted "ab", matches
     *
     * Return true.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Sort s1.
     * 2. Take every substring of s2 with same length as s1.
     * 3. Sort that substring.
     * 4. Compare.
     *
     * Time Complexity: O(n * m log m)
     * Space Complexity: O(m)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public boolean bruteForce(String s1, String s2) {
        char[] sortedS1 = s1.toCharArray();
        Arrays.sort(sortedS1);

        int windowSize = s1.length();

        for (int start = 0; start + windowSize <= s2.length(); start++) {
            String window = s2.substring(start, start + windowSize);

            char[] sortedWindow = window.toCharArray();
            Arrays.sort(sortedWindow);

            if (Arrays.equals(sortedS1, sortedWindow)) {
                return true;
            }
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Sorting every window is extra work.
     *
     * A permutation only needs the same character counts.
     *
     * Example:
     * "ab" and "ba"
     *
     * Both have:
     * a -> 1
     * b -> 1
     *
     * So we compare frequency arrays instead of sorted strings.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s1 = "ab"
     * need:
     * a = 1, b = 1
     *
     * s2 window "ba":
     * b = 1, a = 1
     *
     * frequency arrays match.
     * Return true.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Count characters in s1.
     * 2. Slide a window of size s1.length() over s2.
     * 3. Add right character.
     * 4. Remove left character when window is too large.
     * 5. If frequency arrays match, return true.
     *
     * Time Complexity: O(26 * n), treated as O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Instead of comparing whole frequency arrays every time, keep a match count
     * for how many character frequencies currently agree. That keeps the same
     * fixed-window idea but reduces repeated comparison work.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public boolean optimized(String s1, String s2) {
        if (s1.length() > s2.length()) {
            return false;
        }

        int[] need = new int[26];
        int[] window = new int[26];

        for (char ch : s1.toCharArray()) {
            need[ch - 'a']++;
        }

        for (int right = 0; right < s2.length(); right++) {
            /*
             * Add the new character entering the window.
             */
            window[s2.charAt(right) - 'a']++;

            /*
             * If window became larger than s1,
             * remove the character that is leaving from the left.
             */
            if (right >= s1.length()) {
                char leftChar = s2.charAt(right - s1.length());
                // Remove the character just outside the fixed-size window.
                window[leftChar - 'a']--;
            }

            if (Arrays.equals(need, window)) {
                return true;
            }
        }

        return false;
    }
}
