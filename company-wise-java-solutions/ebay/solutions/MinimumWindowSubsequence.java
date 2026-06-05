public class MinimumWindowSubsequence {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Find the shortest substring of s1 that contains s2 as a subsequence.
     * Characters of s2 must appear in order, but do not need to be adjacent.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Walk forward until all characters of s2 are matched. Then walk backward
     * from that end to shrink the window as much as possible.
     */

    /*
     * Time Complexity: O(n * m) in the worst case
     * Space Complexity: O(1)
     */
    public String minWindow(String s1, String s2) {
        int bestStart = -1;
        int bestLength = Integer.MAX_VALUE;
        int i = 0;

        while (i < s1.length()) {
            int j = 0;

            while (i < s1.length()) {
                if (s1.charAt(i) == s2.charAt(j)) {
                    j++;
                    if (j == s2.length()) {
                        break;
                    }
                }
                i++;
            }

            if (i == s1.length()) {
                break;
            }

            int end = i;
            j = s2.length() - 1;

            while (i >= 0) {
                if (s1.charAt(i) == s2.charAt(j)) {
                    j--;
                    if (j < 0) {
                        break;
                    }
                }
                i--;
            }

            int start = i;
            int length = end - start + 1;
            if (length < bestLength) {
                bestLength = length;
                bestStart = start;
            }

            i = start + 1;
        }

        return bestStart == -1 ? "" : s1.substring(bestStart, bestStart + bestLength);
    }
}
