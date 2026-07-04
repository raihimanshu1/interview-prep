
package com.patternwisejavasolutions.dynamicProgramming.grid;
public class MinimumPathSum {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: grid = [[1,3,1],[1,5,1],[4,2,1]]
     * Sample Output: 7
     *
     * Input grid = [[1,3,1],[1,5,1],[4,2,1]]
     * Move only right or down.
     * Output: 7 from path 1 -> 3 -> 1 -> 1 -> 1.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * From any cell, choose the cheaper future path:
     * go right or go down.
     * Add current cell cost to that cheaper choice.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * From each cell, try both legal continuations: go down or go right. Add
     * the current cell cost to the cheaper future. The same cell's best future
     * is recalculated many times by different paths that arrive there.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * At top-left 1:
     * Option down starts with 1.
     * Option right starts with 3.
     * But we must consider full remaining path, so recurse and take minimum.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively try down and right.
     * 2. If out of bounds, return huge value.
     * 3. If target reached, return its value.
     * Time Complexity: exponential
     * Space Complexity: O(m+n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This recursively asks for the cheapest suffix path from each cell.
     */
    public int bruteForce(int[][] grid) {
        return minFrom(grid, 0, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Let dp[row][col] be minimum cost to reach that cell from start.
     * The previous step can only come from top or left.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * dp[0][0] = 1
     * First row accumulates: 1,4,5
     * First column accumulates: 1,2,6
     * Inner cell uses min(top,left) + grid value.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Initialize dp[0][0].
     * 2. Fill first row and first column.
     * 3. For every other cell, add grid[row][col] to min(top,left).
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[row][col] stores the cheapest cost to reach that cell once.
     */
    public int optimized(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[][] dp = new int[rows][cols];
        // Starting cell cost is paid immediately.
        dp[0][0] = grid[0][0];

        for (int row = 1; row < rows; row++) {
            // First column can only come from above.
            dp[row][0] = dp[row - 1][0] + grid[row][0];
        }

        for (int col = 1; col < cols; col++) {
            // First row can only come from the left.
            dp[0][col] = dp[0][col - 1] + grid[0][col];
        }

        for (int row = 1; row < rows; row++) {
            for (int col = 1; col < cols; col++) {
                // Choose the cheaper previous cell: top or left.
                dp[row][col] = grid[row][col] + Math.min(dp[row - 1][col], dp[row][col - 1]);
            }
        }

        return dp[rows - 1][cols - 1];
    }


    private int minFrom(int[][] grid, int row, int col) {
        if (row == grid.length || col == grid[0].length) {
            return Integer.MAX_VALUE / 2;
        }

        if (row == grid.length - 1 && col == grid[0].length - 1) {
            return grid[row][col];
        }

        return grid[row][col] + Math.min(minFrom(grid, row + 1, col), minFrom(grid, row, col + 1));
    }
}
