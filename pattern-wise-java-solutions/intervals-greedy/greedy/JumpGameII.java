public class JumpGameII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: nums = [2,3,1,1,4]
     * Sample Output: 2
     *
     * Input: nums = [2,3,1,1,4]
     * Output: 2 minimum jumps: 0 -> 1 -> 4.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try all possible jumps and choose the path with fewer jumps.
     * This is clear but expensive.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to try every jump length from the current
     * index, solve the rest of the path, and take the smallest jump count.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * From index 0, try jump to 1 and jump to 2.
     * Path through index 1 reaches end in one more jump.
     * So total jumps = 2.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. Recursively try every jump from current index.
     * 2. Return 1 + minimum jumps from reachable indices.
     * Time Complexity: exponential
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * Recursion asks: "if I stand here, what is the fewest jumps from here?"
     */
    public int bruteForce(int[] nums) {
        return minJumps(nums, 0);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Think level by level like BFS.
     * All indices inside current range are reachable with the same number of jumps.
     * Brute force wastes time comparing many paths inside the same range. We only
     * need the farthest next range those paths can create.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Start range [0,0]. From 0, farthest = 2, jumps = 1.
     * Next range [1,2]. From index 1, farthest = 4, jumps = 2.
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. currentEnd marks end of current jump range.
     * 2. farthest tracks next range.
     * 3. When index reaches currentEnd, increase jumps and move currentEnd to farthest.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * currentEnd is the boundary of the current jump count; crossing it means
     * committing one more jump.
     */
    public int optimized(int[] nums) {
        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;

        for (int index = 0; index < nums.length - 1; index++) {
            // Best landing point reachable using one more jump from this range.
            farthest = Math.max(farthest, index + nums[index]);

            if (index == currentEnd) {
                // We finished all positions reachable with the current jump count.
                jumps++;
                currentEnd = farthest;
            }
        }

        return jumps;
    }


    private int minJumps(int[] nums, int index) {
        if (index >= nums.length - 1) {
            return 0;
        }

        int best = Integer.MAX_VALUE / 2;

        for (int jump = 1; jump <= nums[index]; jump++) {
            best = Math.min(best, 1 + minJumps(nums, index + jump));
        }

        return best;
    }
}
