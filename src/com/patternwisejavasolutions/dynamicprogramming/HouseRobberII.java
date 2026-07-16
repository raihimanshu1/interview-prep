package com.patternwisejavasolutions.dynamicprogramming;

public class HouseRobberII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [2, 3, 2]
     * Sample Output: 3
     *
     * Houses are in a circle, so the first and last houses are adjacent.
     * Rob the maximum money without robbing adjacent houses.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * In a straight street, we choose between robbing and skipping each house.
     * In a circle, we cannot take both the first and last house.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Try every possible rob or skip decision.
     * At the end, reject choices that robbed adjacent houses or both ends.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Recursively try skip or rob for every house.
     * 2. Track which houses were robbed.
     * 3. At the end, check all adjacency rules.
     * Time Complexity: O(2^n * n)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [2, 3, 2]
     * Try robbing houses [0, 2], but first and last touch, so invalid.
     * Try house [1], valid money = 3.
     */
    public int bruteForce(int[] nums) {
        return tryChoices(nums, 0, new boolean[nums.length]);
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Break the circle into two straight-line cases:
     * Case 1: do not use the last house.
     * Case 2: do not use the first house.
     * The better answer is the final answer. Each straight-line case reuses the
     * House Robber DP state: best before previous and best till previous.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. If there is only one house, return it.
     * 2. Solve house robber from index 0 to n - 2.
     * 3. Solve house robber from index 1 to n - 1.
     * 4. Return the larger answer.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [2, 3, 2]
     * Exclude last: [2, 3] -> 3
     * Exclude first: [3, 2] -> 3
     * Return 3.
     */
    public int optimized(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        if (nums.length == 1) {
            return nums[0];
        }

        return Math.max(robLine(nums, 0, nums.length - 2), robLine(nums, 1, nums.length - 1));
    }

    private int tryChoices(int[] nums, int index, boolean[] robbed) {
        if (index == nums.length) {
            // A complete rob/skip pattern is counted only if the circle rule holds.
            return isValidCircleChoice(robbed) ? totalMoney(nums, robbed) : 0;
        }

        // Choice 1: skip this house.
        int skip = tryChoices(nums, index + 1, robbed);
        // Choice 2: rob this house now, then undo before returning.
        robbed[index] = true;
        int rob = tryChoices(nums, index + 1, robbed);
        robbed[index] = false;

        return Math.max(skip, rob);
    }

    private boolean isValidCircleChoice(boolean[] robbed) {
        int n = robbed.length;
        for (int i = 0; i < n; i++) {
            if (robbed[i] && robbed[(i + 1) % n]) {
                return false;
            }
        }
        return true;
    }

    private int totalMoney(int[] nums, boolean[] robbed) {
        int total = 0;
        for (int i = 0; i < nums.length; i++) {
            if (robbed[i]) {
                total += nums[i];
            }
        }
        return total;
    }

    private int robLine(int[] nums, int start, int end) {
        int bestBeforePrevious = 0;
        int bestTillPrevious = 0;

        for (int i = start; i <= end; i++) {
            // Same transition as House Robber on a straight street.
            int robCurrent = bestBeforePrevious + nums[i];
            int skipCurrent = bestTillPrevious;
            int bestTillCurrent = Math.max(robCurrent, skipCurrent);

            bestBeforePrevious = bestTillPrevious;
            bestTillPrevious = bestTillCurrent;
        }

        return bestTillPrevious;
    }
}
