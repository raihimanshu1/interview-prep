# Pattern-Wise Java Solutions — Master Learning Index

> **Goal:** Learn DSA by **pattern**, not by random memorization.  
> Each pattern page lists problems in **learning order** — from foundational to advanced — with conceptual notes.

---

## 📘 How To Use This Repository

| Step | Action |
|------|--------|
| 1️⃣ | Pick a **pattern** (start with Arrays & Hashing or Strings) |
| 2️⃣ | Read the **concept notes** at the top of each category README |
| 3️⃣ | Solve problems in the **listed order** — each builds on the previous |
| 4️⃣ | Mark progress in the checklist |
| 5️⃣ | Move to **next pattern** only when comfortable |

---

## 📚 Pattern Categories (Recommended Learning Order)

| # | Category | Sub-Patterns | Difficulty Span | Key Skill |
|---|----------|-------------|----------------|-----------|
| 1 | **[Arrays & Hashing](arrays-hashing/README.md)** | Core, Multi-pointer, Prefix Sum, Rotation | Easy → Medium | HashMaps, sliding, cumulative |
| 2 | **[Strings](strings/README.md)** | Core, Manipulation, Palindrome | Easy → Medium | Char frequency, two-pointer |
| 3 | **[Binary Search](binary-search/README.md)** | Core, Search-on-Answer, Variations | Easy → Hard | Divide & conquer |
| 4 | **[Sliding Window & Two Pointers](sliding-window-two-pointers/README.md)** | Fixed, Variable, K-Distinct, Two-Pointer | Easy → Hard | Window management |
| 5 | **[Stack](stack/README.md)** | Core, Monotonic, Range/Histogram | Easy → Hard | LIFO, NGE/PSE |
| 6 | **[Linked List](linked-list/README.md)** | Core, Two-Pointer, Variations | Easy → Medium | Pointer manipulation |
| 7 | **[Trees](trees/README.md)** | Traversal, BFS, DFS, BST, Structural | Easy → Hard | Recursion, BST properties |
| 8 | **[Heaps & Priority Queues](heap-priority-queue/README.md)** | K-Elements, Scheduling | Easy → Medium | Top-K pattern |
| 9 | **[Graphs](graphs/README.md)** | Traversal, Connectivity, Shortest Path, Advanced | Medium → Hard | BFS/DFS, Topological |
| 10 | **[Backtracking](backtracking/README.md)** | Subsets, Permutations, Combinations | Medium → Hard | Recursion tree pruning |
| 11 | **[Intervals & Greedy](intervals-greedy/README.md)** | Intervals, Greedy | Medium → Hard | Sorting + sweep |
| 12 | **[Dynamic Programming](dynamic-programming/README.md)** | 1D, 2D, Subset, Grid, Strings, LIS | Medium → Hard | State transition |
| 13 | **[Matrix](matrix/README.md)** | Traversal, Transformation, BFS/DFS | Medium → Hard | 2D grid manipulation |
| 14 | **[Queue & Deque](queue-deque/README.md)** | Stack-Queue, Deque | Easy → Medium | FIFO, sliding max |
| 15 | **[Tries](tries/README.md)** | Prefix Tree, Word Search | Medium → Hard | Prefix matching |
| 16 | **[Design](design/README.md)** | Cache, OOD patterns | Medium → Hard | DS design |
| 17 | **[Bit Manipulation](bit-manipulation/README.md)** | XOR, Bitmask | Easy → Medium | Bitwise ops |
| 18 | **[Math & Number Theory](math-number-theory/README.md)** | Primes, Gcd, Exponentiation | Easy → Medium | Math fundamentals |

---

## 🔄 Cross-Pattern Learning Path

```
Arrays & Hashing ──→ Sliding Window ──→ Two Pointers
       │                    │
       ↓                    ↓
   Prefix Sum          Stack (Monotonic)
       │                    │
       ↓                    ↓
Binary Search ───→ Trees ───→ Graphs
       │                    │
       ↓                    ↓
  Search-on-Answer     Backtracking ──→ DP
```

---

## ✅ Progress Tracker

- [ ] Arrays & Hashing
- [ ] Strings
- [ ] Binary Search
- [ ] Sliding Window & Two Pointers
- [ ] Stack
- [ ] Linked List
- [ ] Trees
- [ ] Heaps & Priority Queues
- [ ] Graphs
- [ ] Backtracking
- [ ] Intervals & Greedy
- [ ] Dynamic Programming
- [ ] Matrix
- [ ] Queue & Deque
- [ ] Tries
- [ ] Design
- [ ] Bit Manipulation
- [ ] Math & Number Theory

---

## 📖 Quick Conceptual Reference

### HashMap Pattern
```
Problem detects: duplicates, frequency, complements
Data Structure: HashMap<Character/Integer, Integer/Frequency>
```

### Two Pointer Pattern
```
Sorted array → Left/Right pointers
Linked List → Slow/Fast pointers
In-place → Partition pointers
```

### Sliding Window Pattern
```
Fixed size → for loop + slide
Variable size → expand/shrink while condition
```

### Monotonic Stack Pattern
```
Next Greater Element → Increasing stack
Next Smaller Element → Decreasing stack
```

### BFS Pattern
```
Queue + visited → Level order / Shortest path
```

### DFS Pattern
```
Recursion + visited → Connectivity / Backtracking
```

### DP Pattern
```
1D → Fibonacci-like, house robber
2D → Grid paths, edit distance
Subset → Knapsack, partition
LIS → Patience sorting variant
```

### Backtracking Pattern
```
Result list → temp list → for choice → add → recurse → remove