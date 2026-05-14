import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WordLadder {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input beginWord = "hit", endWord = "cog".
     * Change one letter at a time, every intermediate word must be in wordList.
     * Return length of shortest transformation.
     *
     * Sample Input:
     * beginWord = "hit", endWord = "cog",
     * wordList = ["hot","dot","dog","lot","log","cog"]
     *
     * Sample Output:
     * 5
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Each word is a graph node.
     * There is an edge between two words if they differ by one letter.
     * Since every word change costs exactly one step, shortest transformation means BFS by levels.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Build the word graph in the most literal way: compare every pair of words to see if they differ by one letter.
     * After that, run BFS from the begin word to find the shortest chain.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * hit -> hot -> dot -> dog -> cog
     * This path has 5 words.
     * BFS finds it level by level.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build all word connections by comparing every pair.
     * 2. BFS from beginWord to endWord.
     * Time Complexity: O(N^2 * L)
     * Space Complexity: O(N^2)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(String beginWord, String endWord, List<String> wordList) {
        List<String> words = new ArrayList<>(wordList);

        if (!words.contains(endWord)) {
            return 0;
        }

        if (!words.contains(beginWord)) {
            words.add(beginWord);
        }

        Map<String, List<String>> graph = new HashMap<>();

        for (String word : words) {
            graph.put(word, new ArrayList<>());
        }

        for (int i = 0; i < words.size(); i++) {
            for (int j = i + 1; j < words.size(); j++) {
                String first = words.get(i);
                String second = words.get(j);

                if (differsByOneLetter(first, second)) {
                    // If two words differ by one letter, BFS can move between them in one step.
                    graph.get(first).add(second);
                    graph.get(second).add(first);
                }
            }
        }

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(beginWord);
        visited.add(beginWord);
        int length = 1;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                String word = queue.poll();

                if (word.equals(endWord)) {
                    return length;
                }

                for (String next : graph.get(word)) {
                    if (visited.add(next)) {
                        queue.offer(next);
                    }
                }
            }

            length++;
        }

        return 0;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain is comparing every pair of words before the search even starts.
     * Instead, when BFS is at one word, generate only possible one-letter changes.
     * A generated word in the dictionary is a real neighbor; anything else can be ignored immediately.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * From hit:
     * Change first letter: ait, bit, ... none useful.
     * Change second letter: hot is in set, so enqueue hot.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Put wordList in a set.
     * 2. BFS from beginWord.
     * 3. For each word, try changing each position to a-z.
     * 4. If generated word exists, enqueue and remove from set.
     * Time Complexity: O(N * L * 26)
     * Space Complexity: O(N)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(String beginWord, String endWord, List<String> wordList) {
        Set<String> words = new HashSet<>(wordList);

        if (!words.contains(endWord)) {
            return 0;
        }

        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        int length = 1;

        while (!queue.isEmpty()) {
            int size = queue.size(); // All words in this level have the same chain length.

            for (int i = 0; i < size; i++) {
                String word = queue.poll();

                if (word.equals(endWord)) {
                    return length;
                }

                char[] chars = word.toCharArray();

                for (int pos = 0; pos < chars.length; pos++) {
                    char original = chars[pos];

                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        chars[pos] = ch;
                        String next = new String(chars);

                        if (words.remove(next)) {
                            // Removing acts as visited, so this word is reached by its shortest chain.
                            queue.offer(next);
                        }
                    }

                    chars[pos] = original;
                }
            }

            length++;
        }

        return 0;
    }

    private boolean differsByOneLetter(String first, String second) {
        if (first.length() != second.length()) {
            return false;
        }

        int differences = 0;

        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) != second.charAt(i)) {
                differences++;
            }

            if (differences > 1) {
                return false;
            }
        }

        return differences == 1;
    }
}
