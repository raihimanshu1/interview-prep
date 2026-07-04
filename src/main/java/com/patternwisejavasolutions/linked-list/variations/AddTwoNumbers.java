
package com.patternwisejavasolutions.linkedList.variations;
import java.math.BigInteger;

public class AddTwoNumbers {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Two non-negative numbers are stored in reverse order as linked lists. Add them and return the
     * sum as a linked list in reverse order.
     *
     * Sample Input: l1 = 2 -> 4 -> 3, l2 = 5 -> 6 -> 4
     * Sample Output: 7 -> 0 -> 8
     * Explanation: 342 + 465 = 807
     *
     * SCHOOL-LEVEL INTUITION:
     * This is the same as school addition from right to left. Each node is one digit, and carry
     * moves to the next digit.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Convert both lists into whole numbers, add them, then convert the sum back to a list.
     * BigInteger keeps this brute force idea correct even when the linked lists are very long.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Read each list into a BigInteger using place values 1, 10, 100, ...
     * 2. Add both numbers.
     * 3. Build result digits by repeatedly taking sum % 10.
     *
     * BRUTE FORCE DRY RUN
     * l1 2->4->3 becomes 342
     * l2 5->6->4 becomes 465
     * sum 807 becomes 7->0->8
     *
     * Time: O(m+n), Space: O(m+n)
     */
    public ListNode bruteForce(ListNode l1, ListNode l2) {
        BigInteger first = toNumber(l1);
        BigInteger second = toNumber(l2);
        BigInteger sum = first.add(second);

        if (sum.equals(BigInteger.ZERO)) {
            return new ListNode(0);
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        BigInteger ten = BigInteger.TEN;

        while (sum.compareTo(BigInteger.ZERO) > 0) {
            tail.next = new ListNode(sum.mod(ten).intValue());
            tail = tail.next;
            sum = sum.divide(ten);
        }

        return dummy.next;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is overflow. Instead, add digit by digit directly from the lists,
     * exactly like paper addition, keeping only a carry.
     *
     * Pattern used: Linked-list digit simulation.
     *
     * OPTIMIZED ALGORITHM
     * 1. Create dummy and carry = 0.
     * 2. While either list has digits or carry exists, add available digits plus carry.
     * 3. New digit is sum % 10.
     * 4. New carry is sum / 10.
     * 5. Move input pointers forward.
     *
     * OPTIMIZED DRY RUN
     * 2+5=7 -> node 7 carry 0
     * 4+6=10 -> node 0 carry 1
     * 3+4+1=8 -> node 8 carry 0
     * result 7->0->8
     *
     * Time: O(max(m,n)), Space: O(1) extra besides output
     */
    public ListNode optimized(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            // Start with carry from the previous digit, just like paper addition.
            int sum = carry;

            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }

            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            // The current result digit is the ones place; tens place becomes next carry.
            tail.next = new ListNode(sum % 10);
            tail = tail.next;
            carry = sum / 10;
        }

        return dummy.next;
    }

    private BigInteger toNumber(ListNode node) {
        BigInteger value = BigInteger.ZERO;
        BigInteger place = BigInteger.ONE;

        while (node != null) {
            value = value.add(place.multiply(BigInteger.valueOf(node.val)));
            place = place.multiply(BigInteger.TEN);
            node = node.next;
        }

        return value;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
