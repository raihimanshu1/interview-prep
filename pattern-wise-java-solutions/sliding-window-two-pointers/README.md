# Sliding Window & Two Pointers

> **Core Pattern:** Maintain a window (range) over the array and slide it based on conditions.  
> **Learning Path:** Fixed window → variable window → two-pointer styles → K-distinct → optimization.

---

## 📖 Conceptual Foundation

### When to use Sliding Window?
| Condition | Pattern |
|-----------|---------|
| Subarray/substring with constraints | Sliding Window |
| Exactly K or At Most K something | Variable Window |
| Fixed size K window | Fixed Window |
| Two pointers comparing characters | Two-pointer style |

### Decision Tree
```
Need subarray/substring?
  ├── Fixed size K → Fixed Window (for loop)
  ├── Variable size (condition-based) → Variable Window (expand/shrink)
  ├── Need two separate pointers → Two Pointer Style
  └── K distinct / K different → K-Distinct Count
```

### Template: Fixed Window
```java
int windowSum = 0;
for (int i = 0; i < n; i++) {
    windowSum += arr[i];                          // add to window
    if (i >= k - 1) {
        result = max(result, windowSum);          // process window
        windowSum -= arr[i - k + 1];              // remove leftmost
    }
}
```

### Template: Variable Window
```java
int left = 0;
for (int right = 0; right < n; right++) {
    // expand window by adding arr[right]
    while (conditionViolated()) {
        // shrink window by removing arr[left]
        left++;
    }
    // valid window → update result
    result = max(result, right - left + 1);
}
```

---

## 📚 Learning Order

### Phase 1: Fixed Window

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Maximum Sum Subarray of Size K** | [fixed-window/MaximumSumSubarrayOfSizeK.java](fixed-window/MaximumSumSubarrayOfSizeK.java) | Sliding sum, subtract leftmost | 🟢 Easy |
| 2 | **Contains Duplicate II** | [fixed-window/ContainsDuplicateII.java](fixed-window/ContainsDuplicateII.java) | Sliding window of size k, HashSet | 🟢 Easy |
| 3 | **First Negative in Window** | [fixed-window/FirstNegativeNumberInWindow.java](fixed-window/FirstNegativeNumberInWindow.java) | Deque for negative tracking | 🟢 Easy |
| 4 | **Find All Anagrams in a String** | [fixed-window/FindAllAnagramsInAString.java](fixed-window/FindAllAnagramsInAString.java) | Fixed window + frequency array match | 🟡 Medium |

### Phase 2: Variable Window Core

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 5 | **Longest Substring Without Repeating** | [variable-window-core/LongestSubstringWithoutRepeatingCharacters.java](variable-window-core/LongestSubstringWithoutRepeatingCharacters.java) | HashSet/Map, shrink on duplicate | 🟡 Medium |
| 6 | **Longest Repeating Character Replacement** | [variable-window-core/LongestRepeatingCharacterReplacement.java](variable-window-core/LongestRepeatingCharacterReplacement.java) | `windowLen - maxFreq <= k` to shrink | 🟡 Medium |
| 7 | **Permutation in String** | [variable-window-core/PermutationInString.java](variable-window-core/PermutationInString.java) | Fixed anagram check as variable window | 🟡 Medium |
| 8 | **Minimum Window Substring** | [variable-window-core/MinimumWindowSubstring.java](variable-window-core/MinimumWindowSubstring.java) | Expand until all chars matched, shrink while matched | 🔴 Hard |
| 9 | **Longest Nice Subarray** | [variable-window-core/LongestNiceSubarray.java](variable-window-core/LongestNiceSubarray.java) | Bitwise AND condition, shrink when violated | 🟡 Medium |

