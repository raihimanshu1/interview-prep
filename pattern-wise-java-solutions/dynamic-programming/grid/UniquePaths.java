public class UniquePaths {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: m = 3, n = 7
     * Sample Output: 28
     *
     * Input: m = 3, n = 7
     * A robot starts top-left and moves only right or down.
     * Output: number of ways to reach bottom-right.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * From any cell, the robot can go right or down.
     * So paths from a cell = paths from below + paths from right.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * From a cell, try the two legal moves: down and right. Each recursion path
     * is one possible robot route. The same cell is reached by many different
     * partial routes, so brute force recounts paths from that cell repeatedly.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * At the bottom-right, there is 1 way because we are already there.
     * At any cell in last row, only right moves remain, so 1 way.
     * At inner cells, add down and right answers.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively move down and right.
     * 2. If out of bounds, return 0.
     * 3. If target reached, return 1.
     * Time Complexity: exponential
     * Space Complexity: O(m+n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This explores routes directly from the top-left cell.
     */
    public int bruteForce(int m, int n) {
        return countFrom(0, 0, m, n);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Many cells are solved repeatedly.
     * Let dp[row][col] store number of ways to reach that cell from start.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * First row is all 1 because only right moves.
     * First column is all 1 because only down moves.
     * Each other cell = top + left.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Fill first row and first column with 1.
     * 2. For each cell, dp[row][col] = dp[row-1][col] + dp[row][col-1].
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[row][col] stores the number of ways to reach that cell once.
     */
    public int optimized(int m, int n) {
        int[][] dp = new int[m][n];

        for (int row = 0; row < m; row++) {
            // First column has only one route: keep moving down.
            dp[row][0] = 1;
        }

        for (int col = 0; col < n; col++) {
            // First row has only one route: keep moving right.
            dp[0][col] = 1;
        }

        for (int row = 1; row < m; row++) {
            for (int col = 1; col < n; col++) {
                // Every inner cell can only be entered from top or left.
                dp[row][col] = dp[row - 1][col] + dp[row][col - 1];
            }
        }

        return dp[m - 1][n - 1];
    }


    private int countFrom(int row, int col, int m, int n) {
        if (row == m || col == n) {
            return 0;
        }

        if (row == m - 1 && col == n - 1) {
            return 1;
        }

        return countFrom(row + 1, col, m, n) + countFrom(row, col + 1, m, n);
    }
}
