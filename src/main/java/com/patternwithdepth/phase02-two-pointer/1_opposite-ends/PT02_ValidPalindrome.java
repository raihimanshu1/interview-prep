package com.patternwithdepth.phase02_two_pointer.opposite_ends;

/**
 * PROBLEM: Valid Palindrome
 *
 * DESCRIPTION:
 * A phrase is a palindrome if it reads the same forward and backward after
 * converting all uppercase letters to lowercase and removing all non-alphanumeric characters.
 *
 * Example 1:
 * Input: s = "A man, a plan, a canal: Panama"
 * Output: true
 * Explanation: "amanaplanacanalpanama" is a palindrome.
 *
 * Example 2:
 * Input: s = "race a car"
 * Output: false
 *
 * CONSTRAINTS:
 * - 1 <= s.length <= 2*10^5
 * - s consists only of printable ASCII characters
 *
 * APPROACH:
 * Two pointers from ends, skip non-alphanumeric, compare ignoring case.
 * Time: O(n), Space: O(1)
 */
public class PT02_ValidPalindrome {}
