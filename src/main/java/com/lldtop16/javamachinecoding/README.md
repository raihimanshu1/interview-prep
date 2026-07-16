# Java Machine Coding & DSA — Complete Interview Guide

> 🎯 **Real Interview Questions** from top companies (Amazon, Google, Uber, Flipkart, Nielsen, etc.) with **deep intuition** — not just solutions.

---

## 📋 Table of Contents

### SECTION 1: Java Machine Coding Rounds
1.1 [What Companies Ask & How To Prepare](#11-machine-coding-patterns)
1.2 [Top 50 Machine Coding Questions by Company](#12-top-50-questions)
1.3 [Evaluation Criteria & Time Management](#13-evaluation-criteria)
1.4 [Common Pitfalls & How to Avoid Them](#14-common-pitfalls)

### SECTION 2: Collections & DSA Deep Dive
2.1 [ArrayList — The Workhorse](#21-arraylist)
2.2 [LinkedList — When You Need Deletion](#22-linkedlist)
2.3 [Stack — LIFO Based Problems](#23-stack)
2.4 [Queue — FIFO Based Problems](#24-queue)
2.5 [PriorityQueue & Heap — The K-th Element King](#25-priorityqueue)
2.6 [HashMap — The O(1) Superstar](#26-hashmap)
2.7 [HashSet — Uniqueness Tracker](#27-hashset)
2.8 [TreeMap/TreeSet — Sorted Structures](#28-treemap-treeset)
2.9 [Deque — The Double-Ended Swiss Knife](#29-deque)
2.10 [Concurrent Collections](#210-concurrent-collections)
2.11 [Common DSA Problems by Pattern](#211-dsa-patterns)

---

# SECTION 1: Java Machine Coding Rounds

---

## 1.1 Machine Coding Patterns

### What Actually Gets Asked (2024-2025 Trends)

```
Tier 1 (95% chance):    Parking Lot, Splitwise, BookMyShow, Elevator
Tier 2 (70% chance):    Vending Machine, Cab Booking, Task Management
Tier 3 (40% chance):    Chess, File System, Logging Framework, Pub-Sub
Tier 4 (20% chance):    Key-Value Store, Database, Load Balancer
```

### Company-Specific Patterns

| Company | Focus Area | Typical Problem Duration |
|---------|-----------|------------------------|
| **Amazon** | OOP + Design Patterns + Concurrency | 90 min |
| **Google** | Scalability + Clean APIs + Edge Cases | 90 min |
| **Uber** | Real-time + State Machines | 120 min |
| **Flipkart** | E-commerce flows + Inventory | 90 min |
| **Nielsen** | Data processing + Caching | 90 min |
| **Microsoft** | System thinking + Integration | 90 min |
| **Walmart** | Inventory + Checkout flows | 60 min |
| **Oracle** | Database concepts + Transactions | 120 min |

### What They Judge (Exact Rubric)

```
1. Requirements Clarification     15%  ← Can you handle ambiguity?
2. OOP Design & Abstraction       25%  ← Can you model real-world?
3. Design Patterns Usage          20%  ← Can you make it extensible?
4. Working Code                   25%  ← Can you actually code it?
5. Edge Cases & Error Handling    10%  ← Can you think about failures?
6. Clean Code & Naming             5%  ← Can you write maintainable code?
```

---

## 1.2 Top 50 Machine Coding Questions

### Most Commonly Asked (2024-2025)

```
┌─────────────────────────────────────────────────────────────────────┐
│                     TOP 10 (NEARLY EVERY INTERVIEW)                  │
├─────────────────────────────────────────────────────────────────────┤
│ 1.  Parking Lot System           │ 6.  In-Memory Cache (LRU/LFU)    │
│ 2.  Splitwise Clone              │ 7.  Rate Limiter (All 4 algos)   │
│ 3.  Movie Ticket Booking         │ 8.  Elevator System              │
│ 4.  Cab Booking System (Uber)    │ 9.  Vending Machine (State)      │
│ 5.  Snake & Ladder Game          │ 10. Task Management (Jira)       │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                     NEXT 15 (VERY COMMON)                            │
├─────────────────────────────────────────────────────────────────────┤
│ 11. Library Management System    │ 19. Coffee Vending Machine       │
│ 12. ATM Machine                  │ 20. Airline Reservation          │
│ 13. Chess Game                   │ 21. Social Media Feed            │
│ 14. Tic-Tac-Toe                  │ 22. URL Shortener                │
│ 15. Hotel Booking System         │ 23. Search Autocomplete (Trie)   │
│ 16. E-Commerce Cart              │ 24. Pub-Sub System               │
│ 17. Restaurant Table Reservation │ 25. Meeting Scheduler (Calendar)  │
│ 18. Logger Framework             │                                  │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                     NEXT 25 (SPECIFIC DOMAINS)                       │
├─────────────────────────────────────────────────────────────────────┤
│ 26. Key-Value Store             │ 39. API Rate Limiter (Redis)     │
│ 27. Distributed Cache           │ 40. Content Delivery Network     │
│ 28. File System (Unix)          │ 41. Web Crawler                  │
│ 29. Database (SQLite-like)      │ 42. Job Scheduler                │
│ 30. Load Balancer               │ 43. Message Queue (Kafka-lite)   │
│ 31. Connection Pool             │ 44. Event Bus                    │
│ 32. Thread Pool                 │ 45. Config Management System     │
│ 33. Object Pool                 │ 46. Feature Flag System          │
│ 34. Notification System         │ 47. A/B Testing Framework        │
│ 35. Payment Gateway             │ 48. Log Aggregator (ELK-lite)    │
│ 36. Inventory Management        │ 49. Monitoring Dashboard         │
│ 37. Shopping Cart               │ 50. Real-Time Leaderboard        │
│ 38. Order Management System     │                                  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 1.3 Evaluation Criteria & Time Management

### The 60-Minute Clock

```
 0-5 min:   Requirements clarification
 5-10 min:  Problem decomposition + HLD sketch
10-20 min:  Design decisions + Pattern selection
20-45 min:  Implementation (core flow only!)
45-50 min:  Edge cases discussion
50-55 min:  Testing strategy
55-60 min:  Follow-up questions
```

### The #1 Mistake Candidates Make

**❌ WRONG**: "Let me write all the code first, then discuss design."

**✅ RIGHT**: "Let me discuss the design first, agree on scope, then implement the CORE flow."

**Why?** Your code will have bugs. The interviewer doesn't judge you on bug-free code. They judge you on:
1. Did you ask questions BEFORE coding? (indicates real-world readiness)
2. Did you identify the hard parts FIRST? (indicates problem-solving)
3. Did you make sensible trade-offs? (indicates experience)
4. Did you handle edge cases? (indicates thoroughness)

---

## 1.4 Common Pitfalls & How to Avoid Them

| Pitfall | Why It Happens | How To Avoid |
|---------|---------------|--------------|
| **God Class** | Putting everything in one class | Split into Model/Service/Strategy layers |
| **No Interfaces** | Not planning for change | Start each major component with an interface |
| **Hardcoded Values** | "I'll parameterize later" | Use constants/enums from the start |
| **Missing Edge Cases** | Only thinking happy path | After writing code, spend 2 min on "what could break" |
| **Too Many Patterns** | Trying to impress | Only use a pattern if it SOLVES a problem |
| **Over-engineering** | Building for future that may never come | Ask "is this required NOW?" |
| **Silent Coding** | Not talking while coding | Narrate every line — "I'm adding this because..." |

---

# SECTION 2: Collections & DSA Deep Dive

---

## 2.1 ArrayList

### What It Is
A **resizable array** that grows dynamically when elements are added.

### Internal Implementation

```java
/**
 * INTUITION: Think of ArrayList as a regular array that auto-expands.
 * 
 * When you create: ArrayList<String> list = new ArrayList<>();
 * Internally: Object[] elementData = new Object[10];  // default capacity = 10
 * 
 * When you add the 11th element:
 * 1. Check: is size == elementData.length? (capacity full)
 * 2. Grow: newCapacity = oldCapacity + (oldCapacity >> 1)  // 1.5x growth
 * 3. Copy: Arrays.copyOf(elementData, newCapacity)  // O(n) copy!
 * 4. Add: elementData[size++] = newElement
 * 
 * WHY 1.5x growth? Not 2x, not 1.25x?
 * - 2x wastes too much memory (after 10→20→40→80, you might use only 41)
 * - 1x would cause too many copies (10→11→12→13..., each add is O(n))
 * - 1.5x is the sweet spot: amortized O(1) add, ~33% wasted space max
 */
public class ArrayList<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elementData;  // The internal array
    private int size;              // Number of elements (NOT array length)

    public boolean add(E e) {
        // INTUITION: This is amortized O(1), but occasionally O(n) when resizing
        ensureCapacityInternal(size + 1);  // Check if we need to grow
        elementData[size++] = e;           // Place at end, increment size
        return true;
    }

    private void ensureCapacityInternal(int minCapacity) {
        if (minCapacity > elementData.length) {
            // INTUITION: Right shift by 1 = divide by 2
            // oldCapacity + (oldCapacity/2) = 1.5x growth
            int newCapacity = elementData.length + (elementData.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;  // Handle overflow case
            }
            // INTUITION: Arrays.copyOf creates a NEW array and copies ALL elements
            // This is O(n) — the expensive operation
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
    }

    public E get(int index) {
        // INTUITION: O(1) because array access is direct memory address
        // elementData[0] is at memory address X
        // elementData[i] is at memory address X + (i * elementSize)
        // This is why array access is the fastest data structure operation
        rangeCheck(index);
        return (E) elementData[index];
    }

    public void add(int index, E element) {
        // INTUITION: O(n) because we must SHIFT all elements right
        // Example: [A, B, C, D, _] insert X at index 1
        // Step 1: Shift right: [A, B, B, C, D]  (D→4, C→3, B→2)
        // Step 2: Insert:      [A, X, B, C, D]
        rangeCheckForAdd(index);
        ensureCapacityInternal(size + 1);
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size++;
    }

    public E remove(int index) {
        // INTUITION: O(n) because we must SHIFT all elements left
        // Example: [A, B, C, D] remove index 1 (B)
        // Step 1: Shift left: [A, C, D, D]  (C→1, D→2)
        // Step 2: Null last:  [A, C, D, null]
        rangeCheck(index);
        E oldValue = (E) elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null;  // Clear for GC
        return oldValue;
    }
}
```

### Complexity Summary

| Operation | Time | Why |
|-----------|------|-----|
| get(i) | O(1) | Direct array index access |
| set(i, val) | O(1) | Direct array index access |
| add(e) | **Amortized O(1)** | Usually O(1), but O(n) when array grows |
| add(i, e) | O(n) | Must shift elements right |
| remove(i) | O(n) | Must shift elements left |
| contains(e) | O(n) | Linear search |
| indexOf(e) | O(n) | Linear search |
| size() | O(1) | Pre-computed field |

### When to Use

```
✅ USE ArrayList when:
   - You need fast random access (get/set by index)
   - You mostly add/remove at the END
   - You read more than you write
   - You know approximate size in advance

❌ DON'T use ArrayList when:
   - You frequently add/remove at the BEGINNING
   - You frequently insert in the MIDDLE
   - Memory is extremely tight (reserved capacity)
```

### Thread-Safe Variant

```java
/**
 * INTUITION: ArrayList is NOT thread-safe.
 * If Thread A adds while Thread B reads → ConcurrentModificationException or stale data.
 * 
 * Solutions (in order of preference):
 * 1. CopyOnWriteArrayList — Best for READ-heavy, write-rare scenarios
 * 2. Collections.synchronizedList — Wraps with synchronized blocks
 * 3. Vector — Legacy, synchronized, DON'T use
 */
List<String> threadSafe = new CopyOnWriteArrayList<>();

/**
 * INTUITION behind CopyOnWriteArrayList:
 * - Every mutative operation (add, set, remove) creates a NEW copy of the array
 * - Reads never need synchronization (they read the old copy while write happens)
 * - EXPENSIVE for writes but FAST for reads
 * 
 * Perfect for: Listener lists, observer registries (added rarely, read on every event)
 * Terrible for: Frequent writes
 */
```

### Common Interview Questions

**Q: What's the difference between ArrayList and LinkedList? When would you use each?**

```
ArrayList:
- Internal: dynamic array
- get(i): O(1) — fast random access
- add(e): amortized O(1) — fast at end
- add(0, e): O(n) — SLOW at beginning
- Memory: less overhead (only stores data + array ref)

LinkedList:
- Internal: doubly linked list of nodes
- get(i): O(n) — must traverse from head
- add(e): O(1) — fast at end (if tail pointer)
- add(0, e): O(1) — fast at beginning
- Memory: MORE overhead (node has prev, next, data references)

INTUITION:
- Need fast INDEX access? → ArrayList
- Need fast HEAD/TAIL insertion? → LinkedList (or ArrayDeque)
- Need to frequently delete from middle? → Neither! Use a Map or skip list
```

---

## 2.2 LinkedList

### What It Is
A **doubly linked list** — each element (node) has references to the previous and next nodes.

### Internal Implementation

```java
/**
 * INTUITION: Think of a train. Each carriage (node) has:
 * - A link to the previous carriage (prev)
 * - A link to the next carriage (next)  
 * - Passengers inside (data)
 * 
 * To add a carriage at the front: 
 *   Just connect it to the current first carriage. O(1)
 *   
 * To remove the last carriage:
 *   Just disconnect the second-last carriage's next link. O(1)
 *   
 * BUT to find the 5th carriage from the front:
 *   You must walk: 1st → 2nd → 3rd → 4th → 5th. O(n)
 */
public class LinkedList<E> {
    // INTUITION: These three fields are ALL you need to represent a linked list
    Node<E> first;  // Head of the list — null if empty
    Node<E> last;   // Tail of the list — null if empty
    int size;       // Number of elements

    private static class Node<E> {
        // INTUITION: Each node is a self-contained package
        E item;          // The actual data
        Node<E> next;    // Pointer to next node (forward direction)
        Node<E> prev;    // Pointer to previous node (backward direction)

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * INTUITION: Adding at the beginning.
     * BEFORE: first → [B] → [C]
     * Step 1: Create new node [A] with next = [B]
     * Step 2: Set [B].prev = [A]  
     * Step 3: Set first = [A]
     * AFTER:  first → [A] ↔ [B] ↔ [C]
     * 
     * Time: O(1) — only pointer changes, no elements moved!
     */
    public void addFirst(E e) {
        Node<E> f = first;
        Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null) {
            last = newNode;  // List was empty, new node is also the last
        } else {
            f.prev = newNode;
        }
        size++;
    }

    /**
     * INTUITION: Getting by index REQUIRES traversal.
     * 
     * Optimization: If index < size/2, search from front.
     *               If index >= size/2, search from back.
     * 
     * This is still O(n) — just a 2x constant factor improvement.
     */
    public E get(int index) {
        checkElementIndex(index);
        if (index < (size >> 1)) {  // index < size/2? Search from front
            Node<E> x = first;
            for (int i = 0; i < index; i++) {
                x = x.next;  // Walk forward
            }
            return x.item;
        } else {  // Search from back
            Node<E> x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;  // Walk backward
            }
            return x.item;
        }
    }
}
```

### Complexity Summary

| Operation | Time | Why |
|-----------|------|-----|
| addFirst(e) | O(1) | Just update head pointer |
| addLast(e) | O(1) | Just update tail pointer |
| removeFirst() | O(1) | Just update head pointer |
| removeLast() | O(1) | Just update tail pointer |
| get(i) | O(n) | Must traverse from head/tail |
| contains(e) | O(n) | Must traverse all nodes |
| remove(e) | O(n) | Must find the node first |

### When to Use

```
✅ USE LinkedList when:
   - You need a Queue/Deque (use ArrayDeque instead — faster)
   - You frequently add/remove at BOTH ends
   - You NEVER need random access by index
   - Memory overhead isn't a concern

❌ DON'T use LinkedList when:
   - You need get(i) frequently (O(n) kills performance)
   - Memory is tight (each node has 2 extra references)
   - You need thread safety (must use ConcurrentLinkedDeque)
```

### Interview Trick: When LinkedList Is Actually Better

```java
/**
 * INTUITION: If you're frequently removing from the middle,
 * LinkedList CAN be O(1) if you already have the node reference!
 * 
 * Real-world example: A task list where you have a reference to a task
 * and want to delete it quickly.
 */
class TaskList {
    // INTUITION: HashMap gives O(1) lookup to find the node
    // LinkedList gives O(1) deletion once you have the node
    Map<String, Node<Task>> taskMap = new HashMap<>();
    LinkedList<Task> taskList = new LinkedList<>();
    
    void addTask(Task t) {
        Node<Task> node = taskList.addLastNode(t);  // O(1)
        taskMap.put(t.getId(), node);                // O(1)
    }
    
    void removeTask(String taskId) {
        // INTUITION: WITHOUT map → O(n) to find, O(1) to delete
        // WITH map → O(1) to find node, O(1) to delete
        Node<Task> node = taskMap.get(taskId);  // O(1)
        taskList.removeNode(node);              // O(1) — THIS IS THE KEY!
    }
}
```

---

## 2.3 Stack

### What It Is
**Last-In-First-Out (LIFO)** data structure. Like a stack of plates — you take the top one first.

### Internal Implementation

```java
/**
 * INTUITION: Stack can be implemented with:
 * 1. Array (grows at end, shrinks from end) — ArrayList style
 * 2. LinkedList (add/remove from head)
 * 
 * Java's Stack class extends Vector (synchronized ArrayList).
 * But for interviews, use ArrayDeque — it's FASTER and more modern.
 * 
 * WHY ArrayDeque over Stack?
 * - Stack is synchronized (unnecessary overhead)
 * - Stack extends Vector (inherits all Vector's baggage)
 * - ArrayDeque is unsynchronized, faster, and has better APIs
 */
Deque<Integer> stack = new ArrayDeque<>();
// OR
Stack<Integer> oldStack = new Stack<>();  // Legacy, avoid in interviews

/**
 * INTUITION: Stack operations are ALL O(1) because we always work with the TOP.
 * 
 * push(e)  → add at end of array (amortized O(1))
 * pop()    → remove from end of array (O(1))
 * peek()   → look at end of array (O(1))
 */
stack.push(1);    // [1]
stack.push(2);    // [1, 2]
stack.push(3);    // [1, 2, 3]
int top = stack.pop();   // returns 3, stack becomes [1, 2]
int peek = stack.peek(); // returns 2, stack stays [1, 2]
boolean empty = stack.isEmpty(); // false
```

### Classic Interview Problems — Deep Dive

#### Problem 1: Valid Parentheses

```java
/**
 * INTUITION: Think of opening brackets as "push to stack" 
 * and closing brackets as "check if top matches, then pop".
 * 
 * If at the end the stack is empty, all brackets matched.
 * 
 * WHY stack here? Because brackets MUST close in reverse order of opening.
 * 
 * { [ ( ) ] }  → correct: last opened = first to close
 * { [ ( ] ) }  → wrong:   ( was opened before [ but closed inside it
 * 
 * LIFO nature of stack perfectly captures "last opened = first closed" rule.
 */
public boolean isValid(String s) {
    // INTUITION: Map closing brackets to their matching opening brackets
    // This avoids a chain of if-else statements
    Map<Character, Character> matching = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );
    
    Deque<Character> stack = new ArrayDeque<>();
    
    for (char c : s.toCharArray()) {
        if (matching.containsKey(c)) {
            // INTUITION: c is a closing bracket like ')', '}', ']'
            // If stack is empty → no opening bracket → INVALID
            // If top doesn't match → wrong type of bracket → INVALID
            char top = stack.isEmpty() ? '#' : stack.pop();
            if (top != matching.get(c)) {
                return false;
            }
        } else {
            // INTUITION: c is an opening bracket like '(', '{', '['
            // Push it to stack — it must be closed LATER
            stack.push(c);
        }
    }
    
    // INTUITION: At end, stack must be empty.
    // If not empty → some opening bracket was never closed
    return stack.isEmpty();
}
```

#### Problem 2: Min Stack (Design a stack that supports getMin() in O(1))

```java
/**
 * INTUITION: Store the minimum alongside each element.
 * 
 * When you push 5:  stack = [5],    minStack = [5],  min = 5
 * When you push 3:  stack = [5,3],  minStack = [5,3], min = 3  
 * When you push 7:  stack = [5,3,7],minStack = [5,3,3], min = 3
 * When you pop 7:   stack = [5,3],  minStack = [5,3], min = 3
 * When you pop 3:   stack = [5],    minStack = [5],   min = 5
 * 
 * KEY INSIGHT: Each element in minStack stores the minimum value
 * at that point in the stack. When we pop, we discard that min.
 * 
 * WHY TWO stacks? Because the minimum changes as elements are removed.
 * If we only stored a single variable 'min', we couldn't restore the
 * previous minimum after popping.
 */
class MinStack {
    // INTUITION: Two stacks running in parallel
    // stack stores all elements
    // minStack stores the current minimum at each level
    private Deque<Integer> stack = new ArrayDeque<>();
    private Deque<Integer> minStack = new ArrayDeque<>();

    public void push(int val) {
        stack.push(val);
        // INTUITION: The new minimum is whichever is smaller:
        // the new value or the previous minimum
        if (minStack.isEmpty()) {
            minStack.push(val);
        } else {
            minStack.push(Math.min(val, minStack.peek()));
        }
    }

    public void pop() {
        stack.pop();
        minStack.pop();  // Discard the min for this level
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}
```

#### Problem 3: Daily Temperatures (Next Greater Element)

```java
/**
 * INTUITION: This is the classic "Next Greater Element" pattern.
 * 
 * Problem: Given [73, 74, 75, 71, 69, 72, 76, 73]
 * Return:  [1,  1,  4,  2,  1,  1,  0,  0]
 *         (days until a warmer temperature)
 * 
 * KEY INSIGHT: We process from LEFT to RIGHT.
 * Store indices in stack where we HAVEN'T found a warmer day yet.
 * When we find a warmer day, POP all colder days from stack and update.
 * 
 * WHY LEFT TO RIGHT? 
 * When we see a warm day, it's the NEXT warmer day for ALL previous colder days.
 * 
 * Visual:
 * Day 0 (73°): Stack = [0]  — no warmer day yet
 * Day 1 (74°): 74 > 73 → pop 0, answer[0] = 1-0 = 1. Stack = [1]
 * Day 2 (75°): 75 > 74 → pop 1, answer[1] = 2-1 = 1. Stack = [2]
 * Day 3 (71°): 71 < 75 → push 3. Stack = [2, 3]
 * Day 4 (69°): 69 < 71 → push 4. Stack = [2, 3, 4]
 * Day 5 (72°): 72 > 69 → pop 4, answer[4] = 5-4 = 1
 *              72 > 71 → pop 3, answer[3] = 5-3 = 2
 *              72 < 75 → push 5. Stack = [2, 5]
 * Day 6 (76°): 76 > 72 → pop 5, answer[5] = 6-5 = 1
 *              76 > 75 → pop 2, answer[2] = 6-2 = 4. Stack = [6]
 * Day 7 (73°): 73 < 76 → push 7. Stack = [6, 7]
 * End: remaining indices [6, 7] have answer = 0 (no warmer day)
 */
public int[] dailyTemperatures(int[] temperatures) {
    int n = temperatures.length;
    int[] answer = new int[n];
    
    // INTUITION: Stack stores INDICES (not temperatures)
    // We keep indices of days waiting for a warmer temperature
    Deque<Integer> stack = new ArrayDeque<>();
    
    for (int currentDay = 0; currentDay < n; currentDay++) {
        int currentTemp = temperatures[currentDay];
        
        // INTUITION: While there are colder days waiting,
        // today is their FIRST warmer day
        while (!stack.isEmpty() && 
               temperatures[stack.peek()] < currentTemp) {
            int prevDay = stack.pop();
            answer[prevDay] = currentDay - prevDay;
        }
        
        // INTUITION: Today is now waiting for a warmer day
        stack.push(currentDay);
    }
    
    // INTUITION: Remaining days in stack never found a warmer day
    // They already have default value 0 — no need to update
    
    return answer;
}
```

### Stack Interview Problem Patterns

```
┌─────────────────────────────────────────────────────────────────────┐
│                    STACK PATTERN CATALOG                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                    │
│ 1. PARENTHESES PATTERN                                             │
│    "Valid Parentheses", "Generate Parentheses", "Remove Duplicates"│
│    → Opening = push, Closing = check & pop                         │
│                                                                    │
│ 2. MONOTONIC STACK PATTERN                                          │
│    "Next Greater Element", "Daily Temperatures", "Stock Span"       │
│    → Maintain increasing/decreasing stack, pop when order breaks   │
│                                                                    │
│ 3. EXPRESSION EVALUATION                                            │
│    "Evaluate Reverse Polish Notation", "Basic Calculator"           │
│    → Operand stack + Operator stack, apply operators on pop         │
│                                                                    │
│ 4. ITERATIVE TREE TRAVERSAL                                         │
│    "Inorder/Preorder/Postorder without Recursion"                   │
│    → Stack simulates recursion call stack                           │
│                                                                    │
│ 5. DESIGN PATTERN                                                   │
│    "Min Stack", "Max Stack", "Stack with Increment"                 │
│    → Auxiliary stack to track additional state                      │
│                                                                    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2.4 Queue

### What It Is
**First-In-First-Out (FIFO)** data structure. Like a line at a ticket counter.

### Internal Implementation

```java
/**
 * INTUITION: Queue can be implemented with:
 * 1. Array (circular buffer) — ArrayDeque
 * 2. LinkedList — slower but simpler
 * 
 * In Java, Queue is an INTERFACE, not a class.
 * LinkedList and ArrayDeque both implement it.
 */

// INTUITION: ArrayDeque is FASTER than LinkedList for queue operations
// because it uses a circular array (no node objects, better cache locality)
Queue<Integer> queue = new ArrayDeque<>();

// INTUITION: LinkedList is useful when you need Queue + List capabilities
Queue<Integer> linkedQueue = new LinkedList<>();

/**
 * INTUITION: Queue operations are ALL O(1).
 * 
 * offer(e) → add at tail (amortized O(1))
 * poll()   → remove from head (O(1))
 * peek()   → look at head (O(1))
 */
queue.offer(1);  // [1]
queue.offer(2);  // [1, 2]
queue.offer(3);  // [1, 2, 3]
int head = queue.poll();   // returns 1, queue = [2, 3]
int peek = queue.peek();   // returns 2, queue = [2, 3]
```

### Classic Problems — Deep Dive

#### Problem 1: Implement Stack Using Queues

```java
/**
 * INTUITION: Queues are FIFO. Stacks are LIFO.
 * To make a stack out of queues, we need a trick.
 * 
 * KEY INSIGHT: After pushing, rotate the queue so the new element is at FRONT.
 * 
 * push(1): queue = [1]
 * push(2): queue = [1, 2], then rotate: [2, 1]
 * push(3): queue = [2, 1, 3], then rotate: [3, 2, 1]
 * pop():   returns 3 (front), queue = [2, 1]  ← STACK behavior!
 * 
 * HOW ROTATION WORKS:
 * After adding new element at back, repeatedly move front to back
 * (size-1) times. This brings the new element to the front.
 * 
 * Example: push(3) when queue = [2, 1]
 * Step 1: offer(3) → [2, 1, 3]
 * Step 2: poll()=2 → offer(2) → [1, 3, 2]
 * Step 3: poll()=1 → offer(1) → [3, 2, 1]  ← 3 is at front!
 */
class StackUsingQueues {
    private Queue<Integer> queue = new ArrayDeque<>();

    public void push(int x) {
        queue.offer(x);  // Add at back
        // INTUITION: Rotate so that x comes to front
        // This is the TRICK that makes FIFO behave like LIFO
        int size = queue.size();
        for (int i = 1; i < size; i++) {
            // Move all previous elements to back, one by one
            queue.offer(queue.poll());
        }
    }

    public int pop() {
        return queue.poll();  // Front is the most recent push
    }

    public int top() {
        return queue.peek();
    }

    public boolean empty() {
        return queue.isEmpty();
    }
}
```

#### Problem 2: Sliding Window Maximum

```java
/**
 * INTUITION: This is the KING of deque problems.
 * 
 * Problem: Given [1,3,-1,-3,5,3,6,7] and window size k=3
 * Return:  [3,3,5,5,6,7] — maximum in each window
 * 
 * KEY INSIGHT: Use a Deque (not Queue) that stores INDICES.
 * Maintain elements in DECREASING order of VALUE.
 * Front of deque = index of maximum element in current window.
 * 
 * WHY deque? We need to:
 * - Remove from back when new element is larger (for decreasing order)
 * - Remove from front when element falls out of window
 * - Peek at front to get the maximum
 * 
 * Visual walkthrough:
 * 
 * Window [0-2]: [1, 3, -1]
 *   Process 0: deque = [0] (value 1)
 *   Process 1: 3 > 1 → pop back(0) → deque = [1] (value 3)
 *   Process 2: -1 < 3 → push back → deque = [1, 2]
 *   Max: 3 (at index 1)
 * 
 * Window [1-3]: [3, -1, -3]
 *   Process 3: -3 < -1 → push back → deque = [1, 2, 3]
 *   Max: 3 (at index 1)
 * 
 * Window [2-4]: [-1, -3, 5]
 *   Process 4: 5 > -3 → pop back(3) → 5 > -1 → pop back(2) → deque = [4]
 *   Also: index 1 fell out of window → remove from front if needed
 *   Max: 5 (at index 4) 
 */
public int[] maxSlidingWindow(int[] nums, int k) {
    if (nums == null || nums.length == 0) return new int[0];
    
    int n = nums.length;
    int[] result = new int[n - k + 1];
    
    // INTUITION: Deque stores INDICES, not values
    // Elements are in DECREASING order of value
    Deque<Integer> deque = new ArrayDeque<>();
    
    for (int i = 0; i < n; i++) {
        // INTUITION STEP 1: Remove indices that are out of current window
        // If deque front is index 0 and window starts at 2 → index 0 is out
        if (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // INTUITION STEP 2: Maintain decreasing order
        // Remove from back while last element is smaller than current
        // WHY? Current element is newer AND larger — older smaller elements
        // can NEVER be the maximum for this or any future window
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        // INTUITION STEP 3: Add current index
        deque.offerLast(i);
        
        // INTUITION STEP 4: Once we have a full window, record the maximum
        // Front of deque = maximum element in current window
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

---

## 2.5 PriorityQueue & Heap

### What It Is
A **heap** — complete binary tree where parent is always smaller (min-heap) or larger (max-heap) than children.

### Internal Implementation

```java
/**
 * INTUITION: PriorityQueue in Java is a MIN-HEAP by default.
 * 
 * Heap property: For min-heap, every parent ≤ its children.
 * 
 * Internal representation: The heap is stored as an ARRAY.
 * For element at index i:
 *   Left child:  index 2*i + 1
 *   Right child: index 2*i + 2
 *   Parent:      index (i-1)/2
 * 
 * This array representation is WHY heap operations are efficient:
 * - No actual "tree" objects needed
 * - All nodes are implicit in the array layout
 * - Cache-friendly sequential memory access
 */
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
// INTUITION: For MAX-HEAP, use reverse order comparator
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
// OR
PriorityQueue<Integer> maxHeap2 = new PriorityQueue<>(Comparator.reverseOrder());

/**
 * INTUITION: 
 * offer(e) → O(log n) — add at end, bubble UP
 * poll()   → O(log n) — remove root, bubble DOWN
 * peek()   → O(1)     — just look at root (array[0])
 * size()   → O(1)
 * 
 * The key insight: peek is O(1) because the minimum is ALWAYS at index 0.
 * This is what makes heaps useful for "get min/max quickly" problems.
 */
minHeap.offer(5);   // [5]
minHeap.offer(3);   // [3, 5]  — 3 bubbles up
minHeap.offer(7);   // [3, 5, 7]
minHeap.offer(1);   // [1, 3, 7, 5] — 1 bubbles up to root
int min = minHeap.peek();  // returns 1 (O(1))
int removed = minHeap.poll();  // returns 1, heap reorganizes
```

### Sift Up / Sift Down — The Core Operations

```java
/**
 * INTUITION: These two operations are ALL a heap does.
 * Everything else (offer, poll, build) uses one or both.
 * 
 * SIFT UP (bubble up/swim): Used in offer()
 * - Add new element at end of array
 * - While element < parent, swap with parent
 * - Stops when element >= parent (heap property restored)
 * 
 * SIFT DOWN (bubble down/sink): Used in poll() and heapify()
 * - Replace root with last element
 * - While element > either child, swap with SMALLER child
 * - Stops when element <= both children
 */

private void siftUp(int k) {
    // INTUITION: k is the index of the newly added element
    // We compare with parent (at (k-1)/2) and swap if needed
    while (k > 0) {
        int parent = (k - 1) >>> 1;  // Integer division by 2
        if (heap[k] >= heap[parent]) break;  // Heap property satisfied
        swap(k, parent);
        k = parent;  // Move up and continue checking
    }
}

private void siftDown(int k) {
    // INTUITION: k is the index of the element to sink
    // We compare with children and swap with SMALLER child
    int half = size >>> 1;  // Only need to check up to halfway (last parent)
    while (k < half) {
        int leftChild = 2 * k + 1;
        int rightChild = 2 * k + 2;
        int smaller = leftChild;  // Assume left is smaller
        
        // INTUITION: Find the SMALLER of the two children
        // We swap with the smaller child to maintain heap property
        if (rightChild < size && heap[rightChild] < heap[leftChild]) {
            smaller = rightChild;
        }
        
        if (heap[k] <= heap[smaller]) break;  // Heap property satisfied
        
        swap(k, smaller);
        k = smaller;  // Move down and continue
    }
}
```

### Classic Problems — Deep Dive

#### Problem 1: Kth Largest Element in an Array

```java
/**
 * INTUITION: 
 * 
 * Approach 1: Sort → O(n log n), O(1) space
 * Approach 2: Min-Heap of size k → O(n log k), O(k) space  ← BEST for interviews
 * Approach 3: QuickSelect → O(n) average, O(n²) worst, O(1) space ← BEST for performance
 * 
 * WHY use min-heap of size k?
 * - Maintain heap of size k containing the k largest elements seen so far
 * - The MIN of these k elements is the kth largest overall
 * - When we see a new element, if it's larger than heap's min, REPLACE the min
 * - At end, heap's min = kth largest
 * 
 * Visual with [3, 2, 1, 5, 6, 4] and k=2:
 * Process 3: heap = [3] (size=1 < k, add)
 * Process 2: heap = [2, 3] (size=2 = k, add)
 * Process 1: 1 < 2 (min of heap), skip
 * Process 5: 5 > 2 → pop 2, add 5 → heap = [3, 5]
 * Process 6: 6 > 3 → pop 3, add 6 → heap = [5, 6]
 * Process 4: 4 < 5 (min of heap), skip
 * Result: heap.min = 5 ← 2nd largest
 */
public int findKthLargest(int[] nums, int k) {
    // INTUITION: Min-heap of size k
    // At any point, heap contains the k largest elements seen so far
    PriorityQueue<Integer> heap = new PriorityQueue<>();  // Min-heap
    
    for (int num : nums) {
        if (heap.size() < k) {
            // INTUITION: Heap isn't full yet — just add
            // We need at least k elements to find the kth largest
            heap.offer(num);
        } else if (num > heap.peek()) {
            // INTUITION: Found an element larger than current kth largest
            // Remove current kth largest, add this new larger element
            // Now heap size is still k, but all k elements are larger
            heap.poll();
            heap.offer(num);
        }
        // INTUITION: If num <= heap.peek(), it's not among k largest
        // We can safely ignore it
    }
    
    // INTUITION: heap.peek() is the kth largest
    // Why? We've maintained k largest elements. The smallest of them (min-heap root)
    // is exactly the kth largest.
    return heap.peek();
}
```

#### Problem 2: Merge K Sorted Lists

```java
/**
 * INTUITION: 
 * 
 * Naive: Compare first elements of all k lists, pick smallest, repeat → O(k * n)
 * Optimized with heap: Push first element of each list into min-heap
 * Pop smallest, add to result, push next element from SAME list
 * → O(n log k) where n = total elements, k = number of lists
 * 
 * WHY heap? 
 * We always need the SMALLEST current element among k lists.
 * Min-heap gives us O(1) access to smallest and O(log k) insertion.
 * 
 * Visual:
 * Lists: [1→4→5], [1→3→4], [2→6]
 * 
 * Step 1: Push heads → heap = [1(1), 1(2), 2(3)]  (node(value, listIndex))
 * Step 2: Pop 1→ result=[1], push next=4 from list 1 → heap = [1(2), 2(3), 4(1)]
 * Step 3: Pop 1→ result=[1,1], push next=3 from list 2 → heap = [2(3), 3(2), 4(1)]
 * Step 4: Pop 2→ result=[1,1,2], push next=6 from list 3 → heap = [3(2), 4(1), 6(3)]
 * ...and so on until all lists are exhausted
 */
public ListNode mergeKLists(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    
    // INTUITION: Min-heap stores ListNode, ordered by value
    // We need to track WHICH list each node came from to push next element
    PriorityQueue<ListNode> heap = new PriorityQueue<>(
        (a, b) -> a.val - b.val
    );
    
    // INTUITION: Push the HEAD of each non-empty list
    // This gives us k candidates for the smallest element
    for (ListNode list : lists) {
        if (list != null) {
            heap.offer(list);
        }
    }
    
    ListNode dummy = new ListNode(0);  // Dummy head for result
    ListNode current = dummy;
    
    while (!heap.isEmpty()) {
        // INTUITION: Pop the SMALLEST current node
        // This is the next element in the merged result
        ListNode smallest = heap.poll();
        current.next = smallest;
        current = current.next;
        
        // INTUITION: If this node's list has more elements,
        // push the next element as a candidate
        if (smallest.next != null) {
            heap.offer(smallest.next);
        }
    }
    
    return dummy.next;
}
```

#### Problem 3: Top K Frequent Elements

```java
/**
 * INTUITION: Two-step process:
 * 1. Count frequencies using HashMap  → O(n)
 * 2. Use min-heap of size k to track top k frequencies  → O(n log k)
 * 
 * WHY min-heap of size k (not max-heap)?
 * - With max-heap, we'd push everything, then pop k times → O(n log n)
 * - With min-heap of size k, we only keep k largest → O(n log k)
 * - When size > k, pop the SMALLEST (which we don't need)
 * - At end, heap contains k LARGEST frequencies
 */
public int[] topKFrequent(int[] nums, int k) {
    // INTUITION: Step 1 — Count frequencies
    // num → its frequency in the array
    Map<Integer, Integer> frequency = new HashMap<>();
    for (int num : nums) {
        frequency.put(num, frequency.getOrDefault(num, 0) + 1);
    }
    
    // INTUITION: Step 2 — Min-heap of size k
    // Store Map.Entry in heap, ordered by value (frequency)
    PriorityQueue<Map.Entry<Integer, Integer>> heap = new PriorityQueue<>(
        (a, b) -> a.getValue() - b.getValue()  // Min-heap by frequency
    );
    
    for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
        heap.offer(entry);
        // INTUITION: Keep only k largest frequencies
        // When heap > k, remove the smallest frequency
        if (heap.size() > k) {
            heap.poll();
        }
    }
    
    // INTUITION: Extract results from heap (order doesn't matter)
    int[] result = new int[k];
    for (int i = 0; i < k; i++) {
        result[i] = heap.poll().getKey();
    }
    return result;
}
```

---

## 2.6 HashMap

### What It Is
An **array of buckets**, where each bucket holds entries that share the same hash code.

### Internal Implementation

```java
/**
 * INTUITION: HashMap works like a library catalog system.
 * 
 * The library has 16 shelves (buckets).
 * To find a book: 
 * 1. Compute hash of title → determines WHICH shelf
 * 2. Search through books on that shelf for exact match
 * 
 * If too many books on one shelf (hash collision), searching is slow.
 * So when shelves get too full, library adds MORE shelves and reorganizes.
 * 
 * KEY PARAMETERS:
 * - INITIAL_CAPACITY = 16 (number of shelves)
 * - LOAD_FACTOR = 0.75 (when 75% of shelves have at least 1 book, reorganize)
 * - TREEIFY_THRESHOLD = 8 (when a shelf has 8+ books, convert to tree for faster search)
 */
public class HashMap<K, V> {
    // INTUITION: Each bucket is either null, a linked list, or a Red-Black tree
    // JDK 8+ optimization: if list length > 8, convert to tree (O(n) → O(log n))
    Node<K, V>[] table;  // The array of buckets
    int size;            // Number of key-value pairs
    int threshold;       // Next size to resize (capacity * load factor)
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    static class Node<K, V> {
        final int hash;  // Cached hash code — computed once, stored forever
        final K key;     // Immutable key (by convention but not required)
        V value;         // Mutable value
        Node<K, V> next; // Linked list for collision handling
    }

    /**
     * INTUITION: The hash function determines which bucket an entry goes to.
     * 
     * Step 1: Compute key.hashCode()    → returns int (could be anything)
     * Step 2: Transform to bucket index → (n-1) & hash
     * 
     * WHY (n-1) & hash instead of hash % n?
     * Because n is always power of 2 (16, 32, 64...).
     * For power of 2: (n-1) & hash == hash % n
     * AND bitwise AND is MUCH faster than modulo operation!
     */
    static int hash(Object key) {
        int h;
        // INTUITION: This "disturbance function" mixes high bits into low bits
        // Why? If hashCode() only varies in high bits, without this function 
        // two keys with same low bits would collide
        // 
        // Example:
        // hashCode = 0x12340000, table size = 16 (index = hashCode & 15)
        // Without XOR shift: index = 0x0000 & 0xF = 0
        // With XOR shift: h = 0x12340000 ^ (0x12340000 >>> 16) = 0x12341234
        //                index = 0x12341234 & 0xF = 4
        // 
        // The XOR shift brings high bits down to influence the low bits
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    public V put(K key, V value) {
        // INTUITION: The put algorithm
        // 1. If table is null or empty, resize it
        // 2. Compute bucket index from hash
        // 3. If bucket is null → create new node, done
        // 4. If bucket has entries → search for existing key
        //    - Found: replace value, return old value
        //    - Not found: append to list/tree
        // 5. If size > threshold → resize (double capacity)
        
        if (table == null || table.length == 0) {
            resize();
        }
        
        int hash = hash(key);
        int index = (table.length - 1) & hash;  // Bucket index
        
        Node<K, V> first = table[index];
        if (first == null) {
            // INTUITION: Empty bucket → just add the node
            table[index] = new Node<>(hash, key, value, null);
        } else {
            // INTUITION: Collision — bucket already has entries
            // Search for existing key in the linked list (or tree)
            Node<K, V> e = null;
            if (first.hash == hash && 
                (first.key == key || (key != null && key.equals(first.key)))) {
                e = first;  // First node matches!
            } else {
                for (Node<K, V> p = first; p.next != null; p = p.next) {
                    if (p.next.hash == hash && 
                        (p.next.key == key || (key != null && key.equals(p.next.key)))) {
                        e = p.next;  // Found in list
                        break;
                    }
                }
            }
            
            if (e != null) {
                // INTUITION: Key already exists — replace value
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
            
            // INTUITION: New key — append to list
            // (Tree insertion if bucket is treeified)
            table[index] = new Node<>(hash, key, value, first);  // Add to HEAD
        }
        
        size++;
        if (size > threshold) {
            resize();  // Double the capacity
        }
        return null;
    }

    /**
     * INTUITION: Resizing doubles the capacity.
     * 
     * Why double? 
     * - Doubling ensures capacity stays power of 2 (needed for fast index calculation)
     * - 1.5x or 2x growth is common. HashMap chose 2x.
     * 
     * Expensive operation: ALL entries must be REHASHED and relocated.
     * This is O(n) — but happens rarely (amortized O(1) for puts).
     */
    final Node<K, V>[] resize() {
        int oldCap = table.length;
        int newCap = oldCap << 1;  // Double: 16 → 32 → 64...
        Node<K, V>[] newTable = new Node[newCap];
        threshold = (int) (newCap * DEFAULT_LOAD_FACTOR);
        
        // INTUITION: Rehash each entry
        // Old index: hash & (oldCap - 1)
        // New index is either:
        //   - Same as old (if the new bit of hash is 0)
        //   - Old + oldCap (if the new bit of hash is 1)
        // This is because doubling capacity adds ONE MORE BIT to the index mask
        for (Node<K, V> entry : table) {
            while (entry != null) {
                Node<K, V> next = entry.next;
                int newIndex = (newCap - 1) & entry.hash;
                entry.next = newTable[newIndex];
                newTable[newIndex] = entry;
                entry = next;
            }
        }
        
        return newTable;
    }
}
```

### Complexity Summary

| Operation | Average | Worst Case | Why Worst Case |
|-----------|---------|------------|----------------|
| put(k, v) | O(1) | O(n) | All keys collide (bad hashCode) |
| get(k) | O(1) | O(n) | All keys in same bucket |
| containsKey(k) | O(1) | O(n) | Same — must search bucket |
| remove(k) | O(1) | O(n) | Same |
| size() | O(1) | O(1) | Just returns field |

### When to Use

```
✅ USE HashMap when:
   - You need O(1) lookups by key
   - You have unique keys per value
   - Order doesn't matter
   - Keys have good hashCode implementation

❌ DON'T use HashMap when:
   - You need sorted/ordered keys → TreeMap/LinkedHashMap
   - You need thread safety → ConcurrentHashMap
   - You have mutable keys → Should NEVER use mutable keys!
   - Memory is critical → HashMap has overhead per entry
```

### Common Interview Questions

#### Problem: Two Sum

```java
/**
 * INTUITION: 
 * 
 * Problem: Find two numbers in array that add to target.
 * 
 * Brute force: Check every pair → O(n²)
 * HashMap: Store each number's complement → O(n)
 * 
 * KEY INSIGHT: As we iterate, for each number nums[i],
 * we need to know if (target - nums[i]) was seen before.
 * That's a LOOKUP — exactly what HashMap is for.
 * 
 * Why HashMap and not HashSet?
 * We need the INDEX of the complement, not just whether it exists.
 * Map stores number → index. Set only stores the number.
 */
public int[] twoSum(int[] nums, int target) {
    // INTUITION: Store number → its index
    // We check if complement exists as we go
    Map<Integer, Integer> seen = new HashMap<>();
    
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        
        // INTUITION: If complement was seen, we found the pair
        // The complement is at index seen.get(complement)
        // Current number is at index i
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        
        // INTUITION: Store current number for future lookups
        seen.put(nums[i], i);
    }
    
    return new int[]{-1, -1};  // No solution (shouldn't happen per problem)
}
```

#### Problem: Group Anagrams

```java
/**
 * INTUITION: Two strings are anagrams if they have the same characters.
 * 
 * Key insight: Sorting anagram strings produces the SAME result.
 * "eat" → "aet", "tea" → "aet", "ate" → "aet"
 * 
 * So we can use sorted string as the KEY in HashMap.
 * Anagrams → same key → same bucket!
 * 
 * Time: O(n * k log k) where n = words, k = max word length
 * Optimization: Use character count array instead of sorting → O(n * k)
 */
public List<List<String>> groupAnagrams(String[] strs) {
    // INTUITION: Map sorted string → list of original strings
    // All anagrams produce the same sorted key
    Map<String, List<String>> groups = new HashMap<>();
    
    for (String s : strs) {
        // INTUITION: Sort characters to create the key
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        String key = new String(chars);
        
        // INTUITION: Add to group (create group if first time)
        groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        // computeIfAbsent: if key missing, create empty list, then add
    }
    
    return new ArrayList<>(groups.values());
}
```

---

## 2.7 HashSet

### What It Is
A **HashMap with only keys** (values are dummy). Uses same hash-based lookup for O(1) contains().

### Internal Implementation

```java
/**
 * INTUITION: HashSet is literally a HashMap with a dummy value.
 * 
 * Behind the scenes:
 * private transient HashMap<E, Object> map;
 * private static final Object PRESENT = new Object();  // Dummy value
 * 
 * public boolean add(E e) {
 *     return map.put(e, PRESENT) == null;  // Returns true if key was new
 * }
 * 
 * public boolean contains(Object o) {
 *     return map.containsKey(o);  // O(1) lookup
 * }
 * 
 * That's it! HashSet IS a HashMap. All the complexity is in HashMap.
 * This is the COMPOSITION pattern — "has-a" instead of "is-a".
 */
Set<Integer> set = new HashSet<>();
set.add(1);          // map.put(1, PRESENT) → returns null → true
set.add(2);          // map.put(2, PRESENT) → returns null → true
set.add(1);          // map.put(1, PRESENT) → returns old value → false (duplicate!)
boolean exists = set.contains(1);  // map.containsKey(1) → true
```

### When to Use

```
✅ USE HashSet when:
   - You only care about UNIQUENESS
   - You need O(1) contains() check
   - You want to remove duplicates from a collection
   - Order doesn't matter

❌ DON'T use HashSet when:
   - You need to maintain insertion order → LinkedHashSet
   - You need sorted order → TreeSet
   - You need to store duplicate values → Can't, use List
```

---

## 2.8 TreeMap/TreeSet

### What It Is
A **Red-Black Tree** — self-balancing binary search tree. Elements are stored in sorted order.

### Internal Implementation

```java
/**
 * INTUITION: TreeMap/TreeSet maintain elements in sorted order.
 * 
 * Internal: Red-Black Tree — a self-balancing BST.
 * Properties of Red-Black Tree:
 * 1. Each node is RED or BLACK
 * 2. Root is always BLACK
 * 3. Red nodes cannot have red children (no consecutive reds)
 * 4. Every path from root to leaf has the same number of BLACK nodes
 * 
 * These rules keep the tree BALANCED → O(log n) for all operations
 * 
 * Without balancing, BST can degenerate to linked list:
 * Insert 1, 2, 3, 4, 5 → tree is a chain → O(n) operations
 * Red-Black tree would restructure to keep height ~log n
 */
TreeMap<Integer, String> sortedMap = new TreeMap<>();
sortedMap.put(3, "three");
sortedMap.put(1, "one");
sortedMap.put(2, "two");
// Keys are sorted: {1="one", 2="two", 3="three"}

// INTUITION: TreeMap provides ORDER-BASED operations — HashMap can't do these!
sortedMap.firstKey();           // 1 — smallest key
sortedMap.lastKey();            // 3 — largest key
sortedMap.ceilingKey(2);        // 2 — smallest key >= 2
sortedMap.floorKey(2);          // 2 — largest key <= 2
sortedMap.higherKey(2);         // 3 — smallest key > 2
sortedMap.lowerKey(2);          // 1 — largest key < 2
sortedMap.subMap(2, 4);         // {2="two", 3="three"} — keys in [2, 4)
```

### Complexity Summary

| Operation | TreeMap | HashMap | Why Difference |
|-----------|---------|---------|----------------|
| put/get/remove | O(log n) | O(1) | TreeMap must maintain tree structure |
| iteration | O(n) | O(n + capacity) | TreeMap is always sorted |
| first()/last() | O(log n) | O(n) | TreeMap can go leftmost/rightmost |
| subMap() | O(log n + k) | O(n) | TreeMap can bound the range |

### When to Use

```
✅ USE TreeMap when:
   - You need sorted keys
   - You need range queries (subMap, headMap, tailMap)
   - You need nearest neighbor queries (ceiling, floor)

❌ DON'T use TreeMap when:
   - You only need O(1) lookups → HashMap is faster
   - You don't need sorted order → HashMap is simpler
```

---

## 2.9 Deque

### What It Is
**Double-Ended Queue** — you can add/remove from BOTH ends.

### Internal Implementation

```java
/**
 * INTUITION: ArrayDeque is implemented as a CIRCULAR ARRAY.
 * 
 * A circular array means when we reach the end, we wrap around to the beginning.
 * This avoids O(n) shifting that ArrayList would need for front operations.
 * 
 * 
 * Initial: [_, _, _, _, _, _, _, _]  head=0, tail=0
 * addLast(1): [1, _, _, _, _, _, _, _]  head=0, tail=1
 * addLast(2): [1, 2, _, _, _, _, _, _]  head=0, tail=2
 * addFirst(3): [1, 2, _, _, _, _, _, 3]  head=7 (wrapped!), tail=2
 * 
 * Why is addFirst O(1)?
 * Because head moves BACKWARD (decrement, wrapping around).
 * No elements need to shift!
 * 
 * Circular array is the TRICK that makes ArrayDeque better than LinkedList:
 * - Uses a single contiguous array (cache-friendly)
 * - No per-node memory overhead (LinkedList has 3 references per node)
 * - All operations O(1)
 */
Deque<Integer> deque = new ArrayDeque<>();

deque.addFirst(1);   // [1] — head points to 1
deque.addFirst(2);   // [2, 1] — head points to 2
deque.addLast(3);    // [2, 1, 3] — tail points to 3
int first = deque.removeFirst();  // returns 2 → deque = [1, 3]
int last = deque.removeLast();    // returns 3 → deque = [1]
```

### When to Use

```
✅ USE Deque when:
   - You need Stack behavior (LIFO) → push/pop/pop
   - You need Queue behavior (FIFO) → offer/poll
   - You need to work from BOTH ends
   - You want the fastest Deque implementation

❌ DON'T use Deque when:
   - You need thread safety → concurrent variant
   - You need random access → use ArrayList
   - You have unlimited memory but need bounded collection
```

---

## 2.10 Concurrent Collections

### The Concurrency Hierarchy

```java
/**
 * INTUITION: Java provides thread-safe collections at different levels.
 * 
 * Level 1: Synchronized wrappers (Collections.synchronizedXxx)
 *   - Wraps existing collection with synchronized blocks
 *   - Thread-safe but SLOW (one lock for entire collection)
 *   - Use for: Simple scenarios, legacy code
 * 
 * Level 2: Concurrent collections (java.util.concurrent)
 *   - Designed for concurrency from the ground up
 *   - Use fine-grained locking or lock-free algorithms
 *   - MUCH better performance under contention
 *   - Use for: All new code
 * 
 * Level 3: Immutable collections (List.of(), Set.of(), Map.of())
 *   - Cannot be modified after creation
 *   - Inherently thread-safe (no writes = no race conditions)
 *   - Use for: Constants, configuration, shared read-only data
 */

// LEVEL 1: Synchronized wrapper (DON'T USE for new code)
Map<String, String> slowMap = Collections.synchronizedMap(new HashMap<>());

// LEVEL 2: Concurrent collections (USE THESE)
Map<String, String> fastMap = new ConcurrentHashMap<>();
Queue<String> queue = new ConcurrentLinkedQueue<>();
Deque<String> deque = new ConcurrentLinkedDeque<>();

// LEVEL 3: Immutable (for read-only shared data)
Map<String, String> config = Map.of("key1", "val1", "key2", "val2");
```

### Why ConcurrentHashMap is Better Than Synchronized HashMap

```java
/**
 * INTUITION: The performance difference is DRAMATIC.
 * 
 * Collections.synchronizedMap(new HashMap<>()):
 * - ONE lock for the ENTIRE map
 * - Thread A reading bucket 0 → blocks Thread B from writing bucket 7
 * - This is "coarse-grained locking" — simple but poor concurrency
 * 
 * ConcurrentHashMap:
 * - Separate locks for different BUCKET REGIONS
 * - Thread A reading bucket 0 → Thread B can write bucket 7
 * - This is "fine-grained locking" — complex but excellent concurrency
 * 
 * JDK 8+ improvement: Uses compare-and-swap (CAS) + synchronized 
 *   CAS for common operations (no lock at all!)
 *   synchronized only for heavy operations (resizing, tree conversion)
 * 
 * Key operations:
 * get() → NO LOCK (CAS-based). Just read the volatile reference!
 * put() → Lock only the specific bucket, not entire map
 * size() → Uses CounterCells for O(1) without contention
 */

// INTUITION: The pattern of ConcurrentHashMap methods
// They provide atomic COMPOUND operations that HashMap doesn't

ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();

// BAD: These two operations are NOT atomic together
if (!scores.containsKey("Alice")) {
    scores.put("Alice", 0);  // Race condition!
}

// GOOD: Atomic operation built into ConcurrentHashMap
scores.putIfAbsent("Alice", 0);  // Atomic "check-then-act"

// BAD: Read-modify-write race condition
int old = scores.get("Alice");
scores.put("Alice", old + 1);  // Thread could interleave here!

// GOOD: Atomic update
scores.compute("Alice", (key, val) -> (val == null) ? 1 : val + 1);
// Entire lambda runs atomically

// EVEN BETTER for counters
scores.merge("Alice", 1, Integer::sum);  // Shortcut for increment
```

---

## 2.11 DSA Problem Patterns by Collection

### Pattern: Frequency Counting

```java
/**
 * INTUITION: When you need to count occurrences → HashMap
 * 
 * Use cases:
 * - "Most frequent element" 
 * - "Anagram detection"
 * - "First non-repeating character"
 * - "Ransom note construction"
 * 
 * WHY HashMap? We need to map element → its count.
 * O(1) update per element. O(n) total.
 */
public boolean canConstruct(String ransomNote, String magazine) {
    // INTUITION: Count available letters in magazine
    // Then check if ransom note can be formed
    Map<Character, Integer> counts = new HashMap<>();
    
    // Step 1: Count all letters in magazine
    for (char c : magazine.toCharArray()) {
        counts.put(c, counts.getOrDefault(c, 0) + 1);
    }
    
    // Step 2: Use letters from magazine for ransom note
    for (char c : ransomNote.toCharArray()) {
        int remaining = counts.getOrDefault(c, 0);
        if (remaining == 0) return false;  // Not enough of this letter
        counts.put(c, remaining - 1);  // Use one occurrence
    }
    
    return true;
}
```

### Pattern: Sliding Window (Queue-based)

```java
/**
 * INTUITION: When you need to maintain a "window" of recent elements → Queue/Deque
 * 
 * Use cases:
 * - "Maximum in sliding window" → Deque (monotonic)
 * - "First negative in every window of size k"
 * - "Rate limiter sliding window log"
 * 
 * WHY Queue? 
 * Windows remove oldest elements and add newest.
 * That's FIFO — exactly what Queue does.
 */
public int[] maxSlidingWindow(int[] nums, int k) {
    // Solution provided above in Queue section
}
```

### Pattern: Finding K-th Element (Heap-based)

```java
/**
 * INTUITION: When you need "top k" or "kth smallest/largest" → PriorityQueue
 * 
 * Decision: Min-heap or Max-heap?
 * 
 * "kth SMALLEST" → use MAX-HEAP of size k
 *   WHY? Keep k smallest elements. The LARGEST of them is the kth smallest.
 *   When new element < max of heap → replace (we want smaller elements)
 * 
 * "kth LARGEST" → use MIN-HEAP of size k
 *   WHY? Keep k largest elements. The SMALLEST of them is the kth largest.
 *   When new element > min of heap → replace (we want larger elements)
 * 
 * "TOP k FREQUENT" → use MIN-HEAP of size k by frequency
 *   WHY? Same logic as kth largest, but by frequency value
 */

// kth SMALLEST → Max-heap
public int kthSmallest(int[] nums, int k) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int num : nums) {
        maxHeap.offer(num);
        if (maxHeap.size() > k) {
            maxHeap.poll();  // Remove largest — we only want k smallest
        }
    }
    return maxHeap.peek();  // Largest among k smallest = kth smallest
}

// kth LARGEST → Min-heap
public int kthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();  // Remove smallest — we only want k largest
        }
    }
    return minHeap.peek();  // Smallest among k largest = kth largest
}
```

### Pattern: Two Pointers (List-based)

```java
/**
 * INTUITION: When you need to find pairs in sorted array → Two Pointers
 * 
 * Use cases:
 * - "Two Sum in sorted array"
 * - "Remove duplicates from sorted array"
 * - "Container with most water"
 * - "Three sum"
 * 
 * WHY Two Pointers? O(n) instead of O(n²) for nested loops.
 * 
 * The trick: Start pointers at BOTH ends and move inward.
 * Since array is sorted, we know direction to move based on comparison.
 */
public int[] twoSumSorted(int[] numbers, int target) {
    // INTUITION: Array is sorted ascending
    // Left pointer at smallest, right pointer at largest
    int left = 0;
    int right = numbers.length - 1;
    
    while (left < right) {
        int sum = numbers[left] + numbers[right];
        
        if (sum == target) {
            return new int[]{left + 1, right + 1};  // 1-indexed
        } else if (sum < target) {
            // INTUITION: Sum is too small
            // We need a larger sum → move left pointer RIGHT (to bigger numbers)
            left++;
        } else {
            // INTUITION: Sum is too large
            // We need a smaller sum → move right pointer LEFT (to smaller numbers)
            right--;
        }
    }
    
    return new int[]{-1, -1};
}
```

### Pattern: Monotonic Stack/Queue

```java
/**
 * INTUITION: When you need "next greater/smaller element" → Stack/Deque
 * 
 * The monotonic pattern:
 * - Maintain elements in INCREASING or DECREASING order
 * - When a new element BREAKS the order, pop until order is restored
 * - The popped elements have found their "next greater/smaller"
 * 
 * Increasing stack: [1, 3, 5] — each element is larger than previous
 *   → Used for "next SMALLER element" problems
 * 
 * Decreasing stack: [5, 3, 1] — each element is smaller than previous
 *   → Used for "next GREATER element" problems
 * 
 * The pattern is ALWAYS:
 * while (!stack.isEmpty() && nums[stack.peek()] OP nums[i]) {
 *     int idx = stack.pop();
 *     answer[idx] = i - idx;  // or nums[i]
 * }
 * stack.push(i);
 * 
 * OP is < for "next greater" (current > top → top found its greater)
 * OP is > for "next smaller" (current < top → top found its smaller)
 */
public int[] dailyTemperatures(int[] temperatures) {
    // Solution provided in Stack section — it uses monotonic DECREASING stack
    // (stack stores temperatures in decreasing order)
}
```

---

## 📊 Quick Reference: When to Use What

```
┌───────────────────┬──────────────────────┬──────────────────────────────┐
│ You Need...       │ Use This             │ Why Not The Other           │
├───────────────────┼──────────────────────┼──────────────────────────────┤
│ Fast lookup by key│ HashMap              │ TreeMap O(log n) vs O(1)     │
│ Sorted keys       │ TreeMap              │ HashMap can't sort           │
│ Insertion order   │ LinkedHashMap        │ HashMap doesn't preserve     │
│ Unique elements   │ HashSet              │ List allows duplicates       │
│ Fast stack        │ ArrayDeque           │ Stack is synchronized(slow)  │
│ Fast queue        │ ArrayDeque           │ LinkedList has node overhead │
│ Kth element       │ PriorityQueue        │ Sorting is O(n log n)        │
│ Thread-safe map   │ ConcurrentHashMap    │ Hashtable is legacy (slow)   │
│ Index access      │ ArrayList            │ LinkedList is O(n) for get() │
│ Front/back ops    │ ArrayDeque/LinkedList│ ArrayList is O(n) for front  │
│ Range queries     │ TreeMap              │ HashMap can't do ranges      │
└───────────────────┴──────────────────────┴──────────────────────────────┘
```

---

## 🎯 Final Interview Tips

1. **Always think out loud**: "I'll use HashMap here because I need O(1) lookups by key."

2. **Mention trade-offs**: "I could use TreeMap for sorted order, but O(log n) is slower than HashMap's O(1). Since I don't need sorting, HashMap is better."

3. **Consider concurrency**: "For single-threaded use, HashMap is fine. If this needs to be thread-safe, I'd use ConcurrentHashMap."

4. **Be specific with constraints**: "With n=10^5 elements, O(n) iteration is fine. But if n=10^9, I need a different approach."

5. **Know your defaults**: "ArrayList default capacity is 10. If I know I'll store 1000 elements, I'll construct it with initial capacity 1000 to avoid resizing."

6. **Watch for boxing overhead**: "Using `Map<Integer, Integer>` causes boxing/unboxing overhead. For performance-critical code, consider specialized libraries like Trove or use arrays."