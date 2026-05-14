public class NumberOfIslands {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input grid has '1' for land and '0' for water.
     * Output: number of separate islands.
     * An island is connected land horizontally or vertically.
     *
     * Sample Input:
     * grid = [
     *   ['1','1','0'],
     *   ['0','1','0'],
     *   ['1','0','1']
     * ]
     *
     * Sample Output:
     * 3
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Think of the grid as a map where land squares touching by sides belong to the same island.
     * When we step on a new land square, we walk across the whole connected land mass.
     * After that walk, every square in that island must be marked so we do not count it again.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Scan the map cell by cell like reading a book.
     * Whenever we find land we have not visited, start a flood-fill from there.
     * That flood-fill may visit many cells, but it represents exactly one island, so add 1.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Grid has land at a cell.
     * Start DFS and mark all connected land.
     * That complete DFS counts as one island.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create visited matrix.
     * 2. Scan every cell.
     * 3. If land and not visited, count one island and DFS from it.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(char[][] grid) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        int islands = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == '1' && !visited[row][col]) {
                    islands++;
                    explore(grid, row, col, visited);
                }
            }
        }

        return islands;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force version needs a separate visited grid.
     * Since visited land never needs to be counted again, we can "sink" it by changing '1' to '0'.
     * Later scans naturally skip it because it now looks like water.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * When DFS sees land, change it to water.
     * Its neighbors are then explored.
     * Later scans will skip it because it is no longer land.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Scan grid.
     * 2. When cell is '1', increment islands.
     * 3. Sink that island with DFS.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn) recursion in worst case
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(char[][] grid) {
        int islands = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == '1') {
                    islands++;
                    sink(grid, row, col);
                }
            }
        }

        return islands;
    }


    private void explore(char[][] grid, int row, int col, boolean[][] visited) {
        if (row < 0 || row == grid.length || col < 0 || col == grid[0].length || visited[row][col] || grid[row][col] == '0') {
            return;
        }

        visited[row][col] = true; // This land square belongs to the current island.
        explore(grid, row + 1, col, visited);
        explore(grid, row - 1, col, visited);
        explore(grid, row, col + 1, visited);
        explore(grid, row, col - 1, visited);
    }

    private void sink(char[][] grid, int row, int col) {
        if (row < 0 || row == grid.length || col < 0 || col == grid[0].length || grid[row][col] == '0') {
            return;
        }

        grid[row][col] = '0'; // Sink this land so later scans do not count the same island again.
        sink(grid, row + 1, col);
        sink(grid, row - 1, col);
        sink(grid, row, col + 1);
        sink(grid, row, col - 1);
    }
}
