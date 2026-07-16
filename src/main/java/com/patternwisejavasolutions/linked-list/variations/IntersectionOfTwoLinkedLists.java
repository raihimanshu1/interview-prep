package com.patternwisejavasolutions.linkedlist.variations;

import java.util.HashSet;
import java.util.Set;

public class IntersectionOfTwoLinkedLists {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Two linked lists may share the exact same node object. Return that shared node, or null.
     *
     * Sample Input: A = 4->1->8->4->5, B = 5->6->1->8->4->5 where node 8 is shared
     * Sample Output: node with value 8
     *
     * SCHOOL-LEVEL INTUITION:
     * Intersection is about the same node reference, not just the same value. Two separate nodes
     * both holding 8 are not an intersection.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Store every node from list A in a set. Then walk list B and return the first node already seen.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Add all nodes from headA into a set.
     * 2. Walk headB.
     * 3. If a B node is in the set, return it.
     * 4. Return null if no shared node appears.
     *
     * BRUTE FORCE DRY RUN
     * set from A has nodes 4,1,8,4,5
     * walking B: 5 not shared, 6 not shared, 1 not shared, 8 is shared -> return 8
     *
     * Time: O(m+n), Space: O(m)
     */
    public ListNode bruteForce(ListNode headA, ListNode headB) {
        Set<ListNode> seen = new HashSet<>();

        while (headA != null) {
            seen.add(headA);
            headA = headA.next;
        }

        while (headB != null) {
            if (seen.contains(headB)) {
                return headB;
            }

            headB = headB.next;
        }

        return null;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is memory. Let pointer A walk list A then list B, and pointer B walk
     * list B then list A. They travel equal total distance, so if there is an intersection, they meet there.
     *
     * Pattern used: Two-pointer path switching.
     *
     * OPTIMIZED ALGORITHM
     * 1. Start pointerA at headA and pointerB at headB.
     * 2. Move each one step.
     * 3. When a pointer reaches null, redirect it to the other list's head.
     * 4. Stop when both pointers are equal.
     *
     * OPTIMIZED DRY RUN
     * A length before shared part may differ from B, but after switching, both pointers cover A+B length.
     * They align at the shared node 8, or both become null if no intersection exists.
     *
     * Time: O(m+n), Space: O(1)
     */
    public ListNode optimized(ListNode headA, ListNode headB) {
        ListNode pointerA = headA;
        ListNode pointerB = headB;

        while (pointerA != pointerB) {
            // Switching heads cancels the length difference between the two lists.
            pointerA = pointerA == null ? headB : pointerA.next;
            pointerB = pointerB == null ? headA : pointerB.next;
        }

        return pointerA;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
