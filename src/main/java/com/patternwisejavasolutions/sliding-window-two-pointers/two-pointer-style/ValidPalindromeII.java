package com.patternwisejavasolutions.slidingwindowtwopointers.twopointerstyle;

public class ValidPalindromeII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Check if a string can become a palindrome after deleting at most one character.
     *
     * Sample Input:
     * s = "abca"
     *
     * Sample Output:
     * true
     *
     * Why?
     * Delete 'b' and string becomes "aca".
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try deleting each character one by one.
     * After each deletion, check whether the remaining string is a palindrome.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Since we may delete at most one character, the plain first idea is to try
     * every possible single deletion. After each deletion, check the remaining
     * string from both ends like a normal palindrome.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "abca"
     *
     * Delete index 0 -> "bca", not palindrome.
     * Delete index 1 -> "aca", palindrome.
     *
     * So answer is true.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. First check if original string is palindrome.
     * 2. Delete each index once.
     * 3. Check the remaining string.
     * 4. If any version is palindrome, return true.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public boolean bruteForce(String s) {
        if (isPalindrome(s, 0, s.length() - 1)) {
            return true;
        }

        for (int skip = 0; skip < s.length(); skip++) {
            String candidate = s.substring(0, skip) + s.substring(skip + 1);

            if (isPalindrome(candidate, 0, candidate.length() - 1)) {
                return true;
            }
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * A palindrome is checked from both ends.
     *
     * If left and right characters match, keep moving inward.
     * The first mismatch is the only place where deletion matters.
     *
     * At mismatch, we have two choices:
     * - delete left character
     * - delete right character
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "abca"
     *
     * left = 0 'a', right = 3 'a' -> match.
     * left = 1 'b', right = 2 'c' -> mismatch.
     *
     * Try skipping 'b': "aca" part is palindrome.
     * Return true.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Use left and right pointers.
     * 2. Move inward while characters match.
     * 3. On first mismatch, check:
     *    s[left + 1..right] or s[left..right - 1]
     * 4. If either is palindrome, return true.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A recursive helper can try deleting the left character or the right
     * character at the first mismatch. Passing a deletion count makes the idea
     * flexible, but the two-pointer helper is more direct here.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public boolean optimized(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            if (s.charAt(left) == s.charAt(right)) {
                left++;
                right--;
            } else {
                // The only useful deletion is one of the two mismatched characters.
                return isPalindrome(s, left + 1, right)
                    || isPalindrome(s, left, right - 1);
            }
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
