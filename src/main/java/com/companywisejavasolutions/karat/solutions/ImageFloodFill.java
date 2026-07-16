package com.companywisejavasolutions.karat.solutions;

import java.util.*;

public class ImageFloodFill {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given an image, a start cell, and a new color, recolor the connected region that has the start cell original color.
     *
     * INPUT
     * image grid, startRow, startCol, newColor.
     *
     * OUTPUT
     * The mutated image after fill.
     *
     * EXAMPLE
     * image = [[1,1,1], [1,1,0], [1,0,1]], start=(1,1), newColor=2
     * Output: [[2,2,2], [2,2,0], [2,0,1]]
     * 
     * Only connected 1-cells from the starting point are recolored.
     *
     * WHAT IT MEANS
     * BFS/DFS spreads only through adjacent cells with the original color.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * Imagine clicking a paint bucket inside a drawing program.
     *
     * The bucket does not repaint the whole picture. It only repaints the area
     * that is connected to the clicked pixel and has the same original color.
     *
     * Connected means up, down, left, and right. Diagonal cells are close on the
     * screen, but they do not count as connected for this problem.
     *
     * Example 1 - Fill a normal connected area
     *
     * image = {
     *     {1, 1, 1},
     *     {1, 1, 0},
     *     {1, 0, 1}
     * }
     * start = (1, 1), newColor = 2
     *
     * Output:
     * {
     *     {2, 2, 2},
     *     {2, 2, 0},
     *     {2, 0, 1}
     * }
     *
     * Why:
     * The top-left group of 1s touches the starting cell through four-direction
     * movement, so it changes. The bottom-right 1 is separated by 0s, so it stays.
     *
     * Example 2 - Fill from a corner
     *
     * image = {
     *     {3, 3, 4},
     *     {3, 4, 4}
     * }
     * start = (0, 0), newColor = 9
     *
     * Output:
     * {
     *     {9, 9, 4},
     *     {9, 4, 4}
     * }
     *
     * Why:
     * Only the connected 3s are repainted.
     *
     * Example 3 - Starting color appears in two separate places
     *
     * image = {
     *     {5, 0, 5},
     *     {5, 0, 5}
     * }
     * start = (0, 0), newColor = 7
     *
     * Output:
     * {
     *     {7, 0, 5},
     *     {7, 0, 5}
     * }
     *
     * Why:
     * The right-side 5s are the same color, but they are not connected to the
     * starting cell.
     *
     * Edge case 1 - New color is the same as the old color
     *
     * If start cell is already newColor, return the image immediately.
     * Otherwise recursion would keep seeing the same color and could loop forever.
     *
     * Edge case 2 - Single cell image
     *
     * image = {{1}}, start = (0, 0), newColor = 2
     * Output = {{2}}
     *
     * There is only one reachable cell, so only that cell changes.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. Save the starting cell's original color before changing anything.
     * 2. Only cells with that original color are allowed to join the fill.
     * 3. Movement is four-directional, not diagonal.
     * 4. A boundary check must happen before reading image[row][col].
     * 5. Recoloring a cell also acts like marking it visited, because it will no
     *    longer equal the old color.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * Start at the clicked cell.
     * If the cell has the old color, repaint it.
     * Then ask the same question of its four neighbors.
     * The recursion naturally stops at borders, different colors, and cells that
     * have already been repainted.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of a paint bucket. Paint spreads only to connected cells with the same
     * original color, and it stops at borders or different colors.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    Starting from one cell, we only repaint connected cells with the same original color.
     *
     * 2. What data structure does that naturally suggest?
     *    Use queue/visited or DFS because the fill spreads through neighboring cells.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: recursively try all four directions from every matching cell.
     *
     * 4. What repeated work should I remove?
     *    Optimized: use iterative BFS to avoid repeated work and deep recursion risk.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: recursively try all four directions from every matching cell.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Read the old color from the starting cell.
     * 2. If oldColor == newColor, return immediately.
     * 3. Repaint the current cell.
     * 4. Recursively try down, up, right, and left.
     * 5. Stop a recursive call when it leaves the grid or reaches a different color.
     * 
     * Time Complexity: O(rows * cols) because each repaintable cell is changed once.
     * Space Complexity: O(rows * cols) in the worst case for the recursion stack.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the 3x3 image above.
     * Starting at (1,1), the connected 1-region includes the top-left area.
     * The isolated 1 at bottom-right is not connected, so it remains 1.
     */
    public int[][] bruteForce(int[][] image, int startRow, int startCol, int newColor) {

        // The whole fill is based on the color that was present before we paint.
        // Example: if the start cell is 1, only connected 1-cells may change.
        int oldColor = image[startRow][startCol];

        // If we are "changing" 1 to 1, there is nothing to do.
        // This guard also prevents recursion from revisiting unchanged cells forever.
        if (oldColor == newColor) {
            return image;
        }

        // Let the recursive helper do the plain paint-bucket work:
        // paint this cell, then spread to its four direct neighbors.
        fillRecursive(image, startRow, startCol, oldColor, newColor);
        return image;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: use iterative BFS to avoid repeated work and deep recursion risk.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: recolor connected same-color pixels.
     * 2. Remove repeated work: use BFS queue to avoid deep recursion and repeated call stack growth.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the 3x3 image above.
     * Starting at (1,1), the connected 1-region includes the top-left area.
     * The isolated 1 at bottom-right is not connected, so it remains 1.
     */
    public int[][] optimized(int[][] image, int startRow, int startCol, int newColor) {
        int oldColor = image[startRow][startCol];
        if (oldColor == newColor) {
            return image;
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        // Queue is used for BFS because it processes nodes in distance/order layers.
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {startRow, startCol});
        image[startRow][startCol] = newColor;
                while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] direction : directions) {
                int row = cell[0] + direction[0];
                int col = cell[1] + direction[1];
                if (row >= 0 && row < image.length && col >= 0 && col < image[0].length
                && image[row][col] == oldColor) {
                    image[row][col] = newColor;
                    queue.offer(new int[] {row, col});
                }
            }
        }
        return image;
    }

    private void fillRecursive(int[][] image, int row, int col, int oldColor, int newColor) {
        // Boundary guard: before reading image[row][col], make sure the position
        // is still inside the grid. A flood fill naturally tries neighbors beyond
        // edges, and those calls should simply stop.
        if (row < 0 || row >= image.length || col < 0 || col >= image[0].length) {
            return;
        }

        // Color guard: only cells with the original starting color belong to this
        // connected region. Different colors are walls, and already-painted cells
        // no longer equal oldColor, so this also prevents revisiting work.
        if (image[row][col] != oldColor) {
            return;
        }

        // Paint the current cell as soon as it qualifies. This both changes the
        // output and marks the cell as visited for the recursive search.
        image[row][col] = newColor;

        // Continue the paint bucket in the four allowed directions. Diagonals are
        // intentionally absent because the problem defines connection by shared
        // edges: down, up, right, and left.
        fillRecursive(image, row + 1, col, oldColor, newColor);
        fillRecursive(image, row - 1, col, oldColor, newColor);
        fillRecursive(image, row, col + 1, oldColor, newColor);
        fillRecursive(image, row, col - 1, oldColor, newColor);
    }

    public static void main(String[] args) {
        ImageFloodFill solver = new ImageFloodFill();

        int[][][] images = {
                {
                        {1, 1, 1},
                        {1, 1, 0},
                        {1, 0, 1}
                },
                {
                        {3, 3, 4},
                        {3, 4, 4}
                },
                {
                        {5, 0, 5},
                        {5, 0, 5}
                }
        };
        int[][] startsAndColors = {
                {1, 1, 2},
                {0, 0, 9},
                {0, 0, 7}
        };

        for (int i = 0; i < images.length; i++) {
            int startRow = startsAndColors[i][0];
            int startCol = startsAndColors[i][1];
            int newColor = startsAndColors[i][2];

            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + format(solver.bruteForce(copyGrid(images[i]), startRow, startCol, newColor)));
            System.out.println("optimized:  " + format(solver.optimized(copyGrid(images[i]), startRow, startCol, newColor)));
            System.out.println();
        }
    }

    private static int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[grid.length][grid[0].length];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                copy[row][col] = grid[row][col];
            }
        }
        return copy;
    }

    private static String format(int[][] grid) {
        return java.util.Arrays.deepToString(grid);
    }
}
