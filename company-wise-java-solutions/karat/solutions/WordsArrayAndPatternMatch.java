package karat.solutions;

import java.util.*;

public class WordsArrayAndPatternMatch {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given words and a pattern, return words that match the same bijective character pattern.
     *
     * INPUT
     * words array and pattern string.
     *
     * OUTPUT
     * Words matching the pattern.
     *
     * EXAMPLE
     * words = ["mee", "aqq", "dkd", "ccc"], pattern = "abb"
     * Output: [mee, aqq]
     * 
     * The first pattern letter differs from the repeated second/third pattern letters.
     *
     * WHAT IT MEANS
     * Pattern chars and word chars must map one-to-one consistently.
     */
    /*
     * IN-DEPTH EXPLANATION
     *
     * Think of the pattern as a shape, not as actual letters.
     *
     * pattern = "abb"
     *
     * This shape means:
     *
     * position 0 is different
     * position 1 and position 2 are the same
     *
     * So "mee" matches:
     *
     * m is the different first character
     * e and e repeat in the last two positions
     *
     * "aqq" also matches for the same reason.
     *
     * "dkd" does not match:
     *
     * pattern positions 1 and 2 are the same,
     * but word positions 1 and 2 are k and d, which are different.
     *
     * "ccc" does not match:
     *
     * pattern positions 0 and 1 are different,
     * but word positions 0 and 1 are both c.
     *
     * What you need to know before solving:
     *
     * 1. The word and pattern must have the same length.
     * 2. If two pattern positions are equal, the matching word positions must be equal.
     * 3. If two pattern positions are different, the matching word positions must
     *    also be different.
     * 4. That gives us a one-to-one, or bijective, relationship.
     *
     * What we will do in brute force:
     *
     * For each word, compare every pair of positions.
     * Ask the same question for the pattern and the word:
     *
     * "Are these two positions equal?"
     *
     * If the pattern says yes but the word says no, reject the word.
     * If the pattern says no but the word says yes, reject the word.
     * If every pair agrees, the word matches.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Classic repeated ending
     *
     * words = ["mee", "aqq", "dkd", "ccc"]
     * pattern = "abb"
     *
     * Output:
     * [mee, aqq]
     *
     * Why:
     * The last two letters must match each other, and the first must be different.
     *
     * Example 2 - All letters different
     *
     * words = ["abc", "deq", "mee", "xyz"]
     * pattern = "abc"
     *
     * Output:
     * [abc, deq, xyz]
     *
     * Why:
     * Each position must use a different character.
     *
     * Example 3 - All letters same
     *
     * words = ["aaa", "bbb", "aba", "ccc"]
     * pattern = "zzz"
     *
     * Output:
     * [aaa, bbb, ccc]
     *
     * Why:
     * Every position must repeat the same character.
     *
     * Edge case 1 - Length mismatch
     *
     * words = ["ab", "abc", "abcd"]
     * pattern = "abc"
     *
     * Output:
     * [abc]
     *
     * Why:
     * Words with different lengths cannot have the same shape.
     *
     * Edge case 2 - Empty pattern and empty word
     *
     * words = ["", "a"]
     * pattern = ""
     *
     * Output:
     * []
     *
     * Why:
     * The empty word matches the empty pattern, so the list contains "".
     * When printed, that can look like an empty list even though it has one value.
     */

