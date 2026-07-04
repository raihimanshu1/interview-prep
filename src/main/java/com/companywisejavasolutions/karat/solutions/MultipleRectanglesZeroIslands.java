

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class MultipleRectanglesZeroIslands {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a matrix with multiple connected zero regions, return a bounding box for each zero region.
     *
     * INPUT
     * grid contains 1s and multiple connected groups of 0s.
     *
     * OUTPUT
     * List of bounding boxes [[top,left],[bottom,right]].
     *
     * EXAMPLE
     * grid = [[0,0,1,1], [1,1,1,0], [0,1,1,0]]
     * Output: [[[0,0],[0,1]], [[1,3],[2,3]], [[2,0],[2,0]]]
     * 
     * There are three separate zero regions, so the answer must include all of them.
     *
     * WHAT IT MEANS
     * Run flood fill from every unseen zero and track min/max row/column.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * Think of the grid as a map. Ones are land we ignore, and zeros are dark
     * regions we need to name.
     *
     * A zero region can be one cell, a straight line, a block, or a crooked
     * shape. For each separate region, we return the smallest rectangle that
     * covers every zero in that region.
     *
     * Example 1 - Three separate zero regions
     *
     * grid = {
     *     {0, 0, 1, 1},
     *     {1, 1, 1, 0},
     *     {0, 1, 1, 0}
     * }
     *
     * Output:
     * {
     *     {{0, 0}, {0, 1}},
     *     {{1, 3}, {2, 3}},
     *     {{2, 0}, {2, 0}}
     * }
     *
     * Why:
     * The top-left zeros touch each other, the right-side zeros touch each other,
     * and the bottom-left zero is alone.
     *
     * Example 2 - One larger region
     *
     * grid = {
     *     {1, 0, 0},
     *     {1, 0, 1},
     *     {1, 0, 0}
     * }
     *
     * Output:
     * {
     *     {{0, 1}, {2, 2}}
     * }
     *
     * Why:
     * All zeros are connected through four-direction movement, so they share one
     * bounding box.
     *
     * Example 3 - Two single-cell regions
     *
     * grid = {
     *     {0, 1, 0},
     *     {1, 1, 1}
     * }
     *
     * Output:
     * {
     *     {{0, 0}, {0, 0}},
     *     {{0, 2}, {0, 2}}
     * }
     *
     * Why:
     * The zeros do not touch, so each zero is its own region.
     *
     * Edge case 1 - No zeros
     *
     * grid = {{1, 1}, {1, 1}}
     * Output = {}
     *
     * There are no regions to report.
     *
     * Edge case 2 - Whole grid is zero
     *
     * grid = {{0, 0}, {0, 0}}
     * Output = {{{0, 0}, {1, 1}}}
     *
     * Everything belongs to one region.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A new region starts only when we see a zero that has not been visited.
     * 2. Four-direction neighbors belong to the same region.
     * 3. Diagonal touching does not merge two regions.
     * 4. The bounding box is tracked with min row, min col, max row, max col.
     * 5. Marking visited prevents reporting the same region multiple times.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Scan the grid from top-left to bottom-right.
     * When an unseen zero appears, flood-fill that entire zero region.
     * During the fill, update the smallest and largest row/column seen.
     * After the fill ends, convert those four numbers into the required box.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of black regions on a white grid. Each unseen zero starts one region;
     * traversal finds that region's boundary.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    Every zero region must be found once; after finding it, we should not report it again.
     *
     * 2. What data structure does that naturally suggest?
     *    Use visited cells because the grid can contain multiple shapes/rectangles.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: scan every cell and flood-fill whenever we see a new zero.
     *
     * 4. What repeated work should I remove?
     *    Optimized: mark visited cells during traversal so each cell is processed once.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: scan every cell and flood-fill whenever we see a new zero.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Scan every grid cell.
     * 2. When a new zero is found, expand or search that zero region.
     * 3. Mark visited zero cells so the same region is not reported twice.
     * 4. Update min/max row and column while exploring the region.
     * 5. Store top-left and bottom-right coordinates for each region.
     * 
     * Time Complexity: O(rows * cols) because each grid cell is scanned and each zero is visited once.
     * Space Complexity: O(rows * cols) for visited cells and discovered rectangles.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the grid with three zero regions.
     * Region one spans [[0,0],[0,1]].
     * Region two spans [[1,3],[2,3]].
     * Region three is a single zero at [[2,0],[2,0]].
     */
    public List<int[][]> bruteForce(int[][] grid) {
        // visited tells us whether a zero already belongs to a region we reported.
        boolean[][] visited = new boolean[grid.length][grid[0].length];

        // Each answer item is {{topRow, leftCol}, {bottomRow, rightCol}}.
        List<int[][]> result = new ArrayList<>();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                // An unseen zero is the first cell of a brand-new island.
                if (grid[row][col] == 0 && !visited[row][col]) {

                    // Start the bounding box as this one cell.
                    // DFS will stretch it as it discovers more connected zeros.
                    int[] box = {row, col, row, col};
                    dfs(grid, row, col, visited, box);

                    // Convert [top, left, bottom, right] into the requested shape.
                    result.add(new int[][] {{box[0], box[1]}, {box[2], box[3]}});
                }
            }
        }
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: mark visited cells during traversal so each cell is processed once.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Use DFS/BFS from each unseen zero.
     * 2. Mark visited immediately when a cell joins the region.
     * 3. Update the bounding box during traversal.
     * 4. Each grid cell is processed at most once.
     * 
     * Time Complexity: Lower than brute force because repeated scanning is replaced with stored state.
     * Space Complexity: O(rows * cols) for visited cells and traversal storage.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the grid with three zero regions.
     * Region one spans [[0,0],[0,1]].
     * Region two spans [[1,3],[2,3]].
     * Region three is a single zero at [[2,0],[2,0]].
     */
    public List<int[][]> optimized(int[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) {
            return new ArrayList<>();
        }

        boolean[][] visited = new boolean[grid.length][grid[0].length];
        List<int[][]> result = new ArrayList<>();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
        if (grid[row][col] == 0 && !visited[row][col]) {
                    int[] box = {row, col, row, col};
                    dfs(grid, row, col, visited, box);
                    result.add(new int[][] {{box[0], box[1]}, {box[2], box[3]}});
                }
            }
        }
        return result;
    }

    private void dfs(int[][] grid, int row, int col, boolean[][] visited, int[] box) {
        // Stop if this recursive step walked off the grid, reached a cell already
        // assigned to this or another zero region, or found a 1. Those cells cannot
        // expand the current zero island's bounding box.
        if (row < 0 || row == grid.length || col < 0 || col == grid[0].length
        || visited[row][col] || grid[row][col] != 0) {
            return;
        }

        // Mark immediately so neighboring recursive calls do not process this
        // same zero again. That is what keeps each island from being reported
        // multiple times.
        visited[row][col] = true;

        // Expand the bounding box to include the current zero. The four entries
        // mean top row, left column, bottom row, and right column.
        box[0] = Math.min(box[0], row);
        box[1] = Math.min(box[1], col);
        box[2] = Math.max(box[2], row);
        box[3] = Math.max(box[3], col);

        // Explore the four edge-connected neighbors. Diagonal zeros are not
        // included because the problem's island definition is four-directional.
        dfs(grid, row + 1, col, visited, box);
        dfs(grid, row - 1, col, visited, box);
        dfs(grid, row, col + 1, visited, box);
        dfs(grid, row, col - 1, visited, box);
    }

    public static void main(String[] args) {
        MultipleRectanglesZeroIslands solver = new MultipleRectanglesZeroIslands();

        int[][][] samples = {
                {
                        {0, 0, 1, 1},
                        {1, 1, 1, 0},
                        {0, 1, 1, 0}
                },
                {
                        {1, 0, 0},
                        {1, 0, 1},
                        {1, 0, 0}
                },
                {
                        {0, 1, 0},
                        {1, 1, 1}
                }
        };

        for (int i = 0; i < samples.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + format(solver.bruteForce(copyGrid(samples[i]))));
            System.out.println("optimized:  " + format(solver.optimized(copyGrid(samples[i]))));
            System.out.println();
        }
    }

    private static int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[grid.length][grid[0].length];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                copy[row][col] = grid[row][col];
            }
        }
        return copy;
    }

    private static String format(List<int[][]> rectangles) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < rectangles.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(java.util.Arrays.deepToString(rectangles.get(i)));
        }
        builder.append("]");
        return builder.toString();
    }
}
