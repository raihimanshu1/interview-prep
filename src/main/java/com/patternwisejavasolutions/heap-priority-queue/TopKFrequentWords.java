
package com.patternwisejavasolutions.heapPriorityQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TopKFrequentWords {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Return the k most common words. If two words have the same count, the alphabetically smaller
     * word comes first.
     *
     * Sample Input: words = ["i","love","leetcode","i","love","coding"], k = 2
     * Sample Output: ["i","love"]
     *
     * SCHOOL-LEVEL INTUITION
     * Count every word like votes in an election. The winners are the words with the most votes.
     * Ties are decided by dictionary order.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * After counting, repeatedly scan all remaining words and pick the best one.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Count each word.
     * 2. Repeat k times.
     * 3. Scan every remaining word and choose highest count; for ties choose smaller word.
     * 4. Remove that word so it is not picked again.
     *
     * BRUTE FORCE DRY RUN
     * counts: i->2, love->2, leetcode->1, coding->1
     * pick i before love because "i" is alphabetically smaller.
     * then pick love -> ["i","love"]
     *
     * Time Complexity: O(n + k*m), m = unique words
     * Space Complexity: O(m)
     */
    public List<String> bruteForce(String[] words, int k) {
        Map<String, Integer> frequency = count(words);
        List<String> answer = new ArrayList<>();

        for (int pick = 0; pick < k; pick++) {
            String best = null;

            for (String word : frequency.keySet()) {
                if (best == null || isBetter(word, best, frequency)) {
                    best = word;
                }
            }

            answer.add(best);
            frequency.remove(best);
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force waste is rescanning every word for each output slot. Keep only k best
     * candidates in a heap. The heap top is the weakest candidate among the k: lower frequency is
     * weaker, and for equal frequency alphabetically larger is weaker.
     *
     * Pattern used: Frequency map + fixed-size heap.
     *
     * OPTIMIZED ALGORITHM
     * 1. Count each word.
     * 2. Push unique words into a min heap of size k.
     * 3. Remove the weakest word whenever size becomes k + 1.
     * 4. Pop heap into the front of the answer list to get strongest to weakest order.
     *
     * OPTIMIZED DRY RUN
     * k = 2, counts i->2, love->2, coding->1, leetcode->1
     * heap keeps i and love.
     * popping weakest first gives love then i, so insert at front -> [i,love]
     *
     * Time Complexity: O(n log k)
     * Space Complexity: O(m + k)
     */
    public List<String> optimized(String[] words, int k) {
        Map<String, Integer> frequency = count(words);
        // Weakest candidate first: lower count, or same count but alphabetically larger.
        PriorityQueue<String> heap = new PriorityQueue<>((a, b) -> {
            int countCompare = Integer.compare(frequency.get(a), frequency.get(b));
            if (countCompare != 0) {
                return countCompare;
            }

            // For equal frequency, alphabetically larger is weaker and should leave first.
            return b.compareTo(a);
        });

        for (String word : frequency.keySet()) {
            heap.offer(word);

            if (heap.size() > k) {
                // Keep the heap bounded to the k strongest words seen so far.
                heap.poll();
            }
        }

        List<String> answer = new ArrayList<>();
        while (!heap.isEmpty()) {
            // Heap removes weakest first, so insert at front to return strongest first.
            answer.add(0, heap.poll());
        }

        return answer;
    }

    private Map<String, Integer> count(String[] words) {
        Map<String, Integer> frequency = new HashMap<>();

        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }

        return frequency;
    }

    private boolean isBetter(String candidate, String currentBest, Map<String, Integer> frequency) {
        int candidateCount = frequency.get(candidate);
        int bestCount = frequency.get(currentBest);

        if (candidateCount != bestCount) {
            return candidateCount > bestCount;
        }

        return candidate.compareTo(currentBest) < 0;
    }
}
