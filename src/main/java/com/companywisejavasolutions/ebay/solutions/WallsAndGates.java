

package com.companywisejavasolutions.ebay.solutions;
import java.util.ArrayDeque;
import java.util.Queue;

public class WallsAndGates {

    private static final int INF = 2147483647;

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Rooms contain walls (-1), gates (0), and empty rooms (INF). Fill each empty
     * room with distance to its nearest gate.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Start BFS from all gates at once. The first time we reach a room, that is
     * the shortest distance from any gate.
     */
    public void wallsAndGates(int[][] rooms) {
        if (rooms == null || rooms.length == 0) return;

        Queue<int[]> queue = new ArrayDeque<>();
        for (int row = 0; row < rooms.length; row++) {
            for (int col = 0; col < rooms[0].length; col++) {
                if (rooms[row][col] == 0) {
                    queue.offer(new int[] {row, col});
                }
            }
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] direction : directions) {
                int nextRow = cell[0] + direction[0];
                int nextCol = cell[1] + direction[1];
                if (nextRow < 0 || nextRow == rooms.length || nextCol < 0 || nextCol == rooms[0].length
                        || rooms[nextRow][nextCol] != INF) {
                    continue;
                }
                rooms[nextRow][nextCol] = rooms[cell[0]][cell[1]] + 1;
                queue.offer(new int[] {nextRow, nextCol});
            }
        }
    }
}
