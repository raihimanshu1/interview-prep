
package com.patternwisejavasolutions.intervalsGreedy.intervals;
import java.util.Arrays;

public class NonOverlappingIntervals {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
     * Sample Output: 1
     *
     * Input: [[1,2],[2,3],[3,4],[1,3]]
     * Output: 1
     * Remove minimum intervals so the rest do not overlap.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * When two intervals overlap, one must be removed.
     * To keep future options open, keep the interval that ends earlier.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to decide for each interval: remove it, or
     * keep it if it does not overlap the last kept interval. Try both choices
     * and keep the smaller removal count.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * [1,2] and [1,3] overlap.
     * Keeping [1,2] is better because it ends earlier and leaves more room.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Try removing each possible overlapping interval recursively.
     * 2. Count minimum removals.
     * Time Complexity: exponential
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * Sorting gives the recursion a left-to-right timeline to make keep/remove decisions.
     */
    public int bruteForce(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        return countOverlapsSlow(intervals, 0, Integer.MIN_VALUE);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Sort by end time.
     * Always keep the interval that finishes earliest.
     * This is safe because it leaves maximum space for future intervals.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Sorted by end: [1,2], [1,3], [2,3], [3,4]
     * Keep [1,2].
     * [1,3] overlaps, remove it.
     * [2,3] fits, keep it.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Sort intervals by end.
     * 2. Keep currentEnd of last kept interval.
     * 3. If interval start < currentEnd, remove it.
     * 4. Else keep it and update currentEnd.
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Sorting by end makes the earliest-finishing interval the safest one to keep.
     */
    public int optimized(int[][] intervals) {
        // Earliest end leaves the largest remaining timeline for future intervals.
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
        int removals = 0;
        int currentEnd = Integer.MIN_VALUE;

        for (int[] interval : intervals) {
            if (interval[0] < currentEnd) {
                // It overlaps the interval we kept; remove this later-ending choice.
                removals++;
            } else {
                // It fits after the last kept interval, so keep it.
                currentEnd = interval[1];
            }
        }

        return removals;
    }


    private int countOverlapsSlow(int[][] intervals, int index, int previousEnd) {
        if (index == intervals.length) {
            return 0;
        }

        int removeCurrent = 1 + countOverlapsSlow(intervals, index + 1, previousEnd);
        int keepCurrent = Integer.MAX_VALUE / 2;

        if (intervals[index][0] >= previousEnd) {
            keepCurrent = countOverlapsSlow(intervals, index + 1, intervals[index][1]);
        }

        return Math.min(removeCurrent, keepCurrent);
    }
}
