package com.patternwisejavasolutions.backtracking;

import java.util.ArrayList;
import java.util.List;

public class LetterCombinationsOfAPhoneNumber {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: digits = "23"
     * Sample Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
     *
     * Input: digits = "23"
     * Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
     * Each digit maps to letters like old phone keypad.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * For each digit, choose one of its letters.
     * After choosing a letter for every digit, we have one complete combination.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Read the digits left to right. For the current digit, branch once for
     * every letter on that keypad button. The brute force version builds a new
     * String for each branch, so the choice tree is easy to see.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * digit 2 -> a,b,c
     * digit 3 -> d,e,f
     * Choose a then d -> "ad"
     * Choose a then e -> "ae"
     * Continue until all choices are tried.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. If digits is empty, return empty list.
     * 2. Recursively process one digit at a time.
     * 3. Try each mapped letter.
     * 4. Save when path length equals digits length.
     * Time Complexity: O(4^n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * Each recursive call receives a fresh String path.
     */
    public List<String> bruteForce(String digits) {
        List<String> answer = new ArrayList<>();

        if (digits == null || digits.isEmpty()) {
            return answer;
        }

        build(digits, 0, "", answer);
        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Backtracking is already the correct approach because we must output all combinations.
     * Use StringBuilder so adding/removing the last character is easy.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * path = "a"
     * Add "d" -> "ad", save.
     * Remove "d", add "e" -> "ae", save.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Keep keypad mapping array.
     * 2. Use index to know which digit we are processing.
     * 3. Append a letter, recurse, delete it.
     * Time Complexity: O(4^n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * StringBuilder lets us append and undo the last letter in place.
     */
    public List<String> optimized(String digits) {
        List<String> answer = new ArrayList<>();

        if (digits == null || digits.isEmpty()) {
            return answer;
        }

        buildFast(digits, 0, new StringBuilder(), answer);
        return answer;
    }


    private static final String[] KEYS = {
        "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    private void build(String digits, int index, String current, List<String> answer) {
        if (index == digits.length()) {
            // One letter has been chosen for every digit.
            answer.add(current);
            return;
        }

        String letters = KEYS[digits.charAt(index) - '0'];

        for (char letter : letters.toCharArray()) {
            // Choose this letter for digits[index] and move to the next digit.
            build(digits, index + 1, current + letter, answer);
        }
    }

    private void buildFast(String digits, int index, StringBuilder current, List<String> answer) {
        if (index == digits.length()) {
            // Copy the built path because StringBuilder will be changed after returning.
            answer.add(current.toString());
            return;
        }

        String letters = KEYS[digits.charAt(index) - '0'];

        for (char letter : letters.toCharArray()) {
            // Choose, explore, undo: the core backtracking rhythm.
            current.append(letter);
            buildFast(digits, index + 1, current, answer);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
