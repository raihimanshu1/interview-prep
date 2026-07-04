
package com.companywisejavasolutions.wellsFargo.solutions;
import java.util.HashSet;
import java.util.Set;

public class VerbalArithmeticPuzzle {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given words on the left side and one result word on the right side, assign
     * each letter a unique digit so the arithmetic equation is true.
     *
     * INPUT
     * words = addends, result = sum word.
     *
     * OUTPUT
     * true if a valid digit assignment exists, otherwise false.
     *
     * EXAMPLE
     * words = ["SEND", "MORE"], result = "MONEY" -> true
     *
     * WHAT IT MEANS
     * This is column addition with letters. Each letter is one digit, and a
     * leading letter cannot be zero.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1: SEND + MORE = MONEY -> true
     * Example 2: SIX + SEVEN + SEVEN = TWENTY -> true
     * Example 3: A + B = A -> false if B must be nonzero distinct digit
     *
     * EDGE CASES
     * - More than 10 unique letters is impossible.
     * - Multi-letter words cannot start with zero.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Assign every letter a digit, then evaluate the full equation only after
     * all letters have values.
     *
     * Time Complexity: O(10!) in the worst case.
     * Space Complexity: O(unique letters).
     */

    /*
     * OPTIMIZED APPROACH
     *
     * Solve like school addition from right to left. After each column, check
     * whether the result digit and carry make sense. This prunes bad assignments
     * before all letters are assigned.
     *
     * Time Complexity: exponential worst case, but heavily pruned.
     * Space Complexity: O(unique letters).
     */

public boolean bruteForce(String[] words, String result) {
        Set<Character> letters = new HashSet<>();
        Set<Character> leading = leadingLetters(words, result);

        for (String word : words) {
            for (char ch : word.toCharArray()) {
                letters.add(ch);
            }
        }

        for (char ch : result.toCharArray()) {
            letters.add(ch);
        }

        if (letters.size() > 10) {
            return false;
        }

        char[] letterArray = new char[letters.size()];
        int index = 0;
        for (char ch : letters) {
            letterArray[index++] = ch;
        }

        return assignAndCheck(words, result, letterArray, 0, new int[26], new boolean[10], leading);
    }

    public boolean optimized(String[] words, String result) {
        Set<Character> letters = new HashSet<>();

        for (String word : words) {
            for (char ch : word.toCharArray()) {
                letters.add(ch);
            }
        }

        for (char ch : result.toCharArray()) {
            letters.add(ch);
        }

        if (letters.size() > 10) {
            return false;
        }

        int[] value = new int[26];
        for (int i = 0; i < value.length; i++) {
            value[i] = -1;
        }

        return solveColumn(words, result, 0, 0, 0, value, new boolean[10],
                leadingLetters(words, result));
    }

    private boolean solveColumn(String[] words, String result, int columnFromRight, int row,
                                int carry, int[] value, boolean[] used, Set<Character> leading) {
        if (columnFromRight >= result.length()) {
            // If the result has no more columns, all word columns and carry must
            // also be gone. Otherwise the sum still has a digit with nowhere to go.
            return carry == 0 && noWordHasColumn(words, columnFromRight);
        }

        if (row < words.length) {
            String word = words[row];
            int charIndex = word.length() - 1 - columnFromRight;

            if (charIndex < 0) {
                return solveColumn(words, result, columnFromRight, row + 1, carry,
                        value, used, leading);
            }

            char letter = word.charAt(charIndex);
            int assigned = value[letter - 'A'];

            if (assigned != -1) {
                return solveColumn(words, result, columnFromRight, row + 1, carry + assigned,
                        value, used, leading);
            }

            for (int digit = 0; digit <= 9; digit++) {
                if (used[digit]) {
                    continue;
                }

                if (digit == 0 && leading.contains(letter)) {
                    continue;
                }

                used[digit] = true;
                value[letter - 'A'] = digit;

                if (solveColumn(words, result, columnFromRight, row + 1, carry + digit,
                        value, used, leading)) {
                    return true;
                }

                value[letter - 'A'] = -1;
                used[digit] = false;
            }

            return false;
        }

        int resultIndex = result.length() - 1 - columnFromRight;
        char resultLetter = result.charAt(resultIndex);
        int neededDigit = carry % 10;
        int nextCarry = carry / 10;
        int assigned = value[resultLetter - 'A'];

        if (assigned != -1) {
            if (assigned != neededDigit) {
                return false;
            }

            return solveColumn(words, result, columnFromRight + 1, 0, nextCarry,
                    value, used, leading);
        }

        if (used[neededDigit]) {
            return false;
        }

        if (neededDigit == 0 && leading.contains(resultLetter)) {
            return false;
        }

        used[neededDigit] = true;
        value[resultLetter - 'A'] = neededDigit;

        if (solveColumn(words, result, columnFromRight + 1, 0, nextCarry,
                value, used, leading)) {
            return true;
        }

        value[resultLetter - 'A'] = -1;
        used[neededDigit] = false;
        return false;
    }

    private boolean noWordHasColumn(String[] words, int columnFromRight) {
        for (String word : words) {
            if (word.length() - 1 - columnFromRight >= 0) {
                return false;
            }
        }

        return true;
    }

    private boolean assignAndCheck(String[] words, String result, char[] letters, int index,
                                   int[] value, boolean[] used, Set<Character> leading) {
        if (index == letters.length) {
            long sum = 0;

            for (String word : words) {
                sum += numericValue(word, value);
            }

            return sum == numericValue(result, value);
        }

        char letter = letters[index];

        for (int digit = 0; digit <= 9; digit++) {
            if (used[digit]) {
                continue;
            }

            if (digit == 0 && leading.contains(letter)) {
                continue;
            }

            used[digit] = true;
            value[letter - 'A'] = digit;

            if (assignAndCheck(words, result, letters, index + 1, value, used, leading)) {
                return true;
            }

            used[digit] = false;
        }

        return false;
    }

    private long numericValue(String word, int[] value) {
        long number = 0;

        for (char ch : word.toCharArray()) {
            number = number * 10 + value[ch - 'A'];
        }

        return number;
    }

    private Set<Character> leadingLetters(String[] words, String result) {
        Set<Character> leading = new HashSet<>();

        for (String word : words) {
            if (word.length() > 1) {
                leading.add(word.charAt(0));
            }
        }

        if (result.length() > 1) {
            leading.add(result.charAt(0));
        }

        return leading;
    }

    public static void main(String[] args) {
        VerbalArithmeticPuzzle solver = new VerbalArithmeticPuzzle();
        System.out.println("Run bruteForce and optimized against the examples in the comment block.");
    }
}
