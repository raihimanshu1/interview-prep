public class JumpGame {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [2,3,1,1,4]
     * Sample Output: true
     *
     * Input: nums = [2,3,1,1,4]
     * Each value tells maximum jump length from that index.
     * Output: true because we can reach the last index.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * From each index, try every jump allowed.
     * If any path reaches the last index, answer is true.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to treat each jump length as a choice.
     * From an index with value 3, try jumping 1, 2, or 3 steps and see whether
     * any path eventually reaches the end.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * At index 0 value 2, jump to index 1 or 2.
     * Index 1 value 3 can jump to the end.
     * So return true.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively stand at an index.
     * 2. Try every jump from 1 to nums[index].
     * 3. If any path reaches the end, return true.
     * Time Complexity: exponential
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * Recursion represents exploring the jump tree from the current index.
     */
    public boolean bruteForce(int[] nums) {
        return canReachFrom(nums, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * We do not need the exact path.
     * We only need the farthest index reachable so far.
     * The waste in brute force is revisiting many paths that only prove the same
     * fact: how far we can reach. If current index is beyond farthest, we got stuck.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * nums [2,3,1,1,4]
     * At 0, farthest = 2.
     * At 1, farthest = max(2, 1+3) = 4.
     * Farthest reaches last index.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. farthest = 0.
     * 2. For each index, if index > farthest, return false.
     * 3. Update farthest with index + nums[index].
     * 4. Return true.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * One pass is enough because farthest summarizes all jump choices seen so far.
     */
    public boolean optimized(int[] nums) {
        int farthest = 0;

        for (int index = 0; index < nums.length; index++) {
            if (index > farthest) {
                // No earlier jump can land on this index, so the end is unreachable.
                return false;
            }

            // From every reachable index, extend the best reach if possible.
            farthest = Math.max(farthest, index + nums[index]);
        }

        return true;
    }


    private boolean canReachFrom(int[] nums, int index) {
        if (index >= nums.length - 1) {
            return true;
        }

        for (int jump = 1; jump <= nums[index]; jump++) {
            if (canReachFrom(nums, index + jump)) {
                return true;
            }
        }

        return false;
    }
}
