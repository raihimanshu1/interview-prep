package wellsfargo.solutions;

public class WordSearch {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return true if a word can be formed in a letter board by moving up, down,
     * left, or right. A cell cannot be reused in the same word path.
     *
     * Sample Input: board = [[A,B,C,E],[S,F,C,S],[A,D,E,E]], word = "ABCCED"
     * Sample Output: true
     */

    /*
     * WHAT TO NOTICE FIRST
     *
     * In the sample, "ABCCED" is formed by stepping from one neighboring cell
     * to the next. The important rule is that a letter cell can be used only
     * once in that attempt, so the path must remember where it has already been.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * This is like tracing a word in a grid puzzle. At each letter, we choose a
     * neighboring cell for the next letter. If a path fails, we undo the choice
     * and try another direction. That is backtracking.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Start from every cell and use a separate visited array to remember which
     * cells are already used in the current path. This is the literal "try a
     * path, and if it fails, back up" idea.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Try every cell as a starting point.
     * 2. If current cell matches current word letter, mark it visited.
     * 3. Recursively try four directions.
     * 4. Unmark before returning so other paths can use it.
     *
     * Time Complexity: O(mn * 4^L)
     * Space Complexity: O(mn + L)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Start at A at (0,0), move right to B, then right to C.
     * From that C, move down to the next C, then down to E, then left to D.
     * Now every letter in "ABCCED" has been matched, so return true.
     */
    public boolean bruteForce(char[][] board, String word) {
        boolean[][] visited = new boolean[board.length][board[0].length];

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (searchWithVisited(board, word, row, col, 0, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is the separate visited grid. We can mark the
     * board cell itself with a temporary character while exploring, then restore
     * the original letter before backing out.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Try DFS from every cell.
     * 2. Reject out-of-bounds cells and wrong letters.
     * 3. Temporarily mark the current cell as used.
     * 4. Explore four directions.
     * 5. Restore the cell before returning.
     *
     * Time Complexity: O(mn * 4^L)
     * Space Complexity: O(L)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * When A matches, replace it with '#'. This blocks reuse in the current path.
     * After trying neighbors, restore A.
     */
    public boolean optimized(char[][] board, String word) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (searchInPlace(board, word, row, col, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean searchWithVisited(char[][] board, String word, int row, int col, int index, boolean[][] visited) {
        if (index == word.length()) {
            // We matched every character in order.
            return true;
        }
        if (row < 0 || row == board.length || col < 0 || col == board[0].length) {
            return false;
        }
        if (visited[row][col] || board[row][col] != word.charAt(index)) {
            return false;
        }

        visited[row][col] = true;
        boolean found = searchWithVisited(board, word, row + 1, col, index + 1, visited)
                || searchWithVisited(board, word, row - 1, col, index + 1, visited)
                || searchWithVisited(board, word, row, col + 1, index + 1, visited)
                || searchWithVisited(board, word, row, col - 1, index + 1, visited);
        // Undo the choice so a different path can use this cell later.
        visited[row][col] = false;
        return found;
    }

    private boolean searchInPlace(char[][] board, String word, int row, int col, int index) {
        if (index == word.length()) {
            return true;
        }
        if (row < 0 || row == board.length || col < 0 || col == board[0].length) {
            return false;
        }
        if (board[row][col] != word.charAt(index)) {
            return false;
        }

        char original = board[row][col];
        // Marking prevents this path from reusing the same cell.
        board[row][col] = '#';
        boolean found = searchInPlace(board, word, row + 1, col, index + 1)
                || searchInPlace(board, word, row - 1, col, index + 1)
                || searchInPlace(board, word, row, col + 1, index + 1)
                || searchInPlace(board, word, row, col - 1, index + 1);
        // Restore the board so other starting cells see the original letters.
        board[row][col] = original;
        return found;
    }
}
