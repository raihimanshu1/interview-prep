

package com.companywisejavasolutions.ebay.solutions;
import java.util.Arrays;

public class ThreeSumClosest {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Pick three numbers whose sum is closest to target. Return that sum.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Sort the array. Fix one number, then use two pointers for the other two.
     * If sum is too small, move left up. If sum is too large, move right down.
     */
    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int closest = nums[0] + nums[1] + nums[2];

        for (int i = 0; i < nums.length - 2; i++) {
            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (Math.abs(target - sum) < Math.abs(target - closest)) {
                    closest = sum;
                }

                if (sum < target) {
                    left++;
                } else if (sum > target) {
                    right--;
                } else {
                    return target;
                }
            }
        }

        return closest;
    }
}
