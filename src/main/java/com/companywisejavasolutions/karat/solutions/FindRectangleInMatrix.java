package com.companywisejavasolutions.karat.solutions;

public class FindRectangleInMatrix {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a matrix filled with 1s and exactly one rectangular region filled
     * with 0s, return the top-left and bottom-right coordinates of the zero
     * rectangle.
     *
     * EXAMPLE
     * grid = [[1,1,1,1],
     *         [1,0,0,1],
     *         [1,0,0,1],
     *         [1,1,1,1]]
     *
     * Output:
     * [[1,1], [2,2]]
     *
     * What It Means:
     * The zero rectangle has multiple cells, so both top-left and bottom-right matter.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Imagine a white paper with one black rectangular sticker on it.
     *
     * The slow way is to inspect every black square and remember the smallest
     * and largest row/column it touches.
     *
     * But because the black area is guaranteed to be one rectangle, the first
     * zero we see while scanning from top-left must be the rectangle's top-left
     * corner. From there, we only need to expand right and down.
     */

    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     * 1. What do I notice first?
     *    The problem gives a strong guarantee: there is one rectangle of zeroes.
     *
     * 2. What does that guarantee buy me?
     *    A rectangle has only four boundaries: top, left, bottom, and right.
     *
     * 3. How do I build brute force?
     *    Scan every cell. Whenever I see zero, update minRow, minCol, maxRow,
     *    and maxCol.
     *
     * 4. How do I optimize?
     *    Stop at the first zero, because that is top-left, then expand only
     *    right and down to find the opposite corner.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * You are standing in front of a board filled with 1s. Somewhere on that
     * board, one clean rectangular patch is made of 0s.
     *
     * The question is not asking how many zeroes exist. It is asking for the
     * address of the rectangle:
     *
     * top-left cell  = where the rectangle starts
     * bottom-right cell = where the rectangle ends
     *
     * In brute force, we behave like someone using a ruler after checking every
     * square. Every zero tells us something about the rectangle boundary:
     *
     * smallest row    -> top edge
     * smallest column -> left edge
     * largest row     -> bottom edge
     * largest column  -> right edge
     *
     * Once every cell has been inspected, those four numbers fully describe the
     * rectangle.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - Rectangle in the middle
     * grid = [[1,1,1,1],
     *         [1,0,0,1],
     *         [1,0,0,1],
     *         [1,1,1,1]]
     * Zeroes are at rows 1..2 and columns 1..2.
     * Output: [[1,1], [2,2]]
     *
     * Example 2 - Single-row rectangle
     * grid = [[1,1,1,1],
     *         [0,0,0,1],
     *         [1,1,1,1]]
     * Zeroes are all on row 1, from column 0 to column 2.
     * Output: [[1,0], [1,2]]
     *
     * Example 3 - Single-column rectangle
     * grid = [[1,0,1],
     *         [1,0,1],
     *         [1,0,1],
     *         [1,1,1]]
     * Zeroes are in column 1, from row 0 to row 2.
     * Output: [[0,1], [2,1]]
     *
     * Edge Case 1 - One zero cell
     * grid = [[1,1],
     *         [1,0]]
     * The top-left and bottom-right are the same cell.
     * Output: [[1,1], [1,1]]
     *
     * Edge Case 2 - No zero found
     * grid = [[1,1],
     *         [1,1]]
     * This is outside the original "exactly one rectangle" guarantee, but this
     * implementation returns an empty matrix instead of fake coordinates.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Matrix coordinates are usually [row, column].
     * 2. A rectangle boundary can be represented by four values:
     *    min row, min column, max row, max column.
     * 3. Brute force is allowed to inspect every cell.
     * 4. The guarantee of one rectangle means every zero belongs to the same
     *    answer area.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * We scan the matrix like reading a page: row by row, left to right.
     * Whenever we see a zero, we update the rectangle boundary.
     *
     * If the zero is higher than all previous zeroes, it changes the top edge.
     * If it is farther left, it changes the left edge.
     * If it is lower, it changes the bottom edge.
     * If it is farther right, it changes the right edge.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Pretend we do not use the rectangle guarantee much. We look at every cell
     * and collect the outer boundary of all zero cells.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Initialize minRow/minCol to large values.
     * 2. Initialize maxRow/maxCol to small values.
     * 3. Scan every cell.
     * 4. When grid[row][col] is 0:
     *    a. Update minRow and minCol.
     *    b. Update maxRow and maxCol.
     * 5. Return [[minRow, minCol], [maxRow, maxCol]].
     *
     * Time Complexity: O(rows * cols)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the 4x4 grid above.
     * First zero appears at (1,1).
     * Expanding right reaches column 2 and expanding down reaches row 2.
     * Final answer: [[1,1], [2,2]]
     */
    public int[][] bruteForce(int[][] grid) {
        // Start with impossible boundaries so the first zero always fixes them.
        int minRow = grid.length;
        int minCol = grid[0].length;
        int maxRow = -1;
        int maxCol = -1;

        // Visit every row because brute force does not assume where the rectangle starts.
        for (int row = 0; row < grid.length; row++) {
            // Visit every column in the current row.
            for (int col = 0; col < grid[0].length; col++) {
                // Only zero cells are part of the rectangle we need to report.
                if (grid[row][col] == 0) {
                    // Every zero can tighten or expand the rectangle boundary.
                    minRow = Math.min(minRow, row);
                    minCol = Math.min(minCol, col);
                    maxRow = Math.max(maxRow, row);
                    maxCol = Math.max(maxCol, col);
                }
            }
        }

        // If no zero was seen, there is no rectangle to describe.
        if (maxRow == -1) {
            return new int[0][0];
        }

        // Return the two corners: top-left first, bottom-right second.
        return new int[][] {{minRow, minCol}, {maxRow, maxCol}};
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Row-major scanning sees cells from top to bottom and left to right.
     * Therefore, the first zero is the top-left corner of the rectangle.
     * Once we know top-left, we expand:
     * right while the same row has zeroes,
     * down while the same column has zeroes.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Scan from top-left.
     * 2. Stop at the first zero.
     * 3. Move right until the zero rectangle ends.
     * 4. Move down until the zero rectangle ends.
     * 5. Return start and end coordinates.
     *
     * Time Complexity: O(rows * cols) worst case, but stops early after first zero.
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the 4x4 grid above.
     * First zero appears at (1,1).
     * Expanding right reaches column 2 and expanding down reaches row 2.
     * Final answer: [[1,1], [2,2]]
     */
    public int[][] optimized(int[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == 0) {
                    int right = col;
                    int bottom = row;

                    // Same row tells us how far the rectangle extends to the right.
                    while (right + 1 < grid[0].length && grid[row][right + 1] == 0) {
                        right++;
                    }

                    // Same column tells us how far the rectangle extends downward.
                    while (bottom + 1 < grid.length && grid[bottom + 1][col] == 0) {
                        bottom++;
                    }

                    return new int[][] {{row, col}, {bottom, right}};
                }
            }
        }

        return new int[0][0];
    }

    public static void main(String[] args) {
        FindRectangleInMatrix solver = new FindRectangleInMatrix();

        int[][][] samples = {
                {
                        {1, 1, 1, 1},
                        {1, 0, 0, 1},
                        {1, 0, 0, 1},
                        {1, 1, 1, 1}
                },
                {
                        {1, 1, 1, 1},
                        {0, 0, 0, 1},
                        {1, 1, 1, 1}
                },
                {
                        {1, 0, 1},
                        {1, 0, 1},
                        {1, 0, 1},
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

    private static String format(int[][] coordinates) {
        return java.util.Arrays.deepToString(coordinates);
    }
}
