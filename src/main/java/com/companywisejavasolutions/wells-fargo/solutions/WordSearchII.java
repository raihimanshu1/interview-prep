package com.companywisejavasolutions.wellsfargo.solutions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordSearchII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: board = [[o,a,a,n],[e,t,a,e],[i,h,k,r],[i,f,l,v]], words = ["oath","pea","eat","rain"]
     * Sample Output: ["oath","eat"]
     *
     * Input board has letters and words = ["oath", "pea", "eat", "rain"].
     * Output: ["oath", "eat"] if those words can be formed on the board.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The simple way is to solve Word Search separately for every word.
     * This is easy to understand but repeats the same board exploration many times.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Take one word at a time and run normal Word Search for it. For each word,
     * start from every board cell and walk up, down, left, and right while
     * matching the next character. This repeats the same prefixes for many words.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * Check "oath" on board -> found.
     * Check "pea" -> not found.
     * Check "eat" -> found.
     * Answer = ["oath", "eat"].
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. For each word, run DFS word search.
     * 2. If found, add it to answer.
     * Time Complexity: O(words * mn * 4^L)
     * Space Complexity: O(L) recursion
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This solves each word independently, so shared prefixes are not reused.
     */
    public List<String> bruteForce(char[][] board, String[] words) {
        List<String> answer = new ArrayList<>();

        for (String word : words) {
            if (exists(board, word)) {
                answer.add(word);
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Put all words into a Trie first.
     * Then while walking the board, we walk the Trie at the same time.
     * If the current path is not a prefix of any word, stop immediately. Prefix
     * sharing means "oa" is explored once for words like "oath" and "oat".
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Path starts with 'x'.
     * If Trie root has no child 'x', no word starts with x.
     * We stop that path immediately instead of exploring deeper.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Build Trie from all words.
     * 2. Start DFS from every board cell.
     * 3. Move only if the character exists in the Trie.
     * 4. When a Trie node has a word, add it to answer.
     * 5. Mark board cell used, explore, then restore.
     * Time Complexity: roughly O(mn * 4^L), heavily pruned by Trie
     * Space Complexity: O(total letters in words)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Board DFS and Trie movement happen together.
     */
    public List<String> optimized(char[][] board, String[] words) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            insert(root, word);
        }

        Set<String> found = new HashSet<>();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                dfs(board, row, col, root, found);
            }
        }

        return new ArrayList<>(found);
    }


    private boolean exists(char[][] board, String word) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (search(board, word, row, col, 0)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean search(char[][] board, String word, int row, int col, int index) {
        if (index == word.length()) {
            // Matched every character of this one word.
            return true;
        }

        if (row < 0 || row == board.length || col < 0 || col == board[0].length || board[row][col] != word.charAt(index)) {
            return false;
        }

        char original = board[row][col];
        // Choose this cell and mark it so this path cannot reuse it.
        board[row][col] = '#';

        boolean found = search(board, word, row + 1, col, index + 1)
            || search(board, word, row - 1, col, index + 1)
            || search(board, word, row, col + 1, index + 1)
            || search(board, word, row, col - 1, index + 1);

        // Undo the cell mark before trying a different path or word.
        board[row][col] = original;
        return found;
    }

    private void insert(TrieNode root, String word) {
        TrieNode current = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';

            if (current.children[index] == null) {
                // Create the next prefix node only when it does not already exist.
                current.children[index] = new TrieNode();
            }

            // Move down the Trie to the node for this character.
            current = current.children[index];
        }

        // Store the full word at its ending node so DFS can report it.
        current.word = word;
    }

    private void dfs(char[][] board, int row, int col, TrieNode node, Set<String> found) {
        if (row < 0 || row == board.length || col < 0 || col == board[0].length) {
            return;
        }

        char ch = board[row][col];

        if (ch == '#' || node.children[ch - 'a'] == null) {
            // No dictionary word has the board path built so far as a prefix.
            return;
        }

        // Move from the prefix so far to the prefix including board[row][col].
        TrieNode next = node.children[ch - 'a'];

        if (next.word != null) {
            // This prefix is a complete dictionary word.
            found.add(next.word);
        }

        // Choose this cell, explore neighbors, then restore it.
        board[row][col] = '#';
        dfs(board, row + 1, col, next, found);
        dfs(board, row - 1, col, next, found);
        dfs(board, row, col + 1, next, found);
        dfs(board, row, col - 1, next, found);
        board[row][col] = ch;
    }

    private static class TrieNode {
        private TrieNode[] children = new TrieNode[26];
        private String word;
    }
}
