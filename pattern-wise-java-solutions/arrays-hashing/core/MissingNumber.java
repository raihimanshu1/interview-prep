public class MissingNumber {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * An array contains n different numbers from the range 0 to n. One number
     * from that range is missing. Return the missing number.
     *
     * Sample Input:
     * nums = [3, 0, 1]
     *
     * Sample Output:
     * 2
     */

    /*
     * WHAT TO NOTICE FIRST
     *
     * The sample has length 3, so the full set of allowed numbers should be
     * 0, 1, 2, and 3. The array shows 3, 0, and 1, so the only absent value is
     * 2.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The range 0 to n is predictable. For n = 3, the full list should be
     * [0, 1, 2, 3]. If the given list is [3, 0, 1], the missing value is 2.
     *
     * This problem fits the math observation: compare what the complete list
     * should contain with what the array actually contains.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * For every possible number from 0 to n, search the array to see whether it
     * exists. The one we cannot find is missing.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Try possible missing values from 0 to nums.length.
     * 2. For each possible value, scan the whole array.
     * 3. If that value is not found, return it.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [3, 0, 1]
     * value 0 -> found
     * value 1 -> found
     * value 2 -> not found, return 2.
     */
    public int bruteForce(int[] nums) {
        for (int possibleMissing = 0; possibleMissing <= nums.length; possibleMissing++) {
            boolean found = false;

            for (int num : nums) {
                if (num == possibleMissing) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return possibleMissing;
            }
        }

        return -1;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain point is searching the array for every possible
     * missing value.
     * Instead, use the known sum of 0 + 1 + ... + n.
     *
     * If we subtract the actual array sum from the expected full sum, the only
     * value left is the missing number.
     *
     * Another useful approach is XOR: XOR all indices and values, and the
     * unpaired number remains. The sum method is easier for first learners.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Compute expectedSum = n * (n + 1) / 2.
     * 2. Compute actualSum from nums.
     * 3. Return expectedSum - actualSum.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [3, 0, 1], n = 3
     * expectedSum = 3 * 4 / 2 = 6
     * actualSum = 3 + 0 + 1 = 4
     * missing = 6 - 4 = 2.
     */
    public int optimized(int[] nums) {
        int n = nums.length;
        int expectedSum = n * (n + 1) / 2;
        int actualSum = 0;

        for (int num : nums) {
            // Add every present number so we can compare present total with full total.
            actualSum += num;
        }

        return expectedSum - actualSum;
    }
}
