import java.util.Arrays;

public class ValidAnagram {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Check whether two strings contain the same letters with the same counts.
 *
 * Sample Input:
 * s = "anagram", t = "nagaram"
 *
 * Sample Output:
 * true
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * "anagram" and "nagaram" look different because the letters are rearranged,
 * but the counts match exactly. A learner should first look for equal letter
 * inventory, not equal order.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * An anagram is like having two piles of letter tiles. The order on the table
 * can change, but the pile must contain the same number of each letter.
 *
 * For "anagram" and "nagaram", both piles have three a's, one n, one g, one r,
 * and one m. That is why the words match even though the order is different.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * A beginner-friendly way is to arrange both piles alphabetically. If the
 * sorted strings become identical, then every letter and every count matched.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. If lengths differ, return false.
 * 2. Convert both strings to char arrays.
 * 3. Sort both arrays.
 * 4. Compare sorted arrays.
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * s = "anagram" -> sorted as "aaagmnr"
 * t = "nagaram" -> sorted as "aaagmnr"
 * Both sorted forms are the same, so return true.
 */
    public boolean bruteForce(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        char[] first = s.toCharArray();
        char[] second = t.toCharArray();
        Arrays.sort(first);
        Arrays.sort(second);
        return Arrays.equals(first, second);
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * Sorting spends time rearranging letters even though we only care about
 * counts. The repeated/wasteful work is ordering the characters.
 *
 * Counting removes that waste. Since lowercase English letters have only 26
 * slots, add letters from s and subtract letters from t. If both words used
 * the same letters, every count returns to zero.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. If lengths differ, return false.
 * 2. Create count array of size 26.
 * 3. Add counts for s and subtract counts for t.
 * 4. If any count is not zero, return false.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * Read both strings together:
 * i = 0: add 'a' from s, subtract 'n' from t.
 * i = 1: add 'n' from s, subtract 'a' from t.
 * By the end, every added letter was canceled by the same letter from t.
 */
    public boolean optimized(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int[] counts = new int[26];
        for (int i = 0; i < s.length(); i++) {
            // Add from s and subtract from t so matching letters cancel out.
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }

        for (int count : counts) {
            if (count != 0) {
                return false;
            }
        }

        return true;
    }

}
