package com.patternwisejavasolutions.linkedlist.core;

import java.util.HashSet;
import java.util.Set;

public class LinkedListCycle {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Return true if a linked list has a cycle. A cycle means some node's next pointer points back
     * to an earlier node instead of eventually reaching null.
     *
     * Sample Input: 3 -> 2 -> 0 -> -4, and -4 points back to 2
     * Sample Output: true
     *
     * SCHOOL-LEVEL INTUITION:
     * Walking a normal list eventually reaches null. In a cycle, you keep revisiting the same nodes.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Remember every node we have visited. If we see the same node again, there is a cycle.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Create a set of visited nodes.
     * 2. Walk through the list.
     * 3. If current is already in the set, return true.
     * 4. Otherwise add current and move forward.
     * 5. Return false if current becomes null.
     *
     * BRUTE FORCE DRY RUN
     * 3 -> 2 -> 0 -> -4 -> back to 2
     * visit 3, 2, 0, -4
     * next is 2, already visited -> true
     *
     * Time: O(n), Space: O(n)
     */
    public boolean bruteForce(ListNode head) {
        Set<ListNode> visited = new HashSet<>();

        while (head != null) {
            if (visited.contains(head)) {
                return true;
            }

            visited.add(head);
            head = head.next;
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is storing every node. Use two walkers: slow moves one step and
     * fast moves two steps. In a cycle, fast will eventually lap slow. Without a cycle, fast reaches null.
     *
     * Pattern used: Fast and slow pointers.
     *
     * OPTIMIZED ALGORITHM
     * 1. Start slow and fast at head.
     * 2. Move slow one step and fast two steps.
     * 3. If they meet, return true.
     * 4. If fast reaches null, return false.
     *
     * OPTIMIZED DRY RUN
     * In a cycle, slow enters the loop and fast keeps gaining one node each round.
     * Eventually both stand on the same node, proving the loop exists.
     *
     * Time: O(n), Space: O(1)
     */
    public boolean optimized(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            // slow takes one step while fast takes two; in a loop, fast eventually catches slow.
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                return true;
            }
        }

        return false;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
