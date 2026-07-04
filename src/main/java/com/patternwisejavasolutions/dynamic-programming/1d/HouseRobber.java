
package com.patternwisejavasolutions.dynamicProgramming.1d;
public class HouseRobber {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [2,7,9,3,1]
     * Sample Output: 12
     *
     * Input: nums = [2,7,9,3,1]
     * You cannot rob adjacent houses.
     * Output: 12 by robbing 2 + 9 + 1.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * At each house, we have two choices:
     * Skip it, or rob it.
     * If we rob it, we must jump over the next adjacent house.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * At each house, branch into two stories: rob this house and jump two
     * spots, or skip it and move one spot. The same future index is reached
     * from many stories, which is why the raw recursion is slow.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * At house 0 value 2:
     * Choice 1 rob it -> then solve from house 2.
     * Choice 2 skip it -> solve from house 1.
     * Take the better choice.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively decide from index.
     * 2. rob = nums[index] + solve(index + 2).
     * 3. skip = solve(index + 1).
     * 4. Return max.
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * This method keeps the rob/skip recursion tree visible.
     */
    public int bruteForce(int[] nums) {
        return robFrom(nums, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The same index is solved many times.
     * Use DP from left to right:
     * best till current house = max(skip current, rob current).
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * nums = [2,7,9]
     * After 2: best = 2
     * After 7: best = 7
     * At 9: max(previous 7, 2 + 9) = 11
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. prevTwo = best before previous house.
     * 2. prevOne = best till previous house.
     * 3. current = max(prevOne, prevTwo + nums[i]).
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * The two variables are compressed DP states from the previous houses.
     */
    public int optimized(int[] nums) {
        int bestBeforePrevious = 0;
        int bestTillPrevious = 0;

        for (int money : nums) {
            // If we rob current, we must add it to the best before the previous house.
            int robCurrent = bestBeforePrevious + money;
            // If we skip current, the best stays what it was at the previous house.
            int skipCurrent = bestTillPrevious;
            int bestTillCurrent = Math.max(robCurrent, skipCurrent);

            bestBeforePrevious = bestTillPrevious;
            bestTillPrevious = bestTillCurrent;
        }

        return bestTillPrevious;
    }


    private int robFrom(int[] nums, int index) {
        if (index >= nums.length) {
            return 0;
        }

        int robCurrent = nums[index] + robFrom(nums, index + 2);
        int skipCurrent = robFrom(nums, index + 1);
        return Math.max(robCurrent, skipCurrent);
    }
}
