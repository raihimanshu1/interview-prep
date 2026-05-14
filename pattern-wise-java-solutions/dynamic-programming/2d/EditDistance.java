public class EditDistance {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: word1 = "horse", word2 = "ros"
     * Sample Output: 3
     *
     * Input: word1 = "horse", word2 = "ros"
     * Output: 3 minimum edits using insert, delete, or replace.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Compare both words from left to right.
     * If characters match, move forward.
     * If not, try all three operations and take the cheapest.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Compare the current letters. If they differ, try the three allowed edits:
     * insert the needed letter, delete the extra letter, or replace one letter.
     * Brute force follows all three stories and picks the shortest.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * horse vs ros
     * h and r do not match.
     * Try replace h->r, delete h, or insert r.
     * Each operation costs 1 plus the smaller remaining problem.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively compare i and j.
     * 2. If one string ends, remaining chars in other must be inserted/deleted.
     * 3. If chars match, move both.
     * 4. Else try insert/delete/replace.
     * Time Complexity: exponential
     * Space Complexity: O(m+n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This directly explores insert/delete/replace choices.
     */
    public int bruteForce(String word1, String word2) {
        return edit(word1, word2, 0, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The same i,j states repeat.
     * Let dp[i][j] mean minimum edits to convert word1 suffix i into word2 suffix j.
     * Every repeated suffix pair is solved once in the table.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * If chars match, dp[i][j] = dp[i+1][j+1].
     * If not, 1 + min(insert, delete, replace).
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Create dp with extra row/col.
     * 2. Base cases handle empty suffixes.
     * 3. Fill from bottom-right.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[i][j] stores the minimum edits for two suffixes.
     */
    public int optimized(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = m; i >= 0; i--) {
            // word2 is empty, so delete the remaining word1 characters.
            dp[i][n] = m - i;
        }

        for (int j = n; j >= 0; j--) {
            // word1 is empty, so insert the remaining word2 characters.
            dp[m][j] = n - j;
        }

        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (word1.charAt(i) == word2.charAt(j)) {
                    // Matching letters need no edit.
                    dp[i][j] = dp[i + 1][j + 1];
                } else {
                    int insert = dp[i][j + 1];
                    int delete = dp[i + 1][j];
                    int replace = dp[i + 1][j + 1];
                    // Pay one edit plus the cheapest remaining suffix state.
                    dp[i][j] = 1 + Math.min(insert, Math.min(delete, replace));
                }
            }
        }

        return dp[0][0];
    }


    private int edit(String a, String b, int i, int j) {
        if (i == a.length()) {
            // Need to insert every remaining character of b.
            return b.length() - j;
        }

        if (j == b.length()) {
            // Need to delete every remaining character of a.
            return a.length() - i;
        }

        if (a.charAt(i) == b.charAt(j)) {
            // No edit needed for this matching pair.
            return edit(a, b, i + 1, j + 1);
        }

        // Try the three legal edits and pay 1 for the chosen operation.
        int insert = edit(a, b, i, j + 1);
        int delete = edit(a, b, i + 1, j);
        int replace = edit(a, b, i + 1, j + 1);
        return 1 + Math.min(insert, Math.min(delete, replace));
    }
}
