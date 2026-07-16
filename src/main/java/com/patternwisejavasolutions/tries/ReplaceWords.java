package com.patternwisejavasolutions.tries;

public class ReplaceWords {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: dictionary = ["cat", "bat", "rat"], sentence = "the cattle was rattled"
     * Sample Output: "the cat was rat"
     *
     * Replace a word with the shortest dictionary root that is its prefix.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A root is like the starting part of a bigger word.
     * If "cat" is a root, then "cattle" can be replaced by "cat".
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * For every sentence word, check every root.
     * Pick the shortest root that matches the beginning of the word.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Split sentence into words.
     * 2. For each word, scan all roots.
     * 3. Keep the shortest matching root.
     * Time Complexity: O(words * roots * word length)
     * Space Complexity: O(words)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * word = "cattle"
     * "cat" is a prefix, so replace "cattle" with "cat".
     */
    public String bruteForce(java.util.List<String> dictionary, String sentence) {
        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++) {
            String bestRoot = words[i];
            for (String root : dictionary) {
                if (words[i].startsWith(root) && root.length() < bestRoot.length()) {
                    bestRoot = root;
                }
            }
            words[i] = bestRoot;
        }
        return String.join(" ", words);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * A trie helps walk a word character by character.
     * The first complete root we meet is automatically the shortest root
     * because we are moving from the first character toward longer prefixes.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Insert all roots into a trie.
     * 2. For each sentence word, walk through the trie.
     * 3. Stop when a root ends or a path is missing.
     * Time Complexity: O(total dictionary chars + sentence chars)
     * Space Complexity: O(total dictionary chars)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * For "rattled", walk r -> a -> t.
     * Node "rat" is a word, so replace with "rat".
     */
    public String optimized(java.util.List<String> dictionary, String sentence) {
        TrieNode root = new TrieNode();
        for (String word : dictionary) {
            insert(root, word);
        }

        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = shortestRoot(root, words[i]);
        }
        return String.join(" ", words);
    }

    private void insert(TrieNode root, String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            int index = ch - 'a';
            if (current.children[index] == null) {
                // Create a node for this prefix if no root has used it before.
                current.children[index] = new TrieNode();
            }
            // Move to the child representing the next character of the root.
            current = current.children[index];
        }
        // This node marks the end of a dictionary root.
        current.isWord = true;
    }

    private String shortestRoot(TrieNode root, String word) {
        TrieNode current = root;
        StringBuilder prefix = new StringBuilder();
        for (char ch : word.toCharArray()) {
            int index = ch - 'a';
            if (current.children[index] == null) {
                // No dictionary root matches this prefix, so keep the original word.
                return word;
            }
            prefix.append(ch);
            // Follow the matching Trie edge for this character.
            current = current.children[index];
            if (current.isWord) {
                // First root found while walking left to right is the shortest.
                return prefix.toString();
            }
        }
        return word;
    }

    private static class TrieNode {
        private TrieNode[] children = new TrieNode[26];
        private boolean isWord;
    }
}
