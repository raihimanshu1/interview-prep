package com.patternwisejavasolutions.backtracking;

import java.util.ArrayList;
import java.util.List;

public class PalindromePartitioning {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: s = "aab"
     * Sample Output: [["a","a","b"],["aa","b"]]
     *
     * Input: s = "aab"
     * Output: [["a","a","b"], ["aa","b"]]
     * Split the string so every piece is a palindrome.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * At each position, try every possible next piece.
     * Only continue if that piece is a palindrome.
     * When we reach the end, the current split is valid.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Put a cut after every possible end position. Each chosen piece must be a
     * palindrome; if it is not, that branch stops immediately. The recursion
     * tree is a tree of cut positions, not a tree of characters.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * s = "aab"
     * Start at 0.
     * Choose "a" because it is palindrome.
     * Then choose another "a".
     * Then choose "b".
     * Save ["a","a","b"].
     * Also choose "aa" first and save ["aa","b"].
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively choose all possible substrings.
     * 2. Check whether each substring is palindrome.
     * 3. If yes, add it and continue.
     * 4. Undo after returning.
     * Time Complexity: exponential
     * Space Complexity: O(n) recursion
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * The helper tries all next cuts and filters them with a palindrome check.
     */
    public List<List<String>> bruteForce(String s) {
        List<List<String>> answer = new ArrayList<>();
        partition(s, 0, new ArrayList<>(), answer);
        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The backtracking idea is already natural.
     * The readable improvement is the choose/explore/undo shape: choose one
     * palindrome piece, explore the suffix, then undo that piece and try a
     * longer one.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * When start = 0, end = 1 gives "aa".
     * Because "aa" is palindrome, we add it and continue from index 2.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. If start reaches s.length(), save current partition.
     * 2. Try end from start to last index.
     * 3. If s[start..end] is palindrome, choose it.
     * 4. Recurse from end + 1.
     * 5. Undo choice.
     * Time Complexity: exponential
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * The same backtracking helper is used because all valid partitions must be listed.
     */
    public List<List<String>> optimized(String s) {
        List<List<String>> answer = new ArrayList<>();
        partition(s, 0, new ArrayList<>(), answer);
        return answer;
    }


    private void partition(String s, int start, List<String> current, List<List<String>> answer) {
        if (start == s.length()) {
            // All characters have been cut into palindrome pieces.
            answer.add(new ArrayList<>(current));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (!isPalindrome(s, start, end)) {
                continue;
            }

            // Choose s[start..end], solve the rest, then undo the cut.
            current.add(s.substring(start, end + 1));
            partition(s, end + 1, current, answer);
            current.remove(current.size() - 1);
        }
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
