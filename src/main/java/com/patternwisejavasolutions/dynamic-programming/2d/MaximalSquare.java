
package com.patternwisejavasolutions.dynamicProgramming.2d;
public class MaximalSquare {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: matrix = [[1,0,1,0,0],[1,0,1,1,1],[1,1,1,1,1],[1,0,0,1,0]]
     * Sample Output: 4
     *
     * Input matrix of '0' and '1'.
     * Return area of the largest square containing only 1s.
     * Example output can be 4 for a 2x2 square.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try every cell as the top-left corner.
     * For every possible square size, check whether all cells are 1.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Pick a top-left corner and test square sizes 1, 2, 3, and so on. For
     * each size, scan every cell inside that square to confirm all are 1.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * At a cell with 1, try size 1.
     * Then try size 2 by checking the 2x2 area.
     * If any 0 appears, that square size fails.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. For each cell, try every square size.
     * 2. Check all cells in that square.
     * 3. Track largest area.
     * Time Complexity: O(mn * min(m,n)^3)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This checks candidate squares directly.
     */
    public int bruteForce(char[][] matrix) {
        int bestSide = 0;

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                int maxSize = Math.min(matrix.length - row, matrix[0].length - col);

                for (int size = 1; size <= maxSize; size++) {
                    if (isAllOnes(matrix, row, col, size)) {
                        bestSide = Math.max(bestSide, size);
                    }
                }
            }
        }

        return bestSide * bestSide;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Let dp[row][col] be the side length of the biggest all-1 square ending at that cell.
     * A square can grow only if top, left, and top-left also support it. Those
     * three stored states replace rescanning the whole square.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * If current cell is 1:
     * side = 1 + min(top, left, top-left).
     * The weakest neighbor limits the square.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Create dp same size as matrix.
     * 2. If cell is 1, compute side from neighbors.
     * 3. Track max side.
     * 4. Return maxSide * maxSide.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * dp[row][col] stores the largest square whose bottom-right corner is here.
     */
    public int optimized(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] dp = new int[rows][cols];
        int bestSide = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (matrix[row][col] == '1') {
                    if (row == 0 || col == 0) {
                        // Border cells can only form a 1x1 square.
                        dp[row][col] = 1;
                    } else {
                        // The smallest neighbor limits how large this square can grow.
                        dp[row][col] = 1 + Math.min(dp[row - 1][col], Math.min(dp[row][col - 1], dp[row - 1][col - 1]));
                    }

                    bestSide = Math.max(bestSide, dp[row][col]);
                }
            }
        }

        return bestSide * bestSide;
    }


    private boolean isAllOnes(char[][] matrix, int startRow, int startCol, int size) {
        for (int row = startRow; row < startRow + size; row++) {
            for (int col = startCol; col < startCol + size; col++) {
                if (matrix[row][col] != '1') {
                    // One zero breaks this candidate square.
                    return false;
                }
            }
        }

        return true;
    }
}
