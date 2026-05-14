public class MoveZeroes {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Move all zeroes to the end of the array.
     * Keep the order of non-zero numbers the same.
     * Do it in-place.
     *
     * Sample Input:
     * nums = [0, 1, 0, 3, 12]
     *
     * Sample Output:
     * nums = [1, 3, 12, 0, 0]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The easiest idea is to create a new array.
     * First copy all non-zero numbers.
     * The remaining places naturally stay zero.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * First build the final order somewhere easier: copy every non-zero value
     * into a new array from the front. The untouched positions stay zero, which
     * gives exactly the required result.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [0,1,0,3,12]
     *
     * Copy non-zero values:
     * copy = [1,3,12,0,0]
     *
     * Then copy it back into nums.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Create a copy array.
     * 2. Put every non-zero number into copy from the front.
     * 3. Copy all values back to nums.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public void bruteForce(int[] nums) {
        int[] copy = new int[nums.length];
        int write = 0;

        for (int num : nums) {
            if (num != 0) {
                copy[write] = num;
                write++;
            }
        }

        for (int index = 0; index < nums.length; index++) {
            nums[index] = copy[index];
        }
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * We do not need a second array.
     *
     * Keep a write pointer.
     * It tells us the next position where a non-zero number should go.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [0,1,0,3,12]
     *
     * read sees 1, write is 0.
     * Swap nums[read] with nums[write]:
     * [1,0,0,3,12]
     *
     * read sees 3, write is 1.
     * Swap:
     * [1,3,0,0,12]
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. read scans every index.
     * 2. write points to the next non-zero position.
     * 3. When nums[read] is non-zero, swap it with nums[write].
     * 4. Move write forward.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * You can copy all non-zero values into a temporary array and then fill the
     * rest with zeroes. That is very easy to reason about, while the two-pointer
     * version keeps the same result in-place.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public void optimized(int[] nums) {
        int write = 0;

        for (int read = 0; read < nums.length; read++) {
            if (nums[read] != 0) {
                // Move this non-zero into the next front position reserved for real values.
                int temp = nums[write];
                nums[write] = nums[read];
                nums[read] = temp;

                write++;
            }
        }
    }
}
