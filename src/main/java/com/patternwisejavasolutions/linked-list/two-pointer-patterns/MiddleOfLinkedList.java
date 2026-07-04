
package com.patternwisejavasolutions.linkedList.twoPointerPatterns;
import java.util.ArrayList;
import java.util.List;

public class MiddleOfLinkedList {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Return the middle node of a linked list. If there are two middle nodes, return the second one.
     *
     * Sample Input: 1 -> 2 -> 3 -> 4 -> 5
     * Sample Output: node with value 3
     *
     * SCHOOL-LEVEL INTUITION:
     * The middle is halfway through the list. A fast pointer moving twice as quickly lets a slow
     * pointer land halfway when fast reaches the end.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Store every node in an array list, then directly return the node at index size / 2.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Walk through the list and store node references.
     * 2. Return nodes.get(nodes.size() / 2).
     *
     * BRUTE FORCE DRY RUN
     * nodes = [1,2,3,4,5], size = 5, index = 2 -> value 3
     * for [1,2,3,4], size = 4, index = 2 -> second middle value 3
     *
     * Time: O(n), Space: O(n)
     */
    public ListNode bruteForce(ListNode head) {
        List<ListNode> nodes = new ArrayList<>();
        while (head != null) {
            nodes.add(head);
            head = head.next;
        }

        return nodes.get(nodes.size() / 2);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is storing nodes. Move slow one step and fast two steps. When fast
     * cannot move, slow has traveled half the list.
     *
     * Pattern used: Fast and slow pointers.
     *
     * OPTIMIZED ALGORITHM
     * 1. Start slow and fast at head.
     * 2. Move slow by one and fast by two while possible.
     * 3. Return slow.
     *
     * OPTIMIZED DRY RUN
     * 1->2->3->4->5
     * slow 1 fast 1 -> slow 2 fast 3 -> slow 3 fast 5 -> stop, return 3
     *
     * Time: O(n), Space: O(1)
     */
    public ListNode optimized(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            // fast covers two nodes while slow covers one, so slow lands at the halfway point.
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
