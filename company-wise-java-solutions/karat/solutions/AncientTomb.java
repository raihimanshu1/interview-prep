package karat.solutions;

import java.util.*;

public class AncientTomb {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a tomb grid with start S, treasure T, open cells, and walls, return shortest path length from S to T.
     *
     * INPUT
     * char grid with S, T, ., #.
     *
     * OUTPUT
     * Minimum steps, or -1.
     *
     * EXAMPLE
     * tomb = {{'S','.','.'}, {'#','#','.'}, {'.','.','T'}}
     * Output: 4
     * 
     * The path must go around the wall to reach T.
     *
     * WHAT IT MEANS
     * Run BFS from S until T is reached.
     */
    /*
     * IN-DEPTH EXPLANATION
     *
     * Think of the tomb as a maze drawn on graph paper. From any open square, we
     * can walk one step up, down, left, or right. We cannot walk through walls,
     * and we cannot walk outside the grid.
     *
     * The problem asks for the shortest path from S to T. The brute-force way is
     * very literal: try every possible path from the start, remember the length
     * of each path that reaches the treasure, and keep the smallest length.
     *
     * What to know before solving:
     *
     * 1. 'S' is the starting cell.
     * 2. 'T' is the treasure cell we want to reach.
     * 3. '#' is a wall and cannot be entered.
     * 4. '.' is open space.
     * 5. A path must avoid revisiting the same cell during that path, or it can loop forever.
     *
     * What we do to solve:
     *
     * First find S. Then recursively try all four directions. Each recursive call
     * represents one possible next step. If a branch hits a wall, leaves the grid,
     * or revisits a cell, that branch is abandoned. If a branch reaches T, its
     * distance becomes a candidate answer.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - Must walk around walls
     *
     * tomb = {
     *     {'S', '.', '.'},
     *     {'#', '#', '.'},
     *     {'.', '.', 'T'}
     * }
     *
     * Output:
     * 4
     *
     * Why:
     * The path goes right, right, down, down.
     *
     * Example 2 - Treasure next to start
     *
     * tomb = {
     *     {'S', 'T'},
     *     {'.', '#'}
     * }
     *
     * Output:
     * 1
     *
     * Why:
     * One move right reaches the treasure.
     *
     * Example 3 - Multiple routes
     *
     * tomb = {
     *     {'S', '.', '.'},
     *     {'.', '#', '.'},
     *     {'.', '.', 'T'}
     * }
     *
     * Output:
     * 4
     *
     * Why:
     * There are routes around the wall, and the shortest one uses four moves.
     *
     * Edge case 1 - No start
     *
     * tomb = {
     *     {'.', 'T'}
     * }
     *
     * Output:
     * -1
     *
     * Why:
     * There is nowhere to begin.
     *
     * Edge case 2 - Treasure blocked
     *
     * tomb = {
     *     {'S', '#', 'T'}
     * }
     *
     * Output:
     * -1
     *
     * Why:
     * The wall prevents any path from S to T.
    */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. The search must start from S.
     * 2. Walls cannot be crossed.
     * 3. The answer is the shortest number of moves to T.
     * 4. A path should not reuse the same cell, or it may loop forever.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * First find S.
     * Then try every possible path from S using DFS.
     * Whenever a path reaches T, return that path length.
     * Keep the smallest reachable length, or return -1 if no path works.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Scan the grid to find the start cell S.
     * 2. If S does not exist, return -1.
     * 3. Start a DFS from S with distance 0.
     * 4. For each cell, reject it if it is outside the grid, a wall, or already used in this path.
     * 5. If the cell is T, return the current distance.
     * 6. Mark the cell as visited for the current path.
     * 7. Recursively try down, up, right, and left.
     * 8. Unmark the cell before returning so other paths can use it.
     * 9. Return the smallest distance found, or -1 if no path reaches T.
     *
     * Time Complexity: Exponential in the number of open cells because many simple paths can be explored.
     * Space Complexity: O(rows * cols) for visited cells and recursion depth.
     */
    public int bruteForce(char[][] tomb) {

        // The brute-force search needs a concrete place to begin.
        int[] start = find(tomb, 'S');
        if (start[0] == -1) {
            return -1;
        }

        boolean[][] visited = new boolean[tomb.length][tomb[0].length];
        // Integer.MAX_VALUE is the helper's way of saying "this branch found no treasure."
        int answer = searchTombSlowly(tomb, start[0], start[1], visited, 0);
        return answer == Integer.MAX_VALUE ? -1 : answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: use BFS/DFS with visited state so each reachable cell is handled once.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find shortest path from S to treasure T.
     * 2. Remove repeated work: BFS from S so the first treasure distance is shortest.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Sample: tomb = {{'S','.','.'}, {'#','#','.'}, {'.','.','T'}} Output: 4 The path must go around the wall to reach T.
     * Walk the records one by one and the expected result above is produced.
     */
    public int optimized(char[][] tomb) {
        int[] start = find(tomb, 'S');
        if (start[0] == -1) {
            return -1;
        }
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        boolean[][] visited = new boolean[tomb.length][tomb[0].length];
        // Queue is used for BFS because it processes nodes in distance/order layers.
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {start[0], start[1], 0});
        visited[start[0]][start[1]] = true;
                while (!queue.isEmpty()) {
            int[] cell = queue.poll();
        if (tomb[cell[0]][cell[1]] == 'T') {
                return cell[2];
            }
            for (int[] direction : directions) {
                int row = cell[0] + direction[0];
                int col = cell[1] + direction[1];
                if (row >= 0 && row < tomb.length && col >= 0 && col < tomb[0].length
                && !visited[row][col] && tomb[row][col] != '#') {
                    visited[row][col] = true;
                    queue.offer(new int[] {row, col, cell[2] + 1});
                }
            }
        }
        return -1;
    }

