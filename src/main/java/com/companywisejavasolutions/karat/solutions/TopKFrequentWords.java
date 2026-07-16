package com.companywisejavasolutions.karat.solutions;

import java.util.*;

public class TopKFrequentWords {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given words and k, return the k most frequent words. Ties are ordered lexicographically.
     *
     * INPUT
     * words array and k.
     *
     * OUTPUT
     * List of k words.
     *
     * EXAMPLE
     * words = ["i", "love", "i", "love", "coding", "love"], k = 2
     * Output: [love, i]
     * 
     * love appears 3 times and i appears 2 times.
     *
     * WHAT IT MEANS
     * Count words, then rank by frequency descending and word ascending.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of counting votes for words. After counting, the winners are the
     * highest counts, with alphabetical order breaking ties.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    We need rank by count, with a tie rule, so counting comes before sorting/ranking.
     *
     * 2. What data structure does that naturally suggest?
     *    Use word -> frequency map because each word's count is updated many times.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: count each distinct word by scanning the whole list repeatedly.
     *
     * 4. What repeated work should I remove?
     *    Optimized: count once with a map, then sort entries or use a heap for top K.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * This problem is like counting votes, except the candidates are words.
     *
     * First, each word receives one vote every time it appears. After counting,
     * we rank the words:
     *
     * 1. Higher frequency comes first.
     * 2. If two words have the same frequency, alphabetical order comes first.
     *
     * Brute force makes the counting step deliberately simple to see. It first
     * creates a list of distinct words. Then, for each distinct word, it scans
     * the entire original array to count how many times that one word appears.
     *
     * This is repetitive, but it is very human: pick a word, count it by walking
     * the list, write down the total, then move to the next word.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - Clear frequency ranking
     * words = ["i", "love", "i", "love", "coding", "love"], k = 2
     * love appears 3 times, i appears 2 times, coding appears 1 time.
     * Output: [love, i]
     *
     * Example 2 - Tie broken alphabetically
     * words = ["banana", "apple", "banana", "apple", "carrot"], k = 2
     * banana and apple both appear twice.
     * Alphabetically, apple comes before banana.
     * Output: [apple, banana]
     *
     * Example 3 - k includes every unique word
     * words = ["dog", "cat", "dog", "bird"], k = 3
     * dog appears twice; bird and cat appear once.
     * Alphabetical tie between bird and cat gives bird before cat.
     * Output: [dog, bird, cat]
     *
     * Edge Case 1 - k is larger than unique words
     * words = ["a", "a", "b"], k = 5
     * Only two unique words exist, so return both.
     * Output: [a, b]
     *
     * Edge Case 2 - Empty input
     * words = [], k = 3
     * There is nothing to count.
     * Output: []
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. "Top" means most frequent, not first seen.
     * 2. The tie breaker matters: alphabetical ascending order.
     * 3. We should return at most k words.
     * 4. Brute force can be slower as long as it expresses the rules clearly.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * We first collect unique words so we do not count the same word twice.
     * Then we count each unique word by scanning the full input.
     *
     * After the counts are known, sorting becomes straightforward:
     * higher count first,
     * alphabetical order if counts are equal.
     *
     * The first k words after sorting are the answer.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: count each distinct word by scanning the whole list repeatedly.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Count every word in a map.
     * 2. Put the distinct words into a list.
     * 3. Sort that list by higher frequency, then alphabetically.
     * 4. Take the first k words from the sorted list.
     * 
     * Time Complexity: O(n + uniqueWords log uniqueWords)
     * Space Complexity: O(uniqueWords)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the word list above.
     * love appears 3 times, i appears 2 times, coding appears 1 time.
     * Top 2 words are [love, i].
     */
    public List<String> bruteForce(String[] words, int k) {

        // frequency map stores:
        // word -> how many times that word appears
        //
        // Example:
        // words = ["love", "i", "love"]
        // frequency = {love=2, i=1}
        Map<String, Integer> frequency = new HashMap<>();

        // Count every word directly.
        // This is the most natural first step because "top frequent"
        // cannot be answered until we know each word's count.
        for (String word : words) {

            // Read the old count.
            // If this word is not present yet, start from 0.
            int oldCount = frequency.getOrDefault(word, 0);

            // Add this occurrence.
            int newCount = oldCount + 1;

            // Save the updated count back into the map.
            frequency.put(word, newCount);
        }

        // The map has unique words as keys.
        // Put those keys into a list because lists are easy to sort.
        List<String> sortedWords = new ArrayList<>(frequency.keySet());

        // Sort rule:
        // 1. Higher frequency first.
        // 2. If frequency is same, smaller alphabetical word first.
        Comparator<String> topWordOrder = new Comparator<String>() {
            public int compare(String first, String second) {

                int firstCount = frequency.get(first);
                int secondCount = frequency.get(second);

                // If counts are different, the bigger count should come first.
                if (firstCount != secondCount) {
                    return secondCount - firstCount;
                }

                // If counts are same, use alphabetical order.
                return first.compareTo(second);
            }
        };

        sortedWords.sort(topWordOrder);

        // answer will store only the first k words.
        List<String> answer = new ArrayList<>();

        // Stop at k, but also stop if there are fewer than k unique words.
        int limit = Math.min(k, sortedWords.size());

        for (int i = 0; i < limit; i++) {
            answer.add(sortedWords.get(i));
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: count once with a map, then sort entries or use a heap for top K.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Count all words once with a frequency map.
     * 2. Use a heap or sorted entries to rank by count and tie breaker.
     * 3. Keep only the best k words.
     * 4. Return them in required order.
     * 
     * Time Complexity: Lower than brute force because repeated scanning is replaced with stored state.
     * Space Complexity: O(uniqueWords + k) for the frequency map and heap/result.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the word list above.
     * love appears 3 times, i appears 2 times, coding appears 1 time.
     * Top 2 words are [love, i].
     */
    public List<String> optimized(String[] words, int k) {
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            int oldCount = frequency.getOrDefault(word, 0);
            int newCount = oldCount + 1;
            frequency.put(word, newCount);
        }

        List<String> sortedWords = new ArrayList<>(frequency.keySet());
        Comparator<String> topWordOrder = new Comparator<String>() {
            public int compare(String first, String second) {
                int firstCount = frequency.get(first);
                int secondCount = frequency.get(second);

                if (firstCount != secondCount) {
                    return secondCount - firstCount;
                }

                return first.compareTo(second);
            }
        };

        sortedWords.sort(topWordOrder);

        List<String> result = new ArrayList<>();
        int limit = Math.min(k, sortedWords.size());

        for (int i = 0; i < limit; i++) {
            result.add(sortedWords.get(i));
        }

        return result;
    }

    public static void main(String[] args) {
        TopKFrequentWords solution = new TopKFrequentWords();

        runSample(solution, new String[]{"i", "love", "i", "love", "coding", "love"}, 2);
        runSample(solution, new String[]{"banana", "apple", "banana", "apple", "carrot"}, 2);
        runSample(solution, new String[]{"dog", "cat", "dog", "bird"}, 3);
    }

    private static void runSample(TopKFrequentWords solution, String[] words, int k) {
        System.out.println("words = " + Arrays.toString(words) + ", k = " + k);
        System.out.println("bruteForce = " + solution.bruteForce(words, k));
        System.out.println("optimized  = " + solution.optimized(words, k));
        System.out.println();
    }
}
