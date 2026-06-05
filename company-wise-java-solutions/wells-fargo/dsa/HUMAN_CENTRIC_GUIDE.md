# Wells Fargo DSA Human-Centric Guide

This guide is written in the same learning style as the Karat solutions: slow down, understand the sentence inside the problem, solve it in the most obvious way first, then improve only after naming the repeated work.

Pair this file with [HUMAN_CENTRIC_DRY_RUNS.md](HUMAN_CENTRIC_DRY_RUNS.md). This file teaches how to think; the dry-run companion shows state changes and complexity for every problem.

For every question, practice saying:

1. "What is the problem really asking?"
2. "What would I do by hand on paper?"
3. "What repeated work is brute force doing?"
4. "What data structure remembers that repeated work for me?"
5. "What edge case would break a careless solution?"

## How To Practice Each Section

For each problem below, turn the notes into a spoken answer using this exact script:

1. `Problem in my words`: Say what the input is, what output is expected, and what the hidden pattern is.
2. `Brute force`: Describe the first correct thing you would do by hand.
3. `Why brute force is slow`: Name the repeated work directly.
4. `Optimized idea`: Say what state you keep so you do not repeat that work.
5. `Dry run`: Use a tiny example and show the state changing.
6. `Complexity`: Say time and space for both approaches.
7. `Pitfalls`: Name at least two traps before moving on.

When the section does not spell out a full dry run, create one from the listed edge cases. The goal is not memorizing the answer; it is training the instinct that turns brute force into an optimized pattern.

## 1. Missing Number

Original problem statement:

Given an array containing `n` distinct numbers from the range `0..n`, exactly one number is missing. Return that missing number.

What it means:

The array is almost complete. If `n = 4`, the full set should be `{0, 1, 2, 3, 4}`. One of those values did not show up.

Think like real life:

Imagine checking attendance from roll numbers. You know roll numbers should be `0` through `n`. The simplest human move is to call every roll number and check whether someone answers.

Brute force approach in plain English:

Try every possible number from `0` to `n`. For each number, scan the array and ask, "Did I see this number?" The first number you cannot find is missing.

What brute force teaches:

It teaches that the answer comes from comparing the expected set with the actual set. The weakness is that we keep scanning the same array again and again.

Optimized approach in plain English:

Instead of checking every number one by one, compare totals. The expected sum of `0..n` is known. Subtract the actual array sum. The leftover value is the missing number.

Optimized thinking habit:

When a problem asks "one value is missing from a known complete range," think about a mathematical invariant: sum or XOR.

Dry run:

`nums = [3, 0, 1]`

Expected numbers are `0, 1, 2, 3`; expected sum is `6`. Actual sum is `4`. Missing number is `6 - 4 = 2`.

Edge cases:

- Missing number is `0`.
- Missing number is `n`.
- Large `n` can overflow integer sum, so XOR is safer in some languages.

## 2. Expressive Words

Original problem statement:

Given a target string and a list of words, count how many words can become the target by stretching groups of repeated letters.

What it means:

`hello` can become `heeellooo` because `e` and `o` are stretched. But a target group must have length at least `3` to be considered stretchable.

Think like real life:

Imagine someone singing a word. A short sound like `e` can become `eee`, but if the target only has `ee`, that is not considered a valid stretch in this problem.

Brute force approach in plain English:

For every word, try to generate all possible stretched versions and check whether any generated version equals the target.

What brute force teaches:

It shows the real action is happening inside repeated-character groups. The weakness is that generating all stretched strings creates many unnecessary possibilities.

Optimized approach in plain English:

Compare the target and word group by group. For each group, the character must match. If counts are equal, continue. If the target count is larger, it is valid only when the target group has at least `3` characters. If the word count is larger, it cannot fit.

Optimized thinking habit:

When strings are made of repeated blocks, compress your thinking into groups instead of individual characters.

Dry run:

Target `heeellooo`, word `hello`.

Groups are `h1 e3 l2 o3` and `h1 e1 l2 o1`. `e1` can stretch to `e3`; `o1` can stretch to `o3`; answer is valid.

Edge cases:

- Target group length `2` cannot stretch.
- Extra group in either string makes the word invalid.
- Empty word only matches empty target.

## 3. Valid Parentheses

Original problem statement:

Given a string of brackets, decide if every opening bracket is closed by the correct type in the correct order.

What it means:

The most recently opened bracket must be the first one closed.

Think like real life:

Think of nested boxes. If you open a small box inside a big box, you must close the small box before closing the big one.

Brute force approach in plain English:

Repeatedly remove valid adjacent pairs like `()`, `{}`, and `[]`. If the whole string eventually disappears, it was valid.

What brute force teaches:

Valid bracket strings contain removable inner pairs. The weakness is that every removal creates a new string and scans again.

Optimized approach in plain English:

Use a stack. Push opening brackets. When a closing bracket appears, it must match the bracket on top of the stack. If it does, pop it. At the end, the stack must be empty.

Optimized thinking habit:

When the last thing opened must be the first thing closed, think stack.

Edge cases:

- Starts with a closing bracket.
- Ends with unmatched opening brackets.
- Mismatched types like `(]`.

## 4. Number of Islands

Original problem statement:

Given a grid of land and water, count how many separated land masses exist.

What it means:

Connected land cells form one island. Water separates islands.

Think like real life:

If you step on a piece of land, you can walk north, south, east, or west to connected land. Everything reachable from that starting point is the same island.

Brute force approach in plain English:

For every land cell, repeatedly search around it to decide whether it belongs to an island already seen. This works, but it keeps rediscovering the same land.

What brute force teaches:

The problem is about connected components. The weakness is not marking explored land clearly.

Optimized approach in plain English:

Scan the grid. When you find unvisited land, count a new island and immediately flood-fill all land connected to it. Mark those cells visited so they are never counted again.

Optimized thinking habit:

When one discovery should absorb a whole connected region, think DFS or BFS.

Edge cases:

- Empty grid.
- All water.
- All land.
- Diagonal land does not count as connected in the standard version.

## 5. Product of Array Except Self

Original problem statement:

For each index, return the product of every number except the number at that index.

What it means:

Every answer is built from the numbers on the left and the numbers on the right of that index.

Think like real life:

For a person standing in a line, their answer is "everyone before me multiplied by everyone after me."

Brute force approach in plain English:

For each index, loop through the whole array and multiply every value except the current index.

What brute force teaches:

Each answer ignores exactly one position. The weakness is that neighboring answers recompute almost the same products.

Optimized approach in plain English:

Make one pass to store products before each index. Make another pass from the right to multiply products after each index. The final answer combines left product and right product.

Optimized thinking habit:

When each index needs information from both sides, think prefix and suffix.

Edge cases:

- One zero in the array.
- Multiple zeros.
- Negative values.

## 6. Kth Largest Element in a Stream

Original problem statement:

Numbers arrive one at a time. After each new number, return the kth largest number seen so far.

What it means:

You do not need all numbers sorted. You only need to know the top `k`.

Think like real life:

If you only care about the top 3 scores, you do not need to keep ranking everyone below third place.

Brute force approach in plain English:

Store every number. Whenever a new number arrives, sort the whole list and pick the kth largest.

What brute force teaches:

Sorting solves ranking. The weakness is that full ranking is too much work when only kth largest matters.

Optimized approach in plain English:

Keep a min-heap of size `k`. The heap contains the current top `k` numbers. The smallest number inside that heap is the kth largest overall.

Optimized thinking habit:

When you need top `k`, keep only `k` useful candidates.

Edge cases:

- Stream has fewer than `k` numbers.
- Duplicate numbers count as separate values.

## 7. Spiral Matrix

Original problem statement:

Return all matrix values in spiral order.

What it means:

Walk around the outside layer, then move inward and repeat.

Think like real life:

Peel an onion layer by layer: top row, right column, bottom row, left column, then the next inner rectangle.

Brute force approach in plain English:

Walk with a direction and a visited matrix. Turn whenever the next cell is out of bounds or already visited.

What brute force teaches:

The movement pattern is right, down, left, up. The weakness is carrying a full visited grid just to know the current boundary.

Optimized approach in plain English:

Track four boundaries: top, bottom, left, right. Print one boundary at a time and shrink it.

Optimized thinking habit:

When movement stays inside a shrinking rectangle, track boundaries instead of every visited cell.

Edge cases:

- Single row.
- Single column.
- One cell.

## 8. Longest Common Prefix

Original problem statement:

Given strings, return the longest prefix shared by all strings.

What it means:

All strings must start with the same characters for as long as possible.

Think like real life:

Line up the words vertically and read column by column. The prefix stops at the first column where someone differs.

Brute force approach in plain English:

Try prefixes of the first string from longest to shortest and check whether every other string starts with that prefix.

What brute force teaches:

The first string gives all possible prefixes. The weakness is rechecking many characters.

Optimized approach in plain English:

Scan column by column. At each character position, compare every string against the first string's character. Stop at the first mismatch.

Optimized thinking habit:

When every item must agree at the same position, compare by column.

Edge cases:

- Empty list.
- One empty string.
- Only one string.

## 9. Maximum Subarray

Original problem statement:

Find the contiguous subarray with the largest sum.

What it means:

You are choosing a continuous stretch, not scattered elements.

Think like real life:

Walking through daily profit/loss, ask: "Does carrying my previous streak help me, or should I start fresh today?"

Brute force approach in plain English:

Try every start point and extend to every end point while calculating sums. Keep the largest sum seen.

What brute force teaches:

All possible answers are intervals. The weakness is checking every interval.

Optimized approach in plain English:

At each number, decide whether to extend the previous subarray or start a new one. If previous sum is harmful, drop it.

Optimized thinking habit:

When a running history can either help or hurt the future, keep only the useful history.

Edge cases:

- All negative numbers.
- One number.

## 10. LFU Cache

Original problem statement:

Design a cache that evicts the least frequently used key. If multiple keys have the same frequency, evict the least recently used among them.

What it means:

Popularity matters first. Recency breaks ties.

Think like real life:

Imagine library books in shelves by how often they are borrowed. If shelf `1 borrow` is the least popular shelf, remove the oldest untouched book from that shelf.

Brute force approach in plain English:

Store value, frequency, and last-used time for each key. On eviction, scan every key to find the lowest frequency and oldest timestamp.

What brute force teaches:

You need two rankings: frequency and recency. The weakness is scanning all keys whenever the cache is full.

Optimized approach in plain English:

Use a map from key to node for direct lookup. Also keep frequency buckets, where each bucket stores keys in recency order. Track the current minimum frequency so eviction jumps directly to the right bucket.

Optimized thinking habit:

When eviction depends on multiple rules, organize data in the same order as the rules.

Edge cases:

- Capacity zero.
- Updating an existing key.
- Frequency bucket becomes empty.

## 11. Meeting Rooms II

Original problem statement:

Given meeting intervals, find the minimum number of rooms needed.

What it means:

The answer is the maximum number of meetings happening at the same time.

Think like real life:

Every start time needs a room. Every end time frees a room.

Brute force approach in plain English:

Check every meaningful time point, such as every meeting start time. For each time point, scan all meetings and count how many meetings are active at that moment. The largest active count is the number of rooms needed.

What brute force teaches:

Rooms are about how many meetings are active at the same time, not how many meetings overlap one long meeting. The weakness is scanning every meeting for every candidate time point.

Optimized approach in plain English:

Sort start times and end times separately. Move through starts. If the next meeting starts before the earliest meeting ends, allocate a new room. Otherwise, reuse the freed room.

Optimized thinking habit:

When intervals ask "how many active at once," turn intervals into start/end events.

Edge cases:

- A meeting ending at `10` and another starting at `10` can share a room.
- Empty schedule needs zero rooms.

## 12. Backspace String Compare

Original problem statement:

Compare two typed strings where `#` means backspace.

What it means:

You need to compare what remains after typing, not the raw text.

Think like real life:

Typing into an editor: normal characters appear, backspace erases the latest visible character.

Brute force approach in plain English:

Build the final visible string for each input using a stack or builder. Then compare the two final strings.

What brute force teaches:

Backspace affects the most recent visible character. The weakness is storing final strings even if you only need comparison.

Optimized approach in plain English:

Walk backward through both strings. Count pending backspaces. Skip deleted characters until you find the next visible character in each string, then compare them.

