

package com.companywisejavasolutions.ebay.solutions;
public class SortArrayByParity {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Rearrange the array so all even numbers come before all odd numbers.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Use two pointers. Left searches for an odd number in the even zone. Right
     * searches for an even number in the odd zone. Swap them.
     */
    public int[] sortArrayByParity(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            while (left < right && nums[left] % 2 == 0) left++;
            while (left < right && nums[right] % 2 == 1) right--;

            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
        }

        return nums;
    }
}
