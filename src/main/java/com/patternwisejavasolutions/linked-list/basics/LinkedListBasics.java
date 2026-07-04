/**
 * LINKED LIST — Basics & Warmup
 * 
 * Before jumping into problems, master these fundamentals:
 * 1. Node structure (singly & doubly)
 * 2. Traversal (iterative & recursive)
 * 3. Insert at head/tail/middle
 * 4. Delete at head/tail/middle
 * 5. Search & Find length
 * 6. Reverse (iterative & recursive)
 * 7. Middle of list (slow & fast pointer)
 */

package com.patternwisejavasolutions.linkedList.basics;




public class LinkedListBasics {
    // ==========================================
    // 1. NODE STRUCTURE
    // ==========================================
    
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { 
            this.val = val; 
        }
        ListNode(int val, ListNode next) { 
            this.val = val; 
            this.next = next; 
        }
    }
    
    static class DoublyListNode {
        int val;
        DoublyListNode prev;
        DoublyListNode next;
        DoublyListNode(int val) { this.val = val; }
    }
    // ==========================================
    // 2. TRAVERSAL — O(n)
    // ==========================================
    
    // Iterative traversal
    public static void traverse(ListNode head) {
        ListNode curr = head;
        while (curr != null) {
            System.out.print(curr.val + " -> ");
            curr = curr.next;
        }
        System.out.println("null");
    }
    
    // Recursive traversal
    public static void traverseRecursive(ListNode head) {
        if (head == null) {
            System.out.println("null");
            return;
        }
        System.out.print(head.val + " -> ");
        traverseRecursive(head.next);
    }
    // ==========================================
    // 3. INSERT — O(1) at head, O(n) at position
    // ==========================================
    
    // Insert at head (0(1))
    public static ListNode insertAtHead(ListNode head, int val) {
        return new ListNode(val, head);
    }
    
    // Insert at tail (O(n))
    public static ListNode insertAtTail(ListNode head, int val) {
        if (head == null) return new ListNode(val);
        ListNode curr = head;
        while (curr.next != null) curr = curr.next;
        curr.next = new ListNode(val);
        return head;
    }
    
    // Insert after given node (O(1))
    public static void insertAfter(ListNode prev, int val) {
        if (prev == null) return;
        prev.next = new ListNode(val, prev.next);
    }
    
    // Insert at index (0-based, O(n))
    public static ListNode insertAtIndex(ListNode head, int val, int index) {
        if (index < 0) return head;
        if (index == 0) return new ListNode(val, head);
        ListNode curr = head;
        for (int i = 0; curr != null && i < index - 1; i++) {
            curr = curr.next;
        }
        if (curr == null) return head;  // index out of bounds
        curr.next = new ListNode(val, curr.next);
        return head;
    }
    // ==========================================
    // 4. DELETE — O(1) at head, O(n) at position
    // ==========================================
    
    // Delete head (O(1))
    public static ListNode deleteHead(ListNode head) {
        if (head == null) return null;
        return head.next;
    }
    
    // Delete tail (O(n))
    public static ListNode deleteTail(ListNode head) {
        if (head == null || head.next == null) return null;
        ListNode curr = head;
        while (curr.next.next != null) curr = curr.next;
        curr.next = null;
        return head;
    }
    
    // Delete by value (first occurrence, O(n))
    public static ListNode deleteByValue(ListNode head, int val) {
        if (head == null) return null;
        if (head.val == val) return head.next;
        ListNode curr = head;
        while (curr.next != null && curr.next.val != val) {
            curr = curr.next;
        }
        if (curr.next != null) curr.next = curr.next.next;
        return head;
    }
    
    // Delete by index (0-based, O(n))
    public static ListNode deleteAtIndex(ListNode head, int index) {
        if (head == null || index < 0) return head;
        if (index == 0) return head.next;
        ListNode curr = head;
        for (int i = 0; curr.next != null && i < index - 1; i++) {
            curr = curr.next;
        }
        if (curr.next != null) curr.next = curr.next.next;
        return head;
    }
    // ==========================================
    // 5. SEARCH — O(n)
    // ==========================================
    
    public static boolean search(ListNode head, int target) {
        ListNode curr = head;
        while (curr != null) {
            if (curr.val == target) return true;
            curr = curr.next;
        }
        return false;
    }
    
    // Find length (O(n))
    public static int length(ListNode head) {
        int count = 0;
        while (head != null) {
            count++;
            head = head.next;
        }
        return count;
    }
    
    // Recursive length
    public static int lengthRecursive(ListNode head) {
        if (head == null) return 0;
        return 1 + lengthRecursive(head.next);
    }
    // ==========================================
    // 6. REVERSE — O(n)
    // ==========================================
    
    // Iterative reverse (3 pointers: prev, curr, next)
    public static ListNode reverseIterative(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode next = curr.next;  // save next
            curr.next = prev;           // reverse link
            prev = curr;                // move prev forward
            curr = next;                // move curr forward
        }
        return prev;  // new head
    }
    
    // Recursive reverse
    public static ListNode reverseRecursive(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode newHead = reverseRecursive(head.next);
        head.next.next = head;  // make next node point back to current
        head.next = null;       // break old link
        return newHead;
    }
    // ==========================================
    // 7. MIDDLE (Slow & Fast Pointer) — O(n)
    // ==========================================
    
    // Return middle node (if even length, return 2nd middle)
    public static ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }
    
    // Get node at index from end (O(n))
    public static ListNode getFromEnd(ListNode head, int k) {
        ListNode fast = head;
        ListNode slow = head;
        for (int i = 0; i < k; i++) {
            if (fast == null) return null;  // k > length
            fast = fast.next;
        }
        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }
    // ==========================================
    // 8. UTILITY: Build list from array
    // ==========================================
    
    public static ListNode buildList(int[] arr) {
        if (arr == null || arr.length == 0) return null;
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        for (int val : arr) {
            curr.next = new ListNode(val);
            curr = curr.next;
        }
        return dummy.next;
    }
    
    // ==========================================
    // 9. PATTERN: Dummy Node Technique
    // ==========================================
    // Use a dummy node when head might change
    // Example: remove all elements equal to val
    
    public static ListNode removeElements(ListNode head, int val) {
        ListNode dummy = new ListNode(0, head);
        ListNode curr = dummy;
        while (curr.next != null) {
            if (curr.next.val == val) {
                curr.next = curr.next.next;
            } else {
                curr = curr.next;
            }
        }
        return dummy.next;
    }
    // ==========================================
    // MAIN — Test everything
    // ==========================================
    
    public static void main(String[] args) {
        // Create: [1, 2, 3, 4, 5]
        ListNode list = buildList(new int[]{1, 2, 3, 4, 5});
        
        System.out.println("=== LINKED LIST BASICS ===");
        
        System.out.print("Original: ");
        traverse(list);
        
        System.out.print("After insert at head (0): ");
        list = insertAtHead(list, 0);
        traverse(list);
        
        System.out.print("After insert at tail (6): ");
        list = insertAtTail(list, 6);
        traverse(list);
        
        System.out.print("After delete head: ");
        list = deleteHead(list);
        traverse(list);
        
        System.out.print("After delete tail: ");
        list = deleteTail(list);
        traverse(list);
        
        System.out.print("After delete value 3: ");
        list = deleteByValue(list, 3);
        traverse(list);
        
        System.out.print("Length: " + length(list) + ", Recursive: " + lengthRecursive(list));
        System.out.println();
        
        System.out.print("Search 4: " + search(list, 4) + ", Search 10: " + search(list, 10));
        System.out.println();
        
        System.out.print("Middle node: " + middleNode(list).val);
        System.out.println();
        
        System.out.print("Reversed: ");
        list = reverseIterative(list);
        traverse(list);
        
        System.out.print("Reversed back (recursive): ");
        list = reverseRecursive(list);
        traverse(list);
        
        System.out.println("\n=== KEY PATTERNS ===");
        System.out.println("1. Slow & Fast Pointer → detect cycle, find middle, find from end");
        System.out.println("2. Dummy Node → simplify head-change operations");
        System.out.println("3. Two Pointers → palindrome, intersection, remove nth from end");
        System.out.println("4. Reverse → reverse entire list, reverse sublist");
        System.out.println("5. Merge → merge two sorted lists, merge sort on linked list");
    }
}