### Phase 3: Two Pointer Style

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 10 | **Move Zeroes** | [two-pointer-style/MoveZeroes.java](two-pointer-style/MoveZeroes.java) | Non-zero pointer, swap with zero | 🟢 Easy |
| 11 | **Remove Element In-Place** | [two-pointer-style/RemoveElementInPlace.java](two-pointer-style/RemoveElementInPlace.java) | Slow pointer for next non-matching | 🟢 Easy |
| 12 | **Remove Duplicates from Sorted** | [two-pointer-style/RemoveDuplicatesFromSortedArray.java](two-pointer-style/RemoveDuplicatesFromSortedArray.java) | Slow pointer tracks unique elements | 🟢 Easy |
| 13 | **Remove Duplicates II** | [two-pointer-style/RemoveDuplicatesII.java](two-pointer-style/RemoveDuplicatesII.java) | Allow at most 2 duplicates | 🟡 Medium |
| 14 | **Sort Colors (Dutch National Flag)** | [two-pointer-style/SortColors.java](two-pointer-style/SortColors.java) | Three pointers for 0,1,2 | 🟡 Medium |
| 15 | **Pair with Given Sum Sorted** | [two-pointer-style/PairWithGivenSumSortedArray.java](two-pointer-style/PairWithGivenSumSortedArray.java) | Left/Right on sorted array | 🟢 Easy |
| 16 | **Valid Palindrome II** | [two-pointer-style/ValidPalindromeII.java](two-pointer-style/ValidPalindromeII.java) | Two-pointer with skip tolerance | 🟢 Easy |
| 17 | **Backspace String Compare** | [two-pointer-style/BackspaceStringCompare.java](two-pointer-style/BackspaceStringCompare.java) | Traverse from end, skip via counter | 🟢 Easy |

### Phase 4: K-Distinct Count Patterns

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 18 | **Longest Substring with At Most K Distinct** | [k-distinct-count-patterns/LongestSubstringWithAtMostKDistinctCharacters.java](k-distinct-count-patterns/LongestSubstringWithAtMostKDistinctCharacters.java) | Variable window + HashMap for distinct count | 🟡 Medium |
| 19 | **Count Substrings with K Distinct** | [k-distinct-count-patterns/CountSubstringsWithKDistinctCharacters.java](k-distinct-count-patterns/CountSubstringsWithKDistinctCharacters.java) | `atMostK(k) - atMostK(k-1)` trick | 🔴 Hard |
| 20 | **Subarrays with K Different Integers** | [k-distinct-count-patterns/SubarraysWithKDifferentIntegers.java](k-distinct-count-patterns/SubarraysWithKDifferentIntegers.java) | Same `atMostK` trick | 🔴 Hard |

### Phase 5: Sum/Product Based Windows

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 21 | **Minimum Size Subarray Sum** | [sum-product-based/MinimumSizeSubarraySum.java](sum-product-based/MinimumSizeSubarraySum.java) | Shrink when sum >= target | 🟡 Medium |
| 22 | **Subarray Product Less Than K** | [sum-product-based/SubarrayProductLessThanK.java](sum-product-based/SubarrayProductLessThanK.java) | Product-based variable window | 🟡 Medium |
| 23 | **Binary Subarrays with Sum** | [sum-product-based/BinarySubarraysWithSum.java](sum-product-based/BinarySubarraysWithSum.java) | Prefix sum OR sliding window with `atMost` | 🟡 Medium |
| 24 | **Count Subarrays Sum ≤ K** | [sum-product-based/CountSubarraysWithSumLessThanOrEqualK.java](sum-product-based/CountSubarraysWithSumLessThanOrEqualK.java) | Variable window counting all subarrays | 🟡 Medium |

### Phase 6: Window Optimization (Deque/Binary)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 25 | **Max Consecutive Ones III** | [window-optimization/MaxConsecutiveOnesIII.java](window-optimization/MaxConsecutiveOnesIII.java) | Variable window with K flips allowed | 🟡 Medium |
| 26 | **Fruit Into Baskets** | [window-optimization/FruitIntoBaskets.java](window-optimization/FruitIntoBaskets.java) | At most 2 distinct → variable window | 🟡 Medium |
| 27 | **Sliding Window Maximum** | [window-optimization/SlidingWindowMaximum.java](window-optimization/SlidingWindowMaximum.java) | Deque for O(n) max tracking in window | 🔴 Hard |

---

## 🔑 Key Insights

1. **Fixed window** → simple for loop with add/remove
2. **Variable window** → expand right, shrink left while violating condition
3. **Count subarrays with exact K** → `atMost(K) - atMost(K-1)` trick
4. **Two-pointer style** → in-place array manipulation (slow/fast)
5. **Deque for Sliding Window Maximum** → maintains decreasing order of values

---

## 🎯 Practice Checklist

- [ ] Phase 1: Fixed window
- [ ] Phase 2: Variable window core
- [ ] Phase 3: Two pointer style
- [ ] Phase 4: K-distinct count patterns
- [ ] Phase 5: Sum/Product based windows
- [ ] Phase 6: Window optimization