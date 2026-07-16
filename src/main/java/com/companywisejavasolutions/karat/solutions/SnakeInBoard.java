package com.companywisejavasolutions.karat.solutions;

import java.util.*;

public class SnakeInBoard {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a board with walls and empty cells, find the shortest path from the snake head to a border exit.
     *
     * INPUT
     * board, startRow, startCol. # is wall, . is open.
     *
     * OUTPUT
     * Minimum number of moves to an exit, or -1.
     *
     * EXAMPLE
     * board = {{'.','.','.'}, {'.','.','#'}, {'#','.','.'}}, start = (1,1)
     * Output: 1
     * 
     * From the center, moving up reaches an edge exit in one step.
     *
     * WHAT IT MEANS
     * This is shortest path in a grid, solved with BFS.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of moving through a board to escape. The nearest exit is found by
     * exploring shorter paths before longer ones.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * The board is a grid maze.
     *
     * A cell with '.' is open.
     * A cell with '#' is blocked.
     * From one open cell, the snake can try four moves: up, down, left, right.
     *
     * An exit is any border cell, but this implementation does not count the
     * starting cell as an exit at distance 0. The snake must make at least one
     * move and then land on a border cell.
     *
     * Brute force asks the most literal question:
     *
     * "What happens if I try every possible path from here and keep the
     * shortest one that reaches the border?"
     */

