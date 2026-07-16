package com.companywisejavasolutions.wellsfargo.solutions;

public class RegularExpressionMatching {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: s = "aab", p = "c*a*b"
     * Sample Output: true
     *
     * '.' matches any one character. '*' means zero or more of the previous
     * pattern character.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Read '*' as "repeat the previous thing as many times as needed, including
     * zero times."
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Compare string and pattern.
     * When the next pattern character is '*', try skipping that pair or using it
     * once if it matches.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Recursively solve string index i and pattern index j.
     * 2. If j + 1 is '*', try zero copies or one more copy.
     * 3. Otherwise consume one matching character.
     * Time Complexity: O(2^(m+n))
     * Space Complexity: O(m+n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "aab", p = "c*a*b"
     * c* is skipped.
     * a* consumes two a characters.
     * b matches b.
     */
    public boolean bruteForce(String s, String p) {
        return matchSlow(s, p, 0, 0);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Store answers for suffixes.
     * dp[i][j] means s from i onward matches p from j onward. The repeated
     * suffix states come from '*' trying "zero copies" and "one more copy".
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start from the end of both strings.
     * 2. Check whether current characters match.
     * 3. If next pattern char is '*', use skip or consume logic.
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * For "c*a*b", dp learns c* can become empty.
     * Then a* can consume both a characters before b is matched.
     */
    public boolean optimized(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        // Empty string suffix matches empty pattern suffix.
        dp[m][n] = true;

        for (int i = m; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                boolean firstMatches = i < m && (p.charAt(j) == '.' || p.charAt(j) == s.charAt(i));

                if (j + 1 < n && p.charAt(j + 1) == '*') {
                    // Skip x*, or consume one matching char and stay on the same pattern.
                    dp[i][j] = dp[i][j + 2] || (firstMatches && dp[i + 1][j]);
                } else {
                    // Without '*', one pattern item must consume one string character.
                    dp[i][j] = firstMatches && dp[i + 1][j + 1];
                }
            }
        }

        return dp[0][0];
    }

    private boolean matchSlow(String s, String p, int i, int j) {
        if (j == p.length()) {
            // Pattern ended; success only if the string ended too.
            return i == s.length();
        }

        boolean firstMatches = i < s.length() && (p.charAt(j) == '.' || p.charAt(j) == s.charAt(i));
        if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
            // Either use zero copies of p[j], or consume one match and try more.
            return matchSlow(s, p, i, j + 2) || (firstMatches && matchSlow(s, p, i + 1, j));
        }

        // Normal character or '.' consumes one character.
        return firstMatches && matchSlow(s, p, i + 1, j + 1);
    }
}
