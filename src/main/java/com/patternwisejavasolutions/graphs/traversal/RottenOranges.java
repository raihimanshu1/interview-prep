package com.patternwisejavasolutions.graphs.traversal;

import java.util.LinkedList;
import java.util.Queue;

public class RottenOranges {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Grid has 0 empty, 1 fresh orange, 2 rotten orange.
     * Every minute, rotten oranges rot adjacent fresh oranges.
     * Return minutes until all fresh are rotten, or -1.
     *
     * Sample Input:
     * grid = [[2,1,1],[1,1,0],[0,1,1]]
     *
     * Sample Output:
     * 4
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Imagine every rotten orange gives infection to its neighbors at the same bell ring.
     * Oranges that become rotten at minute 1 should not spread until minute 2.
     * That "one ring at a time" behavior is exactly BFS level order.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Simulate the clock minute by minute.
     * On each minute, scan the grid and rot fresh oranges next to oranges that were already rotten at the start of that minute.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Minute 0: grid starts with one rotten orange.
     * Minute 1: scan the full grid and mark adjacent fresh oranges as newly rotten.
     * Minute 2: scan again; oranges next to those newly rotten ones now rot.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Repeatedly scan the grid minute by minute.
     * 2. Mark fresh oranges next to rotten ones.
     * 3. Stop when no fresh remains or no progress.
     * Time Complexity: O((mn)^2)
     * Space Complexity: O(mn)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(int[][] grid) {
        int minutes = 0;
        int[][] directions = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        while (hasFresh(grid)) {
            boolean changedThisMinute = false;

            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[0].length; col++) {
                    if (grid[row][col] != 2) {
                        continue;
                    }

                    for (int[] direction : directions) {
                        int nextRow = row + direction[0];
                        int nextCol = col + direction[1];

                        if (isInside(grid, nextRow, nextCol) && grid[nextRow][nextCol] == 1) {
                            // Mark as 3 first so it does not rot another orange in the same minute.
                            grid[nextRow][nextCol] = 3;
                            changedThisMinute = true;
                        }
                    }
                }
            }

            if (!changedThisMinute) {
                return -1;
            }

            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[0].length; col++) {
                    if (grid[row][col] == 3) {
                        grid[row][col] = 2;
                    }
                }
            }

            minutes++;
        }

        return minutes;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is repeated full-grid scans.
     * BFS keeps only the frontier: oranges that are rotten right now and ready to spread.
     * Processing one queue level equals one minute because all those oranges spread together.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Queue starts with all rotten oranges.
     * Process queue size = current minute oranges.
     * Any fresh neighbor becomes rotten and enters queue for next minute.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Count fresh oranges and enqueue rotten oranges.
     * 2. BFS by levels.
     * 3. Each level is one minute.
     * 4. Reduce fresh when orange rots.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(int[][] grid) {
        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == 2) {
                    queue.offer(new int[] { row, col });
                } else if (grid[row][col] == 1) {
                    fresh++;
                }
            }
        }

        int minutes = 0;
        int[][] directions = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size(); // Only these oranges spread in the current minute.
            minutes++;

            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();

                for (int[] direction : directions) {
                    int nextRow = cell[0] + direction[0];
                    int nextCol = cell[1] + direction[1];

                    if (!isInside(grid, nextRow, nextCol) || grid[nextRow][nextCol] != 1) {
                        continue;
                    }

                    grid[nextRow][nextCol] = 2; // Mark immediately so it is not enqueued twice.
                    fresh--;
                    queue.offer(new int[] { nextRow, nextCol });
                }
            }
        }

        return fresh == 0 ? minutes : -1;
    }

    private boolean hasFresh(int[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == 1) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isInside(int[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
    }
}
