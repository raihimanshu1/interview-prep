package com.patternwisejavasolutions.slidingwindowtwopointers.twopointerstyle;

public class RemoveDuplicatesFromSortedArray {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1,1,2]
     * Sample Output: 2, nums starts as [1,2]
     *
     * Remove duplicates in-place from a sorted array and return the number of
     * unique values.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Since the array is sorted, equal values stand next to each other. We only
     * need to keep the first copy of every new value.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Use another array/list to collect unique values, then copy them back into
     * nums.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create a temporary array.
     * 2. Add nums[0].
     * 3. For every next number, add it only if it differs from the previous one.
     * 4. Copy temporary values back to nums and return count.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,1,2]
     * temp gets 1.
     * second 1 is skipped.
     * 2 is added.
     * Copy [1,2] back, return 2.
     */

    public int bruteForce(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        int[] unique = new int[nums.length];
        int count = 0;
        unique[count++] = nums[0];

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                unique[count++] = nums[i];
            }
        }

        for (int i = 0; i < count; i++) {
            nums[i] = unique[i];
        }

        return count;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * The brute force copy array is only storing the unique prefix. We can use
     * the front part of nums itself for that prefix. One pointer reads every
     * value, and one pointer writes the next unique value.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. write = 1 because nums[0] is always kept when array is not empty.
     * 2. read from index 1 to end.
     * 3. When nums[read] differs from nums[read - 1], write it at nums[write].
     * 4. Return write.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [0,0,1,1,2]
     * Keep 0.
     * read sees first 1, write it at index 1.
     * read sees first 2, write it at index 2.
     * return 3.
     */

    public int optimized(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        int write = 1;

        for (int read = 1; read < nums.length; read++) {
            if (nums[read] != nums[read - 1]) {
                // nums[0..write-1] always stores the unique prefix.
                nums[write] = nums[read];
                write++;
            }
        }

        return write;
    }
}
