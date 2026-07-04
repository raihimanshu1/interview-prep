
package com.patternwisejavasolutions.arraysHashing.multiPointerCombination;
public class ContainerWithMostWater {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Given heights of vertical lines, choose two lines that hold the most water.
 *
 * Sample Input:
 * height = [1, 8, 6, 2, 5, 4, 8, 3, 7]
 *
 * Sample Output:
 * 49
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Water is limited by the shorter wall. A very tall wall cannot help if the
 * other wall is short, because water would spill over the shorter side.
 *
 * Area has two parts: width between lines and the shorter height. Two pointers
 * let us start with the biggest width and then search for a better height.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to try every left wall with every later right wall
 * and calculate the water for that container.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Pick the left line with i.
 * 2. Pick the right line with j after i.
 * 3. Area = (j - i) * min(height[i], height[j]).
 * 4. Keep the largest area.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * i = 1 has height 8, j = 8 has height 7.
 * Width = 8 - 1 = 7.
 * Shorter wall = 7.
 * Area = 7 * 7 = 49.
 */

public int bruteForce(int[] height) {
        int best = 0;

        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                // The shorter line decides how high the water can rise.
                int currentArea = (j - i) * Math.min(height[i], height[j]);
                best = Math.max(best, currentArea);
            }
        }

        return best;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is checking pairs that cannot win. Start with the
 * widest pair. If the left wall is shorter, moving the right wall inward only
 * reduces width while the same short left wall still limits height. So move
 * the shorter wall and hope for a taller one.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Put left at start and right at end.
 * 2. Compute area.
 * 3. Move the pointer at the shorter wall.
 * 4. Repeat until pointers meet.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * left height 1, right height 7 gives area 8.
 * Left is shorter, so move left.
 * Later left height 8 and right height 7 gives width 7 and area 49.
 */

public int optimized(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int best = 0;

        while (left < right) {
            int currentArea = (right - left) * Math.min(height[left], height[right]);
            best = Math.max(best, currentArea);

            if (height[left] < height[right]) {
                // Move the limiting wall; keeping it cannot create a better area.
                left++;
            } else {
                // Right wall is limiting, so search for a taller right wall inward.
                right--;
            }
        }

        return best;
    }
}
