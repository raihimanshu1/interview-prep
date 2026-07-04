
package com.patternwisejavasolutions.strings.palindrome;
public class StringValidPalindromeII {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Check whether a string can become a palindrome after deleting at most one character.
 *
 * Sample Input:
 * s = "abca"
 *
 * Sample Output:
 * true
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * The sample "abca" is almost mirrored: the outer a's already match, and the
 * only disagreement is b versus c in the middle. Since the problem allows one
 * deletion, the first clue is that a single bad mirror comparison may still be
 * fixable.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * When both ends match, they are fine. When they do not match, we get only one
 * chance to delete either the left character or the right character.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * A very natural first idea is to try the exact action the problem allows:
 * delete one character at a time and check whether what remains reads the same
 * forward and backward.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Check the original string first.
 * 2. For every index, build a string without that character.
 * 3. Return true if any version is palindrome.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(n)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * Delete b from "abca" to get "aca", which is palindrome.
 */
    public boolean bruteForce(String s) {
        if (isPalindrome(s, 0, s.length() - 1)) {
            return true;
        }

        for (int skip = 0; skip < s.length(); skip++) {
            String afterDeletingOneChar = s.substring(0, skip) + s.substring(skip + 1);
            if (isPalindrome(afterDeletingOneChar, 0, afterDeletingOneChar.length() - 1)) {
                return true;
            }
        }

        return false;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is trying deletions that cannot matter. If the
 * outside letters already match, deleting one of them would only break a part
 * that was already correct. With two pointers, the first mismatch is the only
 * place where the one allowed deletion can help.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Compare left and right while they match.
 * 2. On mismatch, try skipping left OR skipping right.
 * 3. If either remaining middle part is palindrome, return true.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * s = "abca"
 *
 * left = 0, right = 3
 * s[left] = 'a' and s[right] = 'a', so both ends already match.
 * Move inward.
 *
 * left = 1, right = 2
 * s[left] = 'b' and s[right] = 'c', so this is the first mismatch.
 *
 * We are allowed to delete only one character, so only two choices make sense:
 * 1. Skip 'b' and check the remaining middle text "c".
 * 2. Skip 'c' and check the remaining middle text "b".
 *
 * A single character is always a palindrome, so one of these choices works.
 * Therefore "abca" can become a palindrome by deleting either 'b' or 'c'.
 */
    public boolean optimized(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                // Only the mismatch can be fixed by the single allowed deletion.
                return isPalindrome(s, left + 1, right) || isPalindrome(s, left, right - 1);
            }
            left++;
            right--;
        }

        return true;
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
