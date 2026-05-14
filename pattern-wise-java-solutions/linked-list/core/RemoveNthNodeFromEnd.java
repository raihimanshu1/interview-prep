import java.util.ArrayList;
import java.util.List;

public class RemoveNthNodeFromEnd {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Remove the nth node from the end of a linked list and return the new head.
     *
     * Sample Input: head = 1 -> 2 -> 3 -> 4 -> 5, n = 2
     * Sample Output: 1 -> 2 -> 3 -> 5
     *
     * SCHOOL-LEVEL INTUITION:
     * The nth node from the end is easier to find if we know the total length. Optimized pointer
     * thinking lets us find it in one pass by keeping a gap of n nodes.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Store all nodes in a list. Then the node to remove is at index size - n.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Store node references while walking the list.
     * 2. Compute removeIndex = size - n.
     * 3. If removeIndex is 0, return head.next.
     * 4. Link the previous node to the node after the removed node.
     *
     * BRUTE FORCE DRY RUN
     * 1->2->3->4->5, n=2
     * nodes size = 5, removeIndex = 3, remove value 4
     * link 3.next to 5 -> 1->2->3->5
     *
     * Time: O(n), Space: O(n)
     */
    public ListNode bruteForce(ListNode head, int n) {
        List<ListNode> nodes = new ArrayList<>();
        for (ListNode current = head; current != null; current = current.next) {
            nodes.add(current);
        }

        int removeIndex = nodes.size() - n;
        if (removeIndex == 0) {
            return head.next;
        }

        ListNode previous = nodes.get(removeIndex - 1);
        previous.next = previous.next.next;
        return head;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * Storing all nodes is unnecessary. Move fast n steps ahead. Then move slow and fast together.
     * When fast reaches the end, slow is just before the node to remove.
     *
     * Pattern used: Two pointers with fixed gap.
     *
     * OPTIMIZED ALGORITHM
     * 1. Create dummy before head to handle removing the first node.
     * 2. Move fast n steps from dummy.
     * 3. Move slow and fast together until fast.next is null.
     * 4. Remove slow.next.
     * 5. Return dummy.next.
     *
     * OPTIMIZED DRY RUN
     * 1->2->3->4->5, n=2
     * fast moves to 2, then both move until fast is 5
     * slow is 3, so remove slow.next (4)
     *
     * Time: O(n), Space: O(1)
     */
    public ListNode optimized(ListNode head, int n) {
        // Dummy handles the special case where the head itself is removed.
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode fast = dummy;
        ListNode slow = dummy;

        for (int step = 0; step < n; step++) {
            // Create an n-node gap between fast and slow.
            fast = fast.next;
        }

        while (fast.next != null) {
            // Keep the gap fixed; when fast reaches the last node, slow is before the target.
            fast = fast.next;
            slow = slow.next;
        }

        // slow is directly before the target node, so bypass slow.next.
        slow.next = slow.next.next;
        return dummy.next;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }
}
