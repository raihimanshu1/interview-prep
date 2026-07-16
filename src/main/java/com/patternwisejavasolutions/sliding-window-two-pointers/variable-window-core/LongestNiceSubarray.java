package com.patternwisejavasolutions.slidingwindowtwopointers.variablewindowcore;

public class LongestNiceSubarray {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [1,3,8,48,10]
     * Sample Output: 3
     *
     * Find the longest subarray where every pair of numbers has bitwise AND 0.
     * That means no two numbers in the subarray share a 1-bit.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Think of each number as using some switches. A nice group is valid only
     * when no switch is used by two different numbers.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Start every possible subarray and keep adding numbers while their bits do
     * not clash with bits already used.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. For each start index, set usedBits = 0.
     * 2. Move end forward.
     * 3. If nums[end] shares a bit with usedBits, stop this start.
     * 4. Otherwise add its bits and update best length.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,3,8]
     * start 0: used 1. Next 3 shares bit with 1, stop.
     * start 1: used 3, then 8 has no shared bit, length 2.
     */

    public int bruteForce(int[] nums) {
        int best = 0;

        for (int start = 0; start < nums.length; start++) {
            int usedBits = 0;

            for (int end = start; end < nums.length; end++) {
                if ((usedBits & nums[end]) != 0) {
                    break;
                }
                usedBits |= nums[end];
                best = Math.max(best, end - start + 1);
            }
        }

        return best;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Brute force restarts the used-bit check for every start. A sliding window
     * keeps the current used bits. If the new number clashes with used bits,
     * remove numbers from the left until the clash disappears.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Keep usedBits for the current window.
     * 2. For each right index, while nums[right] clashes, remove nums[left].
     * 3. Add nums[right] to usedBits.
     * 4. Update best length.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,3,8,48,10]
     * Add 1. 3 clashes, remove 1, add 3.
     * Add 8, add 48, window length 3.
     * 10 clashes with 8, shrink before adding.
     */

    public int optimized(int[] nums) {
        int left = 0;
        int usedBits = 0;
        int best = 0;

        for (int right = 0; right < nums.length; right++) {
            while ((usedBits & nums[right]) != 0) {
                // Bits are unique inside the window, so XOR cleanly removes nums[left].
                usedBits ^= nums[left];
                left++;
            }

            // No remaining number shares a 1-bit with nums[right], so add its bits.
            usedBits |= nums[right];
            best = Math.max(best, right - left + 1);
        }

        return best;
    }
}