    /*
     * EXAMPLES AND EDGE CASES
     *
     * Example 1 - One move to exit
     *
     * board = {
     *     {'.', '.', '.'},
     *     {'.', '.', '#'},
     *     {'#', '.', '.'}
     * }
     * start = (1, 1)
     *
     * Moving up reaches row 0 in one move.
     * Output: 1
     *
     * Example 2 - Wall blocks the direct route
     *
     * board = {
     *     {'.', '#', '.'},
     *     {'.', '.', '.'},
     *     {'#', '#', '.'}
     * }
     * start = (1, 1)
     *
     * Up is blocked, but right reaches the border in one move.
     * Output: 1
     *
     * Example 3 - Longer path
     *
     * board = {
     *     {'#', '#', '#', '#'},
     *     {'#', '.', '.', '#'},
     *     {'#', '#', '.', '.'}
     * }
     * start = (1, 1)
     *
     * The path goes right, then down, then right to the border.
     * Output: 3
     *
     * Edge case 1 - Surrounded by walls
     *
     * If all possible moves are blocked, there is no exit path.
     * Output: -1
     *
     * Edge case 2 - Start is on the border
     *
     * This code requires distance > 0 before accepting an exit, so a border
     * start only returns 0 if that rule is changed.
     *
     * Edge case 3 - Single open cell board
     *
     * There is nowhere to move, so this implementation returns -1.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    The board is a shortest-path/grid movement problem with obstacles and boundaries.
     *
     * 2. What data structure does that naturally suggest?
     *    Use BFS because nearest exit requires the fewest moves.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: try paths recursively and track the smallest valid exit path.
     *
     * 4. What repeated work should I remove?
     *    Optimized: BFS explores by distance level, so the first exit found is shortest.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Grid bounds matter before reading board[row][col].
     * 2. Walls cannot be entered.
     * 3. A path should not revisit a cell already used by that same path.
     * 4. The brute-force DFS must undo visited when backing up so other paths
     *    can use that cell later.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Brute force starts at the snake's position and recursively tries all four
     * directions. Each recursive call carries the distance traveled so far. If a
     * call lands on a border cell after at least one move, it returns that
     * distance. The caller keeps the smallest distance returned by its four
     * choices.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: try paths recursively and track the smallest valid exit path.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Start from the direct goal: find nearest exit from a board position.
     * 2. Use the plain human method: DFS all possible paths and keep the shortest exit distance.
     * 3. Check the problem rule exactly for each candidate.
     * 4. Add valid results in the requested output shape.
     * 
     * Time Complexity: Higher than optimized because this version repeats the direct work described above.
     * Space Complexity: O(n) for the answer and any direct helper state.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Sample: board = {{'.','.','.'}, {'.','.','#'}, {'#','.','.'}}, start = (1,1) Output: 1 From the center, moving up reaches an edge exit in one step.
     * Walk the records one by one and the expected result above is produced.
     */
    public int bruteForce(char[][] board, int startRow, int startCol) {

        // visited is path-specific: it prevents cycles like left-right-left-right.
        boolean[][] visited = new boolean[board.length][board[0].length];
        // Integer.MAX_VALUE means "no exit was found from this branch."
        int answer = dfsExit(board, startRow, startCol, visited, 0);
        return answer == Integer.MAX_VALUE ? -1 : answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: BFS explores by distance level, so the first exit found is shortest.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find nearest exit from a board position.
     * 2. Remove repeated work: BFS by distance so first exit found is shortest.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: board = {{'.','.','.'}, {'.','.','#'}, {'#','.','.'}}, start = (1,1) Output: 1 From the center, moving up reaches an edge exit in one step.
     * Walk the records one by one and the expected result above is produced.
     */
    public int optimized(char[][] board, int startRow, int startCol) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        boolean[][] visited = new boolean[board.length][board[0].length];
        // Queue is used for BFS because it processes nodes in distance/order layers.
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {startRow, startCol, 0});
        visited[startRow][startCol] = true;
                while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int row = cell[0];
            int col = cell[1];
            int distance = cell[2];
        if (distance > 0 && (row == 0 || col == 0 || row == board.length - 1 || col == board[0].length - 1)) {
                return distance;
            }
            for (int[] direction : directions) {
                int nextRow = row + direction[0];
                int nextCol = col + direction[1];
                if (nextRow >= 0 && nextRow < board.length && nextCol >= 0 && nextCol < board[0].length
                && !visited[nextRow][nextCol] && board[nextRow][nextCol] != '#') {
                    visited[nextRow][nextCol] = true;
                    queue.offer(new int[] {nextRow, nextCol, distance + 1});
                }
            }
        }
        return -1;
    }

    public boolean canMoveFreely(char[][] board, int startRow, int startCol) {
        // Four direction offsets represent the only legal grid moves: down, up, right, and left.
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        // Try each neighboring cell around the starting position.
        for (int[] direction : directions) {
            // Apply the row offset to move vertically when needed.
            int nextRow = startRow + direction[0];
            // Apply the column offset to move horizontally when needed.
            int nextCol = startCol + direction[1];
            // A move is available only if the neighbor is inside the board and is not a wall.
            if (nextRow >= 0 && nextRow < board.length && nextCol >= 0 && nextCol < board[0].length
            && board[nextRow][nextCol] != '#') {
                // Finding even one open neighbor means the snake is not trapped at the start.
                return true;
            }
        }
        // None of the four neighbors was both in bounds and open.
        return false;
    }

    private int dfsExit(char[][] board, int row, int col, boolean[][] visited, int distance) {
        // Before reading board[row][col], make sure row and col are valid coordinates.
        // Out-of-bounds means this branch stepped off the maze rather than onto a real exit cell.
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
            return Integer.MAX_VALUE;
        }
        // A wall cannot be entered.
        // A visited cell would create a cycle in this path, so it is also blocked for this branch.
        if (board[row][col] == '#' || visited[row][col]) {
            return Integer.MAX_VALUE;
        }
        // A border cell is an exit only after at least one move.
        // That distance check preserves this implementation's rule that the starting cell is not a zero-step exit.
        if (distance > 0 && (row == 0 || col == 0 || row == board.length - 1 || col == board[0].length - 1)) {
            return distance;
        }

        // Mark this cell so the current path does not immediately walk back into it and loop.
        visited[row][col] = true;
        // Start with "no answer found"; any real path distance will be smaller than this sentinel.
        int best = Integer.MAX_VALUE;
        // Try moving down and keep the better of the current best and that branch's result.
        best = Math.min(best, dfsExit(board, row + 1, col, visited, distance + 1));
        // Try moving up for the same reason: the shortest exit may be above us.
        best = Math.min(best, dfsExit(board, row - 1, col, visited, distance + 1));
        // Try moving right because grid shortest-path problems must consider all legal neighbors.
        best = Math.min(best, dfsExit(board, row, col + 1, visited, distance + 1));
        // Try moving left to complete the four-direction search.
        best = Math.min(best, dfsExit(board, row, col - 1, visited, distance + 1));
        // Backtrack so sibling paths can consider this cell independently.
        // Without this reset, one attempted route would incorrectly block later routes.
        visited[row][col] = false;
        // Return the smallest exit distance found from this cell, or MAX_VALUE if none worked.
        return best;
    }

    public static void main(String[] args) {
        SnakeInBoard snake = new SnakeInBoard();

        char[][][] boards = {
                {
                        {'.', '.', '.'},
                        {'.', '.', '#'},
                        {'#', '.', '.'}
                },
                {
                        {'.', '#', '.'},
                        {'.', '.', '.'},
                        {'#', '#', '.'}
                },
                {
                        {'#', '#', '#', '#'},
                        {'#', '.', '.', '#'},
                        {'#', '#', '.', '.'}
                }
        };
        int[] startRows = {1, 1, 1};
        int[] startCols = {1, 1, 1};

        for (int i = 0; i < boards.length; i++) {
            System.out.println("Sample " + (i + 1) + ":");
            System.out.println("board = " + formatGrid(boards[i]));
            System.out.println("start = (" + startRows[i] + ", " + startCols[i] + ")");
            System.out.println("bruteForce = " + snake.bruteForce(copyGrid(boards[i]), startRows[i], startCols[i]));
            System.out.println("optimized = " + snake.optimized(copyGrid(boards[i]), startRows[i], startCols[i]));
            System.out.println();
        }
    }

    private static char[][] copyGrid(char[][] grid) {
        char[][] copy = new char[grid.length][grid[0].length];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                copy[row][col] = grid[row][col];
            }
        }
        return copy;
    }

    private static String formatGrid(char[][] grid) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int row = 0; row < grid.length; row++) {
            if (row > 0) {
                builder.append(", ");
            }
            builder.append(Arrays.toString(grid[row]));
        }
        builder.append("]");
        return builder.toString();
    }
}
