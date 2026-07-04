
package com.patternwisejavasolutions.mathNumberTheory;
public class ExcelSheetColumnTitle {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: columnNumber = 28
     * Sample Output: "AB"
     *
     * Convert a number into an Excel column title.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Excel columns work like base 26, but there is no zero digit.
     * A is 1, B is 2, ..., Z is 26, AA is 27.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Generate titles in order and stop when we reach the requested number.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Start from title A.
     * 2. Move to the next Excel title repeatedly.
     * 3. Stop after columnNumber steps.
     * Time Complexity: O(columnNumber * title length)
     * Space Complexity: O(title length)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * 26 -> Z
     * Next title is AA.
     * 28 is AB.
     */
    public String bruteForce(int columnNumber) {
        String title = "";
        for (int count = 1; count <= columnNumber; count++) {
            title = nextTitle(title);
        }
        return title;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Repeatedly take the last letter.
     * Subtract 1 first because A starts at 1, not 0.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. While columnNumber > 0, decrement it.
     * 2. Convert columnNumber % 26 to a letter.
     * 3. Divide columnNumber by 26 and continue.
     * Time Complexity: O(log26 columnNumber)
     * Space Complexity: O(log26 columnNumber)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * 28 -> subtract 1 gives 27, remainder 1 = B.
     * 1 -> subtract 1 gives 0, remainder 0 = A.
     * Reverse gives AB.
     */
    public String optimized(int columnNumber) {
        StringBuilder answer = new StringBuilder();
        while (columnNumber > 0) {
            // Shift from 1-based Excel digits to 0-based remainders: A should map to 0.
            columnNumber--;
            char letter = (char) ('A' + (columnNumber % 26));
            answer.append(letter);
            // Move to the next digit on the left.
            columnNumber /= 26;
        }
        return answer.reverse().toString();
    }

    private String nextTitle(String title) {
        if (title.isEmpty()) {
            return "A";
        }

        char[] letters = title.toCharArray();
        int index = letters.length - 1;
        while (index >= 0 && letters[index] == 'Z') {
            letters[index] = 'A';
            index--;
        }

        if (index < 0) {
            return "A" + new String(letters);
        }

        letters[index]++;
        return new String(letters);
    }
}
