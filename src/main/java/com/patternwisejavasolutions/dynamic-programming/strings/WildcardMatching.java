package com.patternwisejavasolutions.dynamicprogramming.strings;

public class WildcardMatching {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: s = "adceb", p = "*a*b"
     * Sample Output: true
     *
     * Pattern has '?' which matches one character and '*' which matches any
     * sequence, even empty.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Match the string and pattern from left to right.
     * A normal letter must match exactly, '?' is flexible for one character,
     * and '*' is flexible for many characters.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * When pattern has '*', try both meanings:
     * it matches nothing, or it consumes one string character.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Recursively compare indices i and j.
     * 2. For '*', try j + 1 or i + 1.
     * 3. For '?' or equal letters, move both.
     * Time Complexity: O(2^(m+n))
     * Space Complexity: O(m+n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "ab", p = "*b"
     * '*' first tries empty, then 'a'.
     * After consuming 'a', 'b' matches 'b'.
     */
    public boolean bruteForce(String s, String p) {
        return matchSlow(s, p, 0, 0);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * dp[i][j] tells whether the first i string characters match the first j
     * pattern characters. It remembers repeated (i, j) states caused by '*'
     * branching into empty vs. one-or-more characters.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. dp[0][0] is true.
     * 2. Leading '*' can match an empty string.
     * 3. Fill the table using character rules.
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "ab", p = "*b"
     * dp handles '*' as empty or extending a previous match.
     * Last cell becomes true.
     */
    public boolean optimized(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        // Empty pattern matches empty string.
        dp[0][0] = true;

        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                // A leading '*' can keep matching empty if the previous prefix did.
                dp[0][j] = dp[0][j - 1];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char patternChar = p.charAt(j - 1);
                if (patternChar == '*') {
                    // '*' as empty: dp[i][j - 1]; '*' consuming one char: dp[i - 1][j].
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else if (patternChar == '?' || patternChar == s.charAt(i - 1)) {
                    // One pattern character consumes one string character.
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }

        return dp[m][n];
    }

    private boolean matchSlow(String s, String p, int i, int j) {
        if (j == p.length()) {
            // Pattern is finished; it matches only if the string is also finished.
            return i == s.length();
        }

        if (p.charAt(j) == '*') {
            // '*' can match empty, or consume one character and stay on '*'.
            boolean matchEmpty = matchSlow(s, p, i, j + 1);
            boolean matchOneOrMore = i < s.length() && matchSlow(s, p, i + 1, j);
            return matchEmpty || matchOneOrMore;
        }

        boolean firstMatches = i < s.length() && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i));
        // Normal character or '?' consumes exactly one character.
        return firstMatches && matchSlow(s, p, i + 1, j + 1);
    }
}
