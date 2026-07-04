
package com.patternwisejavasolutions.slidingWindowTwoPointers.twoPointerStyle;
public class RemoveElementInPlace {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Remove all occurrences of val from nums in-place.
     * Return the number of remaining elements.
     *
     * Sample Input:
     * nums = [3, 2, 2, 3], val = 3
     *
     * Sample Output:
     * 2
     *
     * First two positions become [2, 2].
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * We only care about values not equal to val.
     * The direct way is to put all allowed values in another array, then copy back.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Build the kept part in a helper array. Read every number, copy it only if
     * it is not val, then copy those kept values back into nums.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [3,2,2,3], val = 3
     *
     * Keep 2.
     * Keep 2.
     *
     * copy = [2,2,...]
     * New length = 2.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create copy array.
     * 2. Add nums[i] only when nums[i] != val.
     * 3. Copy kept values back to nums.
     * 4. Return count of kept values.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] nums, int val) {
        int[] copy = new int[nums.length];
        int count = 0;

        for (int num : nums) {
            if (num != val) {
                copy[count] = num;
                count++;
            }
        }

        for (int index = 0; index < count; index++) {
            nums[index] = copy[index];
        }

        return count;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Use the original array itself as the storage area.
     *
     * write tells us where the next allowed value should be placed.
     * read scans all values.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [3,2,2,3], val = 3
     *
     * read 0 sees 3 -> skip.
     * read 1 sees 2 -> write nums[0] = 2.
     * read 2 sees 2 -> write nums[1] = 2.
     *
     * Return write = 2.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Set write = 0.
     * 2. For each read index, if nums[read] is not val, store it at nums[write].
     * 3. Move write after storing.
     * 4. Return write as the new length.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * If the order of remaining values does not matter, swap each removed value
     * with the end of the active range. The stable two-pointer version below
     * keeps the original order of the kept values.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] nums, int val) {
        int write = 0;

        for (int read = 0; read < nums.length; read++) {
            if (nums[read] != val) {
                // Keep this value by writing it into the next valid front slot.
                nums[write] = nums[read];
                write++;
            }
        }

        return write;
    }
}
