

package com.companywisejavasolutions.ebay.solutions;
import java.util.ArrayDeque;
import java.util.Queue;

public class NearestExitFromEntranceInMaze {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * In a maze, move up/down/left/right through empty cells. Return the fewest
     * steps from entrance to any border exit that is not the entrance.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Breadth-first search expands one step at a time, so the first exit reached
     * is automatically the nearest exit.
     */
    public int nearestExit(char[][] maze, int[] entrance) {
        int rows = maze.length;
        int cols = maze[0].length;
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {entrance[0], entrance[1]});
        maze[entrance[0]][entrance[1]] = '+';
        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                int row = cell[0];
                int col = cell[1];

                if (steps > 0 && (row == 0 || col == 0 || row == rows - 1 || col == cols - 1)) {
                    return steps;
                }

                for (int[] direction : directions) {
                    int nextRow = row + direction[0];
                    int nextCol = col + direction[1];
                    if (nextRow >= 0 && nextRow < rows && nextCol >= 0 && nextCol < cols
                            && maze[nextRow][nextCol] == '.') {
                        maze[nextRow][nextCol] = '+';
                        queue.offer(new int[] {nextRow, nextCol});
                    }
                }
            }
            steps++;
        }

        return -1;
    }
}
