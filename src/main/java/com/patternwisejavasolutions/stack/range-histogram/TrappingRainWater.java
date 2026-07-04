
package com.patternwisejavasolutions.stack.rangeHistogram;
public class TrappingRainWater {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Bars have different heights. After rain, water can be trapped between bars.
     * Return total trapped water.
     *
     * Sample Input:
     * height = [0,1,0,2,1,0,1,3,2,1,2,1]
     *
     * Sample Output:
     * 6
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Water above one index depends on the tallest wall on its left
     * and the tallest wall on its right.
     *
     * Water at index = min(leftMax, rightMax) - height[index]
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For every index, scan left to find leftMax
     * and scan right to find rightMax.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * height = [2, 0, 2]
     *
     * index 1 height 0
     * leftMax = 2
     * rightMax = 2
     * water = min(2, 2) - 0 = 2
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each index, find tallest bar on the left.
     * 2. Find tallest bar on the right.
     * 3. Water level is smaller of those two walls.
     * 4. Add water level minus current height.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] height) {
        int water = 0;

        for (int i = 0; i < height.length; i++) {
            int leftMax = 0;
            int rightMax = 0;

            for (int left = 0; left <= i; left++) {
                leftMax = Math.max(leftMax, height[left]);
            }

            for (int right = i; right < height.length; right++) {
                rightMax = Math.max(rightMax, height[right]);
            }

            water += Math.min(leftMax, rightMax) - height[i];
        }

        return water;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Use two pointers.
     *
     * The smaller side decides the water level,
     * because water spills from the shorter boundary.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * height = [2, 0, 2]
     *
     * leftMax = 2, rightMax = 2.
     * middle height is 0, so it can hold 2 water.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Use left and right pointers.
     * 2. Track best wall seen from both sides.
     * 3. Move the side with smaller height.
     * 4. Add water using that side's max wall.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Precomputing leftMax and rightMax arrays is a very clear O(n) solution. A
     * two-pointer version can reduce extra space, while the stack version views
     * trapped water as bounded valleys.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] <= height[right]) {
                // Left side is the limiting wall, so leftMax decides water here.
                leftMax = Math.max(leftMax, height[left]);
                water += leftMax - height[left];
                left++;
            } else {
                // Right side is the limiting wall, so rightMax decides water here.
                rightMax = Math.max(rightMax, height[right]);
                water += rightMax - height[right];
                right--;
            }
        }

        return water;
    }
}
