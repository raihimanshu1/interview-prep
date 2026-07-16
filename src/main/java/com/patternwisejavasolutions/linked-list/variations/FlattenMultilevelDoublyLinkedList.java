package com.patternwisejavasolutions.linkedlist.variations;

import java.util.Stack;

public class FlattenMultilevelDoublyLinkedList {

    /* PROBLEM IN SIMPLE WORDS
     * A doubly linked list node can also have a child list. Flatten it so all nodes appear in one
     * normal doubly linked list using depth-first order.
     *
     * Sample Input:  1 - 2 - 3 - 4, and 3 has child 7 - 8
     * Sample Output: 1 - 2 - 3 - 7 - 8 - 4
     */

    /* SCHOOL-LEVEL INTUITION
     * Think of every child list like a side road. When we see a side road, we drive through it
     * completely before coming back to the main road.
     */

    /* BRUTE FORCE INTUITION
     * First collect nodes in the correct flattened order, then reconnect all prev and next pointers.
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Use DFS to add each node to a stack/list-like order.
     * 2. Visit current node, then its child, then its next node.
     * 3. Reconnect nodes one by one using next and prev.
     * 4. Set every child pointer to null.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /* BRUTE FORCE DRY RUN
     * At node 3, DFS visits 3, then child 7, then 8, and only then returns to 4.
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public Node bruteForce(Node head) {
        Stack<Node> order = new Stack<>();
        collectReverse(head, order);

        Node newHead = null;
        Node previous = null;
        while (!order.isEmpty()) {
            Node current = order.pop();
            current.prev = previous;
            current.next = null;
            current.child = null;

            if (previous != null) {
                previous.next = current;
            } else {
                newHead = current;
            }
            previous = current;
        }

        return newHead;
    }

    /* OPTIMIZED INTUITION
     * The brute force waste is storing a separate traversal order. We can flatten in place by
     * changing pointers. When a node has a child, splice that child list between the node and its
     * original next node.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Walk through the list.
     * 2. If a node has no child, move to next.
     * 3. If it has a child, find the child's tail.
     * 4. Connect current -> child head and child tail -> old next.
     * 5. Clear current.child and continue.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /* OPTIMIZED DRY RUN
     * 3.child is 7 and old next is 4. Connect 3->7, child tail 8->4, then clear 3.child.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public Node optimized(Node head) {
        Node current = head;

        while (current != null) {
            if (current.child == null) {
                current = current.next;
                continue;
            }

            Node childHead = current.child;
            Node childTail = childHead;
            while (childTail.next != null) {
                // Find the end of the child chain so the old next node can be reattached.
                childTail = childTail.next;
            }

            // Save the original next node so it can be attached after the child chain.
            Node nextNode = current.next;
            // Splice child list directly after current.
            current.next = childHead;
            childHead.prev = current;
            current.child = null;

            // Reconnect the saved main-road node after the child tail.
            childTail.next = nextNode;
            if (nextNode != null) {
                nextNode.prev = childTail;
            }

            current = current.next;
        }

        return head;
    }

    private void collectReverse(Node node, Stack<Node> order) {
        if (node == null) {
            return;
        }

        collectReverse(node.next, order);
        collectReverse(node.child, order);
        // Push after children and next so popping gives node, child, next order.
        order.push(node);
    }

    static class Node {
        int val;
        Node prev;
        Node next;
        Node child;

        Node(int val) {
            this.val = val;
        }
    }
}
