

package com.companywisejavasolutions.karat.solutions;
import java.util.*;

public class GridAdjacentNeighbor {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a grid size and a cell, return valid four-direction neighbors inside the grid.
     *
     * INPUT
     * rows, cols, row, col.
     *
     * OUTPUT
     * List of valid neighbor coordinates.
     *
     * EXAMPLE
     * rows = 3, cols = 4, cell = (1,2)
     * Output: [(2,2), (0,2), (1,3), (1,1)]
     * 
     * A middle cell has four valid neighbors; edge cells would have fewer.
     *
     * WHAT IT MEANS
     * Try up/down/left/right and keep coordinates within bounds.
     */
    /*
     * IN-DEPTH EXPLANATION
     *
     * Imagine standing on one square of a board. A neighboring square is not
     * diagonal here. It is only one step down, up, right, or left.
     *
     * The only tricky part is that the board has edges. A move that looks normal
     * from the middle can fall outside the board from a corner.
     *
     * What to know before solving:
     *
     * 1. Rows go from 0 to rows - 1.
     * 2. Columns go from 0 to cols - 1.
     * 3. A valid neighbor changes exactly one coordinate by 1.
     * 4. Diagonal cells are not neighbors for this problem.
     *
     * What we do to solve:
     *
     * Try the four possible moves one at a time. For each candidate cell, ask:
     * "Is the row still inside the board, and is the column still inside the
     * board?" If yes, keep it. If no, ignore it.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - Middle cell
     *
     * rows = 3, cols = 4, cell = (1, 2)
     *
     * Valid neighbors:
     * [(2,2), (0,2), (1,3), (1,1)]
     *
     * Why:
     * All four moves stay inside the grid.
     *
     * Example 2 - Top-left corner
     *
     * rows = 3, cols = 4, cell = (0, 0)
     *
     * Valid neighbors:
     * [(1,0), (0,1)]
     *
     * Why:
     * Up would be row -1 and left would be col -1, so both are outside.
     *
     * Example 3 - Right edge
     *
     * rows = 3, cols = 4, cell = (1, 3)
     *
     * Valid neighbors:
     * [(2,3), (0,3), (1,2)]
     *
     * Why:
     * Moving right would use column 4, but valid columns are only 0 through 3.
     *
     * Edge case 1 - One-cell grid
     *
     * rows = 1, cols = 1, cell = (0, 0)
     *
     * Output:
     * []
     *
     * Why:
     * Every possible move leaves the grid.
     *
     * Edge case 2 - One-row grid
     *
     * rows = 1, cols = 4, cell = (0, 2)
     *
     * Output:
     * [(0,3), (0,1)]
     *
     * Why:
     * There is no row above or below, but left and right still work.
    */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A cell can have at most four direct neighbors.
     * 2. Diagonal cells are not neighbors here.
     * 3. A neighbor is valid only if it stays inside the grid.
     * 4. Border and corner cells naturally have fewer neighbors.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Try the four possible moves directly:
     * down, up, right, and left.
     * For each move, check whether the new row and column are valid.
     * Add only the valid coordinates to the answer.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create an empty answer list.
     * 2. Try the down coordinate: row + 1, col.
     * 3. Try the up coordinate: row - 1, col.
     * 4. Try the right coordinate: row, col + 1.
     * 5. Try the left coordinate: row, col - 1.
     * 6. Add a coordinate only if its row and column are inside the grid.
     * 7. Return the answer list.
     *
     * Time Complexity: O(1), because there are always only four directions.
     * Space Complexity: O(1), because the result can contain at most four cells.
     */
    public List<int[]> bruteForce(int rows, int cols, int row, int col) {

        List<int[]> result = new ArrayList<>();

        // Down: row increases by one. This is valid unless we are already on the bottom row.
        if (row + 1 < rows) {
            result.add(new int[] {row + 1, col});
        }
        // Up: row decreases by one. This is valid unless we are already on the top row.
        if (row - 1 >= 0) {
            result.add(new int[] {row - 1, col});
        }
        // Right: column increases by one. This is valid unless we are already on the right edge.
        if (col + 1 < cols) {
            result.add(new int[] {row, col + 1});
        }
        // Left: column decreases by one. This is valid unless we are already on the left edge.
        if (col - 1 >= 0) {
            result.add(new int[] {row, col - 1});
        }

        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: loop over direction arrays and keep only positions inside the grid.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Store the four row/column movements in a directions array.
     * 2. Loop over directions instead of writing four separate checks.
     * 3. Keep only coordinates inside the grid.
     * 4. Return the valid neighbors.
     * 
     * Time Complexity: Lower than brute force because repeated scanning is replaced with stored state.
     * Space Complexity: O(1) because the directions array and result are bounded by four.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use rows=3, cols=4, cell=(1,2).
     * All four moves stay inside the grid.
     * Final neighbors: [(2,2), (0,2), (1,3), (1,1)].
     */
    public List<int[]> optimized(int rows, int cols, int row, int col) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        List<int[]> result = new ArrayList<>();
        for (int[] direction : directions) {
            int nextRow = row + direction[0];
            int nextCol = col + direction[1];
        if (nextRow >= 0 && nextRow < rows && nextCol >= 0 && nextCol < cols) {
                result.add(new int[] {nextRow, nextCol});
            }
        }
        return result;
    }

    public static void main(String[] args) {
        GridAdjacentNeighbor solver = new GridAdjacentNeighbor();

        int[][] samples = {
                {3, 4, 1, 2},
                {3, 4, 0, 0},
                {1, 1, 0, 0}
        };

        for (int i = 0; i < samples.length; i++) {
            int rows = samples[i][0];
            int cols = samples[i][1];
            int row = samples[i][2];
            int col = samples[i][3];

            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + format(solver.bruteForce(rows, cols, row, col)));
            System.out.println("optimized:  " + format(solver.optimized(rows, cols, row, col)));
            System.out.println();
        }
    }

    private static String format(List<int[]> coordinates) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < coordinates.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(java.util.Arrays.toString(coordinates.get(i)));
        }
        builder.append("]");
        return builder.toString();
    }
}
