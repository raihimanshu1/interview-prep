public class RepeatedSubstringPattern {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Check whether a string can be built by repeating one smaller substring.
 *
 * Sample Input:
 * s = "abab"
 *
 * Sample Output:
 * true
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * The sample "abab" is true because "ab" appears twice with no leftover
 * characters. A learner should first notice both parts: the repeated block must
 * be smaller than the full string, and it must cover the whole string exactly.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * If a smaller block repeats, the string has a rhythm: "ab" + "ab". The block
 * length must divide the full length.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first human idea is to try a possible repeating block, then see whether
 * copying that block again and again rebuilds the original string.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Try lengths from 1 to n / 2.
 * 2. Skip lengths that do not divide n.
 * 3. Check whether every character matches the repeating block.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * Try block "a" no. Try block "ab" yes, because positions repeat a,b,a,b.
 */
    public boolean bruteForce(String s) {
        int n = s.length();

        for (int length = 1; length <= n / 2; length++) {
            if (n % length != 0) {
                continue;
            }

            boolean works = true;
            for (int i = length; i < n; i++) {
                if (s.charAt(i) != s.charAt(i % length)) {
                    works = false;
                    break;
                }
            }

            if (works) {
                return true;
            }
        }

        return false;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is rechecking the same characters for every
 * possible block length. KMP's prefix table helps because a repeated string has
 * a border: a beginning part that also appears at the end.
 *
 * KMP tells us the longest prefix of the string that is also a suffix.
 * If the string is made by repeating a smaller block, then the repeated block
 * size is n - longestPrefixSuffix.
 *
 * Example:
 * s = "abab"
 * longest prefix that is also suffix = "ab", length 2
 * block size = 4 - 2 = 2
 * 4 is divisible by 2, so "ab" repeats.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Build the KMP LPS array.
 * 2. Read longestPrefixSuffix from lps[n - 1].
 * 3. If longestPrefixSuffix is 0, no repeating structure exists.
 * 4. Let blockLength = n - longestPrefixSuffix.
 * 5. If n % blockLength == 0, the block repeats evenly.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * s = "abab"
 * LPS array becomes [0, 0, 1, 2].
 * longestPrefixSuffix = 2.
 * blockLength = 4 - 2 = 2.
 * Since 4 % 2 == 0, the first 2 characters "ab" repeat to build the string.
 */
    public boolean optimized(String s) {
        int n = s.length();
        int[] lps = buildLps(s);
        int longestPrefixSuffix = lps[n - 1];

        if (longestPrefixSuffix == 0) {
            return false;
        }

        int blockLength = n - longestPrefixSuffix;
        return n % blockLength == 0;
    }

    private int[] buildLps(String s) {
        int[] lps = new int[s.length()];
        int prefixLength = 0;
        int index = 1;

        while (index < s.length()) {
            if (s.charAt(index) == s.charAt(prefixLength)) {
                /*
                 * The current character extends the prefix-suffix match.
                 * Store the new matched length for this index.
                 */
                prefixLength++;
                lps[index] = prefixLength;
                index++;
            } else if (prefixLength > 0) {
                /*
                 * We failed after matching some prefix.
                 * KMP does not restart from zero immediately; it falls back to
                 * the best smaller prefix that may still work.
                 */
                prefixLength = lps[prefixLength - 1];
            } else {
                /*
                 * No prefix match exists for this index.
                 */
                lps[index] = 0;
                index++;
            }
        }

        return lps;
    }
}
