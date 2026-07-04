
package com.patternwisejavasolutions.graphs.bfsShortestPath;
import java.util.LinkedList;
import java.util.Queue;

public class ShortestPathInBinaryMatrix {

    /*
     * PROBLEM IN SIMPLE WORDS
     * In a 0/1 grid, move from top-left to bottom-right through only 0 cells. You may move in 8
     * directions. Return the shortest path length, or -1 if blocked.
     *
     * Sample Input: grid = [[0,1],[1,0]]
     * Sample Output: 2
     *
     * SCHOOL-LEVEL INTUITION
     * This is like walking on safe squares. Since every move costs 1 step, BFS explores all paths
     * with 1 step, then 2 steps, and so on. The first time we reach the end is shortest.
     * Diagonal moves count the same as straight moves, so they belong in the same BFS neighbor list.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Try all possible paths with DFS and keep the shortest successful one.
     *
     * BRUTE FORCE ALGORITHM
     * 1. If start or end is blocked, return -1.
     * 2. From the current cell, try all 8 directions.
     * 3. Mark cells visited so the current path does not loop.
     * 4. Track the smallest distance that reaches the end.
     *
     * BRUTE FORCE DRY RUN
     * [[0,1],[1,0]]
     * start (0,0), diagonal to (1,1), distance 2 -> answer 2
     *
     * Time Complexity: exponential in open cells
     * Space Complexity: O(n^2)
     */
    public int bruteForce(int[][] grid) {
        int n = grid.length;

        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) {
            return -1;
        }

        boolean[][] visited = new boolean[n][n];
        int answer = dfs(grid, 0, 0, visited, 1);
        return answer == Integer.MAX_VALUE ? -1 : answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain is trying many complete paths that overlap.
     * BFS keeps the shortest-known wavefront instead: once a cell is reached, reaching it later
     * cannot give a shorter path because every move costs one.
     *
     * OPTIMIZED ALGORITHM
     * 1. If start or end is blocked, return -1.
     * 2. Put start cell in a queue with distance 1.
     * 3. Process level by level.
     * 4. Add valid unvisited 0 neighbors in all 8 directions.
     * 5. Return distance when bottom-right is reached.
     *
     * OPTIMIZED DRY RUN
     * [[0,0,0],[1,1,0],[1,1,0]]
     * BFS visits top row, then right column, reaches end at distance 4.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n^2)
     */
    public int optimized(int[][] grid) {
        int n = grid.length;

        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) {
            return -1;
        }

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[n][n];
        queue.offer(new int[] {0, 0});
        visited[0][0] = true;
        int pathLength = 1;

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // Every cell in this level uses exactly pathLength cells.

            for (int i = 0; i < levelSize; i++) {
                int[] current = queue.poll();
                int row = current[0];
                int col = current[1];

                if (row == n - 1 && col == n - 1) {
                    return pathLength;
                }

                for (int[] direction : DIRECTIONS) {
                    int nextRow = row + direction[0];
                    int nextCol = col + direction[1];

                    if (isValid(grid, nextRow, nextCol, visited)) {
                        visited[nextRow][nextCol] = true; // Mark when queued to avoid duplicate longer routes.
                        queue.offer(new int[] {nextRow, nextCol});
                    }
                }
            }

            pathLength++;
        }

        return -1;
    }

    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    private int dfs(int[][] grid, int row, int col, boolean[][] visited, int distance) {
        int n = grid.length;

        if (row == n - 1 && col == n - 1) {
            return distance;
        }

        visited[row][col] = true; // Current DFS path uses this cell; do not step on it again.
        int best = Integer.MAX_VALUE;

        for (int[] direction : DIRECTIONS) {
            int nextRow = row + direction[0];
            int nextCol = col + direction[1];

            if (isValid(grid, nextRow, nextCol, visited)) {
                best = Math.min(best, dfs(grid, nextRow, nextCol, visited, distance + 1));
            }
        }

        visited[row][col] = false; // Backtrack so a different route can try this cell.
        return best;
    }

    private boolean isValid(int[][] grid, int row, int col, boolean[][] visited) {
        int n = grid.length;
        return row >= 0 && row < n
            && col >= 0 && col < n
            && grid[row][col] == 0
            && !visited[row][col];
    }
}
