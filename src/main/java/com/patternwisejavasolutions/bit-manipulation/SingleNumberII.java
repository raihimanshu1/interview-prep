package com.patternwisejavasolutions.bitmanipulation;

public class SingleNumberII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [2, 2, 3, 2]
     * Sample Output: 3
     *
     * Every number appears three times except one number. Return the one that
     * appears once.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If every repeated number appears exactly three times, then each bit from
     * those repeated numbers also appears in groups of three.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Count each number by scanning the array.
     * The number with count one is the answer.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick a candidate.
     * 2. Count its occurrences.
     * 3. Return it if count is 1.
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [2, 2, 3, 2]
     * Count 2 -> 3 times.
     * Count 3 -> 1 time, return 3.
     */
    public int bruteForce(int[] nums) {
        for (int candidate : nums) {
            int count = 0;
            for (int num : nums) {
                if (num == candidate) {
                    count++;
                }
            }
            if (count == 1) {
                return candidate;
            }
        }
        return -1;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Count how many times each bit is set.
     * A bit belonging to tripled numbers has count divisible by 3.
     *
     * Think bit by bit, like counting votes:
     * At bit position 1, all numbers that appear three times contribute either 0 votes or 3 votes.
     * Only the unique number can leave a remainder after dividing by 3.
     *
     * Java int has 32 bits, including the sign bit.
     * Checking all 32 positions lets us rebuild negative answers correctly too.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. For each bit position 0 to 31, count set bits.
     * 2. If count % 3 is 1, set that bit in the answer.
     * 3. Return the rebuilt number.
     * Time Complexity: O(32 * n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * For [2, 2, 3, 2], bit counts from 2 appear three times and vanish mod 3.
     * Bits of 3 remain.
     */
    public int optimized(int[] nums) {
        int answer = 0;
        for (int bit = 0; bit < 32; bit++) {
            int count = 0;
            for (int num : nums) {
                if (((num >> bit) & 1) == 1) {
                    /*
                     * Shift this bit down to the last position, then & 1 asks:
                     * "Is this bit turned on for this number?"
                     */
                    count++;
                }
            }
            if (count % 3 != 0) {
                /*
                 * Groups of three disappear after % 3.
                 * If a remainder exists, the unique number owns this bit.
                 * OR turns that bit on in the answer while keeping earlier bits.
                 */
                answer |= (1 << bit);
            }
        }
        return answer;
    }
}
