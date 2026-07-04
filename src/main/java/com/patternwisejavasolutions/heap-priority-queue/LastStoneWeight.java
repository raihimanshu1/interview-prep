
package com.patternwisejavasolutions.heapPriorityQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class LastStoneWeight {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Repeatedly smash the two heaviest stones. If they are equal, both disappear. If not,
     * the leftover weight goes back. Return the final stone weight, or 0 if none remain.
     *
     * Sample Input: stones = [2, 7, 4, 1, 8, 1]
     * Sample Output: 1
     *
     * SCHOOL-LEVEL INTUITION:
     * Every round asks the same question: which two stones are biggest right now? A heap is useful
     * because it is designed to give the biggest or smallest item quickly.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Keep stones in a list. Before every smash, sort the list so the two biggest are at the end.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Put all stones into a list.
     * 2. While more than one stone remains, sort the list.
     * 3. Remove the two largest stones.
     * 4. If they are different, add the difference back.
     * 5. Return the remaining stone or 0.
     *
     * BRUTE FORCE DRY RUN
     * [2,7,4,1,8,1] -> sort [1,1,2,4,7,8]
     * smash 8 and 7 -> add 1
     * continue until one stone remains -> 1
     *
     * Time: O(n^2 log n), Space: O(n)
     */
    public int bruteForce(int[] stones) {
        List<Integer> list = new ArrayList<>();
        for (int stone : stones) {
            list.add(stone);
        }

        while (list.size() > 1) {
            Collections.sort(list);
            int first = list.remove(list.size() - 1);
            int second = list.remove(list.size() - 1);

            if (first != second) {
                // Only the difference survives after the smaller stone breaks the larger one.
                list.add(first - second);
            }
        }

        return list.isEmpty() ? 0 : list.get(0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * Sorting again and again is wasteful. A max heap always keeps the heaviest stone at the top,
     * so each round can remove the two heaviest directly.
     *
     * Pattern used: Max heap / Priority Queue.
     *
     * OPTIMIZED ALGORITHM
     * 1. Add all stones to a max heap.
     * 2. While heap size is greater than 1, poll two stones.
     * 3. If they differ, offer their difference.
     * 4. Return the heap top or 0.
     *
     * OPTIMIZED DRY RUN
     * heap contains [8,7,4,2,1,1]
     * poll 8 and 7 -> push 1
     * poll 4 and 2 -> push 2
     * poll 2 and 1 -> push 1
     * poll 1 and 1 -> both gone
     * poll 1 remains -> return 1
     *
     * Time: O(n log n), Space: O(n)
     */
    public int optimized(int[] stones) {
        /*
         * Larger weight comes first, so each poll gives the current heaviest stone.
         * The smash rule always needs the top two weights, which is the heap clue.
         */
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> {
            return Integer.compare(b, a);
        });

        for (int stone : stones) {
            maxHeap.offer(stone);
        }

        while (maxHeap.size() > 1) {
            int first = maxHeap.poll();
            int second = maxHeap.poll();

            if (first != second) {
                // The heavier stone survives with only the difference left.
                maxHeap.offer(first - second);
            }
        }

        return maxHeap.isEmpty() ? 0 : maxHeap.peek();
    }
}
