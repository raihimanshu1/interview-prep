package com.patternwisejavasolutions.linkedlist.variations;

import java.util.ArrayList;
import java.util.List;

public class ReorderList {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Reorder a list from L0 -> L1 -> ... -> Ln into L0 -> Ln -> L1 -> Ln-1 -> ...
     *
     * Sample Input: 1 -> 2 -> 3 -> 4
     * Sample Output: 1 -> 4 -> 2 -> 3
     *
     * SCHOOL-LEVEL INTUITION:
     * We take one node from the front, then one from the back, and keep alternating.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Store nodes in a list so we can pick from both ends by index, then reconnect them.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Store all node references.
     * 2. Use left at start and right at end.
     * 3. Connect left node, then right node, alternating inward.
     * 4. End with null to avoid old links forming a cycle.
     *
     * BRUTE FORCE DRY RUN
     * nodes [1,2,3,4]
     * connect 1, then 4, then 2, then 3 -> 1->4->2->3
     *
     * Time: O(n), Space: O(n)
     */
    public void bruteForce(ListNode head) {
        if (head == null) {
            return;
        }

        List<ListNode> nodes = new ArrayList<>();
        for (ListNode current = head; current != null; current = current.next) {
            nodes.add(current);
        }

        int left = 0;
        int right = nodes.size() - 1;
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (left <= right) {
            tail.next = nodes.get(left++);
            tail = tail.next;

            if (left <= right) {
                tail.next = nodes.get(right--);
                tail = tail.next;
            }
        }

        tail.next = null;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is storing all nodes. We can split the list in half, reverse the
     * second half, then weave first and reversed second halves together.
     *
     * Pattern used: Middle finding + reverse + merge.
     *
     * OPTIMIZED ALGORITHM
     * 1. Find the middle using slow and fast.
     * 2. Split the list into two halves.
     * 3. Reverse the second half.
     * 4. Alternately connect one node from first half and one from reversed second half.
     *
     * OPTIMIZED DRY RUN
     * 1->2->3->4->5
     * split into 1->2->3 and 4->5
     * reverse second half to 5->4
     * weave -> 1->5->2->4->3
     *
     * Time: O(n), Space: O(1)
     */
    public void optimized(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        ListNode slow = head;
        ListNode fast = head;

        while (fast.next != null && fast.next.next != null) {
            // slow stops at the end of the first half, ready to split the list.
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode second = slow.next;
        // Break the old link so weaving cannot accidentally keep an old cycle/tail.
        slow.next = null;
        second = reverse(second);

        ListNode first = head;
        while (second != null) {
            // Save both next pointers before rewiring the two lists together.
            ListNode firstNext = first.next;
            ListNode secondNext = second.next;

            first.next = second;
            second.next = firstNext;

            first = firstNext;
            second = secondNext;
        }
    }

    private ListNode reverse(ListNode head) {
        ListNode previous = null;
        ListNode current = head;

        while (current != null) {
            // Save the unreversed tail before pointing current backward.
            ListNode next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }

        return previous;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
