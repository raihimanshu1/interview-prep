
package com.patternwisejavasolutions.slidingWindowTwoPointers.variableWindowCore;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LongestSubstringWithoutRepeatingCharacters {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input:
     * s = "abcabcbb"
     *
     * Sample Output:
     * 3
     *
     * Find the length of the longest substring without repeating characters.
     *
     * Substring means continuous part of the string.
     *
     * Example:
     * s = "abcabcbb"
     *
     * Longest substring without repeating:
     * "abc"
     *
     * Answer:
     * 3
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try every starting index.
     * From that start, keep adding characters until a duplicate appears.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Pick a starting character and grow the substring one character at a time.
     * The moment a character repeats, that start cannot grow into a valid
     * longer substring, so move to the next start.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "abcabcbb"
     *
     * start = 0:
     * a -> ok
     * b -> ok
     * c -> ok
     * a -> duplicate, stop
     *
     * length = 3
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick each start index.
     * 2. Use a set to store characters in current substring.
     * 3. Move end forward until duplicate appears.
     * 4. Track maximum length.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(String s) {
        int bestLength = 0;

        for (int start = 0; start < s.length(); start++) {
            Set<Character> seen = new HashSet<>();

            for (int end = start; end < s.length(); end++) {
                char current = s.charAt(end);

                if (seen.contains(current)) {
                    break;
                }

                seen.add(current);
                bestLength = Math.max(bestLength, end - start + 1);
            }
        }

        return bestLength;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Instead of restarting from every index,
     * keep a sliding window with no duplicates.
     *
     * When we see a repeated character,
     * move left pointer after the previous occurrence.
     *
     * To know where the previous occurrence was,
     * store last seen index of each character.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "abcabcbb"
     *
     * right = 0, char = a
     * window = "a", best = 1
     *
     * right = 1, char = b
     * window = "ab", best = 2
     *
     * right = 2, char = c
     * window = "abc", best = 3
     *
     * right = 3, char = a
     * a was seen at index 0
     * move left to 1
     * window becomes "bca"
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Use left and right pointers.
     * 2. Store last seen index for each character.
     * 3. If character was seen inside current window, move left.
     * 4. Update character's latest index.
     * 5. Update best length.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A last-seen index map can jump the left pointer directly after a repeated
     * character. A set-based window moves left one step at a time, which is a
     * little more beginner-friendly.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(String s) {
        Map<Character, Integer> lastSeenIndex = new HashMap<>();

        int left = 0;
        int bestLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            /*
             * If current appeared before, left may need to move.
             * Math.max protects us from moving left backward.
             */
            if (lastSeenIndex.containsKey(current)) {
                // Jump past the duplicate only if that duplicate is inside this window.
                left = Math.max(left, lastSeenIndex.get(current) + 1);
            }

            /*
             * Store the latest position of current character.
             */
            lastSeenIndex.put(current, right);

            int windowLength = right - left + 1;
            bestLength = Math.max(bestLength, windowLength);
        }

        return bestLength;
    }
}
