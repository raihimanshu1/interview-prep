
package com.patternwisejavasolutions.stack.rangeHistogram;
import java.util.Stack;

public class LargestRectangleInHistogram {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * We are given bar heights of a histogram.
     * Find the largest rectangle area that can be formed.
     *
     * Sample Input:
     * heights = [2, 1, 5, 6, 2, 3]
     *
     * Sample Output:
     * 10
     *
     * The rectangle using heights 5 and 6 has height 5 and width 2.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If we choose one bar as the smallest height of a rectangle,
     * we can extend left and right while bars are at least that tall.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For every bar, expand left and right to find how wide it can go.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * heights = [2, 1, 5, 6, 2, 3]
     *
     * choose height 5 at index 2.
     * right index 3 has 6, still okay.
     * next index 4 has 2, stop.
     * width = 2, area = 5 * 2 = 10.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick each bar as the rectangle height.
     * 2. Expand left while bars are at least that height.
     * 3. Expand right while bars are at least that height.
     * 4. Calculate height * width and update best.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] heights) {
        int bestArea = 0;

        for (int i = 0; i < heights.length; i++) {
            int left = i;
            int right = i;

            while (left - 1 >= 0 && heights[left - 1] >= heights[i]) {
                left--;
            }

            while (right + 1 < heights.length && heights[right + 1] >= heights[i]) {
                right++;
            }

            int width = right - left + 1;
            bestArea = Math.max(bestArea, heights[i] * width);
        }

        return bestArea;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * We need to know where a bar first meets a smaller bar.
     * A monotonic increasing stack helps us find that boundary.
     *
     * When current bar is smaller than stack top,
     * the stack top bar cannot extend further right.
     * So we calculate its area now.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * heights = [2, 1, 5, 6, 2]
     *
     * push index 0 height 2.
     * current height 1 is smaller.
     * pop height 2, width is 1, area = 2.
     * push index 1 height 1.
     *
     * Later stack has heights [1, 5, 6].
     * current height 2 is smaller than 6, so pop 6: width 1, area 6.
     * current height 2 is also smaller than 5, so pop 5: width 2, area 10.
     * Stop popping at height 1 because it is smaller than 2.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep increasing bar indices in stack.
     * 2. When current height is smaller, pop taller bars.
     * 3. For each popped bar, current index is right boundary.
     * 4. New stack top is left boundary.
     * 5. Calculate area and update best.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * You can precompute previous smaller and next smaller indices in two arrays,
     * then calculate each bar's widest rectangle. The stack version below does
     * that boundary discovery during one scan.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] heights) {
        Stack<Integer> increasingIndices = new Stack<>();
        int bestArea = 0;

        for (int i = 0; i <= heights.length; i++) {
            // The final 0 acts like a sentinel that forces all remaining bars to finish.
            int currentHeight = i == heights.length ? 0 : heights[i];

            while (
                !increasingIndices.isEmpty()
                    && currentHeight < heights[increasingIndices.peek()]
            ) {
                int heightIndex = increasingIndices.pop();
                int height = heights[heightIndex];

                int leftBoundary = increasingIndices.isEmpty() ? -1 : increasingIndices.peek();
                int rightBoundary = i;
                // Popped bar can extend between the nearest smaller bars on both sides.
                int width = rightBoundary - leftBoundary - 1;

                bestArea = Math.max(bestArea, height * width);
            }

            increasingIndices.push(i);
        }

        return bestArea;
    }
}
