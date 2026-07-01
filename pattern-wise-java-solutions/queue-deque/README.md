# Queue & Deque

> **Core Pattern:** Queue (FIFO) for BFS and level-order; Deque for double-ended operations and sliding window max.  
> **Learning Path:** Stack/Queue implementations → Deque for sliding window max.

---

## 📖 Conceptual Foundation

### Queue vs Deque vs Stack
| Structure | Policy | Use Case |
|-----------|--------|----------|
| Queue | FIFO (First-In, First-Out) | BFS, level order, scheduling |
| Stack | LIFO (Last-In, First-Out) | Balanced brackets, monotonic |
| Deque | Both ends (Double-Ended Queue) | Sliding window max |

### Queue Template
```java
Queue<Integer> queue = new LinkedList<>();
queue.offer(1);     // add to tail
int val = queue.poll();  // remove from head
int peek = queue.peek(); // view head without removing
```

### Deque Template (Sliding Window Max)
```java
Deque<Integer> deque = new ArrayDeque<>(); // stores indices
for (int i = 0; i < n; i++) {
    // remove out-of-window indices
    while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) deque.pollFirst();
    // maintain decreasing order (remove smaller elements)
    while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast();
    deque.offerLast(i);
    if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
}
```

---

## 📚 Learning Order

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Implement Queue using Stack** | [ImplementQueueUsingStack.java](ImplementQueueUsingStack.java) | Two stacks — push to in-stack, reverse to out-stack on pop | 🟢 Easy |
| 2 | **Implement Stack using Queues** | [ImplementStackUsingQueues.java](ImplementStackUsingQueues.java) | Two queues — rotate after push | 🟢 Easy |
| 3 | **Deque Sliding Window Maximum** | [DequeSlidingWindowMaximum.java](DequeSlidingWindowMaximum.java) | Deque — maintain decreasing order of values | 🔴 Hard |

---

## 🔑 Key Insights

1. **Queue via Stack** → two stacks: one for push, one for pop/reverse
2. **Stack via Queue** → after each push, rotate queue n-1 times
3. **Sliding Window Max via Deque** → maintain decreasing order (indices), O(n)

---

## 🎯 Practice Checklist

- [ ] Queue using Stack implemented
- [ ] Stack using Queue implemented
- [ ] Deque Sliding Window Maximum