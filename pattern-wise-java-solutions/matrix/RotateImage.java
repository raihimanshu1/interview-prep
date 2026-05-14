public class RotateImage {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Rotate an n x n matrix 90 degrees clockwise in-place.
     *
     * Sample Input: [[1,2,3],[4,5,6],[7,8,9]]
     * Sample Output: [[7,4,1],[8,5,2],[9,6,3]]
     */

    /*
     * WHAT TO NOTICE FIRST
     *
     * In the sample, the old left column [1,4,7] becomes the new top row
     * [7,4,1]. That tells us rotation is about moving positions, not changing
     * values.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A clockwise rotation moves top row to right column, right column to bottom
     * row, and so on. We can either use another matrix to make this obvious or
     * rotate layers in-place.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Create a new matrix and place every old cell into its rotated position.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each cell row, col, put it at col, n - 1 - row in a copy.
     * 2. Copy the result back into matrix.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(n^2)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Value 1 at (0,0) moves to (0,2).
     * Value 4 at (1,0) moves to (0,1).
     * Value 7 at (2,0) moves to (0,0).
     * So the old first column becomes the new first row in reverse order.
     */
    public void bruteForce(int[][] matrix) {
        int n = matrix.length;
        int[][] rotated = new int[n][n];

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                rotated[col][n - 1 - row] = matrix[row][col];
            }
        }

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                matrix[row][col] = rotated[row][col];
            }
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is storing a whole second matrix. A clockwise
     * rotation can be broken into two in-place moves: transpose, then reverse
     * each row.
     *
     * Transpose swaps rows and columns. Reversing each row then moves the old
     * left side to the right-facing clockwise position.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Transpose matrix by swapping matrix[row][col] with matrix[col][row].
     * 2. Reverse every row.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * [[1,2,3],[4,5,6],[7,8,9]] transposes to [[1,4,7],[2,5,8],[3,6,9]].
     * Reversing rows gives [[7,4,1],[8,5,2],[9,6,3]].
     */
    public void optimized(int[][] matrix) {
        int n = matrix.length;

        for (int row = 0; row < n; row++) {
            for (int col = row + 1; col < n; col++) {
                // Swap only above the diagonal so each pair is swapped once.
                int temp = matrix[row][col];
                matrix[row][col] = matrix[col][row];
                matrix[col][row] = temp;
            }
        }

        for (int row = 0; row < n; row++) {
            reverseRow(matrix[row]);
        }
    }

    private void reverseRow(int[] row) {
        int left = 0;
        int right = row.length - 1;
        while (left < right) {
            int temp = row[left];
            row[left] = row[right];
            row[right] = temp;
            left++;
            right--;
        }
    }
}
