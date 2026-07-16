package com.patternwisejavasolutions.intervalsgreedy.intervals;

import java.util.Arrays;

public class MeetingRoomsI {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: intervals = [[0,30],[5,10],[15,20]]
     * Sample Output: false
     *
     * Input meetings = [[0,30],[5,10],[15,20]]
     * Output: false because [0,30] overlaps the others.
     * We need to know if one person can attend all meetings.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A person cannot attend two meetings that overlap.
     * Sort meetings by start time, then only neighboring meetings can overlap.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to compare every meeting with every other
     * meeting. If any pair overlaps, one person cannot attend all of them.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * [0,30] and [5,10] overlap because 5 < 30.
     * So return false.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Compare every pair of meetings.
     * 2. If any pair overlaps, return false.
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * Every pair is checked because the list may be unsorted.
     */
    public boolean bruteForce(int[][] intervals) {
        for (int i = 0; i < intervals.length; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (intervals[i][0] < intervals[j][1] && intervals[j][0] < intervals[i][1]) {
                    return false;
                }
            }
        }

        return true;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * After sorting by start, if a meeting overlaps, it must overlap with the meeting right before it.
     * So one pass is enough.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Sorted: [0,30], [5,10], [15,20]
     * Compare [0,30] and [5,10]: 5 < 30, overlap.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Sort by start.
     * 2. For each meeting from second onward, compare start with previous end.
     * 3. If start < previous end, return false.
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Sorting by start turns "compare every pair" into "compare neighbors."
     */
    public boolean optimized(int[][] intervals) {
        // Earlier meetings first; any overlap will now appear next to its cause.
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[i - 1][1]) {
                // Current starts before previous ends, so time is double-booked.
                return false;
            }
        }

        return true;
    }
}
