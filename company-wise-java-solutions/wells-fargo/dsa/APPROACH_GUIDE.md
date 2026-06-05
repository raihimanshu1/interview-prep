# Wells Fargo DSA Approach Guide

This guide expands the Wells Fargo DSA index into Karat-style interview notes. Each answer should be spoken in this order:

1. Restate the problem in simple words.
2. Give the brute-force approach first.
3. Explain what repeated work the optimized approach removes.
4. Dry run one example.
5. State complexity and pitfalls.

## Review Agent Checklist Applied

- Brute force and optimized approaches are both included.
- Each optimized approach explains the improvement, not just the data structure.
- Banking-style pitfalls are called out: duplicates, ordering, idempotency, overflow, money precision, timestamps, and audit-safe output.
- The hardest `todo` items are expanded enough to implement later.
- Linked existing Java solutions remain the source of executable code where present.

## Core Reported / Tagged Problems

| # | Problem | Brute Force | Optimized | Pitfalls |
|---:|---|---|---|---|
| 1 | Missing Number | Check each candidate `0..n` by scanning the array. `O(n^2)`, `O(1)`. | Expected sum minus actual sum, or XOR all indexes and values. `O(n)`, `O(1)`. | Missing value can be `0` or `n`; sum can overflow for huge input. |
| 2 | Expressive Words | Generate possible stretched forms for each word and compare to target. Exponential in groups. | Compare character groups on the fly. Target group can absorb smaller word group only if target count is at least `3`. `O(total chars)`. | Target group length `2` cannot stretch; all groups must align exactly. |
| 3 | Valid Parentheses | Repeatedly remove `()`, `{}`, `[]` until unchanged. `O(n^2)`, `O(n)`. | Stack openers and match closers. `O(n)`, `O(n)`. | Closing bracket on empty stack is invalid; stack must end empty. |
| 4 | Number of Islands | For each land cell, repeatedly rediscover connectivity. Up to `O((mn)^2)`. | Count unvisited land, then DFS/BFS flood-fill each island once. `O(mn)`, `O(mn)`. | Standard problem uses 4-direction adjacency, not diagonal. |
| 5 | Product of Array Except Self | For every index, multiply every other index. `O(n^2)`, `O(1)`. | Prefix product from left and suffix product from right. `O(n)`, `O(1)` extra. | Avoid division because zero handling breaks it. |
| 6 | Kth Largest Stream | Store all values and sort after every `add`. `O(n log n)` per add. | Min-heap of size `k`; top is kth largest. `O(log k)` per add. | Define behavior before `k` items have arrived. |
| 7 | Spiral Matrix | Use visited matrix and direction turns. `O(mn)`, `O(mn)`. | Maintain top/bottom/left/right boundaries. `O(mn)`, `O(1)` extra. | Single row/column can be duplicated if boundary checks are loose. |
| 8 | Longest Common Prefix | Try every prefix of first string against all strings. Up to `O(n*s^2)`. | Shrink prefix or scan column-wise once. `O(total chars inspected)`. | Empty list or empty string returns `""`. |
| 9 | Maximum Subarray | Try every start/end and keep running sum. `O(n^2)`, `O(1)`. | Kadane: best subarray ending here is extend-or-restart. `O(n)`, `O(1)`. | All-negative arrays return the least negative value, not zero. |
| 10 | LFU Cache | Store key/value/frequency/timestamp; scan all keys to evict. `O(n)` eviction. | `key -> node`, `frequency -> ordered list`, and `minFrequency`. All operations `O(1)`. | Capacity `0`; update `minFrequency`; tie-break by recency inside same frequency. |
| 11 | Meeting Rooms II | Count overlaps for every meeting. `O(n^2)`. | Sort starts and ends; reuse room when earliest end is <= next start. `O(n log n)`. | End equal to start means room can be reused. |
| 12 | Backspace String Compare | Build both final strings using stack/builder. `O(n+m)`, `O(n+m)`. | Walk backward, skipping deleted chars with counters. `O(n+m)`, `O(1)`. | Extra backspaces before any char should not crash. |
| 13 | Intersection of Two Linked Lists | For each node in A, scan B for same reference. `O(mn)`. | Two pointers switch heads after reaching null. `O(m+n)`, `O(1)`. | Intersection means same node object, not same value. |
| 14 | LRU Cache | Keep ordered list; scan to find key and move it. `O(n)`. | HashMap plus doubly linked list. `O(1)` get/put. | Capacity `0`; updating existing key refreshes recency. |
| 15 | Restore IP Addresses | Put three dots in all positions and validate segments. Bounded but combinational. | Backtrack four segments and prune impossible remaining lengths. Bounded, recursion depth `4`. | Segment must be `0..255`; no leading zero unless exactly `"0"`. |
| 16 | Rotate String | Try every rotation and compare. `O(n^2)`. | Check `goal` inside `s + s` when lengths match. `O(n)` with efficient search. | Empty string rotates to empty string. |
| 17 | Merge Strings Alternately | Concatenate one char at a time. Can become `O(n^2)`. | Use `StringBuilder`, append alternating chars then remainder. `O(n+m)`. | Unequal lengths require leftover suffix. |
| 18 | Remove `AB` / `CD` Substrings | Repeatedly replace pairs until unchanged. `O(n^2)`. | Stack chars; remove when top/current form `AB` or `CD`. `O(n)`. | One removal can create a new removable pair. |
| 19 | Count Binary Substrings | Generate substrings and validate two balanced groups. `O(n^2)` to `O(n^3)`. | Count consecutive groups; add `min(prevGroup, currGroup)`. `O(n)`. | Pattern must be two contiguous groups, not alternating. |
| 20 | Maximum Number of Weeks | Backtrack all valid project schedules. Exponential. | Let `max` be biggest project and `rest` all others. If `max <= rest + 1`, use all; else answer `2*rest+1`. `O(n)`. | Use `long`; this is a greedy proof, not just simulation. |
| 21 | Maximum Earnings From Taxi | Try every subset of non-overlapping rides. Exponential. | Weighted interval DP sorted by end time plus binary search previous compatible ride. `O(n log n)`. | Profit is `end - start + tip`; sort by end, not start. |
| 22 | Verbal Arithmetic Puzzle | Try all digit permutations for letters. Up to `10!`. | Backtrack column by column from right to left with carry and leading-zero pruning. Exponential but heavily pruned. | More than 10 letters impossible; leading letters cannot be zero. |
| 23 | Regex / Pattern Validation | Recursive branching on `*`. Exponential without memo. | DP over `(stringIndex, patternIndex)`. `O(nm)`. | Clarify full-match vs substring; `.`/`*` regex differs from wildcard `?`/`*`. |
| 24 | 0/1 Knapsack | Take/skip every item. `O(2^n)`. | DP over item and capacity. `O(n*capacity)`, space can be `O(capacity)`. | 0/1 means each item once; iterate capacity backward in 1D DP. |
| 25 | Group Anagrams | Compare each word to group representatives. Often `O(n^2*k log k)`. | Map sorted-letter or count-vector signature to group. `O(n*k log k)` or `O(n*k)`. | Deterministic output may require sorting groups/items. |
| 26 | Longest Substring Without Repeating Characters | Try every substring and check uniqueness. `O(n^2)`/`O(n^3)`. | Sliding window with last seen index. `O(n)`. | Left pointer must never move backward. |
| 27 | Best Time to Buy and Sell Stock | Try every buy/sell pair. `O(n^2)`. | Track minimum price so far and best profit. `O(n)`. | If no profit exists, return `0`. |
| 28 | Merge Intervals | Repeatedly scan for overlapping pairs and merge. `O(n^2)`. | Sort by start, merge in one pass. `O(n log n)`. | Touching intervals usually merge: `[1,3]` and `[3,5]`. |
| 29 | Kth Largest in Array | Sort all numbers. `O(n log n)`. | Min-heap size `k` is `O(n log k)`; Quickselect average `O(n)`. | Duplicates count as separate elements. |
| 30 | Top K Frequent Elements | Count then sort all unique values. `O(n + u log u)`. | Bucket by frequency `O(n)` or heap size `k` `O(n log k)`. | Tie order is unspecified unless stated. |
| 31 | Top K Frequent Words | Count then sort unique words by frequency desc and word asc. `O(n + u log u)`. | Heap size `k` with reversed comparator for worst candidate. `O(n + u log k)`. | Final tie-break is lexicographic ascending. |
| 32 | Rotting Oranges | Scan whole grid minute by minute. `O(mn*minutes)`. | Multi-source BFS from all rotten oranges. `O(mn)`. | If fresh oranges remain, return `-1`. |
| 33 | Clone Graph | Clone per edge and fix duplicates later. Messy and duplicate-prone. | DFS/BFS with map `original -> clone`. `O(V+E)`. | Put clone in map before visiting neighbors to handle cycles. |
| 34 | Course Schedule | For every course, DFS for self-dependency. Repeats work. | Topological sort or DFS colors. `O(V+E)`. | Edge direction matters. |
| 35 | Word Ladder | Try all transformation sequences. Exponential. | BFS by one-letter transformations; first reach is shortest. `O(wordCount*wordLength*alphabet)` baseline. | End word absent means `0`; remove visited words. |
| 36 | Word Search | DFS from every cell for every path. `O(mn*4^L)`. | Same DFS with early char pruning and in-place visited marking. Still exponential but practical. | Restore cell after backtracking; no cell reuse in same path. |
| 37 | Subsets | Use all bitmasks. `O(n*2^n)`. | Backtracking builds subsets incrementally. Same output complexity, cleaner. | Output size is already exponential. |
| 38 | Permutations | Recursively try every unused number. `O(n!*n)`. | Backtracking with `used[]` or in-place swap; same output size, less overhead. | Duplicates need sorting and skip logic. |
| 39 | Min Stack | Scan stack for min on `getMin`. `O(n)` getMin. | Store current min with each value or use second min stack. All `O(1)`. | Popping current min must restore previous min. |
| 40 | Time Based Key-Value Store | Scan all timestamps for key on every get. `O(n)`. | Store sorted timestamps and binary search. `O(log n)` get. Use `TreeMap` if events are unordered. | Linked solution assumes increasing timestamps. Banking logs may be unordered. |
| 41 | Median from Data Stream | Sort on demand or insert into sorted list. `O(n log n)` or `O(n)` update. | Max-heap lower half, min-heap upper half. Add `O(log n)`, median `O(1)`. | Avoid overflow averaging two ints. |
| 42 | Set Matrix Zeroes | Copy matrix, zero rows/cols based on original. `O(mn(m+n))`, `O(mn)`. | Use first row/col as markers plus two flags. `O(mn)`, `O(1)`. | First row and first column need separate original-zero flags. |
| 43 | 3Sum | Try every triple. `O(n^3)`. | Sort, fix one number, two-pointer remaining pair. `O(n^2)`. | Skip duplicates at fixed and pointer positions. |
| 44 | Merge k Sorted Lists | Collect all nodes/values and sort. `O(N log N)`. | Min-heap of current list heads. `O(N log k)`. | Be careful if reusing/mutating nodes. |
| 45 | Reverse Linked List | Push values/nodes to stack and rebuild. `O(n)`, `O(n)`. | Reverse pointers with `prev`, `cur`, `next`. `O(n)`, `O(1)`. | Save `next` before overwriting `cur.next`. |
| 46 | Linked List Cycle | Store visited nodes in set. `O(n)`, `O(n)`. | Floyd slow/fast pointers. `O(n)`, `O(1)`. | Check `fast` and `fast.next`. |
| 47 | Longest Palindromic Substring | Check every substring for palindrome. `O(n^3)`. | Expand around all odd/even centers. `O(n^2)`, `O(1)`. | Even-length centers are easy to miss. |
| 48 | Word Break | Recursively try all dictionary prefixes. Exponential. | DP `dp[i] = true` if prefix `0..i` can segment. `O(n^2)`. | Use `HashSet` dictionary; substring cost may matter. |
| 49 | Coin Change | Recursively try every coin at every amount. Exponential. | DP minimum coins for each amount. `O(amount*coins)`. | Use sentinel for impossible states. |
| 50 | Partition Equal Subset Sum | Try every subset. `O(2^n)`. | Subset-sum DP to target `total/2`. `O(n*target)`. | Odd total is immediately impossible. |
| 51 | Binary Search | Linear scan. `O(n)`. | Halve sorted search space. `O(log n)`. | Use overflow-safe mid. |
| 52 | Search Rotated Sorted Array | Linear scan. `O(n)`. | Binary search; one side is sorted each step. `O(log n)`. | Duplicates require extra handling. |
| 53 | Valid Sudoku | For each filled cell, scan row/col/box. Bounded but repetitive. | Sets for row, column, and box seen values. `O(81)`. | Box index: `(r/3)*3 + c/3`. |
| 54 | Implement Trie | Store words in list; scan for exact/prefix lookup. `O(words*length)`. | Trie node children plus end flag. `O(length)` operations. | Prefix existence is different from complete word existence. |
| 55 | Word Search II | Run Word Search separately for each word. `O(words*mn*4^L)`. | Trie all words, DFS board once with prefix pruning. | Avoid duplicate output; empty board and non-lowercase input need guards. |

