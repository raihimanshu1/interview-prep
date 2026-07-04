

package com.companywisejavasolutions.ebay.solutions;
public class RemoveOneElementToMakeTheArrayStrictlyIncreasing {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Return true if removing exactly one element can make the array strictly
     * increasing.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Count places where order breaks. One removal can fix at most one bad area.
     * When a break happens, decide whether the left item or right item should be
     * considered removed.
     */

    /*
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public boolean canBeIncreasing(int[] nums) {
        int removals = 0;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] <= nums[i - 1]) {
                removals++;
                if (removals > 1) {
                    return false;
                }

                if (i > 1 && nums[i] <= nums[i - 2]) {
                    nums[i] = nums[i - 1];
                }
            }
        }

        return true;
    }
}
