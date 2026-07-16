package com.patternwisejavasolutions.strings.palindrome;

public class ValidPalindrome {

/*
 * PROBLEM IN SIMPLE WORDS
 *
 * Check whether a string is a palindrome after ignoring non-letters/digits and case.
 *
 * Sample Input:
 * s = "A man, a plan, a canal: Panama"
 *
 * Sample Output:
 * true
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * The sample contains spaces, punctuation, and capital letters, yet the answer
 * is true. That tells us the real comparison is not on the raw sentence; it is
 * on the letters after ignoring punctuation and case.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * A palindrome reads the same from both ends. We ignore spaces, punctuation,
 * and case, so only letters and digits matter.
 *
 * "A man, a plan, a canal: Panama" becomes "amanaplanacanalpanama" after
 * cleaning, and that cleaned text mirrors correctly.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to create a clean lowercase version that contains
 * only letters and digits. Then reverse that clean string and compare.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Build a cleaned lowercase string of only letters and digits.
 * 2. Reverse it.
 * 3. Compare cleaned with reversed.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * Original: "A man, a plan, a canal: Panama"
 * Cleaned: "amanaplanacanalpanama"
 * Its reverse is the same, so return true.
 */
    public boolean bruteForce(String s) {
        StringBuilder cleaned = new StringBuilder();

        for (char ch : s.toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                cleaned.append(Character.toLowerCase(ch));
            }
        }

        return cleaned.toString().equals(cleaned.reverse().toString());
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is storing a cleaned copy and a reversed copy.
 * Two pointers can skip punctuation in-place and compare only meaningful
 * characters.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. left starts at beginning, right starts at end.
 * 2. Move left until it points to a letter/digit.
 * 3. Move right until it points to a letter/digit.
 * 4. Compare lowercase characters.
 * 5. Move inward until done.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * left starts at 'A', right starts at 'a'; lowercase a matches a.
 * Move inward, skipping spaces, commas, and colon.
 * Every meaningful mirror character matches.
 */
    public boolean optimized(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                // Punctuation does not participate in the palindrome check.
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                // Skip from the right for the same reason we skip from the left.
                right--;
            }

            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

}
