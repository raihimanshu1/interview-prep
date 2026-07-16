package com.patternwisejavasolutions.binarysearch.variations;

public class SearchA2DMatrix {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Search target in a matrix where each row is sorted and the first number
     * of each row is bigger than the last number of the previous row.
     *
     * Sample Input: matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
     * Sample Output: true
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The matrix behaves like one long sorted list written across rows. After the last number of
     * one row, the next row starts with a bigger number. That is the clue that binary search can
     * treat all cells as one sorted line.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Look at every cell until target is found. This is the natural first attempt because every
     * cell is a possible location.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Visit every row.
     * 2. Visit every column in that row.
     * 3. Return true if a cell equals target.
     * 4. Return false after all cells are checked.
     *
     * Time Complexity: O(mn)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * target = 3
     * Check 1, then 3. Found, so return true.
     */
    public boolean bruteForce(int[][] matrix, int target) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                if (matrix[row][col] == target) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The brute force waste is scanning cells even after sorted order could rule out whole ranges.
     * Convert a flat index into row and column:
     * row = index / columns, col = index % columns.
     * Then binary search over indexes from 0 to rows * cols - 1.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Let left = 0 and right = rows * cols - 1.
     * 2. Convert mid into row and column.
     * 3. Compare matrix[row][col] with target.
     * 4. Move left or right like normal binary search.
     *
     * Time Complexity: O(log(mn))
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * target = 3, columns = 4
     * mid = 5 maps to matrix[1][1] = 11, search left.
     * mid = 2 maps to matrix[0][2] = 5, search left.
     * mid = 0 maps to 1, search right. mid = 1 maps to 3, found.
     */
    public boolean optimized(int[][] matrix, int target) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int left = 0;
        int right = rows * cols - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            // Pretend the matrix is flattened; division gives row and remainder gives column.
            int row = mid / cols;
            int col = mid % cols;
            int value = matrix[row][col];

            if (value == target) {
                return true;
            }

            if (value < target) {
                // This flat position is too small, so everything before it is too small too.
                left = mid + 1;
            } else {
                // This flat position is too large, so everything after it is too large too.
                right = mid - 1;
            }
        }

        return false;
    }
}
