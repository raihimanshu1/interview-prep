package com.companywisejavasolutions.karat.solutions;

import java.util.*;

public class ListOfWordsAndString {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given available letters and a list of words, return all words that can be formed using those letters.
     *
     * INPUT
     * letters string and words array.
     *
     * OUTPUT
     * List of all buildable words.
     *
     * EXAMPLE
     * letters = "atepl", words = ["plate", "tea", "apple", "tan"]
     * Output: [plate, tea]
     * 
     * This variant returns all buildable words, not only the first one.
     *
     * WHAT IT MEANS
     * Same character-frequency check, but collect every passing word.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of letters as Scrabble tiles. A word can be built only if every needed
     * tile is available enough times.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * We have a bag of available letters and a list of candidate words. A word is
     * buildable only when every character in that word can be matched to a
     * different character from the available letters.
     *
     * The important detail is repeated letters. If letters = "aple", then "apple"
     * cannot be built because the word needs two p characters but the source only
     * has one. We cannot reuse the same source letter twice.
     *
     * The brute force approach simulates picking tiles by hand. For each word,
     * create a used array over the source letters. Then, for each needed
     * character in the word, search for an unused matching source character.
     */

    /*
     * EXAMPLES TO UNDERSTAND THE PROBLEM
     *
     * Example 1 - Multiple buildable words
     * letters = "atepl"
     * words = ["plate", "tea", "apple", "tan"]
     * Output: [plate, tea]
     * apple needs two p characters. tan needs n.
     *
     * Example 2 - Repeated letters are available
     * letters = "aapple"
     * words = ["apple", "appeal", "pea"]
     * Output: [apple, pea]
     * apple can use two p characters because letters has two p characters.
     *
     * Example 3 - Word uses all letters exactly
     * letters = "stone"
     * words = ["tones", "stone", "note"]
     * Output: [tones, stone, note]
     * A word may use all letters or only some of them.
     *
     * Edge Case 1 - Empty word
     * An empty word needs no letters, so it is buildable.
     *
     * Edge Case 2 - Empty letters string
     * Only empty words can be built.
     *
     * Edge Case 3 - Duplicate words
     * The brute force method adds each buildable occurrence from the input list.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * - Each available letter can be used at most once per candidate word.
     * - The source letters reset for every new word.
     * - Repeated letters matter, so a simple contains check is not enough.
     * - This version returns all buildable words, not just the first one.
     * - The brute force solution uses a used array to model consumed letters.
     */

    /*
     * WHAT WE DO TO SOLVE IT
     *
     * Try one candidate word at a time. For each character in that word, search
     * through the available letters for a matching character that has not already
     * been used for this word. If every character is matched, add the word to the
     * result list.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    Every candidate word is possible only if the source has enough copies of each character.
     *
     * 2. What data structure does that naturally suggest?
     *    Use character frequencies because repeated letters matter.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: for each word, search/remove characters from the source string.
     *
     * 4. What repeated work should I remove?
     *    Optimized: count source letters once and compare candidate frequencies against it.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: for each word, search/remove characters from the source string.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Create an empty result list.
     * 2. For each candidate word:
     *    create a boolean used array the same length as letters.
     * 3. For each character needed by the word:
     *    scan letters from left to right.
     * 4. When an unused matching source letter is found:
     *    mark it used and move to the next needed character.
     * 5. If a needed character cannot be found, reject that word.
     * 6. If all characters are found, add the word to the result.
     * 
     * Time Complexity: O(number of words * word length * letters length).
     * Space Complexity: O(letters length + answer size) for the used array and result.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use letters=atepl.
     * plate and tea can be built.
     * apple needs two p characters, so it cannot be built.
     * Final answer: [plate, tea]
     */
    public List<String> bruteForce(String letters, String[] words) {

        List<String> result = new ArrayList<>();

        for (String word : words) {
            // A fresh used array means every candidate word gets a fresh bag of letters.
            boolean[] used = new boolean[letters.length()];
            boolean canBuild = true;

            for (int i = 0; i < word.length(); i++) {
                char need = word.charAt(i);
                boolean found = false;

                // Brute force searches the source letters again for each character.
                for (int j = 0; j < letters.length(); j++) {
                    // The !used[j] check prevents reusing the same tile twice.
                    if (!used[j] && letters.charAt(j) == need) {
                        used[j] = true;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // One missing character is enough to reject the whole word.
                    canBuild = false;
                    break;
                }
            }

            if (canBuild) {
                // Every needed character found a unique source letter.
                result.add(word);
            }
        }

        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: count source letters once and compare candidate frequencies against it.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: return all words buildable from letters.
     * 2. Remove repeated work: count source letters once and compare word counts.
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
     * plate and tea can be built.
     * apple needs two p characters, so it cannot be built.
     * Final answer: [plate, tea]
     */
    public List<String> optimized(String letters, String[] words) {
        int[] available = count(letters);
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (canBuild(word, available)) {
                result.add(word);
            }
        }
        return result;
    }

    private boolean canBuild(String word, int[] available) {
        // Count the letters required by this one candidate word.
        // This converts "apple" into needs like a:1, p:2, l:1, e:1.
        int[] needed = count(word);
        // Compare every lowercase letter bucket from 'a' through 'z'.
        for (int i = 0; i < 26; i++) {
            // If the word needs more of any letter than the source provides, one tile would have to be reused.
            if (needed[i] > available[i]) {
                // Reusing a source letter is not allowed, so this word cannot be built.
                return false;
            }
        }
        // No lowercase letter was over budget, so every lowercase requirement
        // can be supplied by the source string.
        return true;
    }

    private int[] count(String text) {
        // There are 26 slots, one for each lowercase English letter.
        // Slot 0 is 'a', slot 1 is 'b', and so on.
        int[] counts = new int[26];
        // Walk the text once and turn each character into a frequency update.
        for (char ch : text.toCharArray()) {
            // This implementation intentionally counts lowercase a-z only, matching the array size.
            if (ch >= 'a' && ch <= 'z') {
                // Subtracting 'a' converts the character into the correct index in the counts array.
                counts[ch - 'a']++;
            }
        }
        // The caller can now compare lowercase counts instead of repeatedly
        // searching through strings.
        return counts;
    }

    public static void main(String[] args) {
        ListOfWordsAndString solution = new ListOfWordsAndString();

        runSample(solution, "atepl", new String[]{"plate", "tea", "apple", "tan"});
        runSample(solution, "aapple", new String[]{"apple", "appeal", "pea"});
        runSample(solution, "stone", new String[]{"tones", "stone", "note"});
    }

    private static void runSample(ListOfWordsAndString solution, String letters, String[] words) {
        System.out.println("letters = \"" + letters + "\", words = " + Arrays.toString(words));
        System.out.println("bruteForce = " + solution.bruteForce(letters, words));
        System.out.println("optimized  = " + solution.optimized(letters, words));
        System.out.println();
    }
}
