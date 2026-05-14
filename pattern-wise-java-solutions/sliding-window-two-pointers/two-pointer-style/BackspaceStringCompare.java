public class BackspaceStringCompare {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * # means backspace.
     * Check whether two strings become equal after typing them.
     *
     * Sample Input:
     * s = "ab#c"
     * t = "ad#c"
     *
     * Sample Output:
     * true
     *
     * Why?
     * "ab#c" becomes "ac"
     * "ad#c" becomes "ac"
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Simulate typing.
     * Normal character means add it.
     * # means remove the last typed character if one exists.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Type each string into a temporary builder exactly like a keyboard would.
     * A normal character is appended, and a # removes the most recent visible
     * character if there is one.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "ab#c"
     *
     * type 'a' -> "a"
     * type 'b' -> "ab"
     * type '#' -> "a"
     * type 'c' -> "ac"
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build final version of s.
     * 2. Build final version of t.
     * 3. Compare both final strings.
     *
     * Time Complexity: O(n + m)
     * Space Complexity: O(n + m)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public boolean bruteForce(String s, String t) {
        return buildTypedString(s).equals(buildTypedString(t));
    }

    private String buildTypedString(String text) {
        StringBuilder builder = new StringBuilder();

        for (char ch : text.toCharArray()) {
            if (ch == '#') {
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
            } else {
                builder.append(ch);
            }
        }

        return builder.toString();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Backspace affects characters before it.
     * So instead of building strings, read from right to left.
     *
     * When we see #, we increase skip count.
     * When we see a normal character and skip > 0, that character is deleted.
     * The next real visible character is what we compare.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "ab#c"
     *
     * From right:
     * 'c' is visible.
     * '#' means skip one previous character.
     * 'b' is skipped.
     * 'a' is visible.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start from the end of both strings.
     * 2. Find next visible character in each string.
     * 3. If visible characters differ, return false.
     * 4. Continue until both strings are finished.
     *
     * Time Complexity: O(n + m)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Building the final strings with stacks is often the easiest solution to
     * write first. The two-pointer version compares from the end and avoids
     * storing the rebuilt strings.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public boolean optimized(String s, String t) {
        int sIndex = s.length() - 1;
        int tIndex = t.length() - 1;

        while (sIndex >= 0 || tIndex >= 0) {
            sIndex = nextVisibleIndex(s, sIndex);
            tIndex = nextVisibleIndex(t, tIndex);

            if (sIndex < 0 || tIndex < 0) {
                return sIndex == tIndex;
            }

            if (s.charAt(sIndex) != t.charAt(tIndex)) {
                return false;
            }

            sIndex--;
            tIndex--;
        }

        return true;
    }

    private int nextVisibleIndex(String text, int index) {
        int skip = 0;

        while (index >= 0) {
            if (text.charAt(index) == '#') {
                // This backspace deletes one visible character to its left.
                skip++;
                index--;
            } else if (skip > 0) {
                // This character is erased by a backspace we already passed.
                skip--;
                index--;
            } else {
                return index;
            }
        }

        return index;
    }
}
