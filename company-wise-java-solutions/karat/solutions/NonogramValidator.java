package karat.solutions;

import java.util.*;

public class NonogramValidator {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a binary board and nonogram clues for rows and columns, validate whether the board matches all clues.
     *
     * INPUT
     * board contains 0/1 values; rowClues and colClues list filled-run lengths.
     *
     * OUTPUT
     * true if every row and column matches its clue.
     *
     * EXAMPLE
     * board = [[1,1,0,1], [0,1,1,0], [1,0,0,1]]
     * rowClues = [[2,1], [2], [1,1]]
     * colClues = [[1,1], [2], [1], [1,1]]
     * Output: true
     * 
     * Each row and column clue must match its filled-cell runs.
     *
     * WHAT IT MEANS
     * Convert each row/column into run lengths and compare with expected clues.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * A nonogram clue describes groups of filled cells.
     *
     * For example, the line:
     *
     * 1 1 0 1
     *
     * has clue [2, 1], because there is a block of two filled cells, then later
     * a block of one filled cell.
     *
     * Validation means every row and every column must tell exactly the same
     * story as its clue.
     *
     * Example 1 - Valid board
     *
     * board = {
     *     {1, 1, 0, 1},
     *     {0, 1, 1, 0},
     *     {1, 0, 0, 1}
     * }
     * rowClues = {{2, 1}, {2}, {1, 1}}
     * colClues = {{1, 1}, {2}, {1}, {1, 1}}
     *
     * Output: true
     *
     * Why:
     * Every row and column produces the clue that was expected.
     *
     * Example 2 - Row mismatch
     *
     * board = {
     *     {1, 0, 1}
     * }
     * rowClues = {{3}}
     * colClues = {{1}, {}, {1}}
     *
     * Output: false
     *
     * Why:
     * The row has runs [1, 1], not [3].
     *
     * Example 3 - Column mismatch
     *
     * board = {
     *     {1, 0},
     *     {1, 0}
     * }
     * rowClues = {{1}, {1}}
     * colClues = {{1}, {}}
     *
     * Output: false
     *
     * Why:
     * Column 0 has run [2], not [1].
     *
     * Edge case 1 - Empty clue for a blank line
     *
     * line = {0, 0, 0}, clue = {}
     *
     * This matches because there are no filled runs.
     *
     * Edge case 2 - One long run
     *
     * line = {1, 1, 1}, clue = {3}
     *
     * This matches because the filled block reaches the end of the line and must
     * still be counted.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A run starts when we enter a sequence of 1s.
     * 2. A run ends when we hit 0 or the line ends.
     * 3. The order of runs matters.
     * 4. An empty clue means the row or column must contain no 1s.
     * 5. We must validate both rows and columns before returning true.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Convert one row or column into its actual run lengths.
     * Compare those lengths to the expected clue.
     * Repeat for every row, then every column.
     * The first mismatch proves the board is invalid.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of a row as blocks of filled cells separated by blanks. The clue is
     * just the sizes of those blocks in order.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    A row or column clue is just the lengths of consecutive filled blocks.
     *
     * 2. What data structure does that naturally suggest?
     *    Use run counting because we compare actual runs against expected clues.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: scan each row/column and build the run list from scratch.
     *
     * 4. What repeated work should I remove?
     *    Optimized: the same scan is already optimal; write it cleanly and reuse helper logic for rows and columns.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: scan each row/column and build the run list from scratch.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Scan every row and manually count consecutive filled cells.
     * 2. Compare each row run list to its row clue.
     * 3. Scan every column the same way.
     * 4. Compare each column run list to its column clue.
     * 5. Return false on the first mismatch; otherwise return true.
     * 
     * Time Complexity: O(rows * cols) because every row and column cell is inspected.
     * Space Complexity: O(max(rows, cols)) for the temporary run list.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the 3x4 board above.
     * Row runs are [2,1], [2], and [1,1].
     * Column runs are [1,1], [2], [1], and [1,1].
     * All clues match, so return true.
     */
    public boolean bruteForce(int[][] board, int[][] rowClues, int[][] colClues) {
        // First validate rows because row clues are already aligned by row index.
        for (int row = 0; row < board.length; row++) {
            // Build the row's actual runs and compare them to the expected clue.
            if (!runsMatch(getRow(board, row), rowClues[row])) {
                return false;
            }
        }

        // Then validate columns. A column is not stored as one array, so getCol
        // copies it into a simple line that runsMatch can read.
        for (int col = 0; col < board[0].length; col++) {
            if (!runsMatch(getCol(board, col), colClues[col])) {
                return false;
            }
        }

        // If no row or column failed, the whole board satisfies the clues.
        return true;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: the same scan is already optimal; write it cleanly and reuse helper logic for rows and columns.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Use one shared helper to convert any row/column into run lengths.
     * 2. Reuse the helper for every row and column.
     * 3. Compare generated runs directly with the clues.
     * 4. Return false as soon as a clue fails.
     * 
     * Time Complexity: Lower than brute force because repeated scanning is replaced with stored state.
     * Space Complexity: O(max(rows, cols)) for generated run arrays.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the 3x4 board above.
     * Row runs are [2,1], [2], and [1,1].
     * Column runs are [1,1], [2], [1], and [1,1].
     * All clues match, so return true.
     */
    public boolean optimized(int[][] board, int[][] rowClues, int[][] colClues) {
        if (board.length != rowClues.length || board[0].length != colClues.length) {
            return false;
        }

        for (int row = 0; row < board.length; row++) {
        if (!runsMatch(getRow(board, row), rowClues[row])) {
                return false;
            }
        }
        for (int col = 0; col < board[0].length; col++) {
        if (!runsMatch(getCol(board, col), colClues[col])) {
                return false;
            }
        }
        return true;
    }

    private boolean runsMatch(int[] line, int[] clue) {
        // Store the run lengths we actually see in the row or column.
        // A nonogram clue is just this same list written in advance.
        List<Integer> runs = new ArrayList<>();
        // count tracks the length of the current block of consecutive 1s.
        // It stays at 0 while we are between filled blocks.
        int count = 0;
        // Walk left-to-right through a row, or top-to-bottom through a column.
        // The same rule works for both because each is passed in as one line.
        for (int value : line) {
        // A filled cell extends the current run by one square.
        if (value == 1) {
                count++;
            } else
        // A blank cell ends a run only if we were already counting filled cells.
        if (count > 0) {
                // Save the completed run because clues care about block sizes,
                // not the exact cell indexes where the blocks appeared.
                runs.add(count);
                // Reset so a later group of 1s becomes a separate clue number.
                count = 0;
            }
        }
        // If the line ends while we are still inside a filled block, that final
        // block must be recorded because no trailing 0 exists to close it.
        if (count > 0) {
            runs.add(count);
        }
        // The number of discovered blocks must match the number of clue entries.
        // For example, [2, 1] cannot match a line that produced only [3].
        if (runs.size() != clue.length) {
            return false;
        }
        // Compare each block length in order because nonogram clues are ordered
        // from the start of the line to the end of the line.
        for (int i = 0; i < clue.length; i++) {
        // If any actual run length differs from the expected clue length, this
        // row or column disproves the board immediately.
        if (runs.get(i) != clue[i]) {
                return false;
            }
        }
        // Every run existed in the right position with the right length.
        return true;
    }

    private int[] getRow(int[][] board, int row) {
        // Rows are already stored as arrays in the board, so returning the row
        // directly gives runsMatch the exact line it needs without copying.
        return board[row];
    }

    private int[] getCol(int[][] board, int col) {
        // Columns are vertical in the grid, but runsMatch expects a simple
        // one-dimensional line, so we copy the chosen column into an array.
        int[] result = new int[board.length];
        // Visit each row at the same column index to read the column from top
        // to bottom, which is the order column clues describe.
        for (int row = 0; row < board.length; row++) {
            // The value at board[row][col] is one cell of the column line.
            result[row] = board[row][col];
        }
        // Return the extracted column so the validation logic can stay shared
        // between row checks and column checks.
        return result;
    }

    public static void main(String[] args) {
        NonogramValidator solver = new NonogramValidator();

        int[][][] boards = {
                {
                        {1, 1, 0, 1},
                        {0, 1, 1, 0},
                        {1, 0, 0, 1}
                },
                {
                        {1, 0, 1}
                },
                {
                        {1, 0},
                        {1, 0}
                }
        };
        int[][][] rowClues = {
                {
                        {2, 1},
                        {2},
                        {1, 1}
                },
                {
                        {3}
                },
                {
                        {1},
                        {1}
                }
        };
        int[][][] colClues = {
                {
                        {1, 1},
                        {2},
                        {1},
                        {1, 1}
                },
                {
                        {1},
                        {},
                        {1}
                },
                {
                        {1},
                        {}
                }
        };

        for (int i = 0; i < boards.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + solver.bruteForce(copyGrid(boards[i]), copyGrid(rowClues[i]), copyGrid(colClues[i])));
            System.out.println("optimized:  " + solver.optimized(copyGrid(boards[i]), copyGrid(rowClues[i]), copyGrid(colClues[i])));
            System.out.println();
        }
    }

    private static int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int row = 0; row < grid.length; row++) {
            copy[row] = new int[grid[row].length];
            for (int col = 0; col < grid[row].length; col++) {
                copy[row][col] = grid[row][col];
            }
        }
        return copy;
    }
}
