package com.patternwisejavasolutions.linkedlist.variations;

import java.util.ArrayList;
import java.util.List;

public class PalindromeLinkedList {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Return true if the linked list reads the same forward and backward.
     *
     * Sample Input: 1 -> 2 -> 2 -> 1
     * Sample Output: true
     *
     * SCHOOL-LEVEL INTUITION:
     * A palindrome has matching outer pairs: first with last, second with second-last, and so on.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Store values in an array list so we can compare from both ends easily.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Copy all values into a list.
     * 2. Use left and right indexes.
     * 3. If values differ, return false.
     * 4. Return true after all pairs match.
     *
     * BRUTE FORCE DRY RUN
     * values = [1,2,2,1]
     * compare 1 and 1, then 2 and 2 -> true
     *
     * Time: O(n), Space: O(n)
     */
    public boolean bruteForce(ListNode head) {
        List<Integer> values = new ArrayList<>();
        while (head != null) {
            values.add(head.val);
            head = head.next;
        }

        int left = 0;
        int right = values.size() - 1;

        while (left < right) {
            if (!values.get(left).equals(values.get(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is storing every value. Find the middle, reverse the second half,
     * and compare the first half with the reversed second half.
     *
     * Pattern used: Fast/slow pointer + reverse list.
     *
     * OPTIMIZED ALGORITHM
     * 1. Use slow and fast to find the middle.
     * 2. Reverse the second half starting at slow.
     * 3. Compare first half and reversed second half node by node.
     * 4. Return false on mismatch, otherwise true.
     *
     * OPTIMIZED DRY RUN
     * 1->2->2->1
     * second half 2->1 reversed to 1->2
     * compare 1 with 1, 2 with 2 -> true
     *
     * Time: O(n), Space: O(1)
     */
    public boolean optimized(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            // slow reaches the middle when fast reaches the end.
            slow = slow.next;
            fast = fast.next.next;
        }

        // Reversing the back half lets us compare outside pairs while walking forward.
        ListNode secondHalf = reverse(slow);
        ListNode firstHalf = head;

        while (secondHalf != null) {
            if (firstHalf.val != secondHalf.val) {
                return false;
            }

            firstHalf = firstHalf.next;
            secondHalf = secondHalf.next;
        }

        return true;
    }

    private ListNode reverse(ListNode head) {
        ListNode previous = null;
        ListNode current = head;

        while (current != null) {
            // Save next before changing current.next, or the unreversed tail would be lost.
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
