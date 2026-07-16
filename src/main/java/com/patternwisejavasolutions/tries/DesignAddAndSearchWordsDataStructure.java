package com.patternwisejavasolutions.tries;

public class DesignAddAndSearchWordsDataStructure {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: addWord("bad"), search("b.d")
     * Sample Output: true
     *
     * We need to add words and search words.
     * The search word may contain '.' which means "any one letter".
     *
     * Example:
     * addWord("bad")
     * search("bad") -> true
     * search("b.d") -> true
     * search("..d") -> true
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Store all words in a list.
     * To search a pattern, compare it with every word of the same length.
     * A normal character must match exactly.
     * A '.' can match anything.
     */
    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Store all inserted words in a list.
     * 2. For search, compare the pattern with every saved word.
     * 3. A normal character must match exactly.
     * 4. A '.' can match any one character.
     * Time Complexity: O(number of words * word length)
     * Space Complexity: O(total characters stored)
     */
    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     */
    public static class BruteForceWordDictionary {
        private java.util.List<String> words = new java.util.ArrayList<>();

        public void addWord(String word) {
            words.add(word);
        }

        public boolean search(String pattern) {
            for (String word : words) {
                if (matches(word, pattern)) {
                    return true;
                }
            }

            return false;
        }

        private boolean matches(String word, String pattern) {
            if (word.length() != pattern.length()) {
                return false;
            }

            for (int i = 0; i < word.length(); i++) {
                char patternChar = pattern.charAt(i);

                if (patternChar != '.' && patternChar != word.charAt(i)) {
                    return false;
                }
            }

            return true;
        }
    }

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Keep every added word in a list. During search, compare the pattern to
     * each saved word of the same length. A dot does not choose a specific
     * letter; it simply accepts whatever letter is in that position.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * words = ["bad"]
     * pattern = "b.d"
     * b matches b.
     * . can match a.
     * d matches d.
     * So return true.
     */

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * A Trie helps because words with the same prefix share nodes.
     * For a normal letter, move to that child.
     * For '.', try all possible children because '.' can be any letter.
     */
    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * search("b.d") starts at b.
     * Then '.' tries every child under b.
     * If one branch reaches d and ends at a word, return true.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Insert words into a Trie.
     * 2. For normal letters, follow exactly one child.
     * 3. For '.', recursively try all children.
     * 4. At the end, return true only if the node is a complete word.
     * Time Complexity: O(26^dots * word length) worst case
     * Space Complexity: O(total trie nodes)
     */
    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     */
    public static class OptimizedWordDictionary {
        private TrieNode root = new TrieNode();

        public void addWord(String word) {
            TrieNode current = root;

            for (char ch : word.toCharArray()) {
                int index = ch - 'a';

                if (current.children[index] == null) {
                    // Create the next prefix node if this path does not exist yet.
                    current.children[index] = new TrieNode();
                }

                // Move to the node for this character.
                current = current.children[index];
            }

            // Search should be true only for complete inserted words.
            current.isWord = true;
        }

        public boolean search(String word) {
            return searchFrom(root, word, 0);
        }

        private boolean searchFrom(TrieNode node, String word, int index) {
            if (node == null) {
                return false;
            }

            if (index == word.length()) {
                // Pattern is consumed; success requires a complete word ending here.
                return node.isWord;
            }

            char ch = word.charAt(index);

            if (ch != '.') {
                // Normal letter follows exactly one Trie edge.
                return searchFrom(node.children[ch - 'a'], word, index + 1);
            }

            for (TrieNode child : node.children) {
                // Dot can stand for any one existing child letter.
                if (searchFrom(child, word, index + 1)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class TrieNode {
        private TrieNode[] children = new TrieNode[26];
        private boolean isWord;
    }
}
