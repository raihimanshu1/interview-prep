import java.util.HashMap;
import java.util.Map;

public class IsomorphicStrings {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Check whether characters in s can be replaced to make t, with one-to-one mapping.
 *
 * Sample Input:
 * s = "egg", t = "add"
 *
 * Sample Output:
 * true
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In "egg" -> "add", the first letters map e -> a, and both g's map to d.
 * The repeated positions line up, which is why the mapping is consistent.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Each character in the first string must always become the same character in
 * the second string. Also, two different characters cannot share one target
 * character.
 *
 * In "egg" -> "add", e maps to a and both g's map to d. That pattern is
 * consistent.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * A beginner can ignore the actual letters and compare the pattern of equal
 * positions. If s[i] equals s[j], then t[i] must equal t[j]. If s[i] differs
 * from s[j], then t[i] must also differ from t[j].
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. If lengths differ, return false.
 * 2. For every i and j, compare whether characters match in both strings.
 * 3. If the pattern differs, return false.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * s = "egg", t = "add"
 * Positions 1 and 2 both have 'g' in s.
 * Positions 1 and 2 both have 'd' in t.
 * The repeated-character pattern matches.
 */
    public boolean bruteForce(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j < s.length(); j++) {
                if ((s.charAt(i) == s.charAt(j)) != (t.charAt(i) == t.charAt(j))) {
                    return false;
                }
            }
        }

        return true;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is comparing every pair of positions to rediscover
 * the same mapping rule. Store the rule as we read the strings once.
 *
 * We need two maps: s to t prevents one source from changing targets, and t to
 * s prevents two different sources from using the same target.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Walk through both strings.
 * 2. If a mapping exists, it must match current char.
 * 3. If not, create mappings in both directions.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1) for fixed character set
 */

/*
 * OPTIMIZED DRY RUN
 *
 * e -> a is stored.
 * g -> d is stored.
 * The next g again wants d, and d already points back to g, so it is valid.
 */
    public boolean optimized(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Character> forward = new HashMap<>();
        Map<Character, Character> backward = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            char a = s.charAt(i);
            char b = t.charAt(i);

            if (forward.containsKey(a) && forward.get(a) != b) {
                // One source character is trying to change its target.
                return false;
            }
            if (backward.containsKey(b) && backward.get(b) != a) {
                // One target character is already taken by a different source.
                return false;
            }

            forward.put(a, b);
            backward.put(b, a);
        }

        return true;
    }

}
