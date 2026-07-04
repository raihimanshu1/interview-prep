# Dynamic Programming

> **Core Pattern:** Break problem into overlapping subproblems, store results to avoid recomputation.  
> **Learning Path:** 1D Fibonacci-like → Grid paths → Subset (Knapsack) → Strings → LIS → 2D → Advanced.

---

## 📖 Conceptual Foundation

### DP Decision Framework
```
1. Does the problem ask for min/max, count, or True/False?
2. Can it be broken into smaller subproblems?
3. Do subproblems overlap (not just divide & conquer)?

If YES → Try DP:
  ├── Single variable n → 1D DP: dp[i]
  ├── Two variables i,j → 2D DP: dp[i][j]
  ├── Grid paths → dp[row][col]
  └── Subset/choice → dp[target]
```

### 1D DP Template (Fibonacci-like)
```java
int[] dp = new int[n + 1];
dp[0] = baseCase;
dp[1] = baseCase;
for (int i = 2; i <= n; i++) {
    dp[i] = dp[i - 1] + dp[i - 2];  // recurrence relation
}
return dp[n];
```

### 2D DP Template (Grid/Edit Distance)
```java
int[][] dp = new int[m + 1][n + 1];
// initialize base cases (first row, first column)
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        if (char1 == char2) dp[i][j] = dp[i-1][j-1];
        else dp[i][j] = min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]) + 1;
    }
}
return dp[m][n];
```

---

## 📚 Learning Order

### Phase 1: 1D — Fibonacci & Classic

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Fibonacci Number** | [1d/FibonacciNumber.java](1d/FibonacciNumber.java) | `dp[i] = dp[i-1] + dp[i-2]` — space optimize to 2 vars | 🟢 Easy |
| 2 | **Climbing Stairs** | [1d/ClimbingStairs.java](1d/ClimbingStairs.java) | Same as Fibonacci, `dp[i] = dp[i-1] + dp[i-2]` | 🟢 Easy |
| 3 | **House Robber** | [1d/HouseRobber.java](1d/HouseRobber.java) | `dp[i] = max(dp[i-1], dp[i-2] + nums[i])` | 🟡 Medium |
| 4 | **House Robber II** | [1d/HouseRobberII.java](1d/HouseRobberII.java) | Circular — run HouseRobber on [0, n-2] and [1, n-1] | 🟡 Medium |
| 5 | **Word Break** | [1d/WordBreak.java](1d/WordBreak.java) | `dp[i]` = can segment s[0..i], check all j < i | 🟡 Medium |
| 6 | **Target Sum** | [1d/TargetSum.java](1d/TargetSum.java) | Subset sum variant: `dp[sum] += dp[sum - num]` | 🟡 Medium |

### Phase 2: Subset / Knapsack

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 7 | **Zero-One Knapsack** | [subset/ZeroOneKnapsack.java](subset/ZeroOneKnapsack.java) | `dp[w] = max(dp[w], dp[w-wt[i]] + val[i])` | 🟡 Medium |
| 8 | **Partition Equal Subset Sum** | [subset/PartitionEqualSubsetSum.java](subset/PartitionEqualSubsetSum.java) | Subset sum = total/2 | 🟡 Medium |

### Phase 3: Core DP

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 9 | **Coin Change** | [core/CoinChange.java](core/CoinChange.java) | `dp[amount] = min(dp[amount], dp[amount-coin] + 1)` | 🟡 Medium |
| 10 | **Longest Increasing Subsequence** | [core/LongestIncreasingSubsequence.java](core/LongestIncreasingSubsequence.java) | `dp[i] = max(dp[j] + 1)` for all j < i where nums[j] < nums[i] | 🟡 Medium |

### Phase 4: Grid Paths

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 11 | **Unique Paths** | [grid/UniquePaths.java](grid/UniquePaths.java) | `dp[i][j] = dp[i-1][j] + dp[i][j-1]` — combinatorics also works | 🟡 Medium |
| 12 | **Minimum Path Sum** | [grid/MinimumPathSum.java](grid/MinimumPathSum.java) | `dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1])` | 🟡 Medium |

### Phase 5: Strings

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 13 | **Longest Common Subsequence** | [strings/LongestCommonSubsequence.java](strings/LongestCommonSubsequence.java) | `if match → 1 + dp[i-1][j-1]; else max(dp[i-1][j], dp[i][j-1])` | 🟡 Medium |
| 14 | **Decode Ways** | [strings/DecodeWays.java](strings/DecodeWays.java) | `dp[i] = dp[i-1] (+ dp[i-2] if two-digit valid)` | 🟡 Medium |
| 15 | **Edit Distance** | [2d/EditDistance.java](2d/EditDistance.java) | `min(insert, delete, replace) + 1` | 🟡 Medium |
| 16 | **Wildcard Matching** | [strings/WildcardMatching.java](strings/WildcardMatching.java) | `?` = skip char, `*` = skip pattern or skip char | 🔴 Hard |
| 17 | **Regular Expression Matching** | [strings/RegularExpressionMatching.java](strings/RegularExpressionMatching.java) | `*` = zero or more of preceding char | 🔴 Hard |

### Phase 6: Advanced 2D

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 18 | **Maximal Square** | [2d/MaximalSquare.java](2d/MaximalSquare.java) | `dp[i][j] = min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]) + 1` | 🟡 Medium |
| 19 | **Burst Balloons** | [2d/BurstBalloons.java](2d/BurstBalloons.java) | Divide & conquer DP: `dp[i][j] = max over last balloon k` | 🔴 Hard |

### Phase 7: DAG

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 20 | **Longest Path in DAG** | [dag/LongestPathInDAG.java](dag/LongestPathInDAG.java) | Topological sort + DP relaxation | 🔴 Hard |

---

## 🔑 Key Insights

1. **State definition** is the most important step — what does `dp[i]` represent?
2. **Recurrence relation** — how does `dp[i]` relate to previous states?
3. **Space optimization**: many 2D DP can be reduced to 1D by reusing rows
4. **Subset sum** → boolean dp[target+1] or integer dp for counting
5. **Classic recurrence patterns**:
   - Fibonacci: `dp[i] = dp[i-1] + dp[i-2]`
   - House Robber: `dp[i] = max(dp[i-1], dp[i-2] + nums[i])`
   - LCS: `if match → 1 + diag; else max(left, up)`
   - Grid: `dp[i][j] = f(dp[i-1][j], dp[i][j-1])`
   - Knapsack: `dp[w] = max(dp[w], dp[w-wt[i]] + val[i])`

---

## 🎯 Practice Checklist

- [ ] Phase 1: 1D DP (Fib, Climb, House Robber)
- [ ] Phase 2: Subset / Knapsack
- [ ] Phase 3: Core (Coin Change, LIS)
- [ ] Phase 4: Grid paths
- [ ] Phase 5: String DP (LCS, Edit Distance)
- [ ] Phase 6: Advanced 2D