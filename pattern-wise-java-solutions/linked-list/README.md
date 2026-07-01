# Linked List

> **Core Pattern:** Pointer manipulation — slow/fast pointers, reversal, merging, and cycle detection.  
> **Learning Path:** Basic operations → node reversal → two-pointer patterns → advanced variations.

---

## 📖 Conceptual Foundation

### Linked List Techniques
| Technique | When to Use | Example |
|-----------|-------------|---------|
| Slow/Fast pointers | Cycle detection, middle node | LinkedListCycle, MiddleOfLinkedList |
| Dummy node | Avoid null checks at head | MergeTwoSortedLists, RemoveNthNode |
| Three-pointer reversal | Reverse in-place | ReverseLinkedList |
| Runner technique | Kth from end | RemoveNthNodeFromEnd |

### Template: Reverse Linked List
```java
ListNode prev = null, curr = head;
while (curr != null) {
    ListNode next = curr.next;
    curr.next = prev;
    prev = curr;
    curr = next;
}
return prev;
```

### Template: Slow/Fast (Cycle Detection)
```java
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
    if (slow == fast) { /* cycle detected */ }
}
```

---

## 📚 Learning Order

### Phase 1: Core — Basic manipulations

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Reverse Linked List** | [core/ReverseLinkedList.java](core/ReverseLinkedList.java) | Three-pointer reversal (prev, curr, next) | 🟢 Easy |
| 2 | **Merge Two Sorted Lists** | [core/MergeTwoSortedLists.java](core/MergeTwoSortedLists.java) | Dummy node + merge comparison | 🟢 Easy |
| 3 | **Linked List Cycle** | [core/LinkedListCycle.java](core/LinkedListCycle.java) | Slow/fast — detect if cycle exists | 🟢 Easy |
| 4 | **Remove Nth Node From End** | [core/RemoveNthNodeFromEnd.java](core/RemoveNthNodeFromEnd.java) | Dummy + two-pointer (offset by N) | 🟡 Medium |

### Phase 2: Two-Pointer Patterns

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 5 | **Middle of Linked List** | [two-pointer-patterns/MiddleOfLinkedList.java](two-pointer-patterns/MiddleOfLinkedList.java) | Slow/fast — fast moves twice speed | 🟢 Easy |
| 6 | **Detect Cycle II (Start Node)** | [two-pointer-patterns/DetectCycleIIStartNode.java](two-pointer-patterns/DetectCycleIIStartNode.java) | After detection, reset slow to head, same speed | 🟡 Medium |

### Phase 3: Variations

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 7 | **Palindrome Linked List** | [variations/PalindromeLinkedList.java](variations/PalindromeLinkedList.java) | Find middle → reverse second half → compare | 🟢 Easy |
| 8 | **Intersection of Two Linked Lists** | [variations/IntersectionOfTwoLinkedLists.java](variations/IntersectionOfTwoLinkedLists.java) | Two pointers — reset at head of other list | 🟢 Easy |
| 9 | **Add Two Numbers** | [variations/AddTwoNumbers.java](variations/AddTwoNumbers.java) | Dummy + carry traversal | 🟡 Medium |
| 10 | **Reorder List** | [variations/ReorderList.java](variations/ReorderList.java) | Middle → reverse second half → merge interleave | 🟡 Medium |
| 11 | **Copy List with Random Pointer** | [variations/CopyListWithRandomPointer.java](variations/CopyListWithRandomPointer.java) | Interleaving nodes: `A → A' → B → B'` | 🟡 Medium |
| 12 | **Flatten Multilevel Doubly Linked List** | [variations/FlattenMultilevelDoublyLinkedList.java](variations/FlattenMultilevelDoublyLinkedList.java) | Stack-based or recursive DFS traversal | 🟡 Medium |

---

## 🔑 Key Insights

1. **Dummy node** pattern eliminates null head edge cases
2. **Slow/Fast** = cycle detection (fast moves 2×)
3. **Reverse second half** = common for palindrome, reorder
4. **Interleaving** pattern useful for problems requiring "weaving" two lists

---

## 🎯 Practice Checklist

- [ ] Phase 1: Core operations
- [ ] Phase 2: Two-pointer patterns
- [ ] Phase 3: Advanced variations