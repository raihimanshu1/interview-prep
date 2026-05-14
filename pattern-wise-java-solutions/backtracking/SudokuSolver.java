public class SudokuSolver {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Fill a 9x9 Sudoku board so every row, column, and 3x3 box contains digits
     * 1 through 9 exactly once. Empty cells are marked with '.'.
     *
     * Sample Input:
     * board has dots and fixed digits, for example row 1 = "53..7...."
     *
     * Sample Output:
     * the same board filled with valid digits
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Pick an empty square and try digits 1 to 9. If a digit breaks row, column,
     * or box rules, do not place it. If later choices fail, erase and try again.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Use backtracking and validate a digit by scanning its row, column, and box
     * every time we want to place it.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Find the first empty cell.
     * 2. Try digits '1' to '9'.
     * 3. For each digit, scan row, column, and 3x3 box to check validity.
     * 4. Place the digit and solve the rest.
     * 5. If the rest fails, erase the digit and try the next one.
     *
     * Time Complexity: O(9^emptyCells)
     * Space Complexity: O(emptyCells) recursion depth
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * At the first empty cell, try 1. If row, column, or box already has 1, skip.
     * Try the next valid digit and continue. If a later cell has no valid digit,
     * come back and change this cell.
     */
    public void bruteForce(char[][] board) {
        solveByScanning(board);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The repeated scanning is wasted work. We can remember which digits already
     * exist in each row, column, and box using boolean arrays.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build rowUsed, colUsed, and boxUsed from the starting board.
     * 2. Find empty cells through backtracking.
     * 3. For each digit, check the three boolean arrays in O(1).
     * 4. Place the digit, mark it used, and recurse.
     * 5. Undo the marks if the path fails.
     *
     * Time Complexity: O(9^emptyCells)
     * Space Complexity: O(1) because Sudoku size is fixed
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * If row 0 already has 5, rowUsed[0][5] is true.
     * When trying 5 in another cell of row 0, we reject it immediately without
     * scanning the whole row.
     */
    public void optimized(char[][] board) {
        boolean[][] rowUsed = new boolean[9][10];
        boolean[][] colUsed = new boolean[9][10];
        boolean[][] boxUsed = new boolean[9][10];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {
                    continue;
                }

                int digit = board[row][col] - '0';
                int box = getBox(row, col);
                rowUsed[row][digit] = true;
                colUsed[col][digit] = true;
                boxUsed[box][digit] = true;
            }
        }

        solveWithMemory(board, rowUsed, colUsed, boxUsed);
    }

    private boolean solveByScanning(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] != '.') {
                    continue;
                }

                for (char digit = '1'; digit <= '9'; digit++) {
                    if (!isValid(board, row, col, digit)) {
                        continue;
                    }

                    // Choose this digit for the first empty cell and solve the rest.
                    board[row][col] = digit;
                    if (solveByScanning(board)) {
                        return true;
                    }

                    // Undo the guess so the next digit can be tried cleanly.
                    board[row][col] = '.';
                }

                return false;
            }
        }

        return true;
    }

    private boolean solveWithMemory(char[][] board, boolean[][] rowUsed, boolean[][] colUsed, boolean[][] boxUsed) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] != '.') {
                    continue;
                }

                int box = getBox(row, col);
                for (int digit = 1; digit <= 9; digit++) {
                    if (rowUsed[row][digit] || colUsed[col][digit] || boxUsed[box][digit]) {
                        continue;
                    }

                    // The three memory tables say this digit is currently legal here.
                    board[row][col] = (char) ('0' + digit);
                    rowUsed[row][digit] = true;
                    colUsed[col][digit] = true;
                    boxUsed[box][digit] = true;

                    if (solveWithMemory(board, rowUsed, colUsed, boxUsed)) {
                        return true;
                    }

                    // Undo both the board value and the memory marks for backtracking.
                    board[row][col] = '.';
                    rowUsed[row][digit] = false;
                    colUsed[col][digit] = false;
                    boxUsed[box][digit] = false;
                }

                return false;
            }
        }

        return true;
    }

    private boolean isValid(char[][] board, int row, int col, char digit) {
        for (int index = 0; index < 9; index++) {
            if (board[row][index] == digit || board[index][col] == digit) {
                return false;
            }
        }

        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;

        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (board[r][c] == digit) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getBox(int row, int col) {
        return (row / 3) * 3 + col / 3;
    }
}
