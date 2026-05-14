import java.util.ArrayList;
import java.util.List;

public class PacificAtlanticWaterFlow {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given heights matrix, water can flow from a cell to neighbors with height <= current height.
     * Return cells that can flow to both Pacific and Atlantic oceans.
     *
     * Sample Input:
     * heights = [[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]]
     *
     * Sample Output:
     * [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A cell is good only if rain starting there can slide to both ocean edges.
     * Directly checking every raindrop is slow because nearby cells repeat the same downhill searches.
     * The clever turn is to ask the oceans which cells can drain into them.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * For each cell, pretend a drop of water starts there.
     * Follow only downhill or flat moves and see whether that drop can touch both ocean borders.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Start from one cell.
     * Move only to lower or equal neighbors.
     * If path touches top/left, it reaches Pacific.
     * If path touches bottom/right, it reaches Atlantic.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For every cell, DFS outward.
     * 2. Track whether Pacific and Atlantic are reached.
     * 3. Add cell if both are reached.
     * Time Complexity: O((mn)^2)
     * Space Complexity: O(mn)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public List<List<Integer>> bruteForce(int[][] heights) {
        List<List<Integer>> answer = new ArrayList<>();

        for (int row = 0; row < heights.length; row++) {
            for (int col = 0; col < heights[0].length; col++) {
                boolean[] oceans = new boolean[2];
                flowOut(heights, row, col, new boolean[heights.length][heights[0].length], oceans);

                if (oceans[0] && oceans[1]) {
                    answer.add(List.of(row, col));
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain is starting a fresh DFS from every cell.
     * Reverse the question: start from each ocean border and walk uphill or flat.
     * If the ocean can walk uphill to a cell, then water from that cell can flow downhill to the ocean.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Start DFS from Pacific borders.
     * Mark all cells that can reach Pacific.
     * Start DFS from Atlantic borders.
     * Cells marked by both are answers.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Create two visited matrices.
     * 2. DFS from Pacific borders into higher/equal cells.
     * 3. DFS from Atlantic borders similarly.
     * 4. Collect cells visited by both.
     * Time Complexity: O(mn)
     * Space Complexity: O(mn)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public List<List<Integer>> optimized(int[][] heights) {
        int rows = heights.length;
        int cols = heights[0].length;
        boolean[][] pacific = new boolean[rows][cols];
        boolean[][] atlantic = new boolean[rows][cols];

        for (int row = 0; row < rows; row++) {
            flowIn(heights, row, 0, pacific, heights[row][0]);
            flowIn(heights, row, cols - 1, atlantic, heights[row][cols - 1]);
        }

        for (int col = 0; col < cols; col++) {
            flowIn(heights, 0, col, pacific, heights[0][col]);
            flowIn(heights, rows - 1, col, atlantic, heights[rows - 1][col]);
        }

        List<List<Integer>> answer = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (pacific[row][col] && atlantic[row][col]) {
                    answer.add(List.of(row, col));
                }
            }
        }

        return answer;
    }


    private void flowOut(int[][] heights, int row, int col, boolean[][] visited, boolean[] oceans) {
        if (row < 0 || col < 0) {
            oceans[0] = true;
            return;
        }

        if (row == heights.length || col == heights[0].length) {
            oceans[1] = true;
            return;
        }

        if (visited[row][col]) {
            return;
        }

        visited[row][col] = true; // Prevent this cell's downhill search from looping on flat areas.
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        for (int[] dir : dirs) {
            int nextRow = row + dir[0];
            int nextCol = col + dir[1];

            if (nextRow < 0 || nextCol < 0 || nextRow == heights.length || nextCol == heights[0].length || heights[nextRow][nextCol] <= heights[row][col]) {
                flowOut(heights, nextRow, nextCol, visited, oceans);
            }
        }
    }

    private void flowIn(int[][] heights, int row, int col, boolean[][] visited, int previousHeight) {
        if (row < 0 || row == heights.length || col < 0 || col == heights[0].length || visited[row][col] || heights[row][col] < previousHeight) {
            return;
        }

        visited[row][col] = true; // This cell can reach the ocean that started this DFS.
        flowIn(heights, row + 1, col, visited, heights[row][col]);
        flowIn(heights, row - 1, col, visited, heights[row][col]);
        flowIn(heights, row, col + 1, visited, heights[row][col]);
        flowIn(heights, row, col - 1, visited, heights[row][col]);
    }
}
