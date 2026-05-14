public class DecodeWays {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: s = "226"
     * Sample Output: 3
     *
     * Input: s = "226"
     * Output: 3 because it can be decoded as "2 2 6", "22 6", and "2 26".
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * At each index, we can decode one digit if it is not 0.
     * We can also decode two digits if the number is between 10 and 26.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * At an index, try taking one digit as a letter, and also try taking two
     * digits if they form 10 through 26. Each branch is one possible split of
     * the number string into letter codes.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * s = "226"
     * At index 0:
     * Take "2", solve "26".
     * Take "22", solve "6".
     * Both are valid branches.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively solve from index.
     * 2. If current char is 0, no decoding.
     * 3. Try one digit.
     * 4. Try two digits when valid.
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This recursively counts all valid one-digit/two-digit splits.
     */
    public int bruteForce(String s) {
        return decodeFrom(s, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Let dp[i] mean number of ways to decode suffix starting at i.
     * Build from the end so smaller suffix answers are ready. This removes
     * repeated calls like decoding the suffix starting at index 2 many times.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * s = "226"
     * dp[3]=1 empty suffix
     * At index 2 '6': dp[2]=1
     * At index 1 '2': one digit gives dp[2], two digits "26" gives dp[3], total 2
     * At index 0 total 3
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. dp[n] = 1.
     * 2. Move from right to left.
     * 3. If s[i] is 0, dp[i] = 0.
     * 4. Else add dp[i+1].
     * 5. If two-digit number is 10..26, add dp[i+2].
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[index] stores the answer for s[index..].
     */
    public int optimized(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        // Empty suffix has one valid decoding: choose nothing more.
        dp[n] = 1;

        for (int index = n - 1; index >= 0; index--) {
            if (s.charAt(index) == '0') {
                // A code cannot start with 0.
                dp[index] = 0;
                continue;
            }

            // Take one digit and then decode the suffix after it.
            dp[index] = dp[index + 1];

            if (index + 1 < n) {
                int twoDigits = Integer.parseInt(s.substring(index, index + 2));

                if (twoDigits >= 10 && twoDigits <= 26) {
                    // Take two digits as one letter and decode the suffix after them.
                    dp[index] += dp[index + 2];
                }
            }
        }

        return dp[0];
    }


    private int decodeFrom(String s, int index) {
        if (index == s.length()) {
            // A complete valid split was formed.
            return 1;
        }

        if (s.charAt(index) == '0') {
            // No letter maps to a standalone leading zero.
            return 0;
        }

        // Choice 1: decode one digit.
        int ways = decodeFrom(s, index + 1);

        if (index + 1 < s.length()) {
            int twoDigits = Integer.parseInt(s.substring(index, index + 2));

            if (twoDigits >= 10 && twoDigits <= 26) {
                // Choice 2: decode two digits together.
                ways += decodeFrom(s, index + 2);
            }
        }

        return ways;
    }
}
