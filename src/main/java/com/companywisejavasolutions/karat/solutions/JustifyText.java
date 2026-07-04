

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class JustifyText {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given words and a max line width, format text so each line has exactly maxWidth characters with spaces distributed evenly.
     *
     * INPUT
     * words array and maxWidth.
     *
     * OUTPUT
     * List of justified lines.
     *
     * EXAMPLE
     * words = ["This", "is", "more", "text"], maxWidth = 12
     * Output: ["This      is", "more text   "]
     * 
     * The first line distributes extra spaces; the last line is left-justified.
     *
     * WHAT IT MEANS
     * Greedily pack words into each line, then distribute spaces.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * Think of writing words into boxes where every row has exactly maxWidth
     * spaces available.
     *
     * First decide which words belong on the current row. That part is greedy:
     * keep adding the next word while it still fits with at least one separating
     * space.
     *
     * After the group is chosen, then decide how to place spaces. Normal lines
     * stretch spaces across the gaps. The last line stays natural and gets extra
     * spaces at the end.
     *
     * Example 1 - Two words on the first line
     *
     * words = {"This", "is", "more", "text"}, maxWidth = 12
     *
     * Output:
     * {"This      is", "more text   "}
     *
     * Why:
     * "This is more" is too long for width 12, so the first line is "This" and
     * "is". The final line is left-justified.
     *
     * Example 2 - Several words with uneven extra spaces
     *
     * words = {"What", "must", "be", "done"}, maxWidth = 14
     *
     * Output:
     * {"What  must be", "done          "}
     *
     * Why:
     * The first line has 10 letters and two gaps, so 4 spaces are split as
     * 2 spaces and 2 spaces.
     *
     * Example 3 - Extra space goes to the left gaps first
     *
     * words = {"A", "quick", "brown", "fox"}, maxWidth = 16
     *
     * Output:
     * {"A  quick brown", "fox             "}
     *
     * Why:
     * The first line needs 3 total spaces across 2 gaps. The left gap receives
     * the extra one.
     *
     * Edge case 1 - One word on a non-last line
     *
     * If only one word fits on a line, put the word first and pad the right side.
     *
     * Edge case 2 - Empty input
     *
     * If words is empty, the result is an empty list.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Choosing words for a line and spacing that line are two separate jobs.
     * 2. A candidate next word needs one leading space before it can fit.
     * 3. The last line is special: normal single spaces between words, then pad.
     * 4. A one-word line is also special because there are no gaps to stretch.
     * 5. For non-last multi-word lines, leftover spaces go to earlier gaps first.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Walk through the words from left to right.
     * For each line, find the farthest word that still fits.
     * Once the range is known, build exactly one string for that line.
     * Then continue from the next unused word.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of packing words into a fixed-width line. First decide which words
     * fit, then decide how spaces should be spread between them.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    This is a line-packing problem: words must be grouped before spaces can be distributed.
     *
     * 2. What data structure does that naturally suggest?
     *    Use a current line list because we need to know which words fit together.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: build lines one word at a time and adjust spaces after the line is known.
     *
     * 4. What repeated work should I remove?
     *    Optimized: keep running character count so we know exactly when the next word no longer fits.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: build lines one word at a time and adjust spaces after the line is known.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Start a line at the current word.
     * 2. Keep adding words while they still fit maxWidth.
     * 3. Once the line is full, distribute spaces for that one line.
     * 4. If the line is last or has one word, left-justify it.
     * 5. Otherwise, split spaces evenly and give leftovers to left gaps first.
     * 6. Continue from the next unused word.
     * 
     * Time Complexity: O(total characters) because each word is packed into exactly one line.
     * Space Complexity: O(number of lines * maxWidth) for the built result strings.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Sample: words = ["This", "is", "more", "text"], maxWidth = 12 Output: ["This      is", "more text   "] The first line distributes extra spaces; the last line is left-justified.
     * Walk the records one by one and the expected result above is produced.
     */
    public List<String> bruteForce(String[] words, int maxWidth) {
        // Each result string will already have exactly maxWidth characters.
        List<String> result = new ArrayList<>();

        // index is the first word not yet placed on a line.
        int index = 0;
        while (index < words.length) {

            // Start a new line with one required word.
            int lineLength = words[index].length();
            int last = index + 1;

            // Keep taking words while the next word plus one separating space fits.
            while (last < words.length && lineLength + 1 + words[last].length() <= maxWidth) {
                lineLength += 1 + words[last].length();
                last++;
            }

            // Now words[index ... last - 1] is the complete line.
            // buildLine handles the spacing rules for normal and last lines.
            result.add(buildLine(words, index, last, lineLength, maxWidth, last == words.length));

            // Move to the first word that did not fit on this line.
            index = last;
        }
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: keep running character count so we know exactly when the next word no longer fits.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Maintain the running line length while adding words.
     * 2. Build each completed line exactly once.
     * 3. Distribute spaces using the known number of gaps.
     * 4. Left-justify the final line.
     * 
     * Time Complexity: Lower than brute force because repeated scanning is replaced with stored state.
     * Space Complexity: O(number of lines * maxWidth) for justified output.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: words = ["This", "is", "more", "text"], maxWidth = 12 Output: ["This      is", "more text   "] The first line distributes extra spaces; the last line is left-justified.
     * Walk the records one by one and the expected result above is produced.
     */
    public List<String> optimized(String[] words, int maxWidth) {
        if (words.length == 0) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        int index = 0;
        while (index < words.length) {
            int lineLength = words[index].length();
            int last = index + 1;
            while (last < words.length && lineLength + 1 + words[last].length() <= maxWidth) {
                lineLength += 1 + words[last].length();
                last++;
            }

            result.add(buildLine(words, index, last, lineLength, maxWidth, last == words.length));
            index = last;
        }
        return result;
    }

    private String buildLine(String[] words, int start, int end, int lineLength, int maxWidth, boolean isLast) {
        // Build exactly one output line for words[start] through words[end - 1].
        // The caller has already chosen this range greedily, so this helper's
        // only job is applying the spacing rules.
        StringBuilder line = new StringBuilder();

        // Gaps are the spaces between neighboring words. Three words have two
        // gaps; one word has zero gaps, which needs special handling.
        int gaps = end - start - 1;

        // Last lines and one-word lines are left-justified by the problem rules:
        // normal single spaces between words, then padding on the right.
        if (gaps == 0 || isLast) {
            // Append the chosen words in order with one natural space between them.
            for (int i = start; i < end; i++) {
                if (i > start) {
                    // Add the separator only before words after the first one.
                    line.append(' ');
                }
                line.append(words[i]);
            }

            // Pad the right side until the line reaches the required fixed width.
            // This is what makes the returned string exactly maxWidth characters.
            while (line.length() < maxWidth) {
                line.append(' ');
            }

            // The left-justified line is complete.
            return line.toString();
        }

        // For a fully-justified line, lineLength currently includes one minimum
        // space per gap. Remove those gap spaces to recover the number of actual
        // word characters.
        int wordChars = lineLength - gaps;

        // All remaining width must be spaces distributed across the gaps.
        int totalSpaces = maxWidth - wordChars;

        // Every gap receives this many spaces at minimum.
        int spacesEach = totalSpaces / gaps;

        // Any leftover spaces are assigned to earlier gaps, matching the usual
        // text justification rule and the examples from the problem.
        int extra = totalSpaces % gaps;

        // Append each word, placing its computed spaces before every word after
        // the first. This keeps words in order while stretching the line.
        for (int i = start; i < end; i++) {
            if (i > start) {
                // The current gap gets the base space count plus one extra space
                // while extras remain. The post-decrement spends extras from left
                // to right.
                for (int s = 0; s < spacesEach + (extra-- > 0 ? 1 : 0); s++) {
                    line.append(' ');
                }
            }
            line.append(words[i]);
        }

        // A non-last multi-word line should already be exactly maxWidth because
        // all required spaces were distributed across its gaps.
        return line.toString();
    }

    public static void main(String[] args) {
        JustifyText solver = new JustifyText();

        String[][] wordSamples = {
                {"This", "is", "more", "text"},
                {"What", "must", "be", "done"},
                {"A", "quick", "brown", "fox"}
        };
        int[] widths = {12, 14, 16};

        for (int i = 0; i < wordSamples.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solver.bruteForce(copyWords(wordSamples[i]), widths[i]));
            System.out.println("optimized:  " + solver.optimized(copyWords(wordSamples[i]), widths[i]));
            System.out.println();
        }
    }

    private static String[] copyWords(String[] words) {
        String[] copy = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            copy[i] = words[i];
        }
        return copy;
    }
}
