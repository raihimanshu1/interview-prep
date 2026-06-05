# Wells Fargo DSA Human-Centric Dry Runs

This companion file fills the "show me the state changing" part of the Karat-style answer. For every problem, practice saying the brute force first, then the optimized version, then the dry run and complexity.

## 1. Missing Number

Dry run: `nums = [3, 0, 1]`. Expected range is `0,1,2,3`. Brute force asks: is `0` present, yes; `1`, yes; `2`, no, so answer is `2`. Optimized sum says expected `6`, actual `4`, leftover `2`.

Complexity: brute force `O(n^2)` time, `O(1)` space. Optimized sum/XOR `O(n)` time, `O(1)` space.

## 2. Expressive Words

Dry run: target `heeellooo`, word `hello`. Compare groups: `h1` vs `h1`, ok. `e3` vs `e1`, ok because target group has at least `3`. `l2` vs `l2`, ok. `o3` vs `o1`, ok. Word counts.

Complexity: brute force generated stretches can be exponential in groups. Optimized group scan is `O(target length + word length)` per word and `O(1)` extra if counted on the fly.

## 3. Valid Parentheses

Dry run: `s = "({[]})"`. Stack receives `(`, `{`, `[`. On `]`, top is `[`, pop. On `}`, top is `{`, pop. On `)`, top is `(`, pop. Stack empty means valid.

Complexity: brute force repeated removal is `O(n^2)` time and `O(n)` space. Optimized stack is `O(n)` time and `O(n)` space.

## 4. Number of Islands

Dry run: grid has land at top-left connected to two neighbors. First unvisited land starts island `1`; DFS marks its whole connected region. Later land in that region is skipped. Next unvisited land starts island `2`.

Complexity: brute force repeated reachability can approach `O((mn)^2)`. Optimized DFS/BFS visits each cell once: `O(mn)` time, `O(mn)` worst-case space.

## 5. Product of Array Except Self

Dry run: `nums = [1,2,3,4]`. Left products before each index: `[1,1,2,6]`. Right scan multiplies by suffixes: index 3 gets `6*1=6`; index 2 gets `2*4=8`; index 1 gets `1*12=12`; index 0 gets `1*24=24`.

Complexity: brute force `O(n^2)` time, `O(1)` extra space. Optimized prefix/suffix `O(n)` time, `O(1)` extra space excluding output.

## 6. Kth Largest Element in a Stream

Dry run: `k=3`, values `4,5,8,2`. Heap grows `[4]`, `[4,5]`, `[4,5,8]`; kth largest is heap top `4`. Add `10`: push then remove smallest `4`, heap top becomes `5`.

Complexity: brute force sort each add is `O(n log n)` per add. Optimized heap is `O(log k)` per add and `O(k)` space.

## 7. Spiral Matrix

Dry run: for `[[1,2,3],[4,5,6],[7,8,9]]`, boundaries start top `0`, bottom `2`, left `0`, right `2`. Read top `1,2,3`, right `6,9`, bottom `8,7`, left `4`, then shrink and read center `5`.

Complexity: visited-matrix brute force `O(mn)` time, `O(mn)` space. Boundary optimized `O(mn)` time, `O(1)` extra space.

## 8. Longest Common Prefix

Dry run: `["flower","flow","flight"]`. Column `0` all `f`; column `1` all `l`; column `2` has `o` vs `i`, stop. Answer `fl`.

Complexity: brute force prefix testing can be `O(n*s^2)`. Column scan is `O(total compared characters)` time and `O(1)` space.

## 9. Maximum Subarray

Dry run: `[-2,1,-3,4,-1,2,1,-5,4]`. Running best ending here resets at `4`, then extends to `4 + -1 + 2 + 1 = 6`. Global best becomes `6`.

Complexity: brute force intervals `O(n^2)` time with running sums, `O(1)` space. Kadane `O(n)` time, `O(1)` space.

## 10. LFU Cache

Dry run: capacity `2`. `put(1,A)` and `put(2,B)` both frequency `1`; `minFreq=1`. `get(1)` moves key `1` from freq `1` bucket to freq `2`. `put(3,C)` evicts from `minFreq=1`, so key `2` leaves.

Optimized state transition: on every `get`, remove node from old frequency list, increment its frequency, insert into new frequency list as most recent. If the old list was `minFreq` and becomes empty, increase `minFreq`. On new `put`, frequency starts at `1`, so `minFreq` resets to `1`.

Complexity: brute force scan-on-evict `O(n)` eviction. Optimized hashmap plus frequency lists gives `O(1)` average `get` and `put`, `O(capacity)` space.

## 11. Meeting Rooms II

