package karat.solutions;

import java.util.*;

public class WordsAndStringMatch {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given available letters and a list of words, return the first word that can be formed using those letters.
     *
     * INPUT
     * letters string and words array.
     *
     * OUTPUT
     * First buildable word, or empty string.
     *
     * EXAMPLE
     * letters = "atepl", words = ["plate", "tea", "apple", "tan"]
     * Output: plate
     * 
     * The first buildable word is returned; later buildable words are not needed.
     *
     * WHAT IT MEANS
     * A word is buildable if it never needs more of any character than letters provides.
     */
    /*
     * IN-DEPTH EXPLANATION
     *
     * Imagine the letters string as loose letter tiles on a table.
     *
     * letters = "atepl"
     *
     * You are given candidate words in order:
     *
     * ["plate", "tea", "apple", "tan"]
     *
     * The question is not asking for every buildable word.
     * It is asking for the first word in the list that can be built.
     *
     * For a word to be buildable:
     *
     * 1. Every character in the word must exist in letters.
     * 2. A letter tile can be used only once.
     * 3. If the word needs the same letter twice, letters must also provide it twice.
     *
     * Example:
     *
     * letters = "atepl"
     * word = "plate"
     *
     * p uses p
     * l uses l
     * a uses a
     * t uses t
     * e uses e
     *
     * All needed letters were found, so "plate" is buildable.
     *
     * What you need to know before solving:
     *
     * 1. The order of words matters because we return the first valid word.
     * 2. The order of letters inside letters does not matter.
     * 3. Letter counts matter.
     * 4. Once a source letter is used for a word, it cannot be reused for another
     *    character in that same word check.
     *
     * What we will do in brute force:
     *
     * Try words one by one.
     * For each word, mark every source letter as unused.
     * For each needed character, scan the source letters until we find an unused
     * matching tile.
     * If every character is found, return that word immediately.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - First word works
     *
     * letters = "atepl"
     * words = ["plate", "tea", "apple", "tan"]
     *
     * Output:
     * "plate"
     *
     * Why:
     * plate can be built, and it appears before tea.
     *
     * Example 2 - Skip words that need missing letters
     *
     * letters = "abc"
     * words = ["cab", "bad", "ab"]
     *
     * Output:
     * "cab"
     *
     * Why:
     * cab can be built from a, b, c.
     *
     * Example 3 - Counts matter
     *
     * letters = "aple"
     * words = ["apple", "plea"]
     *
     * Output:
     * "plea"
     *
     * Why:
     * apple needs two p's, but letters has only one p. plea works.
     *
     * Edge case 1 - No word can be built
     *
     * letters = "xyz"
     * words = ["hi", "cat"]
     *
     * Output:
     * ""
     *
     * Why:
     * Every candidate asks for letters that are not available.
     *
     * Edge case 2 - Empty word appears
     *
     * letters = "abc"
     * words = ["", "a"]
     *
     * Output:
     * ""
     *
     * Why:
     * The empty word needs no letters, so the current implementation returns it.
     */

