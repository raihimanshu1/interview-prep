
package com.patternwisejavasolutions.heapPriorityQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class MergeKSortedLists {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given k sorted linked lists, merge them into one sorted linked list.
     *
     * Sample Input: [1->4->5, 1->3->4, 2->6]
     * Sample Output: 1->1->2->3->4->4->5->6
     *
     * SCHOOL-LEVEL INTUITION:
     * Every list is already sorted, so the next smallest value must be at the front of one of the lists.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Collect all values, sort them, then build a new linked list.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Walk every list and store all values.
     * 2. Sort the values.
     * 3. Build a new list from the sorted values.
     *
     * BRUTE FORCE DRY RUN
     * values from [1->4->5, 1->3->4, 2->6] become [1,4,5,1,3,4,2,6]
     * sorted -> [1,1,2,3,4,4,5,6]
     * build output list in that order
     *
     * Time: O(n log n), Space: O(n)
     */
    public ListNode bruteForce(ListNode[] lists) {
        List<Integer> values = new ArrayList<>();

        for (ListNode head : lists) {
            while (head != null) {
                values.add(head.val);
                head = head.next;
            }
        }

        Collections.sort(values);
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        for (int value : values) {
            tail.next = new ListNode(value);
            tail = tail.next;
        }

        return dummy.next;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * Sorting all values ignores that each list is already sorted. Put only the current head of
     * each list into a min heap. The smallest available node comes out first. When a node is used,
     * add its next node from the same list.
     *
     * Pattern used: Min heap for k-way merge.
     *
     * OPTIMIZED ALGORITHM
     * 1. Add each non-null list head to a min heap.
     * 2. Poll the smallest node and attach it to the answer.
     * 3. If that node has a next node, add it to the heap.
     * 4. Continue until heap is empty.
     *
     * OPTIMIZED DRY RUN
     * heap starts with 1,1,2
     * poll 1 from first list, push 4
     * poll 1 from second list, push 3
     * poll 2, push 6
     * continue to produce 1->1->2->3->4->4->5->6
     *
     * Time: O(n log k), Space: O(k)
     */
    public ListNode optimized(ListNode[] lists) {
        /*
         * Smallest node value comes first, so each poll gives the next output node.
         * Only list heads enter the heap because deeper nodes cannot be next until their head is used.
         */
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> {
            return Integer.compare(a.val, b.val);
        });

        for (ListNode head : lists) {
            if (head != null) {
                minHeap.offer(head);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (!minHeap.isEmpty()) {
            ListNode smallest = minHeap.poll();
            // Attach the globally smallest available front node.
            tail.next = smallest;
            tail = tail.next;

            if (smallest.next != null) {
                // The next node from the same list is now a candidate for the global smallest.
                minHeap.offer(smallest.next);
            }
        }

        // Cut any leftover pointer from the reused nodes after the merged tail.
        tail.next = null;
        return dummy.next;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
