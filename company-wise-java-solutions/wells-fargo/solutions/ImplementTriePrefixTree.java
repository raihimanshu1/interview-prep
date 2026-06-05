package wellsfargo.solutions;

public class ImplementTriePrefixTree {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: insert("apple"), search("app"), startsWith("app")
     * Sample Output: false, true
     *
     * We need a data structure that supports:
     * insert("apple")
     * search("apple") -> true
     * search("app") -> false until "app" is inserted
     * startsWith("app") -> true
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A brute force way is to store all words in a list.
     * For search, compare the target word with every stored word.
     * For prefix, check whether any stored word starts with that prefix.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Keep every inserted word exactly as it was given in a plain list.
     * When asked a question, scan the saved words one by one.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Store every inserted word in a list.
     * 2. For search, compare every saved word with the target.
     * 3. For startsWith, check every saved word's prefix.
     * Time Complexity: O(number of words * word length)
     * Space Complexity: O(total characters stored)
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * words = ["apple"]
     * search("app") compares "app" with "apple" -> not equal -> false.
     * startsWith("app") checks "apple" starts with "app" -> true.
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     */
    public static class BruteForceTrie {
        private java.util.List<String> words = new java.util.ArrayList<>();

        public void insert(String word) {
            words.add(word);
        }

        public boolean search(String word) {
            for (String savedWord : words) {
                if (savedWord.equals(word)) {
                    return true;
                }
            }

            return false;
        }

        public boolean startsWith(String prefix) {
            for (String savedWord : words) {
                if (savedWord.startsWith(prefix)) {
                    return true;
                }
            }

            return false;
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Many words share prefixes.
     * Example: app, apple, apply all share a -> p -> p.
     * Trie stores shared prefixes once as nodes.
     * Each node has children for next letters.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Use a root TrieNode.
     * 2. Each node stores 26 possible children.
     * 3. Insert walks character by character and creates missing nodes.
     * 4. search returns true only if the final node is marked as a word.
     * 5. startsWith returns true if the prefix path exists.
     * Time Complexity: O(word length)
     * Space Complexity: O(total trie nodes)
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Insert "apple": create path a -> p -> p -> l -> e.
     * Mark e as a complete word.
     * search("app") reaches node p, but isWord is false.
     * startsWith("app") reaches node p, so it is true.
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     */
    public static class OptimizedTrie {
        private TrieNode root = new TrieNode();

        public void insert(String word) {
            TrieNode current = root;

            for (char ch : word.toCharArray()) {
                int index = ch - 'a';

                if (current.children[index] == null) {
                    // Create the node for this prefix only once, then share it.
                    current.children[index] = new TrieNode();
                }

                // Move from the current prefix to the prefix including ch.
                current = current.children[index];
            }

            // Mark the final node so search("app") differs from startsWith("app").
            current.isWord = true;
        }

        public boolean search(String word) {
            TrieNode node = findLastNode(word);
            return node != null && node.isWord;
        }

        public boolean startsWith(String prefix) {
            return findLastNode(prefix) != null;
        }

        private TrieNode findLastNode(String text) {
            TrieNode current = root;

            for (char ch : text.toCharArray()) {
                int index = ch - 'a';

                if (current.children[index] == null) {
                    // Missing edge means the word/prefix path does not exist.
                    return null;
                }

                // Follow the Trie edge for this character.
                current = current.children[index];
            }

            return current;
        }
    }

    private static class TrieNode {
        private TrieNode[] children = new TrieNode[26];
        private boolean isWord;
    }
}
