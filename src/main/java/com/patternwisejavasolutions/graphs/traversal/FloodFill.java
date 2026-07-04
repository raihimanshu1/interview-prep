
package com.patternwisejavasolutions.graphs.traversal;
public class FloodFill {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input image, starting cell, and new color.
     * Change the starting cell and all connected cells with the same original color.
     *
     * Sample Input:
     * image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
     *
     * Sample Output:
     * [[2,2,2],[2,2,0],[2,0,1]]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * This is like paint bucket in drawing tools.
     * If the clicked square is blue, only the blue region touching that square should change.
     * A same-colored square far away is not part of this spill because paint cannot jump over other colors.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * The natural first attempt is to walk from the clicked cell and try all four neighboring cells.
     * We only continue into a neighbor if it still has the original clicked color.
     * A visited grid is needed because two neighboring cells can point back to each other forever.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Start at image[sr][sc].
     * Remember original color.
     * Color current cell, then try up/down/left/right neighbors with same original color.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Use visited matrix.
     * 2. DFS from start.
     * 3. Change cells that match original color.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int[][] bruteForce(int[][] image, int sr, int sc, int color) {
        boolean[][] visited = new boolean[image.length][image[0].length];
        fillWithVisited(image, sr, sc, image[sr][sc], color, visited);
        return image;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force version spends extra space remembering visited cells.
     * But after a cell is recolored, it no longer equals the original color, so the image itself
     * can act like the visited marker. The one danger is when original color equals new color:
     * then recoloring does not change anything, so DFS would keep revisiting the same region.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * original = 1, newColor = 2
     * Change current 1 to 2.
     * Neighbors with 1 are still eligible.
     * Neighbors already 2 are skipped.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Store original color.
     * 2. If original equals new, return image.
     * 3. DFS and recolor matching cells.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn) recursion
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int[][] optimized(int[][] image, int sr, int sc, int color) {
        int originalColor = image[sr][sc];

        if (originalColor == color) {
            return image;
        }

        fill(image, sr, sc, originalColor, color);
        return image;
    }


    private void fillWithVisited(int[][] image, int row, int col, int originalColor, int newColor, boolean[][] visited) {
        if (row < 0 || row == image.length || col < 0 || col == image[0].length || visited[row][col] || image[row][col] != originalColor) {
            return;
        }

        visited[row][col] = true; // Mark before exploring neighbors so they cannot bounce back here.
        image[row][col] = newColor;
        fillWithVisited(image, row + 1, col, originalColor, newColor, visited);
        fillWithVisited(image, row - 1, col, originalColor, newColor, visited);
        fillWithVisited(image, row, col + 1, originalColor, newColor, visited);
        fillWithVisited(image, row, col - 1, originalColor, newColor, visited);
    }

    private void fill(int[][] image, int row, int col, int originalColor, int newColor) {
        if (row < 0 || row == image.length || col < 0 || col == image[0].length || image[row][col] != originalColor) {
            return;
        }

        image[row][col] = newColor; // Recoloring also marks this cell as already handled.
        fill(image, row + 1, col, originalColor, newColor);
        fill(image, row - 1, col, originalColor, newColor);
        fill(image, row, col + 1, originalColor, newColor);
        fill(image, row, col - 1, originalColor, newColor);
    }
}
