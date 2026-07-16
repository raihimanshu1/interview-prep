package com.companywisejavasolutions.karat.solutions;

public class ValidWordAbbreviation {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a word and an abbreviation, return true if the abbreviation is
     * valid. A number in the abbreviation means "skip this many characters" in
     * the original word. Leading zeroes are not allowed.
     *
     * EXAMPLE
     * word = "internationalization", abbreviation = "i18n"
     *
     * Output:
     * true
     *
     * What It Means:
     * The abbreviation keeps i, skips 18 characters, and ends with n.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Imagine reading a long word with shortcut instructions.
     *
     * If the abbreviation says a letter, that exact letter must appear now.
     * If the abbreviation says a number, we jump forward that many characters.
     *
     * So we need to walk through two strings together: one pointer in the word
     * and one pointer in the abbreviation.
     */

    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     * 1. What do I notice first?
     *    The abbreviation is not a normal string. Digits mean movement, while
     *    letters mean exact matches.
     *
     * 2. What does brute force look like?
     *    Build the expanded abbreviation idea and compare it to the word.
     *
     * 3. What is wasteful?
     *    Expanding skipped characters is unnecessary. We only need to move the
     *    word pointer by the number.
     *
     * 4. What data structure is needed?
     *    No extra structure is needed. Two integer pointers are enough.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * An abbreviation is a set of reading instructions for the original word.
     *
     * A letter means:
     * "The word must have this exact letter here."
     *
     * A number means:
     * "Skip this many letters in the word."
     *
     * For example, "i18n" for "internationalization" means:
     * keep i,
     * skip 18 letters,
     * keep n.
     *
     * Brute force turns those instructions back into the full word. When it
     * sees a letter, it appends the letter. When it sees a number, it copies
     * that many real characters from the word into the expanded builder. At the
     * end, the expanded text must equal the original word exactly.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - Classic valid abbreviation
     * word = "internationalization", abbreviation = "i18n"
     * Keep i, skip 18 characters, keep n.
     * Output: true
     *
     * Example 2 - Letters and numbers mixed
     * word = "substitution", abbreviation = "s10n"
     * Keep s, skip 10 characters, keep n.
     * Output: true
     *
     * Example 3 - Direct letters only
     * word = "word", abbreviation = "word"
     * Every abbreviation character must match the word character.
     * Output: true
     *
     * Edge Case 1 - Leading zero
     * word = "apple", abbreviation = "a03e"
     * "03" is not allowed because numbers cannot start with zero.
     * Output: false
     *
     * Edge Case 2 - Skip goes past the word
     * word = "hi", abbreviation = "3"
     * The abbreviation tries to skip more characters than the word has.
     * Output: false
     *
     * Edge Case 3 - Abbreviation ends too early
     * word = "hello", abbreviation = "h2"
     * Expanded abbreviation covers only "hel", not the full word.
     * Output: false
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Digits in the abbreviation form a full number, not separate skips.
     *    For example, "12" means skip twelve characters, not one then two.
     * 2. A number may not begin with zero.
     * 3. The abbreviation must consume the entire word.
     * 4. A letter in the abbreviation is literal and must match the word at the
     *    current position.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * We walk through the abbreviation from left to right.
     *
     * If the current character is a digit, parse the whole number and copy that
     * many characters from the word into an expanded builder.
     *
     * If the current character is a letter, copy that letter into the builder
     * and move one step in the word.
     *
     * At the end, the builder must exactly match the original word and the word
     * pointer must have reached the end.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Brute force tries to reconstruct what the abbreviation represents.
     * For a letter, append that letter. For a number, copy that many characters
     * from the word into a builder. At the end, compare builder with word.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Keep wordIndex at the current character in the original word.
     * 2. Keep abbrIndex at the current character in the abbreviation.
     * 3. If abbreviation has a letter, copy that letter and move both pointers.
     * 4. If abbreviation has a number:
     *    a. Reject if it starts with zero.
     *    b. Parse the full number.
     *    c. Copy that many characters from the word into the expanded string.
     * 5. Compare expanded abbreviation with word.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n), because we build an expanded string.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * word = "internationalization", abbreviation = "i18n"
     *
     * Read 'i' -> append 'i', wordIndex becomes 1.
     * Read 18  -> copy 18 characters from word.
     * Read 'n' -> append 'n'.
     *
     * Expanded text equals the original word, so return true.
     */
    public boolean bruteForce(String word, String abbreviation) {
        // This builder represents what the abbreviation expands into.
        StringBuilder expanded = new StringBuilder();
        // Points to the next character we need from the original word.
        int wordIndex = 0;
        // Points to the next instruction character in the abbreviation.
        int abbrIndex = 0;

        // Read every abbreviation instruction from left to right.
        while (abbrIndex < abbreviation.length()) {
            char current = abbreviation.charAt(abbrIndex);

            // Digits tell us how many original word characters to copy/skip.
            if (Character.isDigit(current)) {
                // A number cannot start with zero because abbreviations like "a01e" are invalid.
                if (current == '0') {
                    return false;
                }

                int number = 0;
                // Parse the full number, such as "18" or "123".
                while (abbrIndex < abbreviation.length()
                        && Character.isDigit(abbreviation.charAt(abbrIndex))) {
                    number = number * 10 + abbreviation.charAt(abbrIndex) - '0';
                    abbrIndex++;
                }

                // Brute force literally copies the skipped characters into expanded.
                for (int count = 0; count < number; count++) {
                    // If the abbreviation tries to skip past the word, it is invalid.
                    if (wordIndex >= word.length()) {
                        return false;
                    }
                    expanded.append(word.charAt(wordIndex));
                    wordIndex++;
                }
            } else {
                // A literal letter needs one matching character available in the word.
                if (wordIndex >= word.length()) {
                    return false;
                }
                // Copy the abbreviation letter and advance both views.
                expanded.append(current);
                wordIndex++;
                abbrIndex++;
            }
        }

        // Valid only when the expanded abbreviation is exactly the original word.
        return wordIndex == word.length() && expanded.toString().equals(word);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * We do not need to build the expanded word. If a number says skip 18, we
     * can simply do wordIndex += 18. If a letter appears, compare it directly
     * with word.charAt(wordIndex).
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep one pointer in word and one pointer in abbreviation.
     * 2. If abbreviation has a digit:
     *    a. Reject leading zero.
     *    b. Parse the whole number.
     *    c. Move word pointer forward by that number.
     * 3. If abbreviation has a letter:
     *    a. The word must still have a character.
     *    b. The two characters must match.
     *    c. Move both pointers.
     * 4. Return true only if both strings finish together.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * wordIndex = 0, abbrIndex = 0
     * 'i' matches word[0], move both.
     * Parse number 18, move wordIndex from 1 to 19.
     * 'n' matches word[19], move both.
     * Both pointers finish, so return true.
     */
    public boolean optimized(String word, String abbreviation) {
        int wordIndex = 0;
        int abbrIndex = 0;

        while (wordIndex < word.length() && abbrIndex < abbreviation.length()) {
            char current = abbreviation.charAt(abbrIndex);

            if (Character.isDigit(current)) {
                if (current == '0') {
                    return false;
                }

                int number = 0;
                while (abbrIndex < abbreviation.length()
                        && Character.isDigit(abbreviation.charAt(abbrIndex))) {
                    number = number * 10 + abbreviation.charAt(abbrIndex) - '0';
                    abbrIndex++;
                }

                // Instead of expanding skipped letters, jump over them directly.
                wordIndex += number;
            } else {
                if (word.charAt(wordIndex) != current) {
                    return false;
                }

                wordIndex++;
                abbrIndex++;
            }
        }

        return wordIndex == word.length() && abbrIndex == abbreviation.length();
    }

    public static void main(String[] args) {
        ValidWordAbbreviation solution = new ValidWordAbbreviation();

        runSample(solution, "internationalization", "i18n");
        runSample(solution, "substitution", "s10n");
        runSample(solution, "apple", "a03e");
    }

    private static void runSample(ValidWordAbbreviation solution, String word, String abbreviation) {
        System.out.println("word = \"" + word + "\", abbreviation = \"" + abbreviation + "\"");
        System.out.println("bruteForce = " + solution.bruteForce(word, abbreviation));
        System.out.println("optimized  = " + solution.optimized(word, abbreviation));
        System.out.println();
    }
}