    private int[] find(char[][] grid, char target) {
        // Search rows from top to bottom because the grid is stored as row arrays.
        for (int row = 0; row < grid.length; row++) {
            // Search columns left to right inside the current row.
            for (int col = 0; col < grid[0].length; col++) {
        // When the requested marker is found, return its coordinates so the
        // caller can begin the path search from that exact square.
        if (grid[row][col] == target) {
                    return new int[] {row, col};
                }
            }
        }
        // [-1, -1] is a clear "not found" coordinate, used by callers to return
        // -1 when the tomb has no start or requested marker.
        return new int[] {-1, -1};
    }

    private int searchTombSlowly(char[][] tomb, int row, int col, boolean[][] visited, int distance) {
        // Leaving the grid means this path is invalid.
        if (row < 0 || row >= tomb.length || col < 0 || col >= tomb[0].length) {
            return Integer.MAX_VALUE;
        }
        // Walls cannot be crossed, and revisiting a cell would create a loop.
        if (tomb[row][col] == '#' || visited[row][col]) {
            return Integer.MAX_VALUE;
        }
        // The first time this branch reaches treasure, its current distance is a candidate answer.
        if (tomb[row][col] == 'T') {
            return distance;
        }

        // Mark this cell as used for the current route so recursive calls do
        // not circle back and count an endless loop as a path.
        visited[row][col] = true;
        // Start with "no answer yet"; each direction can replace this with a
        // real distance if that branch eventually reaches the treasure.
        int best = Integer.MAX_VALUE;
        // Try every physical direction from this cell and keep the shortest successful branch.
        // Moving down models taking one step to the next row.
        best = Math.min(best, searchTombSlowly(tomb, row + 1, col, visited, distance + 1));
        // Moving up models taking one step to the previous row.
        best = Math.min(best, searchTombSlowly(tomb, row - 1, col, visited, distance + 1));
        // Moving right models taking one step to the next column.
        best = Math.min(best, searchTombSlowly(tomb, row, col + 1, visited, distance + 1));
        // Moving left models taking one step to the previous column.
        best = Math.min(best, searchTombSlowly(tomb, row, col - 1, visited, distance + 1));
        // Backtrack so a different route can pass through this cell later.
        visited[row][col] = false;
        // Return the best treasure distance found from this cell, or
        // Integer.MAX_VALUE if every direction failed.
        return best;
    }

    public static void main(String[] args) {
        AncientTomb solver = new AncientTomb();

        char[][][] samples = {
                {
                        {'S', '.', '.'},
                        {'#', '#', '.'},
                        {'.', '.', 'T'}
                },
                {
                        {'S', 'T'},
                        {'.', '#'}
                },
                {
                        {'S', '#', 'T'}
                }
        };

        for (int i = 0; i < samples.length; i++) {
            char[][] tomb = samples[i];
            System.out.println("Sample " + (i + 1) + ": tomb = " + formatGrid(tomb));
            System.out.println("bruteForce = " + solver.bruteForce(copyGrid(tomb)));
            System.out.println("optimized = " + solver.optimized(copyGrid(tomb)));
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
