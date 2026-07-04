

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class CraftierStudents {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given student names and a prefix, return names that start with that prefix.
     *
     * INPUT
     * names array and prefix.
     *
     * OUTPUT
     * List of matching names.
     *
     * EXAMPLE
     * names = ["amy", "anna", "anika", "bob", "brad"], prefix = "an"
     * Output: [anna, anika]
     * 
     * Multiple names may match the same prefix, while unrelated prefixes are ignored.
     *
     * WHAT IT MEANS
     * A trie can find all words below the prefix node.
     */
    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of a phone contact search. Typing a prefix should return names that
     * start with those letters. A trie stores shared prefixes like folders in a
     * tree.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * We are given many names and one prefix. The task is to return every name
     * whose first characters exactly match the prefix.
     *
     * The simplest way to think about it is contact search:
     * typing "an" should show "anna" and "anika", but not "amy" or "bob".
     *
     * A brute force solution checks each name independently. For every name, ask:
     * "Does this name begin with the prefix?" If yes, keep it.
     *
     * The optimized trie solution is helpful when we expect many prefix queries,
     * but the brute force version is the cleanest first explanation because it
     * directly follows the wording of the problem.
     */

    /*
     * EXAMPLES TO UNDERSTAND THE PROBLEM
     *
     * Example 1 - Basic prefix
     * names = ["amy", "anna", "anika", "bob", "brad"], prefix = "an"
     * Output: [anna, anika]
     *
     * Example 2 - Prefix is a full name
     * names = ["sam", "samantha", "sameer"], prefix = "sam"
     * Output: [sam, samantha, sameer]
     * A word can match even when it is exactly equal to the prefix.
     *
     * Example 3 - No matching names
     * names = ["maya", "nina"], prefix = "zo"
     * Output: []
     *
     * Edge Case 1 - Empty prefix
     * Every string starts with "", so all names are returned.
     *
     * Edge Case 2 - Empty names array
     * There are no names to check, so the answer is empty.
     *
     * Edge Case 3 - Prefix longer than a name
     * "an" can match "anna", but "anna" cannot match the shorter name "an".
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * - This is a prefix problem, not a substring problem.
     * - The match must start at index 0 of the name.
     * - The output order follows the order in which brute force scans names.
     * - Case sensitivity follows Java String comparison; "An" and "an" are different.
     * - A trie is useful for repeated queries, but not required for the first solution.
     */

    /*
     * WHAT WE DO TO SOLVE IT
     *
     * Walk through the names one by one. For each name, compare its beginning with
     * the prefix. If it starts with the prefix, add it to the answer. Otherwise,
     * ignore it and move on.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    This is a prefix/dictionary problem: the important operation is walking character by character through a word.
     *
     * 2. What data structure does that naturally suggest?
     *    Use a trie because shared prefixes should be stored once instead of searched repeatedly.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: compare every query against every dictionary word.
     *
     * 4. What repeated work should I remove?
     *    Optimized: insert words into a trie and walk the query directly through prefix nodes.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: compare every query against every dictionary word.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Create an empty result list.
     * 2. Visit every name in the input array.
     * 3. Check whether the name starts with prefix.
     * 4. If yes, append it to the result.
     * 5. Return the result after all names are checked.
     * 
     * Time Complexity: O(n * p), where n is the number of names and p is the prefix length.
     * Space Complexity: O(m), where m is the number of matching names in the answer.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Sample: names = ["amy", "anna", "anika", "bob", "brad"], prefix = "an" Output: [anna, anika] Multiple names may match the same prefix, while unrelated prefixes are ignored.
     * Walk the records one by one and the expected result above is produced.
     */
    public List<String> bruteForce(String[] names, String prefix) {

        List<String> result = new ArrayList<>();

        for (String name : names) {
            // Brute force checks every name directly instead of building a prefix tree.
            // This is exactly how a person would scan a short contact list.
            if (name.startsWith(prefix)) {
                // Keep only names whose first characters match the whole prefix.
                result.add(name);
            }
        }

        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: insert words into a trie and walk the query directly through prefix nodes.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find names matching a prefix.
     * 2. Remove repeated work: insert names into a trie, walk prefix once, then collect below that node.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: names = ["amy", "anna", "anika", "bob", "brad"], prefix = "an" Output: [anna, anika] Multiple names may match the same prefix, while unrelated prefixes are ignored.
     * Walk the records one by one and the expected result above is produced.
     */
    public List<String> optimized(String[] names, String prefix) {
        TrieNode root = new TrieNode();
        for (String name : names) {
            insert(root, name);
        }

        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            int index = ch - 'a';
        if (index < 0 || index >= 26 || node.children[index] == null) {
                return new ArrayList<>();
            }
            node = node.children[index];
        }
        List<String> result = new ArrayList<>();
        collect(node, new StringBuilder(prefix), result);
        return result;
    }

    private void insert(TrieNode root, String word) {
        // Start at the root because every word begins before its first character.
        TrieNode node = root;
        // Insert the word one character at a time so shared prefixes reuse the
        // same trie nodes instead of storing duplicate prefix strings.
        for (char ch : word.toCharArray()) {
            // Convert 'a'..'z' into 0..25, matching the fixed children array.
            int index = ch - 'a';
        // If this prefix path does not exist yet, create the next node so the
        // word has somewhere to continue.
        if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            // Move down one level; the next character extends this prefix.
            node = node.children[index];
        }
        // Mark the final node as a complete name so collection can return this
        // exact word, not only longer words that share it as a prefix.
        node.isWord = true;
    }

    private void collect(TrieNode node, StringBuilder path, List<String> result) {
        // If this trie node marks a full word, the current path is one valid
        // name that starts with the prefix the caller already walked.
        if (node.isWord) {
            result.add(path.toString());
        }
        // Explore children in alphabetic order because index 0 is 'a', index 1
        // is 'b', and so on.
        for (int i = 0; i < 26; i++) {
        // A null child means no stored name continues with that next letter.
        if (node.children[i] != null) {
                // Add the chosen next letter to the path before descending.
                path.append((char) ('a' + i));
                // Collect every complete name below that child prefix.
                collect(node.children[i], path, result);
                // Remove the letter after returning so the next sibling starts
                // from the original prefix, which is standard backtracking.
                path.deleteCharAt(path.length() - 1);
            }
        }
    }

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    public static void main(String[] args) {
        CraftierStudents solution = new CraftierStudents();

        runSample(solution, new String[]{"amy", "anna", "anika", "bob", "brad"}, "an");
        runSample(solution, new String[]{"sam", "samantha", "sameer"}, "sam");
        runSample(solution, new String[]{"maya", "nina"}, "zo");
    }

    private static void runSample(CraftierStudents solution, String[] names, String prefix) {
        System.out.println("names = " + Arrays.toString(names) + ", prefix = \"" + prefix + "\"");
        System.out.println("bruteForce = " + solution.bruteForce(names, prefix));
        System.out.println("optimized  = " + solution.optimized(names, prefix));
        System.out.println();
    }
}
