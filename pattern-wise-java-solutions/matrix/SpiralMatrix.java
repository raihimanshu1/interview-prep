import java.util.ArrayList;
import java.util.List;

public class SpiralMatrix {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return all matrix values in spiral order.
     *
     * Sample Input: [[1,2,3],[4,5,6],[7,8,9]]
     * Sample Output: [1,2,3,6,9,8,7,4,5]
     */

    /*
     * WHAT TO NOTICE FIRST
     *
     * The sample starts across the top row, then turns down the right side.
     * Spiral order is not row-by-row; it is a repeated walk around the current
     * outside edge before moving inward.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A spiral walks around the outside border, then moves inward and repeats.
     * Boundaries tell us which rows and columns are still unvisited.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Simulate the walk exactly: keep a direction, move cell by cell, and use a
     * visited grid so we know when a step would hit something already collected.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Keep current row, column, and direction.
     * 2. Add current cell and mark it visited.
     * 3. If next cell is invalid or visited, turn right.
     * 4. Continue for m * n cells.
     *
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Start at 1, move right to 2 and 3.
     * The next right step is outside the matrix, so turn down to 6 and 9.
     * Then turn left to 8 and 7, then up to 4, then inward to 5.
     */
    public List<Integer> bruteForce(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int[][] dirs = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };
        int row = 0;
        int col = 0;
        int dir = 0;

        for (int step = 0; step < rows * cols; step++) {
            result.add(matrix[row][col]);
            visited[row][col] = true;

            int nextRow = row + dirs[dir][0];
            int nextCol = col + dirs[dir][1];

            if (nextRow < 0 || nextRow == rows || nextCol < 0 || nextCol == cols || visited[nextRow][nextCol]) {
                dir = (dir + 1) % 4;
                nextRow = row + dirs[dir][0];
                nextCol = col + dirs[dir][1];
            }

            row = nextRow;
            col = nextCol;
        }

        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is storing a visited grid just to avoid
     * crossing old cells. Boundaries already describe the unvisited rectangle.
     * After reading a side, shrink that side so we never step there again.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Set top, bottom, left, and right boundaries.
     * 2. Traverse top row, then right column, then bottom row, then left column.
     * 3. Shrink boundaries after each side.
     * 4. Stop when boundaries cross.
     *
     * Time Complexity: O(mn)
     * Space Complexity: O(1) besides output
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Top row gives 1,2,3. Right column gives 6,9. Bottom row gives 8,7. Left
     * column gives 4. Then inner cell 5 remains.
     */
    public List<Integer> optimized(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int top = 0;
        int bottom = matrix.length - 1;
        int left = 0;
        int right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            for (int col = left; col <= right; col++) {
                result.add(matrix[top][col]);
            }
            // Top row is finished; the next layer starts one row lower.
            top++;

            for (int row = top; row <= bottom; row++) {
                result.add(matrix[row][right]);
            }
            // Right column is finished; the next layer ends one column earlier.
            right--;

            if (top <= bottom) {
                for (int col = right; col >= left; col--) {
                    result.add(matrix[bottom][col]);
                }
                // Guard avoids rereading the same row in a single-row layer.
                bottom--;
            }

            if (left <= right) {
                for (int row = bottom; row >= top; row--) {
                    result.add(matrix[row][left]);
                }
                // Guard avoids rereading the same column in a single-column layer.
                left++;
            }
        }

        return result;
    }
}
