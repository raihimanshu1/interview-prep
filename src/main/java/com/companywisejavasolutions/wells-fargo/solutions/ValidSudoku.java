package com.companywisejavasolutions.wellsfargo.solutions;

import java.util.HashSet;
import java.util.Set;

public class ValidSudoku {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Check whether a 9 x 9 Sudoku board is valid. Filled cells must not repeat
     * the same digit in any row, column, or 3 x 3 box. Empty cells are '.'.
     *
     * Sample Input:
     * A board where the first row is ['5','3','.','.','7','.','.','.','.']
     *
     * Sample Output:
     * true, if no filled digit breaks Sudoku rules.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Sudoku has three rule-checking places: rows, columns, and boxes. A board
     * is valid only when every filled number is unique inside each of those
     * places.
     *
     * Hashing fits because a set can remember what digits have already appeared
     * in one row, column, or box.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For every filled cell, look through its row, its column, and its 3 x 3 box
     * to see if the same digit appears somewhere else.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Visit every cell.
     * 2. Skip empty cells.
     * 3. Count the same digit in its row, column, and box.
     * 4. If any count is more than one, return false.
     *
     * Time Complexity: O(81 * 27), which is constant for a 9 x 9 board
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * If board[0][0] is '5', scan row 0, column 0, and top-left box.
     * If another '5' appears in any of those places, the board is invalid.
     */
    public boolean bruteForce(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] != '.' && hasDuplicateAroundCell(board, row, col)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasDuplicateAroundCell(char[][] board, int row, int col) {
        char value = board[row][col];

        for (int otherCol = 0; otherCol < 9; otherCol++) {
            if (otherCol != col && board[row][otherCol] == value) {
                return true;
            }
        }

        for (int otherRow = 0; otherRow < 9; otherRow++) {
            if (otherRow != row && board[otherRow][col] == value) {
                return true;
            }
        }

        int boxStartRow = (row / 3) * 3;
        int boxStartCol = (col / 3) * 3;

        for (int r = boxStartRow; r < boxStartRow + 3; r++) {
            for (int c = boxStartCol; c < boxStartCol + 3; c++) {
                if ((r != row || c != col) && board[r][c] == value) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is scanning row, column, and box again for each
     * cell. Instead, record every rule we have already seen.
     *
     * For digit '5' at row 0 and col 1, we store strings like "row0:5",
     * "col1:5", and "box0-0:5". If any is already present, a rule repeats.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Create a HashSet of seen rules.
     * 2. Visit each filled cell.
     * 3. Build row, column, and box keys for that digit.
     * 4. If any key already exists, return false.
     * 5. Otherwise, add the keys.
     *
     * Time Complexity: O(81)
     * Space Complexity: O(81)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * First '5' in row 0 adds row0:5.
     * Another '5' in row 0 tries to add row0:5 again.
     * That duplicate key proves the board is invalid.
     */
    public boolean optimized(char[][] board) {
        Set<String> seen = new HashSet<>();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                char value = board[row][col];

                if (value == '.') {
                    continue;
                }

                String rowKey = "row" + row + ":" + value;
                String colKey = "col" + col + ":" + value;
                String boxKey = "box" + (row / 3) + "-" + (col / 3) + ":" + value;

                // add() returns false when the same rule key was already seen.
                if (!seen.add(rowKey) || !seen.add(colKey) || !seen.add(boxKey)) {
                    return false;
                }
            }
        }

        return true;
    }
}