Optimized thinking habit:

When deletion affects previous characters, scanning from the end can avoid rebuilding.

Edge cases:

- More backspaces than characters.
- Both strings reduce to empty.

## 13. Intersection of Two Linked Lists

Original problem statement:

Return the node where two linked lists intersect, or null if they do not intersect.

What it means:

Intersection means the exact same node object is shared, not just the same value.

Think like real life:

Two roads may start in different cities and eventually merge onto the same highway. After they merge, every later road segment is shared.

Brute force approach in plain English:

For each node in list A, scan every node in list B and check whether it is the same node.

What brute force teaches:

Reference equality matters. The weakness is repeatedly walking list B.

Optimized approach in plain English:

Use two pointers. Pointer A walks list A then list B. Pointer B walks list B then list A. After both have walked the same total distance, they meet at the intersection or both become null.

Optimized thinking habit:

When two linked lists have different lengths, switch heads to equalize distance.

Edge cases:

- No intersection.
- Intersection at head.
- One list is empty.

## 14. LRU Cache

Original problem statement:

Design a cache that evicts the least recently used key when full.

What it means:

The key unused for the longest time should leave first.

Think like real life:

Keep recently used papers at the front of your desk. When the desk is full, throw away the paper at the bottom of the pile.

Brute force approach in plain English:

Store entries in a list ordered by recent use. On every get or put, scan the list to find the key and move it to the front.

What brute force teaches:

Recency order must change on every access. The weakness is scanning the list to find keys.

Optimized approach in plain English:

Use a HashMap for key lookup and a doubly linked list for recency order. The map finds the node instantly; the list moves it instantly.

Optimized thinking habit:

Pair a map with a linked list when you need fast lookup plus fast ordering updates.

Edge cases:

- Capacity zero.
- Put existing key should update value and recency.

## 15. Restore IP Addresses

Original problem statement:

Given a string of digits, return all valid IP addresses formed by adding three dots.

What it means:

You must split the string into four valid numbers from `0` to `255`.

Think like real life:

You are placing separators in a long number. Every piece must be a legal IP segment.

Brute force approach in plain English:

Try every possible placement of three dots and validate the four segments.

What brute force teaches:

The answer is a split problem. The weakness is trying splits that are obviously too long or too short.

Optimized approach in plain English:

Backtrack segment by segment. At each step, choose a segment length of 1, 2, or 3, validate it, and stop early if the remaining characters cannot fill the remaining segments.

Optimized thinking habit:

When output is all valid partitions, use backtracking with early pruning.

Edge cases:

- Segment cannot have leading zero unless it is exactly `0`.
- Input length less than `4` or more than `12` has no answer.

## 16. Rotate String

Original problem statement:

Check whether one string can become another by rotating characters.

What it means:

Rotation moves a prefix to the end without changing character order.

Think like real life:

If a necklace is rotated, the same beads appear in the same circular order.

Brute force approach in plain English:

Try every rotation of the first string and compare it with the target.

What brute force teaches:

Rotation is circular order. The weakness is rebuilding many rotated strings.

Optimized approach in plain English:

If `goal` is a rotation of `s`, then `goal` appears inside `s + s`. Also lengths must match.

Optimized thinking habit:

When a problem is circular, duplicate the sequence to make wraparound linear.

Edge cases:

- Empty strings.
- Different lengths.

## 17. Merge Strings Alternately

Original problem statement:

Merge two strings by alternating characters from each.

What it means:

Take one from the first, one from the second, and continue until one finishes. Then append the leftover.

Think like real life:

Like dealing cards from two decks into one pile.

Brute force approach in plain English:

Use string concatenation inside a loop.

What brute force teaches:

The order is simple. The weakness is that strings are immutable, so repeated concatenation can create many temporary strings.

Optimized approach in plain English:

Use `StringBuilder`. Append alternating characters, then append the remaining tail of the longer string.

Optimized thinking habit:

When building a string step by step in Java, think `StringBuilder`.

Edge cases:

- One string is empty.
- Different lengths.

## 18. Minimum String Length After Removing Substrings

Original problem statement:

Repeatedly remove `AB` or `CD` from a string and return the final length.

What it means:

Removing one pair may bring new characters together and create another removable pair.

Think like real life:

Like canceling adjacent tokens. After a cancellation, the neighbors become adjacent and may cancel too.

Brute force approach in plain English:

Keep scanning the full string. Whenever you find `AB` or `CD`, remove it and start checking again.

What brute force teaches:

The string changes after each removal. The weakness is rescanning from scratch.

Optimized approach in plain English:

Use a stack. For each character, check whether it forms `AB` or `CD` with the stack top. If yes, pop. Otherwise push.

Optimized thinking habit:

When adjacent cancellations happen, think stack.

Edge cases:

- No removable pair.
- Entire string disappears.

## 19. Count Binary Substrings

Original problem statement:

Count substrings with the same number of consecutive `0`s and consecutive `1`s.

What it means:

Valid substrings look like `0011` or `1100`, not `0101`.

Think like real life:

You are pairing neighboring groups. A group of three zeros next to two ones can form two valid balanced substrings.

Brute force approach in plain English:

Generate every substring and check whether it has exactly two consecutive groups with equal size.

What brute force teaches:

The answer depends on adjacent groups. The weakness is inspecting too many substrings.

Optimized approach in plain English:

Track the length of the previous group and current group. When a new group starts, the old previous/current pair is complete, so add `min(previous, current)` and shift the group lengths. After the loop, add `min(previous, current)` one final time for the last pair.

Optimized thinking habit:

When substrings are determined by runs, count runs instead of substrings.

Edge cases:

- All same character.
- Alternating string like `010101`.

## 20. Maximum Number of Weeks

Original problem statement:

Given project milestone counts, find the maximum weeks you can work without working on the same project in consecutive weeks.

What it means:

The largest project can dominate the schedule. Other projects are separators.

Think like real life:

If one project has too many tasks, you need other project tasks between them. Once separators run out, you must stop.

Brute force approach in plain English:

Try to build every valid weekly schedule by choosing any project different from last week.

What brute force teaches:

The only rule is "not same as previous." The weakness is the huge number of schedules.

Optimized approach in plain English:

