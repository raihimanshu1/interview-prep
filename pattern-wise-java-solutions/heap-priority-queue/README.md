# Heap / Priority Queue

> **Core Pattern:** Use a priority queue to efficiently track the K largest/smallest elements or merge sorted data.  
> **Learning Path:** K-th element → Top-K → Scheduling → Merge → Advanced.

---

## 📖 Conceptual Foundation

### When to use Heap?
| Situation | Pattern | Example |
|-----------|---------|---------|
| K-th largest/smallest | Min-heap of size K | KthLargestElement |
| Top-K frequent elements | Max-heap or min-heap with freq map | TopKFrequentElements |
| Merge K sorted lists | Min-heap of heads | MergeKSortedLists |
| Scheduling/task ordering | Max-heap for most urgent | TaskScheduler |
| Running median | Two heaps (min + max) | FindMedianFromDataStream |

### Template: K-th Largest (Min-Heap of size K)
```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
for (int num : nums) {
    minHeap.offer(num);
    if (minHeap.size() > K) minHeap.poll();
}
return minHeap.peek(); // K-th largest
```

### Template: Top-K Frequent (Min-Heap with Frequency)
```java
Map<Integer, Integer> freq = new HashMap<>();
// ... count frequencies ...
PriorityQueue<Integer> minHeap = new PriorityQueue<>(
    (a, b) -> freq.get(a) - freq.get(b));
for (int key : freq.keySet()) {
    minHeap.offer(key);
    if (minHeap.size() > K) minHeap.poll();
}
return new ArrayList<>(minHeap); // Top K
```

---

## 📚 Learning Order

### Phase 1: K-th Element

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Kth Largest Element in Array** | [KthLargestElement.java](KthLargestElement.java) | Min-heap of size K OR QuickSelect | 🟡 Medium |
| 2 | **Kth Largest in Stream** | [KthLargestStream.java](KthLargestStream.java) | Min-heap of size K, add one by one | 🟢 Easy |
| 3 | **K Closest Points to Origin** | [KClosestPointsToOrigin.java](KClosestPointsToOrigin.java) | Min-heap by distance squared OR QuickSelect | 🟡 Medium |
| 4 | **K Closest Elements** | [KClosestElements.java](KClosestElements.java) | Binary search + two-pointer OR max-heap of size K | 🟡 Medium |

### Phase 2: Top-K Frequency

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 5 | **Top K Frequent Elements** | [TopKFrequentElements.java](TopKFrequentElements.java) | Freq map + min-heap of size K | 🟡 Medium |
| 6 | **Top K Frequent Words** | [TopKFrequentWords.java](TopKFrequentWords.java) | Same + lexicographical tie-breaker | 🟡 Medium |

### Phase 3: Merge & Combine

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 7 | **Merge K Sorted Lists** | [MergeKSortedLists.java](MergeKSortedLists.java) | Min-heap of ListNode heads | 🔴 Hard |

### Phase 4: Scheduling / Reorganization

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 8 | **Task Scheduler** | [TaskScheduler.java](TaskScheduler.java) | Max-heap by frequency, cooldown queue | 🟡 Medium |
| 9 | **Reorganize String** | [ReorganizeString.java](ReorganizeString.java) | Max-heap, add back with cooldown | 🟡 Medium |
| 10 | **Last Stone Weight** | [LastStoneWeight.java](LastStoneWeight.java) | Max-heap, smash two heaviest | 🟢 Easy |

### Phase 5: Advanced

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 11 | **Find Median from Data Stream** | [FindMedianFromDataStream.java](FindMedianFromDataStream.java) | Two heaps: max-heap for left, min-heap for right | 🔴 Hard |

---

## 🔑 Key Insights

1. **K-th largest** → min-heap of size K (smallest in top K)
2. **K-th smallest** → max-heap of size K (largest in top K)
3. **Top-K frequent** → freq map + heap
4. **Two heaps** → for running median, balance sizes
5. **Scheduling** → heap for urgency, queue for cooldown

---

## 🎯 Practice Checklist

- [ ] Phase 1: K-th element patterns
- [ ] Phase 2: Top-K frequency
- [ ] Phase 3: Merge patterns
- [ ] Phase 4: Scheduling
- [ ] Phase 5: Advanced (two heaps)