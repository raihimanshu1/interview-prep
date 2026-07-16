package com.patternwisejavasolutions.heappriorityqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class KClosestElements {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given a sorted array, return k numbers closest to x. The final answer should be sorted.
     *
     * Sample Input: arr = [1, 2, 3, 4, 5], k = 4, x = 3
     * Sample Output: [1, 2, 3, 4]
     *
     * SCHOOL-LEVEL INTUITION:
     * Closeness means small absolute difference from x. If two numbers are equally close, the
     * smaller number is preferred.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Put all numbers in a list, sort them by closeness to x, take k, then sort the answer normally.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Copy all values into a list.
     * 2. Sort by distance from x, breaking ties by smaller value.
     * 3. Keep first k values.
     * 4. Sort those k values in ascending order.
     *
     * BRUTE FORCE DRY RUN
     * arr = [1,2,3,4,5], k = 4, x = 3
     * closeness order: 3,2,4,1,5
     * take 3,2,4,1 -> sort -> [1,2,3,4]
     *
     * Time: O(n log n), Space: O(n)
     */
    public List<Integer> bruteForce(int[] arr, int k, int x) {
        List<Integer> values = new ArrayList<>();
        for (int num : arr) {
            values.add(num);
        }

        values.sort((a, b) -> {
            int distanceCompare = Long.compare(distance(a, x), distance(b, x));
            if (distanceCompare != 0) {
                return distanceCompare;
            }

            return Integer.compare(a, b);
        });

        List<Integer> answer = new ArrayList<>(values.subList(0, k));
        Collections.sort(answer);
        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * We only need k best values, not a full ordering of all values. A max heap of size k can keep
     * the current closest values while pushing out the worst candidate.
     *
     * Pattern used: Fixed-size max heap.
     *
     * Other useful approach: Because arr is sorted, binary search + sliding window can solve this
     * in O(log(n-k) + k). The heap version keeps the top-k idea very visible.
     *
     * OPTIMIZED ALGORITHM
     * 1. Add each number to a max heap where the worst close candidate is on top.
     * 2. If heap size exceeds k, remove that worst candidate.
     * 3. Sort the remaining heap values in ascending order.
     *
     * OPTIMIZED DRY RUN
     * arr = [1,2,3,4,5], k = 4, x = 3
     * after all insertions, the heap has 1,2,3,4 and rejected 5
     * sort -> [1,2,3,4]
     *
     * Time: O(n log k + k log k), Space: O(k)
     */
    public List<Integer> optimized(int[] arr, int k, int x) {
        /*
         * Worst candidate first: farther from x, or same distance but larger value.
         * That makes poll() remove exactly the value we least want to keep in the k closest group.
         */
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> {
            int distanceCompare = Long.compare(distance(b, x), distance(a, x));
            if (distanceCompare != 0) {
                return distanceCompare;
            }

            return Integer.compare(b, a);
        });

        for (int num : arr) {
            maxHeap.offer(num);

            if (maxHeap.size() > k) {
                // The top is the least useful among selected values, so remove it.
                maxHeap.poll();
            }
        }

        List<Integer> answer = new ArrayList<>(maxHeap);
        Collections.sort(answer);
        return answer;
    }

    private long distance(int value, int x) {
        return Math.abs((long) value - x);
    }
}
