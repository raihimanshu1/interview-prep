package com.patternwisejavasolutions.linkedlist.variations;

import java.util.HashMap;
import java.util.Map;

public class CopyListWithRandomPointer {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Each node has next and random pointers. Create a deep copy of the list, meaning every copied
     * node is new, and copied pointers point to copied nodes.
     *
     * Sample Input: 7 -> 13 -> 11, with random pointers to any nodes or null
     * Sample Output: a new separate list with the same values and same pointer pattern
     *
     * SCHOOL-LEVEL INTUITION:
     * If original node A points randomly to original node B, then copied A must point randomly to
     * copied B. So we need a way to match every original node with its clone.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Use a map from original node to copied node. First create all copied nodes, then connect next
     * and random pointers using the map.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Walk original list and create one clone per node.
     * 2. Store original -> clone in a map.
     * 3. Walk original list again.
     * 4. Set clone.next = map.get(original.next).
     * 5. Set clone.random = map.get(original.random).
     *
     * BRUTE FORCE DRY RUN
     * original A.next = B and A.random = C
     * map gives cloneA, cloneB, cloneC
     * cloneA.next = cloneB and cloneA.random = cloneC
     *
     * Time: O(n), Space: O(n)
     */
    public Node bruteForce(Node head) {
        if (head == null) {
            return null;
        }

        Map<Node, Node> map = new HashMap<>();
        Node current = head;

        while (current != null) {
            map.put(current, new Node(current.val));
            current = current.next;
        }

        current = head;
        while (current != null) {
            Node clone = map.get(current);
            clone.next = map.get(current.next);
            clone.random = map.get(current.random);
            current = current.next;
        }

        return map.get(head);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force map is easy but uses extra space. We can place each copy directly after its
     * original: A -> A' -> B -> B'. Then original.random.next is the copied random target.
     *
     * Pattern used: Interweaving linked-list nodes.
     *
     * OPTIMIZED ALGORITHM
     * 1. Insert each clone right after its original node.
     * 2. Set each clone.random using original.random.next.
     * 3. Separate the woven list into original and copied lists.
     *
     * OPTIMIZED DRY RUN
     * A->B becomes A->A'->B->B'
     * if A.random = B, then A'.random = A.random.next = B'
     * split out A'->B'
     *
     * Time: O(n), Space: O(1) extra besides output
     */
    public Node optimized(Node head) {
        if (head == null) {
            return null;
        }

        Node current = head;
        while (current != null) {
            Node clone = new Node(current.val);
            // Insert clone immediately after original: current -> clone -> old next.
            clone.next = current.next;
            current.next = clone;
            current = clone.next;
        }

        current = head;
        while (current != null) {
            if (current.random != null) {
                // current.random.next is the clone of current's random target.
                current.next.random = current.random.next;
            }

            current = current.next.next;
        }

        current = head;
        Node copiedHead = head.next;

        while (current != null) {
            Node clone = current.next;
            // Restore original next, then point clone to the next clone.
            current.next = clone.next;

            if (clone.next != null) {
                clone.next = clone.next.next;
            }

            current = current.next;
        }

        return copiedHead;
    }

    static class Node {
        int val;
        Node next;
        Node random;

        Node(int val) {
            this.val = val;
        }
    }
}
