# Backtracking

> **Core Pattern:** Explore all possibilities via recursion, making a choice → recurse → undo the choice.  
> **Learning Path:** Subsets → Combinations → Permutations → Constraint-based → Optimization.

---

## 📖 Conceptual Foundation

### Backtracking Template
```java
List<List<Integer>> result = new ArrayList<>();
List<Integer> temp = new ArrayList<>();

void backtrack(int[] nums, int start) {
    result.add(new ArrayList<>(temp));  // add current combination
    
    for (int i = start; i < nums.length; i++) {
        temp.add(nums[i]);              // choose
        backtrack(nums, i + 1);         // explore
        temp.remove(temp.size() - 1);   // un-choose
    }
}
```

### Three Core Variants
| Pattern | Type | Key Difference |
|---------|------|---------------|
| Subsets | Choose/Not choose | `result.add()` at each step |
| Combinations | Choose K from N | Add only when `temp.size() == k` |
| Permutations | All orderings | Use `used[]` boolean array, start from 0 |

### Pruning Conditions
```
if (condition) continue;    // Skip invalid choices
if (sum > target) return;   // Stop exploring this branch
if (i > start && nums[i] == nums[i-1]) continue;  // Skip duplicates (sorted array)
```

---

## 📚 Learning Order

### Phase 1: Subsets (Power Set)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Subsets** | [Subsets.java](Subsets.java) | Classic choose/not-choose at each index | 🟡 Medium |
| 2 | **Subsets II** | [SubsetsII.java](SubsetsII.java) | Same + sort + skip duplicates | 🟡 Medium |

### Phase 2: Combinations

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 3 | **Combination Sum** | [CombinationSum.java](CombinationSum.java) | Unbounded — same element can be reused (no `i+1`, pass `i`) | 🟡 Medium |
| 4 | **Combination Sum II** | [CombinationSumII.java](CombinationSumII.java) | Bounded + sorted + skip duplicates | 🟡 Medium |

### Phase 3: Permutations

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 5 | **Permutations** | [Permutations.java](Permutations.java) | `used[]` array, always start from index 0 | 🟡 Medium |
| 6 | **Permutations II** | [PermutationsII.java](PermutationsII.java) | Same + sort + skip if `used[i-1]` false | 🟡 Medium |

### Phase 4: Constraint-Based

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 7 | **Letter Combinations of a Phone Number** | [LetterCombinationsOfAPhoneNumber.java](LetterCombinationsOfAPhoneNumber.java) | Mapping digits → letters, iterate over mapped chars | 🟡 Medium |
| 8 | **Palindrome Partitioning** | [PalindromePartitioning.java](PalindromePartitioning.java) | Check palindrome prefix, then recurse on remainder | 🟡 Medium |
| 9 | **N-Queens** | [NQueens.java](NQueens.java) | Place row by row, check col + diagonals | 🔴 Hard |
| 10 | **Sudoku Solver** | [SudokuSolver.java](SudokuSolver.java) | Find empty → try 1-9 → check validity → recurse → backtrack | 🔴 Hard |

---

## 🔑 Key Insights

1. **Subsets** = choose/not-choose at each index (start from `i`, pass `i+1`)
2. **Permutations** = use all elements in different orders (start from `0` with `used[]`)
3. **Combinations with duplicates** = sort + `if (i > start && nums[i] == nums[i-1]) continue`
4. **Constraint problems** = prune branches that can't lead to solution
5. **Time complexity**: Subsets O(2^n), Permutations O(n!), Combinations O(C(n,k))

---

## 🎯 Practice Checklist

- [ ] Phase 1: Subsets (I, II)
- [ ] Phase 2: Combinations (I, II)
- [ ] Phase 3: Permutations (I, II)
- [ ] Phase 4: Constraint-based (Phone, Palindrome, N-Queens, Sudoku)