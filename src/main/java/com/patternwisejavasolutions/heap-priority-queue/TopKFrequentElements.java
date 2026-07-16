package com.patternwisejavasolutions.heappriorityqueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TopKFrequentElements {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Return the k numbers that appear most often in the array.
     *
     * Sample Input: nums = [1, 1, 1, 2, 2, 3], k = 2
     * Sample Output: [1, 2]
     *
     * SCHOOL-LEVEL INTUITION:
     * First count how many times each number appears. After that, the problem becomes: choose the
     * k numbers with the biggest counts.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Count frequencies, then repeatedly scan all remaining numbers to pick the most frequent one.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Build a frequency map.
     * 2. Repeat k times: scan all entries and find the unused number with highest frequency.
     * 3. Add it to the answer and mark it used by removing it from the map.
     *
     * BRUTE FORCE DRY RUN
     * nums = [1,1,1,2,2,3], k = 2
     * counts: 1->3, 2->2, 3->1
     * first pick 1, second pick 2 -> [1,2]
     *
     * Time: O(n + k*m), where m is unique values. Space: O(m)
     */
    public int[] bruteForce(int[] nums, int k) {
        Map<Integer, Integer> frequency = count(nums);
        int[] answer = new int[k];

        for (int slot = 0; slot < k; slot++) {
            int bestNumber = 0;
            int bestCount = -1;

            for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
                if (entry.getValue() > bestCount) {
                    bestNumber = entry.getKey();
                    bestCount = entry.getValue();
                }
            }

            answer[slot] = bestNumber;
            // Remove it so the next scan finds the next most frequent number.
            frequency.remove(bestNumber);
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is scanning all unique numbers again for each answer slot.
     * Keep a min heap of size k ordered by frequency. If a new number is more frequent than the
     * smallest inside the heap, it deserves a place in the top k.
     *
     * Pattern used: Frequency map + fixed-size min heap.
     *
     * Other useful approach: Bucket sort can solve this in O(n), but the heap approach generalizes
     * well to many top-k problems.
     *
     * OPTIMIZED ALGORITHM
     * 1. Count all numbers.
     * 2. Add each unique number to a min heap by frequency.
     * 3. If heap size exceeds k, remove the least frequent number.
     * 4. Pop heap values into the answer.
     *
     * OPTIMIZED DRY RUN
     * counts: 1->3, 2->2, 3->1, k = 2
     * add 1, add 2 -> heap has top two so far
     * add 3 -> heap too big, remove frequency 1
     * answer contains 1 and 2
     *
     * Time: O(n log k), Space: O(m + k)
     */
    public int[] optimized(int[] nums, int k) {
        Map<Integer, Integer> frequency = count(nums);
        /*
         * Smaller frequency comes first, so heap top is the weakest top-k candidate.
         * When size passes k, poll() removes the number least deserving a top-k slot.
         */
        PriorityQueue<Integer> minHeap = new PriorityQueue<>((a, b) -> {
            return Integer.compare(frequency.get(a), frequency.get(b));
        });

        for (int num : frequency.keySet()) {
            minHeap.offer(num);

            if (minHeap.size() > k) {
                // The least frequent candidate cannot be in the final top k anymore.
                minHeap.poll();
            }
        }

        int[] answer = new int[k];
        for (int index = k - 1; index >= 0; index--) {
            // Polling gives weakest to strongest, so fill from the back.
            answer[index] = minHeap.poll();
        }

        return answer;
    }

    private Map<Integer, Integer> count(int[] nums) {
        Map<Integer, Integer> frequency = new HashMap<>();

        for (int num : nums) {
            frequency.put(num, frequency.getOrDefault(num, 0) + 1);
        }

        return frequency;
    }
}