    /*
     * BRUTE FORCE APPROACH IN PLAIN ENGLISH
     *
     * Walk through the words from left to right.
     *
     * For one word, pretend each character in letters is a physical tile.
     * When a character is matched, mark that tile as used.
     *
     * If we cannot find a needed tile, this word fails.
     * If all characters are matched, this is the first buildable word, so return it.
    */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A word can be built only if every needed character exists.
     * 2. Each source character can be used once for that word.
     * 3. The output is the first buildable word from the given list.
     * 4. If no word works, return an empty string.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Try words in input order.
     * For one word, search the source string for each needed character.
     * Mark a source character as used once it is consumed.
     * Return the first word that can consume all its characters.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each candidate word:
     *    a. Create a used array for the letters string.
     *    b. For each character needed by the word:
     *       - scan all letters
     *       - find the first unused matching character
     *       - mark it used
     *    c. If any needed character is not found, reject this word.
     *    d. If all needed characters are found, return this word.
     * 2. If no word works, return empty string.
     *
     * Time Complexity: O(w * L * M), where w is number of words,
     * L is average word length, and M is letters length.
     * Space Complexity: O(M), for the used array for each candidate word.
     */
    public String bruteForce(String letters, String[] words) {

        // Try words in the exact order given because the first buildable word wins.
        for (String word : words) {

            // used[j] tells us whether letters.charAt(j) has already been spent
            // while trying to build this one word.
            boolean[] used = new boolean[letters.length()];

            // Start optimistic. One missing character will flip this to false.
            boolean canBuild = true;

            // Try to pay for every character in the current word.
            for (int i = 0; i < word.length(); i++) {
                char need = word.charAt(i);

                // Have we found an unused matching tile for this character?
                boolean found = false;

                // Brute force searches the whole source string for each needed character.
                for (int j = 0; j < letters.length(); j++) {

                    // The tile must match and must not have been used already.
                    if (!used[j] && letters.charAt(j) == need) {

                        // Spend this tile on the current needed character.
                        used[j] = true;
                        found = true;
                        break;
                    }
                }

                // If no tile could pay for this character, the whole word fails.
                if (!found) {
                    canBuild = false;
                    break;
                }
            }

            // The first successful word is the answer; do not keep searching.
            if (canBuild) {
                return word;
            }
        }

        return "";
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: count source letters once and compare word counts.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find first word buildable from given letters.
     * 2. Remove repeated work: count letters once and compare frequencies for each word.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use letters=atepl.
     * plate can be built and appears first in the words list.
     * tea can also be built, but this method returns the first valid word only.
     */
    public String optimized(String letters, String[] words) {
        int[] available = count(letters);
        for (String word : words) {
            if (canBuild(word, available)) {
                return word;
            }
        }
        return "";
    }

    private boolean canBuild(String word, int[] available) {
        // Count the candidate word's required letters so we can compare needs
        // against the source letters one character type at a time.
        int[] needed = count(word);

        // There are 26 buckets because count() tracks lowercase 'a' through 'z'.
        // For each letter, the word is valid only if it asks for no more copies
        // than the source string provides.
        for (int i = 0; i < 26; i++) {
            if (needed[i] > available[i]) {
                // One shortage is enough to reject the word. This reflects the
                // tile idea: if the word needs two p's and we only have one, the
                // word cannot be built.
                return false;
            }
        }

        // No letter was over budget, so every needed character can be paid for
        // using the available letters.
        return true;
    }

    private int[] count(String text) {
        // counts[0] represents 'a', counts[1] represents 'b', and so on. This
        // fixed-size frequency table lets us compare words without repeatedly
        // scanning the source letters.
        int[] counts = new int[26];

        // Read the text character by character.
        // This helper is designed for lowercase a-z inputs, so only lowercase
        // letters are counted.
        for (char ch : text.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') {
                // Subtracting 'a' turns a lowercase letter into a zero-based index:
                // 'a' - 'a' is 0, 'b' - 'a' is 1, ... 'z' - 'a' is 25.
                counts[ch - 'a']++;
            }
        }

        // Return the frequency table so callers can reason about letter supply
        // and demand with simple integer comparisons.
        return counts;
    }

    public static void main(String[] args) {
        WordsAndStringMatch solution = new WordsAndStringMatch();

        runSample(solution, "atepl", new String[]{"plate", "tea", "apple", "tan"});
        runSample(solution, "abc", new String[]{"cab", "bad", "ab"});
        runSample(solution, "aple", new String[]{"apple", "plea"});
    }

    private static void runSample(WordsAndStringMatch solution, String letters, String[] words) {
        System.out.println("letters = \"" + letters + "\", words = " + Arrays.toString(words));
        System.out.println("bruteForce = \"" + solution.bruteForce(letters, words) + "\"");
        System.out.println("optimized  = \"" + solution.optimized(letters, words) + "\"");
        System.out.println();
    }
}
