public class ImplementStrStr {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Return the first index where needle appears inside haystack, or -1.
 *
 * Sample Input:
 * haystack = "sadbutsad", needle = "sad"
 *
 * Sample Output:
 * 0
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In "sadbutsad", the needle "sad" already matches the first three characters,
 * so the answer is 0. The sample is asking for the first full occurrence, not
 * every occurrence and not a partial match like just "sa".
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * We are looking for a small word inside a bigger word. If the needle has
 * length 3, then every possible answer is a window of 3 letters inside the
 * haystack.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * Try placing the needle at each possible starting index. For each placement,
 * compare letters one by one until either all letters match or one letter
 * proves that this start cannot be the answer.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. If needle is empty, return 0.
 * 2. Try every start that has enough room.
 * 3. Compare needle characters with haystack characters.
 * 4. Return start when all match.
 *
 * Time Complexity: O(nm)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * haystack = "sadbutsad", needle = "sad"
 * start = 0: compare s with s, a with a, d with d.
 * All needle letters matched, so return 0.
 */
    public int bruteForce(String haystack, String needle) {
        if (needle.isEmpty()) {
            return 0;
        }

        for (int start = 0; start <= haystack.length() - needle.length(); start++) {
            int matched = 0;
            while (matched < needle.length()
                    && haystack.charAt(start + matched) == needle.charAt(matched)) {
                matched++;
            }
            if (matched == needle.length()) {
                return start;
            }
        }

        return -1;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is rechecking characters after a nearly successful
 * attempt fails. KMP stores how much of the needle can still be trusted using
 * an LPS array, so the haystack pointer does not need to move backward.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Build LPS for needle.
 * 2. Scan haystack with pointer i and needle with pointer j.
 * 3. On match, move both.
 * 4. On mismatch, use LPS to move j without moving i back.
 * 5. Return i - j when j reaches needle length.
 *
 * Time Complexity: O(n + m)
 * Space Complexity: O(m)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * needle = "ababaca"
 * If we matched "ababa" and then fail, LPS tells us that "aba" is still useful.
 * So j falls back to that length instead of starting needle from index 0.
 */
    public int optimized(String haystack, String needle) {
        if (needle.isEmpty()) {
            return 0;
        }

        int[] lps = buildLps(needle);
        int j = 0;

        for (int i = 0; i < haystack.length(); i++) {
            while (j > 0 && haystack.charAt(i) != needle.charAt(j)) {
                // Keep i in place; only move j to the next reusable prefix length.
                j = lps[j - 1];
            }

            if (haystack.charAt(i) == needle.charAt(j)) {
                j++;
                if (j == needle.length()) {
                    // i is at the last matched character, so subtract needle length.
                    return i - j + 1;
                }
            }
        }

        return -1;
    }

    private int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;

        for (int i = 1; i < pattern.length(); i++) {
            while (length > 0 && pattern.charAt(i) != pattern.charAt(length)) {
                // Fall back to the best smaller prefix that is also a suffix.
                length = lps[length - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(length)) {
                // This character extends the current prefix-suffix match.
                length++;
                lps[i] = length;
            }
        }

        return lps;
    }
}
