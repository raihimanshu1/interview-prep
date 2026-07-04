
package com.companywisejavasolutions.wellsFargo.solutions;
public class LongestPalindromicSubstring {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Find the longest continuous substring that is a palindrome.
 *
 * Sample Input:
 * s = "babad"
 *
 * Sample Output:
 * "bab"
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In "babad", the answer "bab" is made from neighboring characters in the
 * original order. We are not allowed to pick scattered letters; the winning
 * text must be one continuous slice of the string.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * A substring is continuous. A palindrome grows evenly from its center, like
 * "racecar" from e or "abba" from the gap between the two b's.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * A beginner's first idea is to list every possible continuous slice, check
 * whether it reads the same forward and backward, and keep the longest one seen
 * so far.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Choose every start.
 * 2. Choose every end.
 * 3. Check if the text from start through end is palindrome.
 * 4. Keep the longest.
 *
 * Time Complexity: O(n^3)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * s = "babad"
 * Try "b", "ba", "bab".
 * "bab" reads the same both ways, so best becomes "bab".
 * Later "aba" also has length 3, but either answer is acceptable.
 */
    public String bruteForce(String s) {
        String best = "";

        for (int start = 0; start < s.length(); start++) {
            for (int end = start; end < s.length(); end++) {
                int length = end - start + 1;
                if (length > best.length() && isPalindrome(s, start, end)) {
                    best = s.substring(start, end + 1);
                }
            }
        }

        return best;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is checking many overlapping pieces from scratch.
 * Every palindrome has a center, so expand around each possible center and stop
 * as soon as the mirror breaks.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. For every index, expand for odd-length palindromes.
 * 2. Also expand around the gap after it for even-length palindromes.
 * 3. Keep the longest palindromic text found.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * Odd center at index 1 ('a'): compare b and b around it, giving "bab".
 * Odd center at index 2 ('b'): compare a and a around it, giving "aba".
 * Keep the longest range found.
 */
    public String optimized(String s) {
        if (s.isEmpty()) {
            return "";
        }

        int bestStart = 0;
        int bestEnd = 0;

        for (int center = 0; center < s.length(); center++) {
            // Odd-length palindrome has one center character.
            int odd = expandFromCenter(s, center, center);
            // Even-length palindrome has a center gap between two characters.
            int even = expandFromCenter(s, center, center + 1);
            int length = Math.max(odd, even);

            if (length > bestEnd - bestStart + 1) {
                bestStart = center - (length - 1) / 2;
                bestEnd = center + length / 2;
            }
        }

        return s.substring(bestStart, bestEnd + 1);
    }

    private int expandFromCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    private boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
