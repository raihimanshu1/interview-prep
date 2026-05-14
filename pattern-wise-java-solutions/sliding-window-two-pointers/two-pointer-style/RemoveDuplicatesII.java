public class RemoveDuplicatesII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * nums is sorted.
     * Remove extra duplicates so each number appears at most twice.
     * Return the new length.
     *
     * Sample Input:
     * nums = [1,1,1,2,2,3]
     *
     * Sample Output:
     * 5
     *
     * First five values become [1,1,2,2,3].
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Because the array is sorted, equal numbers stay together.
     * We can count how many times the current number appeared and copy only the
     * first two occurrences.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Walk through the sorted array and count how many times the current value
     * has appeared in its group. Copy it to a temporary array only for the first
     * and second appearances.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,1,1,2,2,3]
     *
     * For 1: keep first 1, keep second 1, skip third 1.
     * For 2: keep both 2s.
     * For 3: keep 3.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create copy array.
     * 2. Track count of current repeated number.
     * 3. Copy only when count <= 2.
     * 4. Copy kept values back and return length.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] nums) {
        if (nums.length <= 2) {
            return nums.length;
        }

        int[] copy = new int[nums.length];
        int write = 0;
        int countOfCurrent = 0;

        for (int read = 0; read < nums.length; read++) {
            if (read == 0 || nums[read] != nums[read - 1]) {
                countOfCurrent = 1;
            } else {
                countOfCurrent++;
            }

            if (countOfCurrent <= 2) {
                copy[write] = nums[read];
                write++;
            }
        }

        for (int index = 0; index < write; index++) {
            nums[index] = copy[index];
        }

        return write;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The first two numbers are always allowed.
     *
     * For every next number, compare it with the number two positions before the
     * write pointer.
     *
     * If nums[read] == nums[write - 2], keeping it would create three copies.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,1,1,2,2,3]
     *
     * write = 2.
     * read sees third 1.
     * nums[read] == nums[write - 2], so skip it.
     *
     * read sees 2.
     * 2 != nums[0], so keep it.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. If length <= 2, return length.
     * 2. Start write at 2.
     * 3. For read from 2 to end:
     *    keep nums[read] only if it differs from nums[write - 2].
     * 4. Return write.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A temporary list can collect each number up to two times, then copy the
     * list back into the array. The in-place version uses the sorted order to
     * decide whether nums[i] can be written safely.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] nums) {
        if (nums.length <= 2) {
            return nums.length;
        }

        int write = 2;

        for (int read = 2; read < nums.length; read++) {
            if (nums[read] != nums[write - 2]) {
                // If it differs from the value two kept spots back, this cannot be a third copy.
                nums[write] = nums[read];
                write++;
            }
        }

        return write;
    }
}
