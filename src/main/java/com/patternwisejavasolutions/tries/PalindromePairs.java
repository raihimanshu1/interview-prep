
package com.patternwisejavasolutions.tries;
public class PalindromePairs {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: words = ["bat", "tab", "cat"]
     * Sample Output: [[0, 1], [1, 0]]
     *
     * Return index pairs where words[i] + words[j] becomes a palindrome.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A palindrome reads the same forward and backward.
     * "bat" + "tab" = "battab", which is a palindrome.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try every ordered pair of different words.
     * Join them and check whether the result is a palindrome.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For every i and j, skip when i == j.
     * 2. Concatenate words[i] + words[j].
     * 3. If palindrome, add the pair.
     * Time Complexity: O(n^2 * k)
     * Space Complexity: O(1) besides output
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * i = "bat", j = "tab"
     * "battab" reads the same both ways, so add [0, 1].
     */
    public java.util.List<java.util.List<Integer>> bruteForce(String[] words) {
        java.util.List<java.util.List<Integer>> answer = new java.util.ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words.length; j++) {
                if (i != j && isPalindrome(words[i] + words[j], 0, words[i].length() + words[j].length() - 1)) {
                    answer.add(java.util.Arrays.asList(i, j));
                }
            }
        }
        return answer;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Split each word into left and right parts.
     * If one side is already a palindrome, we only need the reverse of the other
     * side from the word list. This is not a Trie solution, but it uses the same
     * "avoid trying every pair" idea by doing direct lookup for the only word
     * that could complete the palindrome.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Map every word to its index.
     * 2. For each word, try every split.
     * 3. If left is palindrome, look for reverse(right) before it.
     * 4. If right is palindrome, look for reverse(left) after it.
     * Time Complexity: O(n * k^2)
     * Space Complexity: O(n * k)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * word = "bat"
     * Split after all letters: left "bat", right "".
     * right is palindrome, reverse(left) = "tab", so add [bat, tab].
     */
    public java.util.List<java.util.List<Integer>> optimized(String[] words) {
        java.util.Map<String, Integer> indexByWord = new java.util.HashMap<>();
        for (int i = 0; i < words.length; i++) {
            // Map lets us ask, "does the exact reversed partner exist?"
            indexByWord.put(words[i], i);
        }

        java.util.List<java.util.List<Integer>> answer = new java.util.ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            for (int split = 0; split <= word.length(); split++) {
                String left = word.substring(0, split);
                String right = word.substring(split);

                if (isPalindrome(left, 0, left.length() - 1)) {
                    // If left is already safe, reverse(right) can be placed before word.
                    Integer other = indexByWord.get(reverse(right));
                    if (other != null && other != i) {
                        answer.add(java.util.Arrays.asList(other, i));
                    }
                }

                if (split != word.length() && isPalindrome(right, 0, right.length() - 1)) {
                    // If right is already safe, reverse(left) can be placed after word.
                    Integer other = indexByWord.get(reverse(left));
                    if (other != null && other != i) {
                        answer.add(java.util.Arrays.asList(i, other));
                    }
                }
            }
        }

        return answer;
    }

    private boolean isPalindrome(String text, int left, int right) {
        while (left < right) {
            if (text.charAt(left) != text.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    private String reverse(String text) {
        return new StringBuilder(text).reverse().toString();
    }
}
