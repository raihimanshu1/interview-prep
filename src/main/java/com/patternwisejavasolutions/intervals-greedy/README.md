# Intervals & Greedy

> **Core Pattern:** Sort intervals by start time → sweep/merge; Greedy: make locally optimal choice at each step.  
> **Learning Path:** Interval merging → greedy scheduling → greedy optimization.

---

## 📖 Conceptual Foundation

### Interval Techniques
| Technique | When to Use | Example |
|-----------|-------------|---------|
| Sort by start time | Overlap detection | Merge Intervals |
| Sort by end time | Max non-overlapping | Non-Overlapping Intervals |
| Sweep line | Count simultaneous events | Meeting Rooms II |
| Min-heap of end times | Track earliest ending room | Meeting Rooms II |

### Greedy Template
```java
// 1. Sort based on some criteria (start time, end time, ratio)
// 2. Iterate and make greedy choice
// 3. Track result
```

### Interval Merge Template
```java
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
List<int[]> merged = new ArrayList<>();
int[] current = intervals[0];
for (int i = 1; i < intervals.length; i++) {
    if (intervals[i][0] <= current[1]) {
        current[1] = Math.max(current[1], intervals[i][1]); // merge
    } else {
        merged.add(current);
        current = intervals[i];
    }
}
merged.add(current);
```

---

## 📚 Learning Order

### Phase 1: Intervals

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Meeting Rooms I** | [intervals/MeetingRoomsI.java](intervals/MeetingRoomsI.java) | Sort by start, check if next start < prev end | 🟢 Easy |
| 2 | **Merge Intervals** | [intervals/MergeIntervals.java](intervals/MergeIntervals.java) | Sort by start, merge overlapping | 🟡 Medium |
| 3 | **Insert Interval** | [intervals/InsertInterval.java](intervals/InsertInterval.java) | Find insertion point, merge overlapping | 🟡 Medium |
| 4 | **Interval Intersection** | [intervals/IntervalIntersection.java](intervals/IntervalIntersection.java) | Two pointers on sorted intervals | 🟡 Medium |
| 5 | **Non-Overlapping Intervals** | [intervals/NonOverlappingIntervals.java](intervals/NonOverlappingIntervals.java) | Sort by end, greedy — keep earliest ending | 🟡 Medium |
| 6 | **Meeting Rooms II** | [intervals/MeetingRoomsII.java](intervals/MeetingRoomsII.java) | Sort by start, min-heap of end times OR sweep line | 🟡 Medium |
| 7 | **Minimum Arrows to Burst Balloons** | [intervals/MinimumNumberOfArrowsToBurstBalloons.java](intervals/MinimumNumberOfArrowsToBurstBalloons.java) | Sort by end, greedy overlap | 🟡 Medium |

### Phase 2: Greedy

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 7 | **Assign Cookies** | [greedy/AssignCookies.java](greedy/AssignCookies.java) | Sort both, greedily assign smallest cookie that satisfies | 🟢 Easy |
| 8 | **Jump Game** | [greedy/JumpGame.java](greedy/JumpGame.java) | Track max reachable index, iterate | 🟡 Medium |
| 9 | **Jump Game II** | [greedy/JumpGameII.java](greedy/JumpGameII.java) | BFS-like greedy: track `currentEnd` and `farthest` | 🟡 Medium |
| 10 | **Gas Station** | [greedy/GasStation.java](greedy/GasStation.java) | If total gas < total cost → impossible; find valid start | 🟡 Medium |
| 11 | **Partition Labels** | [greedy/PartitionLabels.java](greedy/PartitionLabels.java) | Track last occurrence, expand partition end | 🟡 Medium |
| 12 | **Valid Parenthesis String** | [greedy/ValidParenthesisString.java](greedy/ValidParenthesisString.java) | Track `minOpen` / `maxOpen` range | 🟡 Medium |

---

## 🔑 Key Insights

1. **Interval merge** → sort by start, greedily extend end
2. **Minimize removals** (non-overlapping) → sort by end, keep earliest ending
3. **Maximum simultaneous** → sweep line (count starts/ends) OR min-heap
4. **Greedy works** when local optimum leads to global optimum (prove with exchange argument)
5. **Jump Game II** → BFS-like level tracking: current jump range, farthest next range

---

## 🎯 Practice Checklist

- [ ] Phase 1: Intervals (Merge, Insert, Non-overlap, Meeting rooms)
- [ ] Phase 2: Greedy (Jump Game, Gas Station, Partition Labels)