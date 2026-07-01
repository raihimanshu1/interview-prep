# Arrays & Hashing

> **Core Pattern:** Use hash maps for O(1) lookups, frequency counting, and complement detection.  
> **Learning Path:** Start with brute force → optimize with hash maps → add multiple pointers → extend with prefix sums.

---

## 📖 Conceptual Foundation

### When to use HashMap on Arrays?
| Situation | Pattern | Example |
|-----------|---------|---------|
| Need to find complement | Store `target - current` | Two Sum |
| Count frequency | `map[num] = count` | Majority Element |
| Detect duplicates | `set.add()` check | Contains Duplicate |
| Track index + value | `map[value] = index` | Two Sum with indices |
| Consecutive sequence building | `set.contains(num-1)` check | Longest Consecutive |

### HashMap Template
```java
Map<KeyType, ValueType> map = new HashMap<>();
for (element : array) {
    if (map.containsKey(complement)) {
        // Found solution
    }
    map.put(element, someValue);
}
```

---

## 📚 Learning Order

### Phase 1: Core HashMap Problems (warm up)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Two Sum** | [core/TwoSum.java](core/TwoSum.java) | `HashMap<value, index>` — store complement | 🟢 Easy |
| 2 | **Contains Duplicate** | [core/ContainsDuplicate.java](core/ContainsDuplicate.java) | `HashSet` — detect repeat | 🟢 Easy |
| 3 | **Missing Number** | [core/MissingNumber.java](core/MissingNumber.java) | Sum formula OR `HashSet` to find missing | 🟢 Easy |
| 4 | **Valid Sudoku** | [core/ValidSudoku.java](core/ValidSudoku.java) | `HashSet` per row/col/box | 🟡 Medium |
| 5 | **Majority Element** | [core/MajorityElement.java](core/MajorityElement.java) | Boyer-Moore voting OR `HashMap` count | 🟢 Easy |
| 6 | **Find the Duplicate Number** | [core/FindTheDuplicateNumber.java](core/FindTheDuplicateNumber.java) | Floyd's cycle detection (linked list on array) | 🟡 Medium |
| 7 | **Longest Consecutive Sequence** | [core/LongestConsecutiveSequence.java](core/LongestConsecutiveSequence.java) | `HashSet` — check sequence start by `!set.contains(num-1)` | 🟡 Medium |
| 8 | **Encode and Decode Strings** | [core/EncodeAndDecodeStrings.java](core/EncodeAndDecodeStrings.java) | Delimiter-based encoding with escape | 🟡 Medium |

### Phase 2: Multi-Pointer Combination (sorted arrays)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 9 | **Two Sum II (Sorted)** | [multi-pointer-combination/TwoSumIIInputArrayIsSorted.java](multi-pointer-combination/TwoSumIIInputArrayIsSorted.java) | Left/Right pointers | 🟢 Easy |
| 10 | **Two Sum Closest** | [multi-pointer-combination/TwoSumClosest.java](multi-pointer-combination/TwoSumClosest.java) | Sort + L/R pointers tracking closest | 🟡 Medium |
| 11 | **Three Sum** | [multi-pointer-combination/ThreeSum.java](multi-pointer-combination/ThreeSum.java) | Fix one + two-sum on rest | 🟡 Medium |
| 12 | **Three Sum Closest** | [multi-pointer-combination/ThreeSumClosest.java](multi-pointer-combination/ThreeSumClosest.java) | Fix one + L/R tracking closest | 🟡 Medium |
| 13 | **Four Sum** | [multi-pointer-combination/FourSum.java](multi-pointer-combination/FourSum.java) | Nested loops + two-sum (avoid duplicates) | 🟡 Medium |
| 14 | **Container With Most Water** | [multi-pointer-combination/ContainerWithMostWater.java](multi-pointer-combination/ContainerWithMostWater.java) | Two-pointer from edges, move shorter | 🟡 Medium |

### Phase 3: Prefix Sum / Cumulative

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 15 | **Running Sum of Array** | [prefix-subarray/RunningSumOfArray.java](prefix-subarray/RunningSumOfArray.java) | `prefix[i] = prefix[i-1] + arr[i]` | 🟢 Easy |
| 16 | **Pivot Index** | [prefix-subarray/PivotIndex.java](prefix-subarray/PivotIndex.java) | `leftSum == total - leftSum - nums[i]` | 🟢 Easy |
| 17 | **Maximum Subarray (Kadane's)** | [prefix-subarray/MaximumSubarray.java](prefix-subarray/MaximumSubarray.java) | Kadane's — `maxEndingHere = max(num, maxEndingHere+num)` | 🟡 Medium |
| 18 | **Subarray Sum Equals K** | [prefix-subarray/SubarraySumEqualsK.java](prefix-subarray/SubarraySumEqualsK.java) | `HashMap<prefixSum, count>` | 🟡 Medium |
| 19 | **Count Subarrays With Sum K** | [prefix-subarray/CountSubarraysWithSumK.java](prefix-subarray/CountSubarraysWithSumK.java) | Same as above, counting all | 🟡 Medium |
| 20 | **Continuous Subarray Sum** | [prefix-subarray/ContinuousSubarraySum.java](prefix-subarray/ContinuousSubarraySum.java) | `prefixSum % k` in HashMap | 🟡 Medium |

### Phase 4: Rotation & Advanced

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 21 | **Best Time to Buy & Sell Stock** | [rotation-search/BestTimeToBuyAndSellStock.java](rotation-search/BestTimeToBuyAndSellStock.java) | Track min price so far | 🟢 Easy |
| 22 | **Product of Array Except Self** | [rotation-search/ProductOfArrayExceptSelf.java](rotation-search/ProductOfArrayExceptSelf.java) | Prefix product × suffix product (no division) | 🟡 Medium |
| 23 | **Sort an Array (Quick/Merge)** | [rotation-search/SortAnArray.java](rotation-search/SortAnArray.java) | Sorting algorithm implementation | 🟡 Medium |
| 24 | **Find Minimum in Rotated Sorted** | [rotation-search/FindMinimumInRotatedSortedArray.java](rotation-search/FindMinimumInRotatedSortedArray.java) | Binary search on rotated | 🟡 Medium |
| 25 | **Search in Rotated Sorted** | [rotation-search/SearchInRotatedSortedArray.java](rotation-search/SearchInRotatedSortedArray.java) | Binary search on rotated with target | 🟡 Medium |
| 26 | **Count Inversions in Array** | [rotation-search/CountInversionsInArray.java](rotation-search/CountInversionsInArray.java) | Merge sort variation | 🔴 Hard |

---

## 🔑 Key Insights

1. **HashMap first, optimize later** — If O(n²) is obvious, HashMap usually brings it to O(n)
2. **Duplicate detection** = HashSet
3. **Sorted array + pair finding** = two pointers (left/right)
4. **Subarray sums** = prefix sum + HashMap
5. **Sorted & rotated** = binary search with pivot detection
6. **Kadane's** = best for maximum subarray problems

---

## 🎯 Practice Checklist

- [ ] Phase 1: All Core problems solved
- [ ] Phase 2: Multi-pointer patterns mastered
- [ ] Phase 3: Prefix Sum technique clear
- [ ] Phase 4: Rotation & Advanced problems