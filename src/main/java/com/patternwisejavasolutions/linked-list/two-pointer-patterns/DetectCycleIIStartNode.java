package com.patternwisejavasolutions.linkedlist.twopointerpatterns;

import java.util.HashSet;
import java.util.Set;

public class DetectCycleIIStartNode {

    /*
     * PROBLEM IN SIMPLE WORDS
     * If a linked list has a cycle, return the node where the cycle begins. If there is no cycle,
     * return null.
     *
     * Sample Input: 3 -> 2 -> 0 -> -4, and -4 points back to 2
     * Sample Output: node with value 2
     *
     * SCHOOL-LEVEL INTUITION:
     * The first node we visit for a second time is the place where the loop starts.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Store visited nodes. When the next node is already seen, return it.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Create a set.
     * 2. Walk node by node.
     * 3. If current is already in the set, return current.
     * 4. Add current and continue.
     * 5. Return null if the list ends.
     *
     * BRUTE FORCE DRY RUN
     * visit 3, then 2, then 0, then -4
     * next current becomes 2 again, already visited -> return 2
     *
     * Time: O(n), Space: O(n)
     */
    public ListNode bruteForce(ListNode head) {
        Set<ListNode> visited = new HashSet<>();

        while (head != null) {
            if (visited.contains(head)) {
                return head;
            }

            visited.add(head);
            head = head.next;
        }

        return null;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is extra memory. Floyd's cycle method first makes slow and fast
     * meet inside the cycle. Then one pointer starts at head and one at the meeting point; moving
     * both one step at a time makes them meet at the cycle start.
     *
     * Why does the reset trick work?
     * Let distance from head to cycle start be A.
     * Let distance from cycle start to meeting point be B.
     * When slow and fast meet, fast has walked exactly twice as much as slow.
     * That difference is a whole number of cycle lengths.
     * So the distance from the meeting point back around to the cycle start is exactly the same
     * number of steps as the distance from head to the cycle start: A.
     *
     * That is why one pointer from head and one pointer from meeting point, both moving one step,
     * arrive at the entrance together.
     *
     * Pattern used: Floyd's fast and slow pointers.
     *
     * OPTIMIZED ALGORITHM
     * 1. Use slow and fast to detect a meeting point.
     * 2. If no meeting happens, return null.
     * 3. Put one pointer at head and one at the meeting point.
     * 4. Move both one step until they meet.
     * 5. Return that node.
     *
     * OPTIMIZED DRY RUN
     * For 3->2->0->-4 back to 2, slow and fast meet inside the cycle.
     * Start one pointer at 3 and one at meeting point; they step together and meet at 2.
     *
     * Time: O(n), Space: O(1)
     */
    public ListNode optimized(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            // First phase: fast moving two steps proves whether a cycle exists.
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                ListNode fromHead = head;
                ListNode fromMeeting = slow;

                while (fromHead != fromMeeting) {
                    /*
                     * fromHead has A steps to reach the entrance.
                     * fromMeeting also has A steps, going around the loop, to reach the entrance.
                     * Moving both equally turns the math above into code.
                     */
                    fromHead = fromHead.next;
                    fromMeeting = fromMeeting.next;
                }

                return fromHead;
            }
        }

        return null;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
