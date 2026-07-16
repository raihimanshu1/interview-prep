package com.patternwisejavasolutions.slidingwindowtwopointers.kdistinctcountpatterns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LongestSubstringWithAtMostKDistinctCharacters {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We are given a string s and a number k.
     * We need the length of the longest substring that uses at most k different characters.
     *
     * Sample Input:
     * s = "eceba", k = 2
     *
     * Sample Output:
     * 3
     *
     * Why?
     * "ece" has only 2 distinct characters: e and c.
     * Its length is 3.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A substring means characters must stay continuous.
     *
     * If we are learning this for the first time, the most natural idea is:
     * - Try every possible substring.
     * - Count how many different characters it has.
     * - If it has at most k distinct characters, it is allowed.
     * - Keep the longest allowed one.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Choose a start and grow the substring while collecting its different
     * characters in a set. If a new character makes the set bigger than k, that
     * start cannot produce a longer valid substring.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "eceba", k = 2
     *
     * Try substrings starting at index 0:
     * "e"    -> distinct = {e}, allowed, best = 1
     * "ec"   -> distinct = {e, c}, allowed, best = 2
     * "ece"  -> distinct = {e, c}, allowed, best = 3
     * "eceb" -> distinct = {e, c, b}, not allowed
     *
     * Later substrings are checked the same way.
     * Final answer is 3.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick every start index.
     * 2. Expand the end index one character at a time.
     * 3. Put characters in a set so we know how many distinct characters exist.
     * 4. If distinct count is at most k, update answer.
     * 5. If distinct count becomes more than k, stop this start because adding more
     *    characters cannot reduce the distinct count.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(k) or O(character set)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(String s, int k) {
        int bestLength = 0;

        for (int start = 0; start < s.length(); start++) {
            Set<Character> distinctCharacters = new HashSet<>();

            for (int end = start; end < s.length(); end++) {
                distinctCharacters.add(s.charAt(end));

                if (distinctCharacters.size() > k) {
                    break;
                }

                int currentLength = end - start + 1;
                bestLength = Math.max(bestLength, currentLength);
            }
        }

        return bestLength;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Brute force repeats work.
     *
     * Example:
     * If window "ece" is valid, then when we move one step to add 'b',
     * we do not need to recount "ceb" from zero.
     *
     * We can keep a sliding window:
     * - right expands the window by adding a character.
     * - left shrinks the window when we have more than k distinct characters.
     *
     * To know distinct count correctly, we store frequency of each character.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "eceba", k = 2
     *
     * right = 0 -> window "e", valid, best = 1
     * right = 1 -> window "ec", valid, best = 2
     * right = 2 -> window "ece", valid, best = 3
     * right = 3 -> window "eceb", distinct are e,c,b, invalid
     *
     * Shrink from left:
     * remove 'e' once -> window still has e, c, b
     * remove 'c'      -> window has e, b, valid again
     *
     * Continue the same way.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep left pointer at the start of the current window.
     * 2. Move right pointer through the string.
     * 3. Add s[right] to a frequency map.
     * 4. While map has more than k keys, remove s[left] and move left.
     * 5. After the window is valid, update the best length.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(k) or O(character set)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A last-seen index map can remove the character with the smallest last
     * index when the window has too many distinct characters. The frequency map
     * version is easier to follow for a first pass.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(String s, int k) {
        Map<Character, Integer> frequency = new HashMap<>();
        int left = 0;
        int bestLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char rightCharacter = s.charAt(right);

            frequency.put(
                rightCharacter,
                frequency.getOrDefault(rightCharacter, 0) + 1
            );

            while (frequency.size() > k) {
                char leftCharacter = s.charAt(left);
                // Too many distinct characters, so shrink until one type fully leaves.
                frequency.put(leftCharacter, frequency.get(leftCharacter) - 1);

                if (frequency.get(leftCharacter) == 0) {
                    frequency.remove(leftCharacter);
                }

                left++;
            }

            int currentLength = right - left + 1;
            bestLength = Math.max(bestLength, currentLength);
        }

        return bestLength;
    }
}
