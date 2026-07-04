# Binary Search

> **Core Pattern:** Divide search space in half on sorted/range-based data. O(log n).  
> **Learning Path:** Classic binary search → find boundaries → search on answer → rotated/variations.

---

## 📖 Conceptual Foundation

### Binary Search Decision Tree
```
Sorted Array or Monotonic Condition?
  ├── YES ──→ Can we compute mid & decide direction?
  │            ├── Find exact value → BinarySearch.java
  │            ├── Find boundary (first/last) → FirstAndLastPosition.java
  │            ├── Find insertion point → SearchInsertPosition.java
  │            └── Search on answer (min/max satisfying condition) → KokoEatingBananas.java
  └── NO  ──→ Not a binary search problem
```

### Template: Standard Binary Search
```java
int left = 0, right = n - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;
    if (arr[mid] == target) return mid;
    else if (arr[mid] < target) left = mid + 1;
    else right = mid - 1;
}
return -1;
```

### Template: Search on Answer (Minimize/Maximize)
```java
int left = minPossible, right = maxPossible;
while (left < right) {
    int mid = left + (right - left) / 2;
    if (canAchieve(mid)) right = mid;  // or left = mid + 1
    else left = mid + 1;               // or right = mid - 1
}
return left;
```

### Key Distinction: Lower Bound vs Upper Bound
| Boundary | Condition | Search Goal |
|----------|-----------|-------------|
| First occurrence | `arr[mid] >= target` | Find smallest index where condition is true |
| Last occurrence | `arr[mid] <= target` | Find largest index where condition is true |

---

## 📚 Learning Order

### Phase 1: Core Binary Search

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Binary Search (Classic)** | [core/BinarySearch.java](core/BinarySearch.java) | Standard divide & conquer on sorted array | 🟢 Easy |
| 2 | **Search Insert Position** | [core/SearchInsertPosition.java](core/SearchInsertPosition.java) | Lower bound — find where target fits | 🟢 Easy |
| 3 | **Guess Number Higher or Lower** | [core/GuessNumberHigherOrLower.java](core/GuessNumberHigherOrLower.java) | Binary search on range [1, n] | 🟢 Easy |
| 4 | **First and Last Position in Sorted** | [core/FirstAndLastPosition.java](core/FirstAndLastPosition.java) | Two binary searches (lower + upper bound) | 🟡 Medium |
| 5 | **Sqrt(x)** | [core/SqrtX.java](core/SqrtX.java) | Binary search on answer `mid*mid <= x` | 🟢 Easy |

### Phase 2: Search on Answer

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 6 | **Koko Eating Bananas** | [search-on-answer/KokoEatingBananas.java](search-on-answer/KokoEatingBananas.java) | Binary search on speed, `canEatAll(mid)` | 🟡 Medium |
| 7 | **Capacity to Ship Packages** | [search-on-answer/CapacityToShipPackages.java](search-on-answer/CapacityToShipPackages.java) | Binary search on capacity, `canShip(mid)` | 🟡 Medium |

### Phase 3: Variations (Rotated & 2D)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 8 | **Search a 2D Matrix** | [variations/SearchA2DMatrix.java](variations/SearchA2DMatrix.java) | Treat 2D as 1D: `matrix[mid/n][mid%n]` | 🟡 Medium |
| 9 | **Find Minimum in Rotated Sorted** | [variations/BinarySearchFindMinimumRotated.java](variations/BinarySearchFindMinimumRotated.java) | Compare `mid` with `right` to find pivot | 🟡 Medium |
| 10 | **Search in Rotated Sorted Array** | [variations/BinarySearchRotatedArray.java](variations/BinarySearchRotatedArray.java) | Determine sorted half, then search | 🟡 Medium |
| 11 | **Find Peak Element** | [variations/FindPeakElement.java](variations/FindPeakElement.java) | Compare `mid` with `mid+1`, move toward higher | 🟡 Medium |
| 12 | **Median of Two Sorted Arrays** | [variations/MedianOfTwoSortedArrays.java](variations/MedianOfTwoSortedArrays.java) | Partition both arrays at same total half | 🔴 Hard |

---

## 🔑 Key Insights

1. **Binary search works on:** sorted arrays, monotonic functions, ranges with yes/no condition
2. **`left + (right - left) / 2`** prevents integer overflow
3. **Search on answer** pattern: define `canAchieve(mid)` and binary search the answer space
4. **Rotated arrays:** compare `arr[mid]` with `arr[right]` to determine which half is sorted
5. **2D search:** treat matrix as flat array: `mid / cols`, `mid % cols`

---

## 🎯 Practice Checklist

- [ ] Phase 1: Core binary search mastered
- [ ] Phase 2: Search-on-Answer pattern clear
- [ ] Phase 3: Rotated & 2D variations