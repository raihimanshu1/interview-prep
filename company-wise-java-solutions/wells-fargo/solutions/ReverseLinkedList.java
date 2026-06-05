package wellsfargo.solutions;

import java.util.ArrayList;
import java.util.List;

public class ReverseLinkedList {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given the head of a singly linked list, reverse the list and return the new head.
     *
     * Sample Input: 1 -> 2 -> 3 -> 4 -> null
     * Sample Output: 4 -> 3 -> 2 -> 1 -> null
     *
     * SCHOOL-LEVEL INTUITION:
     * A linked list is a chain of arrows. Reversing means every arrow must point backward.
     * Beginners can first store values and rebuild; then learn to reverse the arrows in place.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Store all values in an array list, then create a new list by reading those values backward.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Walk through the list and store values.
     * 2. Create a dummy head for the new list.
     * 3. Read stored values from right to left and append new nodes.
     * 4. Return dummy.next.
     *
     * BRUTE FORCE DRY RUN
     * 1 -> 2 -> 3
     * values = [1,2,3]
     * build from back: 3, then 2, then 1 -> 3 -> 2 -> 1
     *
     * Time: O(n), Space: O(n)
     */
    public ListNode bruteForce(ListNode head) {
        List<Integer> values = new ArrayList<>();

        for (ListNode current = head; current != null; current = current.next) {
            values.add(current.val);
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        for (int index = values.size() - 1; index >= 0; index--) {
            tail.next = new ListNode(values.get(index));
            tail = tail.next;
        }

        return dummy.next;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is creating a second list. We can reverse the existing arrows.
     * For each node, save next before changing current.next, otherwise the rest of the list is lost.
     *
     * Pattern used: Linked-list pointer reversal.
     *
     * OPTIMIZED ALGORITHM
     * 1. previous starts as null.
     * 2. current starts at head.
     * 3. Save current.next.
     * 4. Point current.next to previous.
     * 5. Move previous and current forward.
     * 6. Return previous after current becomes null.
     *
     * OPTIMIZED DRY RUN
     * 1 -> 2 -> 3
     * reverse 1 to point null
     * reverse 2 to point 1
     * reverse 3 to point 2
     * previous is 3 -> new head
     *
     * Time: O(n), Space: O(1)
     */
    public ListNode optimized(ListNode head) {
        ListNode previous = null;
        ListNode current = head;

        while (current != null) {
            // Save the rest of the list before turning current.next backward.
            ListNode next = current.next;
            current.next = previous;
            // previous becomes the head of the already-reversed part.
            previous = current;
            // current moves to the first node we have not reversed yet.
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