## In-Depth Scripts For Highest-Risk Items

### LFU Cache

School-level intuition:

Imagine a shelf where popular documents move into higher-frequency bins. When the shelf is full, remove the document from the lowest-frequency bin. If multiple documents are equally unpopular, remove the oldest one inside that bin.

Human thought process:

1. We need fast lookup by key, so a map is natural.
2. We need to know usage frequency, so every node stores a count.
3. We need oldest item among the same count, so each frequency needs recency order.
4. We need to know which frequency to evict from, so maintain `minFrequency`.

Brute-force algorithm:

1. Store `key -> value`.
2. Store `key -> frequency`.
3. Store `key -> lastUsedTime`.
4. On `get`, return value, increment frequency, update time.
5. On `put`, if full, scan every key and evict the one with lowest frequency; tie by oldest time.
6. Insert/update the key.

Optimized algorithm:

1. Store `key -> node`.
2. Store `frequency -> doubly linked list of nodes`.
3. On access, remove node from old frequency list.
4. Increment frequency.
5. Add node to the front/back of the new frequency list depending on chosen recency convention.
6. If old list was `minFrequency` and became empty, increment `minFrequency`.
7. On eviction, remove the least recent node from `frequencyToNodes[minFrequency]`.

Dry run:

- Capacity `2`.
- `put(1, A)`: key 1 has freq 1, `minFrequency = 1`.
- `put(2, B)`: key 2 also freq 1.
- `get(1)`: key 1 becomes freq 2; key 2 remains freq 1.
- `put(3, C)`: cache full, evict key 2 because freq 1 is lower than freq 2.