Dry run: meetings `[0,30],[5,10],[15,20]`. Brute force checks start time `0`: one active meeting. Start time `5`: `[0,30]` and `[5,10]` are active, count `2`. Start time `15`: `[0,30]` and `[15,20]` are active, count `2`. Answer `2`. Optimized starts/ends reach the same answer by reusing a room when the earliest end is before the next start.

Complexity: brute force active-count scan at each start time is `O(n^2)` time. Optimized sorting `O(n log n)` time, `O(n)` space.

## 12. Backspace String Compare

Dry run: `s="ab#c"` becomes `ac`; `t="ad#c"` becomes `ac`. Backward optimized scan sees `c` vs `c`, then skips `b` and `d` because of `#`, then sees `a` vs `a`.

Complexity: brute force build strings `O(n+m)` time and `O(n+m)` space. Backward scan `O(n+m)` time, `O(1)` space.

## 13. Intersection of Two Linked Lists

Dry run: A length `5`, B length `3`, shared tail starts at node `X`. Pointer A reaches end then switches to B; pointer B reaches end then switches to A. After both walk `5+3`, they align and meet at `X`.

Complexity: brute force `O(mn)` time, `O(1)` space. Two-pointer optimized `O(m+n)` time, `O(1)` space.

## 14. LRU Cache

Dry run: capacity `2`. `put(1,A)`, `put(2,B)` order is `2,1` most-to-least recent. `get(1)` moves `1` to front: `1,2`. `put(3,C)` evicts tail `2`.

Complexity: brute force list scan `O(n)` per operation. Optimized hashmap plus doubly linked list `O(1)` average per operation and `O(capacity)` space.

## 15. Restore IP Addresses

Dry run: `s="25525511135"`. Backtracking tries `255`, `255`, `11`, `135` and accepts `255.255.11.135`. It also tries `255`, `255`, `111`, `35` and accepts `255.255.111.35`.

Complexity: brute force dot placement is bounded because IP length is max `12`; conceptually combinational. Backtracking is also bounded with at most 4 levels and strong pruning, `O(1)` for fixed IP constraints.

## 16. Rotate String

Dry run: `s="abcde"`, goal `"cdeab"`. `s+s = "abcdeabcde"`, and `"cdeab"` appears inside it, so goal is a rotation.

Complexity: brute force rotations `O(n^2)` time. Optimized doubled-string search is `O(n)` with efficient search, `O(n)` space.

## 17. Merge Strings Alternately

Dry run: `word1="abc"`, `word2="pqrs"`. Append `a,p,b,q,c,r`, then append leftover `s`. Answer `apbqcrs`.

Complexity: repeated string concatenation can be `O((n+m)^2)`. `StringBuilder` version is `O(n+m)` time and `O(n+m)` output space.

## 18. Minimum String Length After Removing Substrings

Dry run: `s="ABFCACDB"`. Stack sees `A`, then `B` cancels `AB`. Later `C`,`D` cancels `CD`. Final stack contains remaining chars; return its size.

Complexity: repeated replace `O(n^2)` time. Stack solution `O(n)` time, `O(n)` space.

## 19. Count Binary Substrings

Dry run: `s="00110011"`. Group lengths are `2,2,2,2`. When the third group starts, finalize first pair and add `2`. When the fourth starts, add `2`. After the loop, add the final pair `2`. Total `6`.

Complexity: brute force substring validation `O(n^2)` to `O(n^3)`. Group count optimized `O(n)` time, `O(1)` space.

## 20. Maximum Number of Weeks

Dry run: milestones `[5,2,1]`. Largest `5`, rest `3`. Since `5 > 3 + 1`, cannot use all. Best is alternate big with rest: `P1,other,P1,other,P1,other,P1`, total `2*3+1=7`.

Complexity: brute force scheduling exponential. Greedy math `O(n)` time, `O(1)` space.

## 21. Maximum Earnings From Taxi

Dry run: rides sorted by end. For ride `i`, profit is `end-start+tip`. If previous compatible ride index is `p`, compare skipping `dp[i-1]` versus taking `profit[i] + dp[p]`. Store the larger.

Optimized recurrence: `dp[i] = max(dp[i - 1], rideProfit[i] + dp[previousCompatibleIndex])`.

Complexity: brute force subsets exponential. Weighted interval DP `O(n log n)` time due to sorting and binary search, `O(n)` space.

## 22. Verbal Arithmetic Puzzle

Dry run: `SEND + MORE = MONEY`. Start from right column: `D + E` must produce `Y` and carry. If assigned digits make that impossible, backtrack before assigning the left columns.