Let `max` be the largest milestone count and `rest` be all others. If `max <= rest + 1`, all work can be scheduled. Otherwise, the best schedule alternates largest project with every other project, giving `2 * rest + 1`.

Optimized thinking habit:

When one bucket may dominate all others, compare largest bucket against the sum of the rest.

Edge cases:

- One project only.
- Very large counts need `long`.

## 21. Maximum Earnings From Taxi

Original problem statement:

Given rides with start, end, and tip, choose non-overlapping rides for maximum earnings.

What it means:

Each ride is a job with time interval and profit.

Think like real life:

You are a driver choosing jobs. Taking one ride may prevent you from taking another ride that overlaps.

Brute force approach in plain English:

Try every subset of rides. Keep only subsets where no rides overlap. Return the highest profit.

What brute force teaches:

This is a scheduling choice problem. The weakness is that subsets grow exponentially.

Optimized approach in plain English:

Sort rides by end time. Use DP where each ride has two choices: skip it, or take it plus the best profit before its start time. Binary search finds the previous compatible ride.

Optimized thinking habit:

When jobs have start/end/profit, think weighted interval scheduling.

Edge cases:

- A ride ending at time `x` is compatible with another starting at `x`.
- Profit is `end - start + tip`.

## 22. Verbal Arithmetic Puzzle

Original problem statement:

Assign digits to letters so that a word equation becomes mathematically true.

What it means:

Each letter is one digit, and different letters must have different digits.

Think like real life:

Solve it like column addition from school, starting from the rightmost column and carrying to the next column.

Brute force approach in plain English:

Try every digit assignment for all letters, reject leading zeros, convert words to numbers, and check the equation.

What brute force teaches:

The problem is digit assignment. The weakness is waiting until the end to discover most assignments are impossible.

Optimized approach in plain English:

Backtrack column by column. Assign only letters needed in the current column, check whether the result digit and carry work, and prune immediately when a column cannot be valid.

Optimized thinking habit:

When a constraint can be checked partially, check it early instead of after full assignment.

Edge cases:

- More than 10 unique letters is impossible.
- Leading letters cannot be zero.

## 23. Regex / Pattern Validation

Original problem statement:

Determine whether a string matches a pattern with special characters like `.` and `*`.

What it means:

`.` matches any one character. `*` means the previous pattern item can appear zero or more times.

Think like real life:

The pattern is a rulebook. At each step, decide whether the current rule consumes a character or can be skipped.

Brute force approach in plain English:

Use recursion. If the next pattern character is `*`, branch into "skip this rule" or "consume one matching character and stay on this rule."

What brute force teaches:

The hard part is branching at `*`. The weakness is that the same `(string position, pattern position)` gets solved repeatedly.

Optimized approach in plain English:

Memoize each `(i, j)` state or build a DP table. Once you know whether suffix `s[i:]` matches pattern `p[j:]`, reuse that answer.

Optimized thinking habit:

When recursion revisits the same positions, turn positions into DP state.

Edge cases:

- Empty string with pattern like `a*`.
- Full match vs substring match must be clarified.
- Wildcard syntax is different from regex syntax.

## 24. 0/1 Knapsack

Original problem statement:

Given items with weights and values, pick items within capacity for maximum value. Each item can be used once.

What it means:

For every item, you either take it or leave it.

Think like real life:

Packing a bag: once you put an item in, capacity shrinks; if you skip it, capacity stays.

Brute force approach in plain English:

Recursively branch for every item: take it if it fits, or skip it.

What brute force teaches:

The choice is binary per item. The weakness is recomputing the same index/capacity choices.

Optimized approach in plain English:

Use DP where state means "best value using first `i` items with capacity `c`." Each state chooses max of skip or take.

Optimized thinking habit:

When choices depend on item index and remaining capacity, those two values define DP state.

Edge cases:

- Capacity zero.
- Item weight bigger than capacity.
- 1D DP must iterate capacity backward.

## 25. Group Anagrams

Original problem statement:

Group words that contain the same letters in different order.

What it means:

Words are in the same group if their letter counts match.

Think like real life:

Sort the letters in each word. Anagrams become the same sorted signature.

Brute force approach in plain English:

Compare each word with existing group representatives to see if it is an anagram.

What brute force teaches:

We need a way to recognize anagram identity. The weakness is comparing against many representatives.

Optimized approach in plain English:

Create a signature for each word, such as sorted letters or a letter-count key. Use a map from signature to list of words.

Optimized thinking habit:

When different inputs should be treated as equivalent, create a canonical key.

Edge cases:

- Empty string.
- Duplicate words.
- Output order may need normalization for tests.

## 26. Longest Substring Without Repeating Characters

Original problem statement:

Find the longest contiguous substring with no repeated characters.

What it means:

You need the longest window where every character appears at most once.

Think like real life:

You are stretching a rubber band over a string. If a repeated character enters, move the left side past the previous copy.

Brute force approach in plain English:

Try every start position, expand right until a duplicate appears, and track the best length.

What brute force teaches:

The valid answer is a window. The weakness is restarting from scratch for each start.

Optimized approach in plain English:

Use a sliding window and a map of last seen positions. When a repeat appears inside the window, jump the left pointer after the old occurrence.

Optimized thinking habit:

When a window breaks because of one repeated item, move only as far as needed to restore validity.

Edge cases:

- Empty string.
- All same characters.
- Left pointer must never move backward.

## 27. Best Time to Buy and Sell Stock

Original problem statement:

Given daily stock prices, choose one buy day and one later sell day for maximum profit.

What it means:

For each sell day, the best buy day is the lowest price before it.

Think like real life:

Every day ask, "If I sell today, what was the cheapest earlier day I could have bought?"

Brute force approach in plain English:

Try every buy day and every later sell day. Keep the best profit.

What brute force teaches:

Order matters: buy must happen before sell. The weakness is checking every pair.

Optimized approach in plain English:

Walk once. Keep the minimum price seen so far and the best profit if selling today.

Optimized thinking habit:

When today depends on the best previous value, carry that previous best forward.

Edge cases:

- Prices always decreasing.
- One price only.

## 28. Merge Intervals

Original problem statement:

Merge all overlapping intervals.

What it means:

If two intervals touch or overlap, they become one larger interval.

Think like real life:

