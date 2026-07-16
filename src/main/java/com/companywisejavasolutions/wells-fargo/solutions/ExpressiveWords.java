package com.companywisejavasolutions.wellsfargo.solutions;

public class ExpressiveWords {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a target string s and a list of words, count how many words can be
     * stretched to become s.
     *
     * INPUT
     * s is the target string.
     * words contains candidate words.
     *
     * OUTPUT
     * Number of candidate words that are stretchy versions of s.
     *
     * EXAMPLE
     * s = "heeellooo"
     * words = ["hello", "hi", "helo"]
     * Output: 1
     *
     * "hello" can stretch e -> eee and o -> ooo.
     * "hi" has different letters.
     * "helo" does not have the ll group needed by the target.
     *
     * WHAT IT MEANS
     *
     * A repeated group in s can absorb fewer letters from the word only when
     * that group in s has length at least 3.
     */

    /*
     * MORE INPUTS TO PRACTICE
     *
     * Example 1 - Stretch works
     * s = "heeellooo", word = "hello" -> true
     *
     * Example 2 - Exact match
     * s = "abc", word = "abc" -> true
     *
     * Example 3 - Target group too short
     * s = "abbc", word = "abc" -> false
     * Why: bb has length 2, so it cannot be considered a stretchy group.
     *
     * Example 4 - Word has too many letters
     * s = "heeellooo", word = "heeelloooo" -> false
     *
     * Edge case 1 - Empty words array returns 0.
     * Edge case 2 - Different group characters immediately fail.
     */

    /*
     * BRUTE FORCE APPROACH
     *
     * Compare each word to s with recursion.
     * When characters match, we can move normally.
     * If s has a long repeated group, we also allow the word to use fewer
     * copies of that same character.
     *
     * Time Complexity: O(total characters across words)
     * Space Complexity: O(group recursion depth)
     */
    public int bruteForce(String s, String[] words) {
        int count = 0;

        for (String word : words) {
            // Each word is checked independently because one word's stretching
            // choices do not affect another word.
            if (canStretchRecursively(s, 0, word, 0)) {
                count++;
            }
        }

        return count;
    }

    private boolean canStretchRecursively(String target, int targetIndex, String word, int wordIndex) {
        if (targetIndex == target.length() || wordIndex == word.length()) {
            // Both strings must finish together; otherwise one has extra groups.
            return targetIndex == target.length() && wordIndex == word.length();
        }

        if (target.charAt(targetIndex) != word.charAt(wordIndex)) {
            return false;
        }

        int targetEnd = endOfGroup(target, targetIndex);
        int wordEnd = endOfGroup(word, wordIndex);

        // These counts are the real unit of the problem.
        // We compare "eee" with "e", not just one character at a time.
        int targetCount = targetEnd - targetIndex;
        int wordCount = wordEnd - wordIndex;

        if (targetCount == wordCount) {
            // Same group size means no stretching needed for this group.
            return canStretchRecursively(target, targetEnd, word, wordEnd);
        }

        if (targetCount >= 3 && wordCount < targetCount) {
            // A target group of size 3 or more may absorb a smaller word group.
            return canStretchRecursively(target, targetEnd, word, wordEnd);
        }

        return false;
    }

    /*
     * OPTIMIZED APPROACH
     *
     * The repeated work in brute force is thinking character by character.
     * The better habit is to compare groups directly: character plus count.
     *
     * Time Complexity: O(total characters across words)
     * Space Complexity: O(1)
     */
    public int optimized(String s, String[] words) {
        int count = 0;

        for (String word : words) {
            if (isStretchy(s, word)) {
                count++;
            }
        }

        return count;
    }

    private boolean isStretchy(String target, String word) {
        int targetIndex = 0;
        int wordIndex = 0;

        while (targetIndex < target.length() && wordIndex < word.length()) {
            if (target.charAt(targetIndex) != word.charAt(wordIndex)) {
                return false;
            }

            int targetEnd = endOfGroup(target, targetIndex);
            int wordEnd = endOfGroup(word, wordIndex);
            int targetCount = targetEnd - targetIndex;
            int wordCount = wordEnd - wordIndex;

            if (wordCount > targetCount) {
                // We can stretch the word upward, but we cannot shrink it.
                return false;
            }

            if (targetCount != wordCount && targetCount < 3) {
                // A short target group like "ee" is not allowed to absorb "e".
                return false;
            }

            // Move to the next group in both strings.
            targetIndex = targetEnd;
            wordIndex = wordEnd;
        }

        return targetIndex == target.length() && wordIndex == word.length();
    }

    private int endOfGroup(String text, int start) {
        int end = start;

        while (end < text.length() && text.charAt(end) == text.charAt(start)) {
            end++;
        }

        return end;
    }

    public static void main(String[] args) {
        ExpressiveWords solver = new ExpressiveWords();
        System.out.println(solver.optimized("heeellooo", new String[]{"hello", "hi", "helo"}));
        System.out.println(solver.optimized("abc", new String[]{"abc", "abbc"}));
        System.out.println(solver.bruteForce("abbc", new String[]{"abc"}));
    }
}
