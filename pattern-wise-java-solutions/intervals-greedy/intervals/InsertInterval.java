import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertInterval {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: intervals = [[1,3],[6,9]], newInterval = [2,5]
     * Sample Output: [[1,5],[6,9]]
     *
     * Input intervals = [[1,3],[6,9]], newInterval = [2,5]
     * Output: [[1,5],[6,9]]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Because intervals are already sorted, handle three parts:
     * 1. Intervals before new interval.
     * 2. Intervals that overlap and must merge.
     * 3. Intervals after new interval.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to place the new interval with all old
     * intervals, sort everything, and then use the usual merge-intervals rule.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * [1,3] overlaps [2,5], merge to [1,5].
     * [6,9] starts after 5, so it stays separate.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Add newInterval into the list.
     * 2. Sort all intervals.
     * 3. Use merge intervals logic.
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This treats insertion as "add first, repair order and overlaps later."
     */
    public int[][] bruteForce(int[][] intervals, int[] newInterval) {
        /*
         * Brute force idea:
         * If inserting in the correct position feels confusing, first put every
         * interval together in one array. After that it becomes the normal
         * "merge intervals" problem.
         */
        int[][] all = new int[intervals.length + 1][2];

        for (int i = 0; i < intervals.length; i++) {
            /*
             * Copy each old interval.
             * We are building a bigger list that also contains the new interval.
             */
            all[i] = intervals[i];
        }

        /*
         * Place the new interval at the end for now.
         * Sorting in the next step will move it to its correct position.
         */
        all[intervals.length] = newInterval;

        /*
         * Sort by start time so overlapping intervals come next to each other.
         * Once they are next to each other, merging becomes simple.
         */
        Arrays.sort(all, (a, b) -> Integer.compare(a[0], b[0]));

        return mergeSortedIntervals(all);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Since the original list is already sorted, no need to sort again.
     * Walk once and place the new interval in the right merged position.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Intervals before [2,5]: none.
     * Overlap [1,3] -> new becomes [1,5].
     * After interval [6,9] begins after 5 -> add new, then [6,9].
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Add all intervals ending before new starts.
     * 2. Merge all intervals starting before new ends.
     * 3. Add merged new interval.
     * 4. Add remaining intervals.
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * Because input is already sorted, this pass separates before/overlap/after.
     */
    public int[][] optimized(int[][] intervals, int[] newInterval) {
        List<int[]> answer = new ArrayList<>();
        int index = 0;

        while (index < intervals.length && intervals[index][1] < newInterval[0]) {
            /*
             * This interval ends before the new interval starts.
             * So it cannot overlap with newInterval and can be safely added.
             */
            answer.add(intervals[index]);
            index++;
        }

        while (index < intervals.length && intervals[index][0] <= newInterval[1]) {
            /*
             * This interval starts before the new interval ends.
             * That means both intervals touch/overlap, so we expand newInterval
             * to cover both ranges.
             */
            newInterval[0] = Math.min(newInterval[0], intervals[index][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[index][1]);
            index++;
        }

        /*
         * After all overlaps are consumed, newInterval now represents the full
         * merged block, so add it once.
         */
        answer.add(newInterval);

        while (index < intervals.length) {
            /*
             * These intervals start after the merged interval ends.
             * They also cannot overlap, so add them as they are.
             */
            answer.add(intervals[index]);
            index++;
        }

        return answer.toArray(new int[answer.size()][]);
    }

    private int[][] mergeSortedIntervals(int[][] intervals) {
        List<int[]> merged = new ArrayList<>();

        for (int[] interval : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                /*
                 * No previous interval overlaps with this one.
                 * Start a new block in the answer.
                 */
                merged.add(interval);
            } else {
                /*
                 * Current interval overlaps with the last saved interval.
                 * Keep the earlier start, and only extend the end if needed.
                 */
                int[] last = merged.get(merged.size() - 1);
                last[1] = Math.max(last[1], interval[1]);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }
}