Put time blocks on a calendar. Once sorted by start time, you only need to compare with the latest merged block.

Brute force approach in plain English:

Repeatedly scan all pairs of intervals. If two overlap, merge them and restart.

What brute force teaches:

Overlap is local between intervals. The weakness is checking messy unsorted pairs repeatedly.

Optimized approach in plain English:

Sort by start time. Keep a merged list. For each interval, merge into the last interval if overlapping; otherwise append as new.

Optimized thinking habit:

Sorting often turns pairwise interval chaos into one left-to-right pass.

Edge cases:

- Empty intervals.
- Touching intervals like `[1,3]` and `[3,5]`.

## 29. Kth Largest Element in an Array

Original problem statement:

Return the kth largest element in an unsorted array.

What it means:

You need a rank, not full sorted order.

Think like real life:

If someone asks for the bronze medalist, you do not need a complete ranking of everyone below bronze.

Brute force approach in plain English:

Sort the whole array and pick index `n - k`.

What brute force teaches:

Sorting solves ranking. The weakness is sorting more than needed.

Optimized approach in plain English:

Use a min-heap of size `k`, or use Quickselect to partition around the desired rank.

Optimized thinking habit:

When only one rank matters, avoid full ordering.

Edge cases:

- Duplicates count as separate elements.
- `k = 1` asks for maximum.

## 30. Top K Frequent Elements

Original problem statement:

Return the `k` most frequent numbers.

What it means:

First count, then rank by count.

Think like real life:

Count votes for each number, then choose the top candidates.

Brute force approach in plain English:

Count frequencies, sort all unique numbers by frequency, and take the first `k`.

What brute force teaches:

Frequency map is necessary. The weakness is sorting every unique value.

Optimized approach in plain English:

After counting, use a bucket array where index is frequency, or keep a min-heap of size `k`.

Optimized thinking habit:

When ranking by count, choose between bucket sort and heap depending on constraints.

Edge cases:

- Tie ordering may be unspecified.
- `k` equals number of unique values.

## 31. Top K Frequent Words

Original problem statement:

Return the `k` most frequent words. Ties are ordered alphabetically.

What it means:

It is frequency ranking with a deterministic tie-breaker.

Think like real life:

Count votes. If two candidates have the same votes, choose the alphabetically smaller name first.

Brute force approach in plain English:

Count all words, sort unique words by frequency descending and word ascending, then take `k`.

What brute force teaches:

Comparator rules matter. The weakness is sorting all words even if `k` is small.

Optimized approach in plain English:

Use a heap of size `k`. Keep the weakest candidate on top: lower frequency is weaker; for equal frequency, lexicographically larger is weaker.

Optimized thinking habit:

For top `k` with custom tie rules, define "worst among the kept best" carefully.

Edge cases:

- Same frequency words.
- `k` larger than unique word count.

## 32. Rotting Oranges

Original problem statement:

Every minute, rotten oranges rot adjacent fresh oranges. Return minutes until all fresh oranges rot, or `-1`.

What it means:

Rot spreads outward in waves.

Think like real life:

Multiple fires start at once. Each minute, the fire spreads one step.

Brute force approach in plain English:

For every minute, scan the entire grid and find fresh oranges adjacent to oranges that were rotten at the start of that minute. Mark next-minute oranges with a temporary value first, then commit them after the full scan.

What brute force teaches:

The process is level-by-level time spread. The temporary mark matters because oranges that become rotten in minute `1` should not rot others until minute `2`. The weakness is scanning unaffected cells repeatedly.

Optimized approach in plain English:

Put all initially rotten oranges into a queue. Run multi-source BFS. Each BFS level represents one minute.

Optimized thinking habit:

When many sources spread at the same speed, start BFS from all sources together.

Edge cases:

- No fresh oranges.
- Fresh orange isolated by empty cells.

## 33. Clone Graph

Original problem statement:

Return a deep copy of an undirected graph.

What it means:

Every original node needs exactly one cloned node, and clone edges must point to clones.

Think like real life:

Photocopy a city map. Every city gets one copy, and roads between cities become roads between copied cities.

Brute force approach in plain English:

First walk the graph and collect all original nodes with DFS/BFS. Then create a clone node for each original node in a separate list. Finally, for every original edge, search the collected list to find the corresponding clone nodes and rebuild neighbor links.

What brute force teaches:

Every node must be copied once, and every edge must point to cloned neighbors. The weakness is the expensive search from original node to clone node during edge reconstruction.

Optimized approach in plain English:

Use a map from original node to cloned node. During DFS/BFS, if a clone exists, reuse it; otherwise create it and continue.

Optimized thinking habit:

When copying a graph with cycles, map originals to copies immediately.

Edge cases:

- Null graph.
- Self-loop.
- Cycle.

## 34. Course Schedule

Original problem statement:

Given courses and prerequisites, decide whether all courses can be finished.

What it means:

A cycle means courses are waiting on each other forever.

Think like real life:

You can only take a course when all its prerequisites are done. If nobody can start, the plan is impossible.

Brute force approach in plain English:

Keep scanning all courses. Complete any course whose prerequisites are already done. If a full scan completes nothing, you are stuck.

What brute force teaches:

Courses with no unfinished prerequisites unlock other courses. The weakness is repeatedly scanning all prerequisite pairs.

Optimized approach in plain English:

Build a graph and indegree count. Start with courses that have indegree zero. Remove them and reduce indegree of dependent courses. If all courses are removed, possible.

Optimized thinking habit:

When prerequisites define ordering, think topological sort.

Edge cases:

- Self dependency.
- Disconnected course groups.
- Duplicate prerequisite pairs.

## 35. Word Ladder

Original problem statement:

Transform one word into another by changing one letter at a time, where every intermediate word must be in the dictionary. Return shortest length.

What it means:

Each word is a node; one-letter difference is an edge.

Think like real life:

You are walking through a word maze. Each move changes one letter. The shortest path is found by exploring one step at a time.

Brute force approach in plain English:

Try every possible transformation path recursively until reaching the target.

What brute force teaches:

This is a path search. The weakness is exploring long paths before shorter ones and revisiting words.

Optimized approach in plain English:

Use BFS from the start word. Generate neighbors by changing each position to every letter. The first time you reach the end word is the shortest path.

