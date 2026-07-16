package com.patternwisejavasolutions.heappriorityqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class FindMedianFromDataStream {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Numbers arrive one by one. After any insertion, return the median of all numbers seen so far.
     *
     * Sample Input:
     * add 1, add 2, findMedian, add 3, findMedian
     *
     * Sample Output:
     * 1.5, 2.0
     *
     * SCHOOL-LEVEL INTUITION:
     * The median is the middle number after sorting. If count is even, it is the average of the two
     * middle numbers. The whole challenge is finding that middle quickly after every new number.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Keep all numbers in a list. Whenever median is asked, sort the list and read the middle.
     *
     * BRUTE FORCE ALGORITHM
     * 1. addNum stores the new number at the end of a list.
     * 2. findMedian sorts that list.
     * 3. If size is odd, return the middle value.
     * 4. If size is even, return the average of the two middle values.
     *
     * BRUTE FORCE DRY RUN
     * add 1 -> [1], median 1
     * add 2 -> sorted [1,2], median (1+2)/2 = 1.5
     * add 3 -> sorted [1,2,3], median 2
     *
     * add: O(1), median: O(n log n), Space: O(n)
     */
    static class BruteForceMedianFinder {
        private final List<Integer> values = new ArrayList<>();

        public void addNum(int num) {
            values.add(num);
        }

        public double findMedian() {
            Collections.sort(values);
            int size = values.size();

            if (size % 2 == 1) {
                return values.get(size / 2);
            }

            return ((long) values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * Sorting every time is expensive. We only need the middle, so split numbers into two halves:
     * a max heap for the smaller half and a min heap for the larger half. Their tops are the only
     * values needed for the median.
     *
     * Pattern used: Two heaps.
     *
     * OPTIMIZED ALGORITHM
     * 1. Add number to the smaller-half max heap.
     * 2. Move the largest smaller-half value to the larger-half min heap.
     * 3. If larger half becomes bigger, move its smallest value back.
     * 4. Median is either maxHeap.peek() or average of both heap tops.
     *
     * OPTIMIZED DRY RUN
     * add 1 -> left [1], right [] -> median 1
     * add 2 -> left [1], right [2] -> median 1.5
     * add 3 -> left [2,1], right [3] -> median 2
     *
     * add: O(log n), median: O(1), Space: O(n)
     */
    static class OptimizedMedianFinder {
        /*
         * Max heap: larger numbers should be removed first, so the top is the biggest value
         * in the smaller half. That top is the left middle value of the stream.
         */
        private final PriorityQueue<Integer> leftHalf = new PriorityQueue<>((a, b) -> {
            return Integer.compare(b, a);
        });
        // Min heap: smallest value in the larger half is on top.
        private final PriorityQueue<Integer> rightHalf = new PriorityQueue<>();

        public void addNum(int num) {
            // Add to left, then move its largest value right to preserve ordering between halves.
            leftHalf.offer(num);
            rightHalf.offer(leftHalf.poll());

            if (rightHalf.size() > leftHalf.size()) {
                // Left half is allowed to hold the extra middle value when the count is odd.
                leftHalf.offer(rightHalf.poll());
            }
        }

        public double findMedian() {
            if (leftHalf.size() > rightHalf.size()) {
                // Odd count: leftHalf keeps the extra middle value.
                return leftHalf.peek();
            }

            // Even count: average the two middle border values without overflowing int addition.
            return ((long) leftHalf.peek() + rightHalf.peek()) / 2.0;
        }
    }
}
