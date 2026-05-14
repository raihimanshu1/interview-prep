import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NQueens {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: n = 4
     * Sample Output: two valid boards
     *
     * Input: n = 4
     * Output contains all boards where 4 queens do not attack each other.
     * Queens attack same row, same column, and diagonals.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Place one queen per row.
     * For each row, try every column.
     * Only place a queen if no previous queen attacks that square.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Treat each row like one turn. Try a queen in every column, then scan the
     * queens already placed above it to see whether the square is attacked.
     * If a later row gets stuck, remove the queen and try the next column.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * Row 0: place queen at col 1.
     * Row 1: try columns, skip attacked squares.
     * If stuck later, remove the queen and try another column.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Build board row by row.
     * 2. For each column, scan previous rows to check safety.
     * 3. Place queen, recurse, undo.
     * Time Complexity: O(n!)
     * Space Complexity: O(n^2) board
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * Safety is checked by scanning the board each time.
     */
    public List<List<String>> bruteForce(int n) {
        char[][] board = createBoard(n);
        List<List<String>> answer = new ArrayList<>();
        solveSlow(0, board, answer);
        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Safety checks become faster if we remember used columns and diagonals.
     * Main diagonal can be represented by row - col.
     * Anti diagonal can be represented by row + col.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * If queen is at row 0 col 1:
     * column 1 is blocked.
     * row - col = -1 diagonal is blocked.
     * row + col = 1 diagonal is blocked.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Keep boolean arrays for columns and diagonals.
     * 2. Try each column in current row.
     * 3. Skip if column or diagonal is already used.
     * 4. Mark, place, recurse, undo.
     * Time Complexity: O(n!)
     * Space Complexity: O(n^2)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * The boolean arrays remember attacked columns and diagonals in O(1).
     */
    public List<List<String>> optimized(int n) {
        char[][] board = createBoard(n);
        List<List<String>> answer = new ArrayList<>();
        boolean[] usedColumn = new boolean[n];
        boolean[] usedMainDiagonal = new boolean[2 * n];
        boolean[] usedAntiDiagonal = new boolean[2 * n];
        solveFast(0, board, answer, usedColumn, usedMainDiagonal, usedAntiDiagonal);
        return answer;
    }


    private char[][] createBoard(int n) {
        char[][] board = new char[n][n];

        for (char[] row : board) {
            Arrays.fill(row, '.');
        }

        return board;
    }

    private void solveSlow(int row, char[][] board, List<List<String>> answer) {
        if (row == board.length) {
            // One queen was placed in every row, so this board is complete.
            answer.add(toList(board));
            return;
        }

        for (int col = 0; col < board.length; col++) {
            if (!isSafe(board, row, col)) {
                continue;
            }

            // Choose this square, solve the next row, then erase it.
            board[row][col] = 'Q';
            solveSlow(row + 1, board, answer);
            board[row][col] = '.';
        }
    }

    private boolean isSafe(char[][] board, int row, int col) {
        for (int prevRow = 0; prevRow < row; prevRow++) {
            for (int prevCol = 0; prevCol < board.length; prevCol++) {
                if (board[prevRow][prevCol] == 'Q') {
                    boolean sameColumn = prevCol == col;
                    boolean sameDiagonal = Math.abs(prevRow - row) == Math.abs(prevCol - col);

                    if (sameColumn || sameDiagonal) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void solveFast(int row, char[][] board, List<List<String>> answer, boolean[] usedColumn, boolean[] usedMainDiagonal, boolean[] usedAntiDiagonal) {
        if (row == board.length) {
            // All rows are filled without conflicts.
            answer.add(toList(board));
            return;
        }

        int n = board.length;

        for (int col = 0; col < n; col++) {
            int mainDiagonal = row - col + n;
            int antiDiagonal = row + col;

            if (usedColumn[col] || usedMainDiagonal[mainDiagonal] || usedAntiDiagonal[antiDiagonal]) {
                continue;
            }

            // Mark this queen's column and both diagonals before exploring below.
            board[row][col] = 'Q';
            usedColumn[col] = true;
            usedMainDiagonal[mainDiagonal] = true;
            usedAntiDiagonal[antiDiagonal] = true;

            solveFast(row + 1, board, answer, usedColumn, usedMainDiagonal, usedAntiDiagonal);

            // Undo the board and attack markers so the next column starts clean.
            board[row][col] = '.';
            usedColumn[col] = false;
            usedMainDiagonal[mainDiagonal] = false;
            usedAntiDiagonal[antiDiagonal] = false;
        }
    }

    private List<String> toList(char[][] board) {
        List<String> result = new ArrayList<>();

        for (char[] row : board) {
            result.add(new String(row));
        }

        return result;
    }
}