Optimized thinking habit:

When every move has equal cost and shortest path is needed, think BFS.

Edge cases:

- End word not in dictionary.
- Begin equals end.

## 36. Word Search

Original problem statement:

Determine whether a word exists in a grid by moving adjacent cells without reusing a cell.

What it means:

You are tracing a path through letters.

Think like real life:

Like a pencil path on a word puzzle: once a square is used in the current word, do not step on it again.

Brute force approach in plain English:

Start DFS from every cell and try all directions for every character.

What brute force teaches:

The answer is a path. The weakness is exploring paths even after a character mismatch.

Optimized approach in plain English:

Still use DFS, but prune immediately when the current cell does not match the needed character. Mark cells visited only during the current path.

Optimized thinking habit:

Backtracking is brute force with disciplined undo and early rejection.

Edge cases:

- Word longer than cell count.
- Repeated letters.
- One-cell board.

## 37. Subsets

Original problem statement:

Return all subsets of a list.

What it means:

Every element has two choices: included or not included.

Think like real life:

Packing optional items: for each item, decide yes or no.

Brute force approach in plain English:

Use every bitmask from `0` to `2^n - 1`; each bit says whether to include one element.

What brute force teaches:

There are exactly `2^n` subsets. The output itself is exponential.

Optimized approach in plain English:

Use backtracking: add the current subset, then choose each next element to extend it.

Optimized thinking habit:

When all combinations must be output, optimize clarity and pruning, not asymptotic output size.

Edge cases:

- Empty input returns `[[]]`.
- Duplicate elements require special handling if unique subsets are requested.

## 38. Permutations

Original problem statement:

Return all orderings of the given numbers.

What it means:

Every position chooses one unused number.

Think like real life:

Arranging people in seats: once a person sits, they cannot sit again.

Brute force approach in plain English:

Build every possible ordering by recursively choosing an unused number.

What brute force teaches:

There are `n!` answers. The output is factorial.

Optimized approach in plain English:

Use backtracking with a `used` array or swap in place. It does not beat factorial output, but it avoids invalid repeated choices.

Optimized thinking habit:

For permutation problems, track what is already used before choosing the next slot.

Edge cases:

- Empty input.
- Duplicates require skip logic.

## 39. Min Stack

Original problem statement:

Design a stack that can return the minimum element in constant time.

What it means:

Push/pop/top behave like a normal stack, but `getMin` must be instant.

Think like real life:

Along with every plate in a stack, write the smallest plate number seen up to that plate.

Brute force approach in plain English:

Use a normal stack. When asked for min, scan the whole stack.

What brute force teaches:

Minimum depends on all current values. The weakness is scanning for every `getMin`.

Optimized approach in plain English:

Store the current minimum at each stack level, or maintain a second stack of minimums.

Optimized thinking habit:

When a query asks for an aggregate after every update, store the aggregate as updates happen.

Edge cases:

- Popping the current minimum.
- Calling operations on empty stack should be defined.

## 40. Time Based Key-Value Store

Original problem statement:

Store values by key and timestamp. Retrieve the value for a key at the greatest timestamp less than or equal to a query timestamp.

What it means:

You are asking, "What was the latest known value at this time?"

Think like real life:

Looking through account status history: find the most recent change before the date you care about.

Brute force approach in plain English:

For each get request, scan all records for that key and keep the best timestamp not greater than the query.

What brute force teaches:

The correct answer is the floor timestamp. The weakness is scanning history every time.

Optimized approach in plain English:

Store each key's records sorted by timestamp and binary search for the floor timestamp. If records can arrive unordered, use a `TreeMap`.

Optimized thinking habit:

When you need the nearest value before a target in sorted history, think binary search or ordered map.

Edge cases:

- Query before first timestamp.
- Duplicate timestamps.
- Unordered banking logs.

## 41. Median From Data Stream

Original problem statement:

Numbers arrive one by one. Return the median at any time.

What it means:

You need quick access to the middle of a changing set.

Think like real life:

Keep smaller half of numbers in one pile and larger half in another. The median sits at the boundary.

Brute force approach in plain English:

Store all numbers and sort whenever median is requested.

What brute force teaches:

Median needs order. The weakness is sorting repeatedly.

Optimized approach in plain English:

Use two heaps: a max-heap for the lower half and a min-heap for the upper half. Rebalance sizes so the median is at the top.

Optimized thinking habit:

When you need the middle of a stream, maintain two balanced halves.

Edge cases:

- Even count average may overflow with ints.
- Negative numbers.

## 42. Set Matrix Zeroes

Original problem statement:

If a matrix cell is zero, set its entire row and column to zero.

What it means:

Zeros spread across their row and column, but only based on original zeros.

Think like real life:

Mark broken rows and columns first. Do not let newly created zeros create more broken rows.

Brute force approach in plain English:

Copy the matrix, then for every original zero, zero the row and column in the copy.

What brute force teaches:

You must remember original zero positions. The weakness is extra matrix space.

Optimized approach in plain English:

Use the first row and first column as marker storage. Use two separate flags to remember whether the first row/column originally had zeros.

Optimized thinking habit:

When you need marker arrays, ask whether existing input space can store markers safely.

Edge cases:

- Zero in first row.
- Zero in first column.

## 43. 3Sum

Original problem statement:

Find all unique triplets that sum to zero.

What it means:

Choose one number, then find two others that cancel it.

Think like real life:

If one person owes `-4`, you need two people whose balances add to `4`.

Brute force approach in plain English:

Try every triple and store unique valid triplets.

What brute force teaches:

The answer is combinations of three. The weakness is `O(n^3)` repeated pairing.

Optimized approach in plain English:

Sort the array. Fix one number, then use two pointers to find pairs that sum to the needed value. Skip duplicates.

Optimized thinking habit:

Sorting plus two pointers turns many pair-sum searches into linear scans.

Edge cases:

- Duplicate triplets.
- All zeros.

## 44. Merge k Sorted Lists

Original problem statement:

Merge `k` sorted linked lists into one sorted list.

What it means:

At every step, choose the smallest current head among all lists.

Think like real life:

Merging sorted piles of papers: repeatedly take the smallest visible top paper.

Brute force approach in plain English:

Collect all values, sort them, and rebuild a list.

What brute force teaches:

