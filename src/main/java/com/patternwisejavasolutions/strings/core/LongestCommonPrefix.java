
package com.patternwisejavasolutions.strings.core;
public class LongestCommonPrefix {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Find the longest starting text shared by every string.
 *
 * Sample Input:
 * strs = ["flower", "flow", "flight"]
 *
 * Sample Output:
 * "fl"
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In ["flower", "flow", "flight"], all words begin with "fl", but the next
 * character disagrees: "flower" and "flow" have 'o', while "flight" has 'i'.
 * That first disagreement stops the answer.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * A prefix starts at the beginning. Line the words up under each other and
 * read from left to right. The shared prefix stops at the first column where at
 * least one word disagrees.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The most direct idea is to guess a prefix from the first word and ask every
 * other word, "Do you start with this?" Start with the longest guess, then
 * keep shortening until it works.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Use the first word as the source of possible prefixes.
 * 2. Try prefixes from full length down to empty.
 * 3. Return the first prefix that every word starts with.
 *
 * Time Complexity: O(n * m^2)
 * Space Complexity: O(m)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * first word = "flower"
 * Try "flower": "flow" does not start with it.
 * Try "flow": "flight" does not start with it.
 * Try "fl": all words start with it, so return "fl".
 */
    public String bruteForce(String[] strs) {
        if (strs.length == 0) {
            return "";
        }

        String first = strs[0];
        for (int length = first.length(); length >= 0; length--) {
            String prefixGuess = first.substring(0, length);
            boolean works = true;

            for (String word : strs) {
                if (!word.startsWith(prefixGuess)) {
                    works = false;
                    break;
                }
            }

            if (works) {
                return prefixGuess;
            }
        }

        return "";
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is rebuilding and retesting many separate prefix
 * guesses. We can keep one current prefix and shrink it only when a word
 * rejects it. Once a prefix works for a word, no longer prefix can work.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Start prefix as strs[0].
 * 2. For each next word, shorten prefix until the word starts with it.
 * 3. Return prefix.
 *
 * Time Complexity: O(n * m)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * prefix starts as "flower".
 * Against "flow", shrink to "flow".
 * Against "flight", "flow" fails, "flo" fails, "fl" works.
 */
    public String optimized(String[] strs) {
        if (strs.length == 0) {
            return "";
        }

        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (!strs[i].startsWith(prefix)) {
                // Only remove from the end because a prefix must keep the same start.
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }

        return prefix;
    }

}
