import java.util.Arrays;

public class MinimumNumberOfArrowsToBurstBalloons {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: points = [[10,16],[2,8],[1,6],[7,12]]
     * Sample Output: 2
     *
     * Each balloon covers an x-range. One arrow shot at x bursts every balloon
     * whose range contains x. Find the fewest arrows needed.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If many balloons overlap at one position, one arrow can burst all of them.
     * To use arrows well, aim at the earliest ending balloon so the arrow has
     * the best chance to also hit upcoming balloons.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try each balloon's end as a possible arrow position. Shoot there, remove
     * every balloon hit by it, and repeat.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Mark all balloons as not burst.
     * 2. Find an unburst balloon with the smallest end.
     * 3. Shoot at that end and mark every balloon containing it.
     * 4. Repeat until all balloons are burst.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * [[1,6],[2,8],[7,12],[10,16]]
     * Smallest end is 6, shoot at 6, burst [1,6] and [2,8].
     * Smallest remaining end is 12, shoot at 12, burst [7,12] and [10,16].
     * arrows = 2
     */

    public int bruteForce(int[][] points) {
        boolean[] burst = new boolean[points.length];
        int burstCount = 0;
        int arrows = 0;

        while (burstCount < points.length) {
            int arrow = findSmallestEndOfUnburst(points, burst);
            arrows++;

            for (int i = 0; i < points.length; i++) {
                if (!burst[i] && points[i][0] <= arrow && arrow <= points[i][1]) {
                    burst[i] = true;
                    burstCount++;
                }
            }
        }

        return arrows;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Sorting by end puts the best arrow positions in order. Once an arrow is
     * placed at the current end, every later balloon starting before or at that
     * position is already burst.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Sort balloons by ending coordinate.
     * 2. Shoot first arrow at the first end.
     * 3. For each balloon, if its start is after current arrow, need a new arrow.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(1) extra besides sorting
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sorted: [[1,6],[2,8],[7,12],[10,16]]
     * arrow at 6 bursts first two.
     * [7,12] starts after 6, new arrow at 12.
     * [10,16] starts before 12, same arrow works.
     */

    public int optimized(int[][] points) {
        if (points.length == 0) {
            return 0;
        }

        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));

        int arrows = 1;
        int currentArrow = points[0][1];

        for (int i = 1; i < points.length; i++) {
            // If the next balloon starts after the arrow, it was not burst.
            if (points[i][0] > currentArrow) {
                arrows++;
                currentArrow = points[i][1];
            }
        }

        return arrows;
    }

    private int findSmallestEndOfUnburst(int[][] points, boolean[] burst) {
        int smallestEnd = Integer.MAX_VALUE;

        for (int i = 0; i < points.length; i++) {
            if (!burst[i]) {
                smallestEnd = Math.min(smallestEnd, points[i][1]);
            }
        }

        return smallestEnd;
    }
}
