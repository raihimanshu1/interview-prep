
package com.patternwisejavasolutions.strings.palindrome;
public class PalindromicSubstrings {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Count how many substrings are palindromes.
 *
 * Sample Input:
 * s = "aaa"
 *
 * Sample Output:
 * 6
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * For "aaa", the answer is 6 because every single "a" counts, both "aa" pieces
 * count, and the whole "aaa" counts. The sample is teaching that we count every
 * palindromic occurrence, even when the text looks repeated.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Every single character is a palindrome. Bigger palindromes grow from centers:
 * "aba" grows from 'b', while "aa" grows from the gap between two a's.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to try every continuous piece. For each start and
 * end, check whether that piece reads the same forward and backward.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Choose start and end.
 * 2. Check the text from start through end.
 * 3. Count it if palindrome.
 *
 * Time Complexity: O(n^3)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * s = "aaa"
 * Single letters: "a", "a", "a" -> 3
 * Length 2: "aa", "aa" -> 2
 * Length 3: "aaa" -> 1
 * Total = 6.
 */
    public int bruteForce(String s) {
        int count = 0;

        for (int start = 0; start < s.length(); start++) {
            for (int end = start; end < s.length(); end++) {
                if (isPalindrome(s, start, end)) {
                    count++;
                }
            }
        }

        return count;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is checking overlapping pieces from scratch.
 * Instead, every palindrome has a center. Expand from each center and count
 * each successful mirror match immediately.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. For each index, expand odd center.
 * 2. Expand even center.
 * 3. Each matching expansion is one palindrome.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * For "aaa":
 * center at middle 'a' counts "a" and then "aaa".
 * gap between first two a's counts "aa".
 * gap between last two a's counts another "aa".
 */
    public int optimized(String s) {
        int count = 0;

        for (int center = 0; center < s.length(); center++) {
            // Count odd-length palindromes centered on a character.
            count += countFromCenter(s, center, center);
            // Count even-length palindromes centered between two characters.
            count += countFromCenter(s, center, center + 1);
        }

        return count;
    }

    private int countFromCenter(String s, int left, int right) {
        int count = 0;
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            count++;
            left--;
            right++;
        }
        return count;
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
