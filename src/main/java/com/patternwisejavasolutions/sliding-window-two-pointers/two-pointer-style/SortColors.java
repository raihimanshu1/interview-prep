
package com.patternwisejavasolutions.slidingWindowTwoPointers.twoPointerStyle;
public class SortColors {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * nums contains only 0, 1, and 2.
     * Sort the array in-place so all 0s come first, then 1s, then 2s.
     *
     * Sample Input:
     * nums = [2, 0, 2, 1, 1, 0]
     *
     * Sample Output:
     * nums = [0, 0, 1, 1, 2, 2]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Since there are only three values, count how many 0s, 1s, and 2s exist.
     * Then overwrite the array in sorted order.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Since there are only three colors, count how many of each color exists.
     * Then rewrite the array as all 0s, then all 1s, then all 2s.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [2,0,2,1,1,0]
     *
     * zeroes = 2
     * ones = 2
     * twos = 2
     *
     * Write two 0s, then two 1s, then two 2s.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Count each color.
     * 2. Fill the array with 0 count times.
     * 3. Fill the array with 1 count times.
     * 4. Fill the array with 2 count times.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public void bruteForce(int[] nums) {
        int zeroes = 0;
        int ones = 0;
        int twos = 0;

        for (int num : nums) {
            if (num == 0) {
                zeroes++;
            } else if (num == 1) {
                ones++;
            } else {
                twos++;
            }
        }

        int index = 0;

        while (zeroes > 0) {
            nums[index] = 0;
            index++;
            zeroes--;
        }

        while (ones > 0) {
            nums[index] = 1;
            index++;
            ones--;
        }

        while (twos > 0) {
            nums[index] = 2;
            index++;
            twos--;
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Use the Dutch National Flag idea.
     *
     * Keep three zones:
     * - before low: all 0s
     * - after high: all 2s
     * - current scans the unknown middle
     *
     * When current sees 0, send it to the left.
     * When current sees 2, send it to the right.
     * When current sees 1, leave it in the middle.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [2,0,1]
     *
     * current sees 2.
     * Swap with high:
     * [1,0,2]
     *
     * current now sees 1, move current.
     * current sees 0, swap with low:
     * [0,1,2]
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. low = 0, current = 0, high = n - 1.
     * 2. If nums[current] == 0, swap with low, move both low and current.
     * 3. If nums[current] == 1, just move current.
     * 4. If nums[current] == 2, swap with high and move high only.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Counting the number of zeroes, ones, and twos is a clean two-pass solution.
     * The Dutch National Flag method below does the same rearrangement in one
     * pass and constant space.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public void optimized(int[] nums) {
        int low = 0;
        int current = 0;
        int high = nums.length - 1;

        while (current <= high) {
            if (nums[current] == 0) {
                // Send 0 to the left zone; the swapped-in value is already processed or current.
                swap(nums, low, current);
                low++;
                current++;
            } else if (nums[current] == 1) {
                // 1 belongs in the middle zone, so just move past it.
                current++;
            } else {
                // Send 2 to the right zone; recheck current because the swapped-in value is unknown.
                swap(nums, current, high);
                high--;
            }
        }
    }

    private void swap(int[] nums, int first, int second) {
        int temp = nums[first];
        nums[first] = nums[second];
        nums[second] = temp;
    }
}
