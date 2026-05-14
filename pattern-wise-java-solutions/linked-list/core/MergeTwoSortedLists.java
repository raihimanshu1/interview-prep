import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergeTwoSortedLists {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Merge two sorted linked lists into one sorted linked list.
     *
     * Sample Input: list1 = 1 -> 2 -> 4, list2 = 1 -> 3 -> 4
     * Sample Output: 1 -> 1 -> 2 -> 3 -> 4 -> 4
     *
     * SCHOOL-LEVEL INTUITION:
     * Since both lists are sorted, the smallest remaining value must be at the front of one list.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Collect all values from both lists, sort them, and build a new linked list.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Store every value from list1 and list2.
     * 2. Sort the values.
     * 3. Create a new list in sorted order.
     *
     * BRUTE FORCE DRY RUN
     * values from lists: [1,2,4,1,3,4]
     * sorted: [1,1,2,3,4,4]
     * build output list in that order
     *
     * Time: O((m+n) log(m+n)), Space: O(m+n)
     */
    public ListNode bruteForce(ListNode list1, ListNode list2) {
        List<Integer> values = new ArrayList<>();
        collect(list1, values);
        collect(list2, values);
        Collections.sort(values);

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int value : values) {
            tail.next = new ListNode(value);
            tail = tail.next;
        }

        return dummy.next;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * Sorting again wastes the fact that both lists are already sorted. Compare the two front nodes,
     * attach the smaller one, and move only that list forward.
     *
     * Pattern used: Two pointers / merge step.
     *
     * OPTIMIZED ALGORITHM
     * 1. Create a dummy node and tail pointer.
     * 2. While both lists have nodes, attach the smaller current node.
     * 3. Move the pointer of the list that donated the node.
     * 4. Attach the leftover list.
     *
     * OPTIMIZED DRY RUN
     * list1 1->2->4, list2 1->3->4
     * pick 1 from list1, pick 1 from list2, pick 2, pick 3, pick 4, attach remaining 4
     *
     * Time: O(m+n), Space: O(1) extra
     */
    public ListNode optimized(ListNode list1, ListNode list2) {
        // Dummy gives us a stable node before the answer, so appending the first real node is easy.
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                // list1 has the smaller front node, so it is the next sorted answer node.
                tail.next = list1;
                list1 = list1.next;
            } else {
                // list2 has the smaller front node, so it is the next sorted answer node.
                tail.next = list2;
                list2 = list2.next;
            }

            // Move tail to the node we just attached.
            tail = tail.next;
        }

        // One list may still have sorted nodes left; they can be attached directly.
        tail.next = list1 != null ? list1 : list2;
        return dummy.next;
    }

    private void collect(ListNode node, List<Integer> values) {
        while (node != null) {
            values.add(node.val);
            node = node.next;
        }
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
