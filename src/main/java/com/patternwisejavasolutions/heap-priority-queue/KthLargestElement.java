
package com.patternwisejavasolutions.heapPriorityQueue;
import java.util.Arrays;
import java.util.PriorityQueue;

public class KthLargestElement {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given an array, return the kth largest number.
     *
     * Sample Input: nums = [3, 2, 1, 5, 6, 4], k = 2
     * Sample Output: 5
     *
     * SCHOOL-LEVEL INTUITION:
     * If the numbers were sorted, the largest would be at the end, the second largest just before it,
     * and so on. The question is how much sorting work we really need.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Sort the full array. Once the array is sorted, the kth largest is at index length - k.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Copy nums so the caller's array is not changed.
     * 2. Sort the copy in ascending order.
     * 3. Return copy[copy.length - k].
     *
     * BRUTE FORCE DRY RUN
     * nums = [3,2,1,5,6,4], k = 2
     * sorted = [1,2,3,4,5,6]
     * index = 6 - 2 = 4 -> value 5
     *
     * Time: O(n log n), Space: O(n)
     */
    public int bruteForce(int[] nums, int k) {
        int[] copy = Arrays.copyOf(nums, nums.length);
        Arrays.sort(copy);
        return copy[copy.length - k];
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * Sorting everything is more work than needed. We only care about the biggest k numbers.
     * Keep a min heap of size k. The heap top is the smallest among the current top k numbers,
     * so after all values are processed, that top is exactly the kth largest.
     *
     * Pattern used: Fixed-size min heap.
     *
     * Other useful approach: Quickselect can find this in average O(n), but the heap version is
     * easier to learn and safer for a beginner because the heap always stores a clear top-k group.
     *
     * OPTIMIZED ALGORITHM
     * 1. Add each number to a min heap.
     * 2. If heap size becomes larger than k, remove the smallest.
     * 3. Return heap.peek().
     *
     * OPTIMIZED DRY RUN
     * nums = [3,2,1,5,6,4], k = 2
     * heap after 3,2 -> [2,3]
     * add 1, remove 1 -> [2,3]
     * add 5, remove 2 -> [3,5]
     * add 6, remove 3 -> [5,6]
     * add 4, remove 4 -> [5,6]
     * peek = 5
     *
     * Time: O(n log k), Space: O(k)
     */
    public int optimized(int[] nums, int k) {
        // Natural PriorityQueue is a min heap: the smallest of the kept top-k values is on top.
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);

            if (minHeap.size() > k) {
                // Removing the smallest keeps only the k largest values seen so far.
                minHeap.poll();
            }
        }

        // After keeping exactly k largest values, the smallest among them is the kth largest.
        return minHeap.peek();
    }
}
