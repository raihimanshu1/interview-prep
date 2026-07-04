
package com.patternwisejavasolutions.stack.core;
import java.util.Stack;

public class RemoveAllAdjacentDuplicatesInString {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Repeatedly remove neighboring equal letters until no such pair remains.
     *
     * Sample Input: s = "abbaca"
     * Sample Output: "ca"
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * When a letter meets the same letter just before it, both disappear. The
     * important beginner observation is that removing one pair can make two
     * older letters become neighbors, so the string must be checked again.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Keep scanning the string for equal neighbors and delete the first pair
     * found. Then restart from the beginning because that deletion may have
     * created a brand-new adjacent pair.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Convert string to a builder.
     * 2. Scan for two equal adjacent characters.
     * 3. Delete the pair and restart scanning.
     * 4. Stop when a full scan deletes nothing.
     *
     * BRUTE FORCE DRY RUN
     *
     * "abbaca" -> remove "bb" -> "aaca" -> remove "aa" -> "ca"
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */
    public String bruteForce(String s) {
        StringBuilder builder = new StringBuilder(s);
        boolean changed = true;

        while (changed) {
            changed = false;

            for (int i = 0; i < builder.length() - 1; i++) {
                if (builder.charAt(i) == builder.charAt(i + 1)) {
                    builder.delete(i, i + 2);
                    changed = true;
                    break;
                }
            }
        }

        return builder.toString();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Brute force restarts after each deletion because deleting a pair can
     * create a new pair. A stack keeps the cleaned prefix as we scan once.
     *
     * The stack top is the only character that can become adjacent to the
     * current character. If they match, pop the top and do not add current;
     * that represents both equal neighbors vanishing.
     *
     * OPTIMIZED ALGORITHM
     * 1. Scan characters left to right.
     * 2. If stack top equals current, pop it.
     * 3. Otherwise push current.
     * 4. Convert stack to answer.
     *
     * OPTIMIZED DRY RUN
     *
     * "abbaca"
     * push a -> [a]
     * push b -> [a,b]
     * next b matches top b, so pop b and skip current b -> [a]
     * next a now meets top a, so pop a too -> []
     * push c, push a -> [c,a], answer "ca"
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public String optimized(String s) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char current = s.charAt(i);

            if (!stack.isEmpty() && stack.peek() == current) {
                // Current and the previous kept character form a duplicate pair, so both vanish.
                stack.pop();
            } else {
                // No pair formed yet; keep current as part of the cleaned prefix.
                stack.push(current);
            }
        }

        StringBuilder answer = new StringBuilder();
        for (char ch : stack) {
            answer.append(ch);
        }

        return answer.toString();
    }
}