Complexity: brute force permutations up to `O(10!)`. Column-pruned backtracking is still exponential but prunes early; space `O(unique letters)`.

## 23. Regex / Pattern Validation

Dry run: `s="aab"`, `p="c*a*b"`. `c*` can mean zero `c`, skip it. `a*` consumes two `a`s. `b` matches `b`. Full string matches.

Complexity: brute force recursion exponential because `*` branches repeat states. DP/memo is `O(nm)` time and `O(nm)` space.

## 24. 0/1 Knapsack

Dry run: capacity `5`, items `(w=2,v=3)`, `(w=3,v=4)`, `(w=4,v=5)`. At capacity `5`, taking first two gives value `7`; taking third gives `5`; best `7`.

Complexity: brute force take/skip `O(2^n)` time. DP `O(n*capacity)` time and `O(capacity)` or `O(n*capacity)` space.

## 25. Group Anagrams

Dry run: `["eat","tea","tan","ate"]`. Sorted keys: `aet` gets `eat,tea,ate`; `ant` gets `tan`.

Complexity: brute force group comparison can be `O(n^2*k log k)`. Signature map is `O(n*k log k)` with sorted key or `O(n*k)` with count key.

## 26. Longest Substring Without Repeating Characters

Dry run: `abcabcbb`. Window grows `abc`. Next `a` repeats at index `0`, so left jumps to `1`; best remains `3`.

Complexity: brute force `O(n^2)` or `O(n^3)`. Sliding window `O(n)` time, `O(charset)` space.

## 27. Best Time to Buy and Sell Stock

Dry run: prices `[7,1,5,3,6,4]`. Minimum becomes `1`. Selling at `5` gives profit `4`; selling at `6` gives profit `5`, best answer.

Complexity: brute force pairs `O(n^2)` time. One-pass min tracking `O(n)` time, `O(1)` space.

## 28. Merge Intervals

Dry run: intervals `[1,3],[2,6],[8,10]`. Sort by start. `[2,6]` overlaps `[1,3]`, merge to `[1,6]`. `[8,10]` does not overlap, append.

Complexity: brute force repeated pair merge `O(n^2)` or worse. Sort-and-scan `O(n log n)` time, `O(n)` output space.

## 29. Kth Largest Element in an Array

Dry run: nums `[3,2,1,5,6,4]`, `k=2`. Min-heap size 2 keeps the two largest seen. After all values, heap contains `5,6`; top `5` is second largest.

Complexity: sorting `O(n log n)`. Heap `O(n log k)` time, `O(k)` space. Quickselect average `O(n)` time.

## 30. Top K Frequent Elements

Dry run: nums `[1,1,1,2,2,3]`, `k=2`. Counts are `1->3`, `2->2`, `3->1`. Top two are `[1,2]`.

Complexity: count+sort `O(n + u log u)`. Bucket sort `O(n)` time, `O(n)` space; heap `O(n log k)` time.

## 31. Top K Frequent Words

Dry run: words `["i","love","leetcode","i","love","coding"]`, `k=2`. Counts: `i=2`, `love=2`, others `1`. Tie uses alphabetic order, so output `[i,love]`.

Complexity: count+sort `O(n + u log u)`. Heap size `k` `O(n + u log k)` time, `O(u+k)` space.

## 32. Rotting Oranges

Dry run: in brute force, minute `1` first marks all fresh oranges adjacent to originally rotten oranges with a temporary marker, then commits them to rotten after the full scan. In optimized BFS, initial rotten cells enter queue at minute `0`; their fresh neighbors become minute `1`; those neighbors rot others at minute `2`. Last fresh rotten minute is answer.

Complexity: brute force minute scans `O(mn*minutes)`. Multi-source BFS `O(mn)` time, `O(mn)` space.

## 33. Clone Graph

