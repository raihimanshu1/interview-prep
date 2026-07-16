package com.companywisejavasolutions.wellsfargo.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeIntervals {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
     * Sample Output: [[1,6],[8,10],[15,18]]
     *
     * Input: [[1,3],[2,6],[8,10],[15,18]]
     * Output: [[1,6],[8,10],[15,18]]
     * Overlapping intervals should be merged.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Two intervals overlap when the next start is not after the current end.
     * Sorting by start puts possible overlaps next to each other.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to pick one interval and repeatedly search
     * the whole list for anything that touches it, growing the interval until no
     * more overlap is found.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * [1,3] and [2,6] overlap because 2 <= 3.
     * Merge them into [1,6].
     * [8,10] does not overlap because 8 > 6.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Pick an interval.
     * 2. Compare it with every other interval to find overlaps.
     * 3. Keep merging until no more overlap.
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * visited prevents adding the same interval to two merged groups.
     */
    public int[][] bruteForce(int[][] intervals) {
        boolean[] visited = new boolean[intervals.length];
        List<int[]> merged = new ArrayList<>();

        for (int i = 0; i < intervals.length; i++) {
            if (visited[i]) {
                continue;
            }

            int start = intervals[i][0];
            int end = intervals[i][1];
            visited[i] = true;

            boolean foundNewOverlap = true;

            while (foundNewOverlap) {
                foundNewOverlap = false;

                for (int j = 0; j < intervals.length; j++) {
                    if (visited[j]) {
                        continue;
                    }

                    if (intervals[j][0] <= end && intervals[j][1] >= start) {
                        start = Math.min(start, intervals[j][0]);
                        end = Math.max(end, intervals[j][1]);
                        visited[j] = true;
                        foundNewOverlap = true;
                    }
                }
            }

            merged.add(new int[] { start, end });
        }

        merged.sort((a, b) -> Integer.compare(a[0], b[0]));
        return merged.toArray(new int[merged.size()][]);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * After sorting, we only need to compare with the last merged interval.
     * If current overlaps with last, extend last end.
     * Otherwise start a new interval.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Sorted intervals: [1,3], [2,6], [8,10]
     * Last is [1,3], current [2,6] overlaps -> last becomes [1,6].
     * Current [8,10] does not overlap -> add new.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Sort by start.
     * 2. Add first interval as current merged.
     * 3. For each interval, compare with last merged.
     * 4. Merge or append.
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Sorting puts all possible overlaps beside the current merged interval.
     */
    public int[][] optimized(int[][] intervals) {
        // Sort by start so a future interval cannot overlap an earlier finished group.
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();

        for (int[] interval : intervals) {
            if (merged.isEmpty() || interval[0] > merged.get(merged.size() - 1)[1]) {
                // No overlap with the last block, so begin a new block.
                merged.add(new int[] { interval[0], interval[1] });
            } else {
                int[] last = merged.get(merged.size() - 1);
                // Overlap exists; only the end may need to stretch.
                last[1] = Math.max(last[1], interval[1]);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }
}