Complexity:

- Brute force eviction: `O(n)`.
- Optimized get/put: `O(1)` average.
- Space: `O(capacity)`.

### Expressive Words

School-level intuition:

Stretchy words are like singing a vowel longer. `hello` can become `heeellooo`, but only when the target has a long enough repeated group.

Brute-force algorithm:

1. For each word, split it into groups.
2. Try expanding groups in all possible ways.
3. If any expanded version equals the target, count it.

Optimized algorithm:

1. Walk target and word with two pointers.
2. Count the current repeated character group in both strings.
3. Characters must match.
4. If counts are equal, continue.
5. If target count is bigger, it is valid only if target count is at least `3`.
6. If word count is bigger, invalid.

Dry run:

- Target: `heeellooo`, word: `hello`.
- Groups: target `h1 e3 l2 o3`, word `h1 e1 l2 o1`.
- `e1 -> e3` valid because target count is `3`.
- `o1 -> o3` valid.
- Word is expressive.

Complexity:

- Brute force: exponential in group choices.
- Optimized: `O(target length + word length)` per word.

### Maximum Earnings From Taxi

School-level intuition:

Each ride is a job with start time, end time, and reward. You want the best set of non-overlapping jobs.

Brute-force algorithm:

1. Sort rides.
2. Recursively decide to take or skip each ride.
3. If taking a ride overlaps the previous taken ride, reject that path.
4. Return the best total.