Dry run: node `1` has neighbor `2`; `2` points back to `1`. Brute force first collects originals `[1,2]`, creates clone list `[1',2']`, then reconstructs edges by searching for each neighbor's clone. Optimized DFS clones `1` and stores map. When cloning `2` sees neighbor `1`, it reuses `1'` from the map instead of searching or recursing forever.

Complexity: duplicate-prone brute force is inefficient and unsafe on cycles. Map-based DFS/BFS `O(V+E)` time, `O(V)` space.

## 34. Course Schedule

Dry run: prerequisites `A->B`, `B->C`. Indegree: `A=0`, `B=1`, `C=1`. Queue starts `A`; removing `A` makes `B=0`; removing `B` makes `C=0`; all processed, possible.

Complexity: brute force repeated scanning `O(VE)` worst case. Topological sort `O(V+E)` time and `O(V+E)` space.

## 35. Word Ladder

Dry run: `hit -> cog`, dictionary has `hot,dot,dog,lot,log,cog`. BFS level 1: `hit`; level 2: `hot`; level 3: `dot,lot`; level 4: `dog,log`; level 5: `cog`, answer `5`.

Optimized invariant: BFS explores all transformations at distance `d` before distance `d+1`, so first time we see `endWord` is shortest. Mark visited as soon as a word is queued.

Complexity: baseline neighbor generation `O(wordCount * wordLength * 26)` plus lookup cost; space `O(wordCount)`.

## 36. Word Search

Dry run: board has `A B C E / S F C S / A D E E`, word `ABCCED`. Start at `A`, move to `B`, `C`, `C`, `E`, `D`; mark each visited during path, then restore on return.

Complexity: DFS from every cell `O(mn*4^L)` worst case, `O(L)` recursion space.

## 37. Subsets

Dry run: nums `[1,2]`. Start `[]`. Include `1` gives `[1]`; include `2` gives `[1,2]`; backtrack, choose `[2]`. Output `[[],[1],[1,2],[2]]`.

Complexity: all methods output `2^n` subsets, `O(n*2^n)` time including copy cost and `O(n)` recursion space plus output.

## 38. Permutations

Dry run: nums `[1,2,3]`. First slot choose `1`; second choose `2`; third choose `3`, output `[1,2,3]`. Backtrack third, then second, and try new unused choices.

Complexity: `O(n!*n)` time including copies, `O(n)` recursion space plus output.

## 39. Min Stack

Dry run: push `5` with min `5`; push `3` with min `3`; push `7` with min `3`. `getMin` returns `3`. Pop `7`, min still `3`. Pop `3`, min returns to `5`.

Complexity: brute force `getMin` scan `O(n)`. Optimized all operations `O(1)` time, `O(n)` space.

## 40. Time Based Key-Value Store

Dry run: set `foo=bar` at `1`, `foo=bar2` at `4`. get `foo` at `3` binary searches timestamps `[1,4]` and returns timestamp `1`, value `bar`.

Complexity: brute force get `O(n)` per key history. Sorted-list get `O(log n)`, set `O(1)` only if timestamps are increasing. `TreeMap` handles unordered set/get in `O(log n)`.

## 41. Median From Data Stream

Dry run: add `1`, lower heap `[1]`, median `1`. Add `2`, upper heap `[2]`, median `(1+2)/2`. Add `3`, upper has too many, rebalance so upper top `2` or lower top gives median `2`.

Complexity: brute force sort on demand `O(n log n)` per median or `O(n)` sorted insert. Two heaps add `O(log n)`, median `O(1)`, space `O(n)`.

## 42. Set Matrix Zeroes

Dry run: matrix has zero at `(1,1)`. First pass marks row `1` and column `1` using first column and first row. Second pass zeros marked inner cells. Final pass handles first row/column based on saved flags.

Optimized state transition: do not zero immediately during marker pass. First mark, then apply, because immediate zeroing would create false markers.

Complexity: brute force copy `O(mn(m+n))` naive time and `O(mn)` space. Marker optimized `O(mn)` time, `O(1)` extra space.

## 43. 3Sum

Dry run: nums `[-1,0,1,2,-1,-4]`, sorted `[-4,-1,-1,0,1,2]`. Fix `-1`, two pointers find `0+1=1`, forming `[-1,0,1]`; skip duplicate `-1`.

Complexity: brute force `O(n^3)`. Sort plus two pointers `O(n^2)` time, `O(1)` extra space excluding output.

## 44. Merge k Sorted Lists

Dry run: heads are `1,1,2`. Heap pops `1` from list A, then pushes A's next `4`. Heap again picks the smallest visible head. Repeat until heap empty.

Complexity: collect+sort `O(N log N)` time, `O(N)` space. Heap `O(N log k)` time, `O(k)` space.

## 45. Reverse Linked List

Dry run: `1->2->3`. `prev=null`, `cur=1`. Save `2`, point `1->null`, move. Save `3`, point `2->1`, move. Point `3->2`; new head `3`.

Complexity: stack brute force `O(n)` time, `O(n)` space. Pointer reversal `O(n)` time, `O(1)` space.

## 46. Linked List Cycle

Dry run: list `1->2->3->4->2`. Slow moves one step, fast two. Inside the loop, fast eventually lands on slow, proving a cycle.

Complexity: visited set `O(n)` time, `O(n)` space. Floyd `O(n)` time, `O(1)` space.

## 47. Longest Palindromic Substring

Dry run: `babad`. Center at `a` index `1` expands to `bab`; center at `b` index `2` expands to `aba`. Either length `3` is valid.

Complexity: brute force `O(n^3)` time. Expand centers `O(n^2)` time, `O(1)` space.

## 48. Word Break

Dry run: `leetcode`, dict `{leet, code}`. `dp[0]=true`. At `i=4`, `leet` makes `dp[4]=true`. At `i=8`, `code` from `4` makes `dp[8]=true`.

Complexity: brute force exponential. DP `O(n^2)` states/checks, with substring costs depending on language; `O(n)` space.

## 49. Coin Change

Dry run: coins `[1,2,5]`, amount `11`. `dp[5]=1`, `dp[10]=2`, then `dp[11]=dp[10]+1=3` using coin `1`, or `dp[6]+5`; best is `3` with `5+5+1`.

Complexity: brute force exponential. DP `O(amount * coinCount)` time, `O(amount)` space.

## 50. Partition Equal Subset Sum

Dry run: nums `[1,5,11,5]`, total `22`, target `11`. DP can form `1`, then `5,6`, then `11`, so answer true.

Complexity: brute force `O(2^n)`. Subset DP `O(n*target)` time, `O(target)` space.

## 51. Binary Search

Dry run: nums `[1,3,5,7,9]`, target `7`. Mid `5` too small, search right. Mid `7` found.

Complexity: linear scan `O(n)`. Binary search `O(log n)` time, `O(1)` space.

## 52. Search in Rotated Sorted Array

Dry run: nums `[4,5,6,7,0,1,2]`, target `0`. Mid `7`; left side sorted but target not in `[4,7]`, go right. Mid `1`; left side `[0,1]` contains target, go left and find `0`.

Complexity: brute force `O(n)`. Modified binary search `O(log n)` time, `O(1)` space when no duplicates.

## 53. Valid Sudoku

Dry run: when reading digit `5` at `(0,0)`, add `5` to row `0`, column `0`, box `0`. If another `5` appears in row `0`, the row set already contains it, invalid.

Complexity: brute force repeated scans are bounded for 9x9 but conceptually repetitive. Set approach `O(81)` time and `O(81)` space.

## 54. Implement Trie

Dry run: insert `apple`. Create path `a->p->p->l->e` and mark end at `e`. Search `app` reaches node but end flag false. Prefix `app` returns true.

Complexity: list scan `O(numberOfWords * wordLength)`. Trie operations `O(word length)` time; space is `O(total characters stored)`.

## 55. Word Search II

Dry run: words `oath, pea, eat, rain`. Trie shares prefixes. DFS path `o->a->t->h` reaches trie word end, add `oath`. Paths that are not trie prefixes stop immediately.

Complexity: brute force per word `O(words * mn * 4^L)`. Trie+DFS still has exponential path worst case but prunes heavily; space is trie plus recursion.

## B1. Failed Login Rolling Window

Dry run: failures for account A at `10:00,10:05,10:20,11:30`, threshold `3` in `30` minutes. Window from `10:00` to `10:20` has three failures, so A is flagged.

Complexity: brute force `O(k^2)` per account. Sort+sliding window `O(k log k)` per account, then `O(k)` scan.

## B2. Idempotent Payment Requests

Dry run: request key `abc`, body hash `h1` starts as `IN_PROGRESS`, then stores response `success txn123`. Retry with key `abc` and hash `h1` returns same response. Retry with hash `h2` is rejected.

Complexity: scan history `O(n)` per request. Key-value/database lookup `O(1)` average or `O(log n)` depending storage; space `O(number of retained keys)`.

## B3. Ledger Reconciliation

Dry run: internal has `(ref=R1, amount=10.00, USD)`, external has same key, consume both. Internal `(R2, 20.00)` has no external match, report unmatched internal with reason.

Complexity: brute force `O(nm)`. Composite-key map `O(n+m)` average time and `O(n+m)` space.

## B4. Batch Dependency Scheduler

Dry run: jobs `A,B,C`, dependencies `A->B`, `B->C`. Queue starts `A`. Process `A`, unlock `B`; process `B`, unlock `C`; process `C`. Order `[A,B,C]`.

Complexity: repeated scan `O(VE)` worst case. Topological sort `O(V+E)` time and `O(V+E)` space.

## B5. Transaction Stream Top K Risky Accounts

Dry run: scores update `A=10`, `B=7`, `C=12`, `k=2`. Min-heap keeps `A=10,C=12`; `B=7` is below heap top and stays out. Output sorted top risk accounts `C,A`.

Complexity: sort all accounts after each update `O(u log u)`. Batch heap `O(n + u log k)`; real-time indexed heap/tree updates `O(log u)`.