    /*
     * BRUTE FORCE APPROACH IN PLAIN ENGLISH
     *
     * For each candidate word, compare all pairs of positions.
     *
     * If pattern[i] and pattern[j] are the same, then word[i] and word[j] must
     * also be the same.
     *
     * If pattern[i] and pattern[j] are different, then word[i] and word[j] must
     * also be different.
     *
     * This avoids maps, but it repeats pair checks for every word.
    */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. The word and pattern must have the same length.
     * 2. Same pattern letters must map to same word letters.
     * 3. Different pattern letters cannot secretly map to the same word letter.
     * 4. The brute force check can compare positions directly.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Test each word against the pattern.
     * For a word, compare every pair of positions.
     * If the pattern says two positions are equal, the word must also match there.
     * If the pattern says two positions differ, the word must differ there too.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create an answer list.
     * 2. For each word:
     *    a. Skip it if its length differs from pattern length.
     *    b. Compare every pair of positions i and j.
     *    c. Check whether pattern[i] == pattern[j].
     *    d. Check whether word[i] == word[j].
     *    e. If those answers differ, reject the word.
     *    f. If all pairs agree, add the word to the answer.
     * 3. Return all matching words.
     *
     * Time Complexity: O(w * p^2), where w is number of words and p is pattern length.
     * Space Complexity: O(r), where r is the number of matching words returned.
     */
    public List<String> bruteForce(String[] words, String pattern) {

        // Store every word whose character shape matches the pattern shape.
        List<String> result = new ArrayList<>();

        // Check candidates one by one.
        for (String word : words) {

            // A word with a different length cannot line up with the pattern.
            if (word.length() != pattern.length()) {
                continue;
            }

            // Assume the word matches until a pair of positions proves otherwise.
            boolean matches = true;

            // Compare every pair of positions.
            // We start j at i + 1 because comparing a position to itself
            // always gives the same answer and teaches us nothing.
            for (int i = 0; i < pattern.length(); i++) {
                for (int j = i + 1; j < pattern.length(); j++) {

                    // Does the pattern repeat between these two positions?
                    boolean samePattern = pattern.charAt(i) == pattern.charAt(j);

                    // Does the word repeat between these same two positions?
                    boolean sameWord = word.charAt(i) == word.charAt(j);

                    // The word matches only if both answers agree.
                    // Example:
                    // pattern says same, word says different -> reject.
                    // pattern says different, word says same -> reject.
                    if (samePattern != sameWord) {
                        matches = false;
                        break;
                    }
                }

                // Stop early once this word is already known to fail.
                if (!matches) {
                    break;
                }
            }

            // If no pair contradicted the pattern shape, keep this word.
            if (matches) {
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
     * Optimized: walk characters once while maintaining forward and reverse mappings.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find words matching a bijective pattern.
     * 2. Remove repeated work: walk the word once with forward and reverse maps.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use pattern abb.
     * mee and aqq both follow first-letter-different, second-and-third-same.
     * dkd and ccc do not match the required bijection.
     */
    public List<String> optimized(String[] words, String pattern) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
        if (matches(word, pattern)) {
                result.add(word);
            }
        }
        return result;
    }

    private boolean matches(String word, String pattern) {
        // A pattern match compares positions one-for-one, so different lengths can never match.
        if (word.length() != pattern.length()) {
            return false;
        }
        // This map answers: "when I see this pattern character, which word character must it become?"
        Map<Character, Character> patternToWord = new HashMap<>();
        // This reverse map protects the one-to-one rule in the other direction.
        // Without it, pattern "ab" could incorrectly match word "cc".
        Map<Character, Character> wordToPattern = new HashMap<>();
        // Walk both strings together so each position can either create or verify a mapping.
        for (int i = 0; i < word.length(); i++) {
            // p is the pattern character at this position.
            char p = pattern.charAt(i);
            // w is the actual word character that would correspond to p.
            char w = word.charAt(i);
            // If p was mapped earlier, it must map to the same word character every time.
            if (patternToWord.containsKey(p) && patternToWord.get(p) != w) {
                // Same pattern letter producing two different word letters breaks consistency.
                return false;
            }
            // If w was already used by another pattern character, the mapping is not bijective.
            if (wordToPattern.containsKey(w) && wordToPattern.get(w) != p) {
                // Different pattern letters collapsing to the same word letter would change the shape.
                return false;
            }
            // Record or reaffirm the forward mapping from pattern shape to word letters.
            patternToWord.put(p, w);
            // Record or reaffirm the reverse mapping so future positions cannot reuse w incorrectly.
            wordToPattern.put(w, p);
        }
        // Every position respected both directions of the mapping, so the word has the same pattern shape.
        return true;
    }

    public static void main(String[] args) {
        WordsArrayAndPatternMatch solution = new WordsArrayAndPatternMatch();

        runSample(solution, new String[]{"mee", "aqq", "dkd", "ccc"}, "abb");
        runSample(solution, new String[]{"abc", "deq", "mee", "xyz"}, "abc");
        runSample(solution, new String[]{"aaa", "bbb", "aba", "ccc"}, "zzz");
    }

    private static void runSample(WordsArrayAndPatternMatch solution, String[] words, String pattern) {
        System.out.println("words = " + Arrays.toString(words) + ", pattern = \"" + pattern + "\"");
        System.out.println("bruteForce = " + solution.bruteForce(words, pattern));
        System.out.println("optimized  = " + solution.optimized(words, pattern));
        System.out.println();
    }
}
