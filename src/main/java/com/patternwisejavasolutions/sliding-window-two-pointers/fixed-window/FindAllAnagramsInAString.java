
package com.patternwisejavasolutions.slidingWindowTwoPointers.fixedWindow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindAllAnagramsInAString {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: s = "cbaebabacd", p = "abc"
     * Sample Output: [0, 6]
     *
     * Return every starting index where a substring of s is an anagram of p.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * An anagram uses the same letters with the same counts. Since p has fixed
     * length, only substrings of that same length can be answers.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Check every substring of length p.length(). Count its letters and compare
     * with p's letter counts.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build frequency count for p.
     * 2. For every start index in s, count the next p.length() letters.
     * 3. If both counts match, record start.
     *
     * Time Complexity: O(n * k)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * s = "cbaebabacd", p = "abc"
     * start 0: "cba" has a,b,c, record 0.
     * start 1: "bae" differs.
     * ...
     * start 6: "bac" has a,b,c, record 6.
     */

    public List<Integer> bruteForce(String s, String p) {
        List<Integer> answer = new ArrayList<>();
        int windowSize = p.length();
        int[] target = buildCount(p, 0, p.length());

        for (int start = 0; start + windowSize <= s.length(); start++) {
            int[] current = buildCount(s, start, start + windowSize);
            if (Arrays.equals(target, current)) {
                answer.add(start);
            }
        }

        return answer;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Brute force recounts almost the same letters for neighboring substrings.
     * Neighboring fixed-size windows share all but one letter. Slide the window:
     * add the new right letter and remove the old left letter.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Count letters in p.
     * 2. Move right through s and add s[right].
     * 3. Once window is longer than p, remove s[left].
     * 4. When window size equals p length and counts match, record left.
     *
     * Time Complexity: O(26 * n), treated as O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * s = "cbaebabacd", p = "abc"
     * Window "cba" matches, add 0.
     * Slide to "bae", not match.
     * Keep sliding until "bac" matches, add 6.
     */

    public List<Integer> optimized(String s, String p) {
        List<Integer> answer = new ArrayList<>();
        int[] target = buildCount(p, 0, p.length());
        int[] window = new int[26];
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            // Add the new character entering this fixed-size window.
            window[s.charAt(right) - 'a']++;

            if (right - left + 1 > p.length()) {
                // Window grew too large, so remove the old left character.
                window[s.charAt(left) - 'a']--;
                left++;
            }

            // Counts are tiny fixed arrays, so comparison is still constant work.
            if (right - left + 1 == p.length() && Arrays.equals(target, window)) {
                answer.add(left);
            }
        }

        return answer;
    }

    private int[] buildCount(String text, int start, int end) {
        int[] count = new int[26];
        for (int i = start; i < end; i++) {
            count[text.charAt(i) - 'a']++;
        }
        return count;
    }
}
