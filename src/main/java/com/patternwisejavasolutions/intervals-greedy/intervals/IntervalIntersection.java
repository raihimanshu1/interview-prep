package com.patternwisejavasolutions.intervalsgreedy.intervals;

import java.util.ArrayList;
import java.util.List;

public class IntervalIntersection {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: firstList = [[0,2],[5,10],[13,23],[24,25]],
     *               secondList = [[1,5],[8,12],[15,24],[25,26]]
     * Sample Output: [[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]]
     *
     * Given two lists of sorted, disjoint intervals, find their intersection.
     * Each list is sorted by start time and has no overlaps within itself.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Two meetings — one from list A, one from list B — overlap when
     * the later start is <= the earlier end.
     * The overlapping region is [max(startA, startB), min(endA, endB)].
     * Move forward in the list whose interval ends first, because
     * the other interval might still overlap with the next interval.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Compare every interval from list A with every interval from list B.
     * If they overlap, compute the intersection.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * A[0]=[0,2] with B[0]=[1,5] → overlap, intersection [1,2]
     * A[0]=[0,2] with B[1]=[8,12] → no overlap (2 < 8)
     * A[1]=[5,10] with B[0]=[1,5] → overlap, intersection [5,5]
     * ... and so on
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Loop through every interval in firstList.
     * 2. For each, loop through every interval in secondList.
     * 3. If they overlap, compute intersection.
     * Time Complexity: O(n * m)
     * Space Complexity: O(1) extra (excluding output)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * Simple double loop. Works but slow for large inputs.
     */
    public int[][] bruteForce(int[][] firstList, int[][] secondList) {
        List<int[]> result = new ArrayList<>();

        for (int[] a : firstList) {
            for (int[] b : secondList) {
                int start = Math.max(a[0], b[0]);
                int end = Math.min(a[1], b[1]);

                if (start <= end) {
                    result.add(new int[]{start, end});
                }
            }
        }

        return result.toArray(new int[result.size()][]);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Since both lists are sorted, we can use two pointers to walk through
     * both lists in a single pass. At each step, check overlap between
     * the current intervals. Then advance the pointer whose interval
     * ends first — that interval cannot overlap with anything else.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * A[0]=[0,2], B[0]=[1,5] → overlap [1,2], endA=2 < endB=5 → i++
     * A[1]=[5,10], B[0]=[1,5] → overlap [5,5], endB=5 < endA=10 → j++
     * A[1]=[5,10], B[1]=[8,12] → overlap [8,10], endA=10 < endB=12 → i++
     * A[2]=[13,23], B[1]=[8,12] → no overlap (13 > 12) → j++
     * A[2]=[13,23], B[2]=[15,24] → overlap [15,23], endB=24 > endA=23 → i++
     * A[3]=[24,25], B[2]=[15,24] → overlap [24,24], endB=24 < endA=25 → j++
     * A[3]=[24,25], B[3]=[25,26] → overlap [25,25]
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Use two pointers i (for firstList) and j (for secondList).
     * 2. While i < firstList.length && j < secondList.length:
     *    a. Compute intersection start = max(aStart, bStart)
     *    b. Compute intersection end = min(aEnd, bEnd)
     *    c. If start <= end, add to result.
     *    d. If aEnd < bEnd, i++ else j++
     * Time Complexity: O(n + m)
     * Space Complexity: O(1) extra (excluding output)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Two-pointer merge is possible because both lists are sorted
     * and intervals within each list are disjoint.
     */
    public int[][] optimized(int[][] firstList, int[][] secondList) {
        List<int[]> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < firstList.length && j < secondList.length) {
            int aStart = firstList[i][0], aEnd = firstList[i][1];
            int bStart = secondList[j][0], bEnd = secondList[j][1];

            // Overlap check: later start must be <= earlier end
            int start = Math.max(aStart, bStart);
            int end = Math.min(aEnd, bEnd);

            if (start <= end) {
                result.add(new int[]{start, end});
            }

            // Move the pointer whose interval ends first
            if (aEnd < bEnd) {
                i++;
            } else {
                j++;
            }
        }

        return result.toArray(new int[result.size()][]);
    }
}