Optimized algorithm:

1. Convert each ride profit to `end - start + tip`.
2. Sort rides by end time.
3. Let `dp[i]` be best profit considering rides up to `i`.
4. For ride `i`, binary search the latest ride ending before `ride[i].start`.
5. `dp[i] = max(dp[i - 1], profit[i] + dp[previousCompatible])`.

Dry run:

- If ride A ends at 5 and ride B starts at 5, they are compatible.
- Taking B means add B profit to best profit up to time 5.
- Skipping B means keep previous best.

Complexity:

- Brute force: `O(2^n)`.
- Optimized: `O(n log n)` time and `O(n)` space.

### Verbal Arithmetic Puzzle

School-level intuition:

This is column addition like school math. The rightmost column decides a result digit and carry, then the carry affects the next column.

Brute-force algorithm:

1. Collect all distinct letters.
2. Assign digits by trying every permutation.
3. Reject assignments where leading letters are zero.
4. Convert all words to numbers and test the equation.

Optimized algorithm:

1. Work from rightmost column to leftmost column.
2. Assign only letters needed for the current column.
3. Maintain carry.
4. If the column result digit does not match, prune immediately.
5. Continue until all columns and final carry are valid.

Complexity:

- Worst case is still exponential, but column pruning avoids most invalid full assignments.
- Space is `O(unique letters)`.

Pitfalls:

- More than 10 unique letters is impossible.
- Multi-letter words cannot start with zero.
- Final carry must be consumed exactly.

### Regex / Pattern Validation

School-level intuition:

