package com.patternwisejavasolutions.strings.palindrome;

public class Palindrome {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Check whether a plain string reads the same forward and backward.
 *
 * Sample Input:
 * s = "racecar"
 *
 * Sample Output:
 * true
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In "racecar", the first r matches the last r, the a's match, and the c's
 * match. The sample is telling us to compare mirror positions, not to count
 * letters.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * A palindrome mirrors around its center. The first and last characters must
 * match, then the second and second-last, and so on until the pointers meet.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * A very natural check is to reverse the whole string. If the reversed version
 * is exactly the same as the original, the string reads the same both ways.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Build a reversed copy.
 * 2. Compare original and reversed.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * s = "racecar"
 * reversed = "racecar"
 * Original and reversed match, so return true.
 */
    public boolean bruteForce(String s) {
        StringBuilder reversed = new StringBuilder(s).reverse();
        return s.equals(reversed.toString());
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is building a full reversed copy. We only need to
 * compare matching mirror positions, so two pointers can do the same check
 * without extra string storage.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. left starts at 0 and right starts at n - 1.
 * 2. If characters differ, return false.
 * 3. Move inward until pointers cross.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * left 'r' matches right 'r'.
 * Then 'a' matches 'a'.
 * Then 'c' matches 'c'.
 * The middle 'e' does not need a partner, so return true.
 */
    public boolean optimized(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                // A mismatch at mirror positions breaks the palindrome rule.
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

}
