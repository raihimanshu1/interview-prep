import java.util.HashMap;
import java.util.Map;

public class MinimumWindowSubstring {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input:
     * s = "ADOBECODEBANC", t = "ABC"
     *
     * Sample Output:
     * "BANC"
     *
     * Find the smallest substring of s that contains all characters of t.
     *
     * Example:
     * s = "ADOBECODEBANC"
     * t = "ABC"
     *
     * Smallest substring containing A, B, and C:
     * "BANC"
     *
     * Answer:
     * "BANC"
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try every substring of s.
     * Check if it contains all characters from t.
     * Keep the smallest valid substring.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The first human idea is to try all possible substrings. For each one,
     * count its letters and ask: does it have enough A's, B's, C's, and every
     * other character required by t? If yes, keep the shortest such substring.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "ADOBEC"
     * t = "ABC"
     *
     * Try substrings:
     * "A" -> missing B and C
     * "ADOB" -> missing C
     * "ADOBEC" -> has A, B, C
     *
     * It is valid.
     * Keep it as best for now.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Generate every substring.
     * 2. Check whether substring contains all characters from t.
     * 3. Keep the shortest valid substring.
     *
     * Time Complexity: O(n^3) in simple form
     * Space Complexity: O(1) or O(character set)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public String bruteForce(String s, String t) {
        String best = "";

        for (int start = 0; start < s.length(); start++) {
            for (int end = start; end < s.length(); end++) {
                String current = s.substring(start, end + 1);

                if (containsAll(current, t)) {
                    if (best.isEmpty() || current.length() < best.length()) {
                        best = current;
                    }
                }
            }
        }

        return best;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * We use a sliding window.
     *
     * Step 1:
     * Expand right until the window contains all required characters.
     *
     * Step 2:
     * Once valid, shrink from left to make it smaller.
     *
     * We need frequency maps because t can contain repeated characters.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "ADOBECODEBANC"
     * t = "ABC"
     *
     * Expand until window has A, B, C:
     * "ADOBEC"
     *
     * Now try shrinking from left.
     *
     * Later we find:
     * "BANC"
     *
     * It has A, B, C and is shorter.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Count required characters from t.
     * 2. Expand right and count window characters.
     * 3. When all requirements are satisfied, update best.
     * 4. Shrink left while still valid.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(character set)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A filtered list of only characters that appear in t can reduce scanning
     * when s is large and most characters are irrelevant. The basic window is
     * usually clearer and still linear.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public String optimized(String s, String t) {
        Map<Character, Integer> required = new HashMap<>();

        for (char ch : t.toCharArray()) {
            required.put(ch, required.getOrDefault(ch, 0) + 1);
        }

        Map<Character, Integer> window = new HashMap<>();

        int formed = 0;
        int requiredTypes = required.size();
        int left = 0;

        int bestStart = 0;
        int bestLength = Integer.MAX_VALUE;

        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            // Expanding right gives the window one more chance to satisfy t.
            window.put(rightChar, window.getOrDefault(rightChar, 0) + 1);

            /*
             * formed increases only when one required character type
             * has enough count inside the window.
             */
            if (
                required.containsKey(rightChar)
                    && window.get(rightChar).intValue() == required.get(rightChar).intValue()
            ) {
                formed++;
            }

            /*
             * If formed == requiredTypes,
             * the window contains all required characters.
             */
            while (formed == requiredTypes) {
                int currentLength = right - left + 1;

                if (currentLength < bestLength) {
                    bestLength = currentLength;
                    bestStart = left;
                }

                char leftChar = s.charAt(left);
                // The window is valid now, so remove from left to test if it can be smaller.
                window.put(leftChar, window.get(leftChar) - 1);

                /*
                 * If removing leftChar makes its count too small,
                 * the window is no longer valid.
                 */
                if (
                    required.containsKey(leftChar)
                        && window.get(leftChar) < required.get(leftChar)
                ) {
                    formed--;
                }

                left++;
            }
        }

        if (bestLength == Integer.MAX_VALUE) {
            return "";
        }

        return s.substring(bestStart, bestStart + bestLength);
    }

    private boolean containsAll(String source, String target) {
        int[] count = new int[128];

        for (char ch : source.toCharArray()) {
            count[ch]++;
        }

        for (char ch : target.toCharArray()) {
            count[ch]--;

            if (count[ch] < 0) {
                return false;
            }
        }

        return true;
    }
}