The final order is global sorted order. The weakness is ignoring that each input list is already sorted.

Optimized approach in plain English:

Put each list head into a min-heap. Pop the smallest node, append it, then push that node's next node.

Optimized thinking habit:

When merging many sorted sources, a heap keeps track of the next best candidate.

Edge cases:

- Empty list array.
- Some lists are null.

## 45. Reverse Linked List

Original problem statement:

Reverse a singly linked list.

What it means:

Every arrow should point backward instead of forward.

Think like real life:

You are turning a chain around one link at a time while holding onto the next link before you change the arrow.

Brute force approach in plain English:

Push nodes or values onto a stack, then pop them to create reversed order.

What brute force teaches:

Stack reverses order. The weakness is extra memory.

Optimized approach in plain English:

Use three pointers: previous, current, next. Save next, point current to previous, then move forward.

Optimized thinking habit:

When changing linked-list pointers, always save the next node before rewiring.

Edge cases:

- Empty list.
- One node.

## 46. Linked List Cycle

Original problem statement:

Determine whether a linked list contains a cycle.

What it means:

Following `next` pointers may eventually loop forever.

Think like real life:

Two runners on a circular track: if there is a loop, the faster runner eventually catches the slower one.

Brute force approach in plain English:

Store every visited node in a set. If you see a node again, there is a cycle.

What brute force teaches:

Revisiting the same node proves a loop. The weakness is extra memory.

Optimized approach in plain English:

Use slow and fast pointers. Slow moves one step, fast moves two. If they meet, there is a cycle.

Optimized thinking habit:

When detecting loops in linked structures, think slow/fast pointers.

Edge cases:

- Empty list.
- One node pointing to itself.

## 47. Longest Palindromic Substring

Original problem statement:

Find the longest substring that reads the same forward and backward.

What it means:

Palindromes expand symmetrically around a center.

Think like real life:

Stand at the center of a mirror word and expand outward while both sides match.

Brute force approach in plain English:

Generate every substring and test whether it is a palindrome.

What brute force teaches:

Every answer is a substring. The weakness is checking too many substrings.

Optimized approach in plain English:

Try each possible center and expand outward while characters match. Check both odd and even centers.

Optimized thinking habit:

When a structure is symmetric, search from the center outward.

Edge cases:

- Even-length palindrome.
- All same characters.

## 48. Word Break

Original problem statement:

Determine whether a string can be segmented into dictionary words.

What it means:

You are asking whether the string can be cut into valid pieces.

Think like real life:

Put dividers in a sentence without spaces. Every piece must be a known word.

Brute force approach in plain English:

Try every prefix that is a dictionary word, then recursively solve the remaining suffix.

What brute force teaches:

The problem is recursive cutting. The weakness is solving the same suffix many times.

Optimized approach in plain English:

Use DP where `dp[i]` means the prefix ending at `i` can be segmented. For each `i`, check earlier cut positions.

Optimized thinking habit:

When recursion asks the same suffix question repeatedly, store whether each prefix/suffix is solvable.

Edge cases:

- Empty string.
- Dictionary with overlapping words.

## 49. Coin Change

Original problem statement:

Given coin denominations and an amount, return the fewest coins needed.

What it means:

You can reuse coins, and you want minimum count.

Think like real life:

For each amount, ask: "If my last coin was this denomination, how many coins did I need before it?"

Brute force approach in plain English:

Recursively try every coin as the next coin and solve the remaining amount.

What brute force teaches:

The answer for an amount depends on smaller amounts. The weakness is recomputing those smaller amounts.

Optimized approach in plain English:

Build DP from `0` up to target amount. For each amount, try every coin and keep the minimum.

Optimized thinking habit:

When target value is built from smaller target values, think bottom-up DP.

Edge cases:

- Amount zero.
- Impossible amount.

## 50. Partition Equal Subset Sum

Original problem statement:

Determine whether the array can be split into two subsets with equal sum.

What it means:

If total sum is even, you only need to find one subset with sum `total / 2`.

Think like real life:

Dividing weights into two equal bags: once one bag reaches half the total, the rest automatically forms the other half.

Brute force approach in plain English:

Try every subset and check whether its sum equals half of total.

What brute force teaches:

This is a subset-sum problem. The weakness is exponential subsets.

Optimized approach in plain English:

Use DP where `dp[s]` means some subset can make sum `s`. For each number, update possible sums backward.

Optimized thinking habit:

When selecting each number at most once to hit a target, think 0/1 subset DP.

Edge cases:

- Odd total sum.
- Number bigger than target.

## 51. Binary Search

Original problem statement:

Find a target in a sorted array.

What it means:

Sorted order lets you discard half the array after each comparison.

Think like real life:

Looking up a word in a dictionary: open near the middle, then decide left or right.

Brute force approach in plain English:

Scan each element from left to right until target is found.

What brute force teaches:

Linear search works without using sorted order. The weakness is ignoring the main advantage.

Optimized approach in plain English:

Check the middle. If target is smaller, search left half. If larger, search right half.

Optimized thinking habit:

When sorted data lets one comparison eliminate many items, think binary search.

Edge cases:

- Empty array.
- Target absent.
- Midpoint overflow in some languages.

## 52. Search in Rotated Sorted Array

Original problem statement:

Find a target in a sorted array that was rotated.

What it means:

The array has two sorted pieces glued together.

Think like real life:

A sorted book index was cut and swapped. At any midpoint, at least one side is still sorted.

Brute force approach in plain English:

Scan every element until target is found.

What brute force teaches:

Rotation does not hide the value. The weakness is not using partial sorted order.

Optimized approach in plain English:

Use binary search. At each step, determine which side is sorted. If target lies inside that sorted side, search there; otherwise search the other side.

Optimized thinking habit:

When global sorted order is broken, look for a local sorted half.

Edge cases:

- No rotation.
- One element.
- Duplicates require extra handling.

## 53. Valid Sudoku

Original problem statement:

Determine whether a partially filled Sudoku board is valid.

What it means:

No row, column, or 3x3 box can contain the same digit twice.

Think like real life:

Every filled number makes three claims: "I am in this row, this column, and this box."

Brute force approach in plain English:

For each filled cell, scan its row, column, and box for duplicates.

What brute force teaches:

