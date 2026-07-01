# Stack

> **Core Pattern:** LIFO (Last-In-First-Out) — use for nested/balanced structures, monotonic sequences, and expression evaluation.  
> **Learning Path:** Balanced brackets → expression evaluation → monotonic stack (NGE/PSE) → range histogram.

---

## 📖 Conceptual Foundation

### When to use Stack?
| Situation | Pattern | Example |
|-----------|---------|---------|
| Balanced brackets/parsing | Push open, pop on close | Valid Parentheses |
| Expression evaluation | Operand/operator stack | Evaluate Reverse Polish Notation |
| Next/previous greater/smaller | Monotonic stack | Daily Temperatures |
| Range-based area calculation | Monotonic + expansion | Largest Rectangle in Histogram |

### Template: Monotonic Increasing Stack (NGE)
```java
Stack<Integer> stack = new Stack<>();
for (int i = 0; i < n; i++) {
    while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
        int idx = stack.pop();
        // nums[idx]'s next greater is nums[i]
    }
    stack.push(i);
}
```

### Template: Monotonic Decreasing Stack (NSE)
```java
Stack<Integer> stack = new Stack<>();
for (int i = 0; i < n; i++) {
    while (!stack.isEmpty() && nums[i] < nums[stack.peek()]) {
        int idx = stack.pop();
        // nums[idx]'s next smaller is nums[i]
    }
    stack.push(i);
}
```

---

## 📚 Learning Order

### Phase 1: Core Stack (Balancing & Parsing)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Valid Parentheses** | [core/ValidParentheses.java](core/ValidParentheses.java) | Push open → pop on close, check matching | 🟢 Easy |
| 2 | **Remove All Adjacent Duplicates** | [core/RemoveAllAdjacentDuplicatesInString.java](core/RemoveAllAdjacentDuplicatesInString.java) | Stack, remove if top == current | 🟢 Easy |
| 3 | **Baseball Game** | [core/BaseballGame.java](core/BaseballGame.java) | Stack for scoring operations | 🟢 Easy |
| 4 | **Min Stack** | [core/MinStack.java](core/MinStack.java) | Two stacks (values + min) | 🟡 Medium |
| 5 | **Generate Parentheses** | [core/GenerateParentheses.java](core/GenerateParentheses.java) | Backtracking with open/close count | 🟡 Medium |
| 6 | **Evaluate Reverse Polish Notation** | [core/EvaluateReversePolishNotation.java](core/EvaluateReversePolishNotation.java) | Operand stack, pop two on operator | 🟡 Medium |
| 7 | **Asteroid Collision** | [core/AsteroidCollision.java](core/AsteroidCollision.java) | Stack, compare direction + magnitude | 🟡 Medium |
| 8 | **Car Fleet** | [core/CarFleet.java](core/CarFleet.java) | Sort by position, stack for fleet merging | 🟡 Medium |

### Phase 2: Monotonic Increasing (Next Greater Element)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 9 | **Next Greater Element I** | [monotonic-increasing-next-greater/NextGreaterElement.java](monotonic-increasing-next-greater/NextGreaterElement.java) | Monotonic increasing stack + HashMap | 🟢 Easy |
| 10 | **Next Greater Element II (Circular)** | [monotonic-increasing-next-greater/NextGreaterElementII.java](monotonic-increasing-next-greater/NextGreaterElementII.java) | Circular → traverse 2×n array | 🟡 Medium |
| 11 | **Daily Temperatures** | [monotonic-increasing-next-greater/DailyTemperatures.java](monotonic-increasing-next-greater/DailyTemperatures.java) | NGE variant — track distance | 🟡 Medium |

### Phase 3: Monotonic Decreasing (Next Smaller Element)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 12 | **Next Smaller Element** | [monotonic-decreasing-next-smaller/NextSmallerElement.java](monotonic-decreasing-next-smaller/NextSmallerElement.java) | Monotonic decreasing stack | 🟡 Medium |
| 13 | **Previous Smaller Element** | [monotonic-decreasing-next-smaller/PreviousSmallerElement.java](monotonic-decreasing-next-smaller/PreviousSmallerElement.java) | Traverse left, same decreasing stack | 🟡 Medium |

### Phase 4: Range & Histogram (NSE+PSE Combined)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 14 | **Largest Rectangle in Histogram** | [range-histogram/LargestRectangleInHistogram.java](range-histogram/LargestRectangleInHistogram.java) | Find NSE + PSE for each bar → `height * (nse - pse - 1)` | 🔴 Hard |
| 15 | **Trapping Rain Water** | [range-histogram/TrappingRainWater.java](range-histogram/TrappingRainWater.java) | Stack-based or two-pointer (left max, right max) | 🔴 Hard |

### Phase 5: Contribution Pattern

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 16 | **Sum of Subarray Minimums** | [contribution-pattern/SumOfSubarrayMinimums.java](contribution-pattern/SumOfSubarrayMinimums.java) | NSE + PSE → each element's contribution = `arr[i] * (i-pse) * (nse-i)` | 🟡 Medium |

---

## 🔑 Key Insights

1. **Balanced brackets** → simple push/pop with matching map
2. **Monotonic stack** → elements in stack are in sorted order (increasing or decreasing)
3. **NGE/PSE + NSE/PSE** → used for range-based calculations (histogram, rain water, subarray mins)
4. **Circular arrays** → traverse `2*n` and use `i % n`
5. **Contribution pattern** → for each element, find its "span" using PSE/NSE

---

## 🎯 Practice Checklist

- [ ] Phase 1: Core stack (balancing, parsing, RPN)
- [ ] Phase 2: Monotonic increasing (NGE)
- [ ] Phase 3: Monotonic decreasing (NSE/PSE)
- [ ] Phase 4: Range & histogram
- [ ] Phase 5: Contribution pattern