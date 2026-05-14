import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class KthLargestStream {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Keep receiving numbers. After each new number, return the kth largest number seen so far.
     *
     * Sample Input: k = 3, nums = [4,5,8,2], add(3), add(5), add(10)
     * Sample Output: 4, 5, 5
     *
     * SCHOOL-LEVEL INTUITION
     * Imagine keeping a score board. The kth largest is the score at position k when scores are
     * sorted from biggest to smallest.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Store every number. Whenever add is called, sort all numbers and read index k - 1.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Put initial numbers in a list.
     * 2. On add, append the new value.
     * 3. Sort in descending order.
     * 4. Return the number at index k - 1.
     *
     * BRUTE FORCE DRY RUN
     * values [4,5,8,2], k = 3 -> sorted [8,5,4,2], kth is 4.
     * add 10 -> [10,8,5,4,2], kth is 5.
     *
     * Time Complexity: constructor O(n), add O(n log n)
     * Space Complexity: O(n)
     */
    public static class BruteForce {
        private final int k;
        private final List<Integer> values;

        public BruteForce(int k, int[] nums) {
            this.k = k;
            this.values = new ArrayList<>();

            for (int num : nums) {
                values.add(num);
            }
        }

        public int add(int val) {
            values.add(val);
            values.sort(Collections.reverseOrder());
            return values.get(k - 1);
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force waste is sorting the whole history after every add. Keep only the k largest
     * numbers seen so far. The smallest among those k numbers is exactly the kth largest overall.
     *
     * Pattern used: Fixed-size min heap.
     *
     * OPTIMIZED ALGORITHM
     * 1. Use a min heap.
     * 2. Add each number to the heap.
     * 3. If heap size is greater than k, remove the smallest.
     * 4. Heap top is the kth largest.
     *
     * OPTIMIZED DRY RUN
     * k = 3, nums [4,5,8,2]
     * heap keeps [4,5,8], top 4.
     * add 10 -> heap [5,8,10], top 5.
     *
     * Time Complexity: constructor O(n log k), add O(log k)
     * Space Complexity: O(k)
     */
    public static class Optimized {
        private final int k;
        private final PriorityQueue<Integer> minHeap;

        public Optimized(int k, int[] nums) {
            this.k = k;
            // Min heap keeps the weakest value among the current top k at the top.
            this.minHeap = new PriorityQueue<>();

            for (int num : nums) {
                add(num);
            }
        }

        public int add(int val) {
            minHeap.offer(val);

            if (minHeap.size() > k) {
                // Size bound: remove the smallest so only k largest values remain.
                minHeap.poll();
            }

            // The top is the smallest of the k largest values, which is the kth largest.
            return minHeap.peek();
        }
    }
}
