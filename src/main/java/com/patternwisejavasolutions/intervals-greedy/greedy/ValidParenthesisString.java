
package com.patternwisejavasolutions.intervalsGreedy.greedy;
public class ValidParenthesisString {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: s = "(*)"
     * Sample Output: true
     *
     * Input: s = "(*)"
     * Output: true
     * '*' can be '(', ')' or empty.
     * Check whether the string can become valid parentheses.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each '*', try all three choices.
     * Track how many open '(' are currently unmatched.
     * At the end, valid means open count is 0.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to try each meaning of '*': treat it as '(',
     * as ')', or as empty. If any full choice leaves no unmatched opens, the
     * string can be valid.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * s = "(*)"
     * Read '(' -> open = 1
     * Read '*' -> choose empty
     * Read ')' -> open = 0
     * Valid.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively process characters.
     * 2. '(' increases open.
     * 3. ')' decreases open.
     * 4. '*' branches into three choices.
     * 5. open can never be negative.
     * Time Complexity: O(3^stars)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * open is the number of unmatched '(' in the current branch.
     */
    public boolean bruteForce(String s) {
        return validFrom(s, 0, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Instead of exact open count, keep a range.
     * low = minimum possible opens.
     * high = maximum possible opens.
     * Brute force wastes work by tracking every branch separately. Many branches
     * only differ by the possible open-count range, so we keep that range.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * For "(*)":
     * '(' -> low=1 high=1
     * '*' -> low=0 high=2
     * ')' -> low=0 high=1
     * low is 0 at end, so valid.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. For '(' increase low and high.
     * 2. For ')' decrease both.
     * 3. For '*' low decreases and high increases.
     * 4. Keep low at least 0.
     * 5. If high becomes negative, invalid.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * low/high summarize all possible choices for '*' up to this point.
     */
    public boolean optimized(String s) {
        int low = 0;
        int high = 0;

        for (char ch : s.toCharArray()) {
            if (ch == '(') {
                low++;
                high++;
            } else if (ch == ')') {
                low--;
                high--;
            } else {
                low--;
                high++;
            }

            if (high < 0) {
                // Even the most generous choices cannot supply an opening '('.
                return false;
            }

            // Negative low just means some choices used more ')' than needed;
            // the minimum realistic open count is zero.
            low = Math.max(low, 0);
        }

        return low == 0;
    }

    private boolean validFrom(String s, int index, int open) {
        if (open < 0) {
            return false;
        }

        if (index == s.length()) {
            return open == 0;
        }

        char ch = s.charAt(index);

        if (ch == '(') {
            return validFrom(s, index + 1, open + 1);
        }

        if (ch == ')') {
            return validFrom(s, index + 1, open - 1);
        }

        return validFrom(s, index + 1, open + 1)
            || validFrom(s, index + 1, open - 1)
            || validFrom(s, index + 1, open);
    }
}
