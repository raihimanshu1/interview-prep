package com.patternwisejavasolutions.dynamicprogramming.strings;

public class LongestCommonSubsequence {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: text1 = "abcde", text2 = "ace"
     * Sample Output: 3
     *
     * Input: text1 = "abcde", text2 = "ace"
     * Output: 3 because "ace" is common in both strings in the same order.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Compare characters from both strings.
     * If they match, we can use that character.
     * If they do not match, skip one character from either string and take the better result.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Compare the two current characters. If they match, use that character in
     * the answer. If not, branch: skip one character from text1 or skip one
     * from text2, then keep the better branch.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * abcde and ace
     * 'a' matches 'a', so count 1 and move both.
     * 'b' and 'c' do not match, try skipping b.
     * Later c and e match. Total 3.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively compare i and j.
     * 2. If characters match, return 1 + solve(i+1,j+1).
     * 3. Otherwise return max(skip from first, skip from second).
     * Time Complexity: O(2^(m+n))
     * Space Complexity: O(m+n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This follows the match/skip recursion directly.
     */
    public int bruteForce(String text1, String text2) {
        return solve(text1, text2, 0, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The same i,j pair repeats.
     * Let dp[i][j] mean LCS length between suffix text1[i..] and text2[j..].
     * Once that pair is solved, every branch can reuse it.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * If text1[i] == text2[j], dp[i][j] = 1 + dp[i+1][j+1].
     * Otherwise we cannot use both characters together, so take max of skipping one.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Create dp with extra row and column for empty suffix.
     * 2. Fill from bottom-right to top-left.
     * 3. Use match or max rule.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[i][j] stores the answer for two suffixes.
     */
    public int optimized(String text1, String text2) {
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];

        for (int i = text1.length() - 1; i >= 0; i--) {
            for (int j = text2.length() - 1; j >= 0; j--) {
                if (text1.charAt(i) == text2.charAt(j)) {
                    // Matching characters can be taken together.
                    dp[i][j] = 1 + dp[i + 1][j + 1];
                } else {
                    // Otherwise skip one side and keep the longer result.
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j + 1]);
                }
            }
        }

        return dp[0][0];
    }


    private int solve(String a, String b, int i, int j) {
        if (i == a.length() || j == b.length()) {
            // One suffix is empty, so no more common characters can be added.
            return 0;
        }

        if (a.charAt(i) == b.charAt(j)) {
            // Use the matching character and move both pointers.
            return 1 + solve(a, b, i + 1, j + 1);
        }

        // Try skipping from either string.
        return Math.max(solve(a, b, i + 1, j), solve(a, b, i, j + 1));
    }
}
