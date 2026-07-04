
package com.patternwisejavasolutions.bitManipulation;
public class BitMissingNumber {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Numbers are from 0 to n, but one is missing. Return the missing number.
     *
     * Sample Input: nums = [3, 0, 1]
     * Sample Output: 2
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If every expected number is paired with every actual number, all matching
     * pairs cancel. XOR is useful because x ^ x = 0.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Try each possible value from 0 to n and search for it in the array.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For candidate 0 through n, scan nums.
     * 2. If candidate is not found, return it.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * 0 is found. 1 is found. 2 is not found, so return 2.
     */
    public int bruteForce(int[] nums) {
        for (int candidate = 0; candidate <= nums.length; candidate++) {
            boolean found = false;
            for (int num : nums) {
                if (num == candidate) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return candidate;
            }
        }
        return -1;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is searching repeatedly. XOR the full expected
     * range and the actual values. Everything present cancels, leaving the
     * missing value.
     *
     * Other useful approach: expected sum minus actual sum.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start xor as n because n is part of the expected range.
     * 2. For each index i, XOR i and nums[i].
     * 3. Return xor.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [3,0,1], start xor = 3.
     * XOR indices 0,1,2 and values 3,0,1. All pairs cancel except 2.
     */
    public int optimized(int[] nums) {
        int xor = nums.length;
        for (int i = 0; i < nums.length; i++) {
            // Include the expected number i in the cancellation pile.
            xor ^= i;
            // Include the actual number nums[i]; numbers present in both piles vanish.
            xor ^= nums[i];
        }
        return xor;
    }
}