Validity is about duplicate detection in three regions. The weakness is repeated scanning.

Optimized approach in plain English:

Use sets for rows, columns, and boxes. When seeing a digit, if it already exists in any relevant set, invalid.

Optimized thinking habit:

When checking duplicates repeatedly, store what you have already seen.

Edge cases:

- Empty cells should be ignored.
- Box index calculation must be correct.

## 54. Implement Trie

Original problem statement:

Design a data structure for inserting words, searching full words, and checking prefixes.

What it means:

Words with shared prefixes should share path nodes.

Think like real life:

A dictionary tree: first letter narrows choices, second letter narrows more, and so on.

Brute force approach in plain English:

Store all words in a list. Search scans for exact match. Prefix check scans for any word starting with that prefix.

What brute force teaches:

Search and prefix are different questions. The weakness is scanning every word.

Optimized approach in plain English:

Build a trie. Each node has child links and a flag saying whether a full word ends there.

Optimized thinking habit:

When many strings share prefixes, store the shared prefix once.

Edge cases:

- Searching a prefix that is not a full word.
- Empty string behavior should be defined.

## 55. Word Search II

Original problem statement:

Given a board and many words, return all words that can be found by moving through adjacent cells.

What it means:

It is Word Search repeated for a dictionary.

Think like real life:

Instead of searching the board separately for every word, carry a dictionary tree while walking the board.

Brute force approach in plain English:

Run normal Word Search DFS separately for each word.

What brute force teaches:

Each word is a path problem. The weakness is repeating the same board paths for words with shared prefixes.

Optimized approach in plain English:

Build a trie of all words. DFS from each board cell, following trie nodes. If the current path is not a dictionary prefix, stop immediately.

Optimized thinking habit:

When many target words share prefixes, combine trie with backtracking.

Edge cases:

- Duplicate words in input.
- Empty board.
- Non-lowercase characters if implementation assumes `a-z`.

## Banking-Style Human-Centric Prompts

These are especially useful for Wells Fargo senior Java interviews because they connect DSA thinking to banking systems.

## B1. Failed Login Rolling Window

Original problem statement:

Given login events, flag accounts with more than `N` failed attempts within any `T` minute window.

Think like real life:

Security is watching for bursts, not lifetime totals. Three failures over a month may be normal; three failures in two minutes is suspicious.

Brute force approach in plain English:

For each account, pick each failed login as a possible window start. Scan all failed logins for that account and count how many fall inside `start..start+T`.

Optimized approach in plain English:

Group failures by account, sort timestamps, and use a sliding window. Move the right side as events arrive; move the left side when the window becomes too wide.

Thinking habit:

When a rule says "within the last T minutes," think sorted timestamps plus sliding window.

Pitfalls:

- Time zones.
- Inclusive/exclusive boundary.
- Duplicate events from retries.

## B2. Idempotent Payment Requests

Original problem statement:

Process payment requests so a repeated request with the same idempotency key does not create duplicate money movement.

Think like real life:

If a customer taps "Pay" twice because the network lagged, the bank should not send money twice.

Brute force approach in plain English:

For each request, scan all previous requests for the same idempotency key. If found, return the first response.

Optimized approach in plain English:

Use a map or database table keyed by idempotency key. Store request hash, status, and response. If the key repeats with the same hash, replay the response. If it repeats with a different body, reject it.

Thinking habit:

When duplicate external calls are possible, store the decision boundary before doing the dangerous action.

Pitfalls:

- Concurrent duplicate requests.
- Same key with different payload.
- Expiration window too short.
- Use a database uniqueness constraint on the idempotency key.
- Insert the idempotency record atomically before the external side effect.
- Model retry-safe status transitions like `IN_PROGRESS`, `SUCCEEDED`, and `FAILED_RETRYABLE`.
- Decide whether a second in-flight request waits, returns conflict, or polls.

## B3. Ledger Reconciliation

Original problem statement:

Compare internal ledger transactions with external settlement records and find mismatches.

Think like real life:

Two accountants have two ledgers. Every transaction should have a matching partner with the same reference, amount, currency, and date rules.

Brute force approach in plain English:

For every internal transaction, scan every external transaction looking for a match.

Optimized approach in plain English:

Build a composite key from match fields. Store lists of records for each key because duplicates can exist. Consume matches from both sides and report what remains.

Thinking habit:

When matching two datasets, build a stable key but preserve duplicates.

Pitfalls:

- Use `BigDecimal`, not `double`.
- Same reference can appear more than once.
- Audit output should include mismatch reason.
- Normalize currency and `BigDecimal` scale before comparing.
- Be explicit about transaction date vs settlement date.
- Preserve a reconciliation trace: matched records, rule used, timestamp, and reviewer state.

## B4. Batch Dependency Scheduler

Original problem statement:

Given batch jobs and dependencies, return a valid execution order or report that jobs are cyclic.

Think like real life:

Some jobs prepare files that later jobs need. A job can run only after all upstream jobs finish.

Brute force approach in plain English:

Repeatedly scan jobs and run any job whose dependencies are already completed. If a full scan runs nothing, the remaining jobs are stuck.

Optimized approach in plain English:

Build indegree counts and adjacency lists. Start with jobs that have no dependencies. Remove them one by one and unlock downstream jobs.

Thinking habit:

Dependency ordering is topological sort. Brute force teaches the rule; indegree makes it efficient.

Pitfalls:

- Include isolated jobs.
- Cycle should return useful information in real systems.
- Deterministic ordering may matter.

## B5. Transaction Stream Top K Risky Accounts

Original problem statement:

Maintain the top `K` accounts by risk score from a transaction stream.

Think like real life:

Fraud analysts do not need every account sorted every second. They need the current watchlist.

Brute force approach in plain English:

After each score update, sort all accounts by score and take the first `K`.

Optimized approach in plain English:

For batch mode, aggregate scores in a map and keep a min-heap of size `K`. For real-time mutable scores, use an indexed heap or balanced tree so updates can move an account efficiently.

Thinking habit:

When only the top `K` matters, avoid maintaining a full sorted list unless updates require it.

Pitfalls:

- Tie-break by account id for deterministic output.
- Score decreases are harder than only score increases.
- Do not log sensitive account identifiers in plain text.