The pattern is a rulebook. A normal character must match itself. A dot can match any one character. A star means the previous pattern piece can repeat zero or more times.

Brute-force algorithm:

1. Recursively compare current string position and pattern position.
2. If next pattern char is `*`, branch:
   - skip this pattern piece,
   - or consume one matching string char and stay on the same pattern piece.
3. If no `*`, current chars must match and both pointers advance.

Optimized algorithm:

1. Memoize `(i, j)` so repeated suffix comparisons are solved once.
2. Or fill DP table bottom-up.
3. `dp[i][j]` means `s[i:]` matches `p[j:]`.

Complexity:

- Brute force: exponential.
- Optimized: `O(nm)` time and `O(nm)` space.

Banking variant notes:

- Transaction reference validation may use wildcard syntax, not regex syntax.
- Always clarify whether the match must cover the full string.
- Validate malformed patterns before matching.

## Banking-Style Prompts To Promote

### Failed Login Rolling Window

Problem: Given login events, flag accounts with more than `N` failed attempts in `T` minutes.

Brute force:

1. Group events by account.
2. For each failed event as a possible start, scan all other failed events for that account.
3. Count events inside `start..start+T`.
4. Flag if count exceeds threshold.

Optimized:

1. Group by account.
2. Sort failed timestamps.
3. Use sliding window over timestamps.
4. Move right forward for each event.
5. Move left while the window is too wide.
6. If window size exceeds threshold, flag account.

Complexity:

- Brute force: `O(k^2)` per account.
- Optimized: `O(k log k)` for sorting plus `O(k)` scan.

Pitfalls:

- Normalize timestamps and time zones.
- Define whether the boundary is inclusive.
- Avoid logging raw usernames if they are sensitive.

### Idempotent Payment Requests

Problem: Process duplicate payment requests only once.

Brute force:

1. For each new request, scan all previous requests.
2. If the same idempotency key exists, return the first response.
3. Otherwise process the payment and append the request to history.

Optimized:

1. Store `idempotencyKey -> requestHash, response, status`.
2. On first request, insert `IN_PROGRESS`.
3. Process payment and store final response.
4. On duplicate same key and same request hash, replay final response or wait on in-progress status.
5. On same key but different request hash, reject.

Complexity:

- Brute force: `O(n)` per request.
- Optimized: `O(1)` average per request.

Pitfalls:

- Same key with different body is not safe to replay.
- Concurrent duplicates need locking or database uniqueness.
- TTL must be long enough for retry behavior.

### Ledger Reconciliation

Problem: Given internal ledger events and external settlement events, find unmatched or mismatched transactions.

Brute force:

1. For every internal event, scan every external event.
2. Match by reference, amount, currency, and date tolerance.
3. Mark unmatched records.

Optimized:

1. Build maps by composite matching key.
2. Store lists, not single values, because duplicates can exist.
3. Walk one side and consume matching records from the other side.
4. Anything left is unmatched or duplicate.

Complexity:

- Brute force: `O(nm)`.
- Optimized: `O(n + m)` average.

Pitfalls:

- Use `BigDecimal` for money.
- Never overwrite duplicate references.
- Preserve mismatch reason for audit review.

### Batch Dependency Scheduler

Problem: Given batch jobs and dependencies, return a valid execution order or report a cycle.

Brute force:

1. Repeatedly scan all unprocessed jobs.
2. Pick any job whose dependencies are already processed.
3. If a full pass picks nothing, there is a cycle.

Optimized:

1. Build adjacency list and indegree count.
2. Add all zero-indegree jobs to queue.
3. Pop a job, append to result, reduce indegree of dependents.
4. If processed count is less than total jobs, report cycle.

Complexity:

- Brute force: `O(VE)` worst case.
- Optimized: `O(V + E)`.

Pitfalls:

- Use a priority queue if deterministic order matters.
- Include jobs that have no dependencies and no dependents.

### Transaction Stream Top K Risky Accounts

Problem: Maintain the top `K` accounts by fraud score.

Brute force:

1. Update account score.
2. Sort all account scores after every event.
3. Return top `K`.

Optimized:

1. For batch mode, aggregate scores in a map.
2. Use min-heap size `K` to keep only the best accounts.
3. For real-time mutable scores, use a balanced tree or indexed heap plus account map.

Complexity:

- Brute force: `O(u log u)` per update.
- Batch optimized: `O(n + u log k)`.
- Real-time optimized: `O(log u)` per update with tree/indexed heap.

Pitfalls:

- Tie-break deterministically by account id.
- Be careful when score updates decrease an existing account score.
