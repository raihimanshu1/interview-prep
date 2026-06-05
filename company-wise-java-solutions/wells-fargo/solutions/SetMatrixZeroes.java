package wellsfargo.solutions;

public class SetMatrixZeroes {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * If a cell is 0, make its entire row and column 0.
     *
     * Sample Input: [[1,1,1],[1,0,1],[1,1,1]]
     * Sample Output: [[1,0,1],[0,0,0],[1,0,1]]
     */

    /*
     * WHAT TO NOTICE FIRST
     *
     * The single 0 in the center of the sample wipes out the whole middle row
     * and whole middle column. A learner should notice that only original zeroes
     * should cause this; zeroes written later must not create new wipes.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A zero acts like a marker saying, "my row and my column must become zero."
     * We must remember original zero positions before changing anything.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The most literal beginner idea is:
     * "First remember the original matrix, then for every original zero,
     * walk across its full row and full column and write zero."
     *
     * This is slower, but very easy to trust because it follows the problem
     * sentence directly.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Copy the original matrix.
     * 2. Scan the copy, not the changing matrix.
     * 3. Whenever copy[row][col] is 0, zero that full row in matrix.
     * 4. Also zero that full column in matrix.
     *
     * Time Complexity: O(mn * (m + n))
     * Space Complexity: O(mn)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Zero at row 1 col 1 marks row 1 and col 1. Final matrix zeros those lines.
     */
    public void bruteForce(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] original = new int[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                /*
                 * We copy first because newly written zeroes should not create
                 * more zero rows/columns. Only original zeroes count.
                 */
                original[row][col] = matrix[row][col];
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (original[row][col] == 0) {
                    /*
                     * This follows the problem statement directly:
                     * a zero makes its whole row and whole column zero.
                     */
                    zeroEntireRow(matrix, row);
                    zeroEntireColumn(matrix, col);
                }
            }
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The extra row/column arrays can be stored inside the matrix itself: use
     * the first row and first column as markers. One extra boolean remembers
     * whether the first column originally needed zeroing.
     *
     * The tricky part:
     * matrix[0][0] sits in both the first row and first column.
     * We use matrix[0][0] to remember whether the first row should become zero.
     * We use firstColZero separately to remember whether the first column should become zero.
     * This avoids one cell trying to store two different answers.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Check whether first column needs zeroing.
     * 2. Use first row and first column as markers for inner cells.
     * 3. Zero inner cells based on markers.
     * 4. Zero first row if needed.
     * 5. Zero first column if needed.
     *
     * Time Complexity: O(mn)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * For zero at (1,1), mark matrix[1][0] and matrix[0][1] as zero. Later, any
     * cell in row 1 or column 1 becomes zero.
     */
    public void optimized(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean firstColZero = false;

        for (int row = 0; row < rows; row++) {
            if (matrix[row][0] == 0) {
                /*
                 * Column 0 cannot use matrix[0][0] safely as its only marker,
                 * because matrix[0][0] is already needed for the first row.
                 */
                firstColZero = true;
            }
            for (int col = 1; col < cols; col++) {
                if (matrix[row][col] == 0) {
                    /*
                     * Mark this row using its first cell.
                     * Mark this column using the first row.
                     */
                    matrix[row][0] = 0;
                    matrix[0][col] = 0;
                }
            }
        }

        for (int row = 1; row < rows; row++) {
            for (int col = 1; col < cols; col++) {
                if (matrix[row][0] == 0 || matrix[0][col] == 0) {
                    matrix[row][col] = 0;
                }
            }
        }

        if (matrix[0][0] == 0) {
            /*
             * matrix[0][0] is our first-row marker.
             * If it is zero, the whole first row must become zero.
             */
            for (int col = 0; col < cols; col++) {
                matrix[0][col] = 0;
            }
        }

        if (firstColZero) {
            /*
             * firstColZero is separate because the first column needed its own marker.
             */
            for (int row = 0; row < rows; row++) {
                matrix[row][0] = 0;
            }
        }
    }

    private void zeroEntireRow(int[][] matrix, int row) {
        for (int col = 0; col < matrix[0].length; col++) {
            matrix[row][col] = 0;
        }
    }

    private void zeroEntireColumn(int[][] matrix, int col) {
        for (int row = 0; row < matrix.length; row++) {
            matrix[row][col] = 0;
        }
    }
}
