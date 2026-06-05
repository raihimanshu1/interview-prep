package karat.solutions;

import java.util.*;

public class CompleteString {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a pattern with ? wildcards and a candidate string, determine if the candidate completes the pattern.
     *
     * INPUT
     * pattern and candidate.
     *
     * OUTPUT
     * true if every non-wildcard character matches.
     *
     * EXAMPLE
     * pattern = "c?t", candidate = "cat"
     * Output: true
     * 
     * The question mark can match exactly one character, but fixed letters must match.
     *
     * WHAT IT MEANS
     * ? can match any one character; normal letters must match exactly.
     */
    /*
     * IN-DEPTH EXPLANATION
     *
     * Think of the pattern as a word with blanks in it. Normal letters are fixed:
     * if the pattern says 'c', the candidate must also have 'c' in that same
     * position. A question mark is a one-character blank, so it can stand for
     * whatever character the candidate has at that position.
     *
     * The important detail is "same position." We are not rearranging letters,
     * choosing from a bag, or matching a variable-length wildcard. Each pattern
     * character is paired with exactly one candidate character.
     *
     * What to know before solving:
     *
     * 1. The pattern and candidate must have the same length.
     * 2. '?' matches exactly one character.
     * 3. Any non-'?' character must match the candidate character exactly.
     * 4. The answer is false as soon as one fixed character disagrees.
     *
     * What we do to solve:
     *
     * The brute-force version builds the completed string described by the
     * pattern. For fixed letters, it copies the pattern letter. For '?', it
     * borrows the candidate character at that same index. At the end, it compares
     * the built string with the candidate.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - One blank in the middle
     *
     * pattern = "c?t"
     * candidate = "cat"
     *
     * Output:
     * true
     *
     * Why:
     * 'c' matches 'c', '?' can become 'a', and 't' matches 't'.
     *
     * Example 2 - Fixed letter mismatch
     *
     * pattern = "c?t"
     * candidate = "car"
     *
     * Output:
     * false
     *
     * Why:
     * The last pattern character is 't', but the candidate has 'r'.
     *
     * Example 3 - All wildcards
     *
     * pattern = "???"
     * candidate = "dog"
     *
     * Output:
     * true
     *
     * Why:
     * Each '?' can match the candidate character at the same position.
     *
     * Edge case 1 - Different lengths
     *
     * pattern = "a?"
     * candidate = "abc"
     *
     * Output:
     * false
     *
     * Why:
     * A question mark matches one character, not any number of characters.
     *
     * Edge case 2 - Empty strings
     *
     * pattern = ""
     * candidate = ""
     *
     * Output:
     * true
     *
     * Why:
     * There are no fixed characters to violate.
    */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A '?' matches exactly one character.
     * 2. A normal letter must match the same letter in the candidate.
     * 3. If the lengths differ, the answer is false before any deeper check.
     * 4. Empty pattern and empty candidate are a valid match.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Build the string that the pattern would represent for this candidate.
     * At each index, copy the candidate character when the pattern has '?'.
     * Otherwise, copy the fixed pattern letter.
     * At the end, compare the built string with the candidate.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. If the lengths differ, return false immediately.
     * 2. Start at index 0 with an empty built string.
     * 3. If the pattern character is '?', append the candidate character at that index.
     * 4. Otherwise, append the fixed pattern character.
     * 5. Recursively move to the next index.
     * 6. When the pattern is fully processed, compare the built string with the candidate.
     *
     * Time Complexity: O(n), where n is the pattern length.
     * Space Complexity: O(n) for the built string and recursion stack.
     */
    public boolean bruteForce(String pattern, String candidate) {

        // A one-character wildcard cannot repair a length mismatch.
        if (pattern.length() != candidate.length()) {
            return false;
        }
        return bruteForceBuild(pattern, candidate, 0, new StringBuilder());
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: pre-count available characters once and reuse those counts for each candidate check.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: validate candidate against a pattern with wildcards.
     * 2. Remove repeated work: walk pattern and candidate directly without building extra strings.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: pattern = "c?t", candidate = "cat" Output: true The question mark can match exactly one character, but fixed letters must match.
     * Walk the records one by one and the expected result above is produced.
     */
    public boolean optimized(String pattern, String candidate) {
        if (pattern.length() != candidate.length()) {
            return false;
        }
        for (int i = 0; i < pattern.length(); i++) {
            char expected = pattern.charAt(i);
        if (expected != '?' && expected != candidate.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean bruteForceBuild(String pattern, String candidate, int index, StringBuilder built) {
        // Base case: once every pattern position has been processed, we have built
        // the one possible completed version of the pattern for this candidate.
        // The problem asks whether the candidate completes the pattern, so the
        // final check is a direct string comparison.
        if (index == pattern.length()) {
            return built.toString().equals(candidate);
        }

        // Look at the pattern character for the current position because matching
        // is index-by-index: pattern[0] compares with candidate[0], pattern[1]
        // compares with candidate[1], and so on.
        char current = pattern.charAt(index);
        if (current == '?') {
            // A '?' can stand for exactly one character, and the only useful
            // character to try here is the candidate character at the same index.
            // This mirrors the problem rule that wildcards are one-position blanks,
            // not variable-length gaps.
            built.append(candidate.charAt(index));
        } else {
            // A fixed pattern letter has no choice: it must stay as itself.
            // If it differs from the candidate at this position, the completed
            // string will fail the final equality check.
            built.append(current);
        }

        // Move to the next position with the partial completed string we have so far.
        boolean answer = bruteForceBuild(pattern, candidate, index + 1, built);

        // Backtrack the character we appended for this position. There is only
        // one branch today, but this keeps the helper locally clean and makes the
        // recursion pattern easy to explain in an interview.
        built.deleteCharAt(built.length() - 1);

        // Return the result found after completing the rest of the pattern.
        return answer;
    }

    public static void main(String[] args) {
        CompleteString solution = new CompleteString();

        runSample(solution, "c?t", "cat");
        runSample(solution, "c?t", "car");
        runSample(solution, "???", "dog");
    }

    private static void runSample(CompleteString solution, String pattern, String candidate) {
        System.out.println("pattern = \"" + pattern + "\", candidate = \"" + candidate + "\"");
        System.out.println("bruteForce = " + solution.bruteForce(pattern, candidate));
        System.out.println("optimized  = " + solution.optimized(pattern, candidate));
        System.out.println();
    }
}
