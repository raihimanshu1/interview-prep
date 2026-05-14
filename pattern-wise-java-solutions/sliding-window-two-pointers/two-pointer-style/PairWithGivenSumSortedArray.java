public class PairWithGivenSumSortedArray {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given a sorted array, find whether two numbers add up to target.
     *
     * Sample Input:
     * nums = [1, 2, 4, 6, 8], target = 10
     *
     * Sample Output:
     * true
     *
     * Why?
     * 2 + 8 = 10.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The simplest way is to try every pair.
     * If any pair sum equals target, return true.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Try each number with every number after it. If any pair adds to target,
     * we are done. This ignores the sorted order, but it is the simplest way to
     * define exactly what counts as a successful pair.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,2,4,6,8], target = 10
     *
     * Try 1 with all later numbers.
     * Try 2 with 4, then 6, then 8.
     * 2 + 8 = 10, return true.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick i.
     * 2. Pick j after i.
     * 3. If nums[i] + nums[j] == target, return true.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public boolean bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The array is sorted.
     *
     * If left + right is too small, we need a bigger sum, so move left forward.
     * If left + right is too large, we need a smaller sum, so move right backward.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,2,4,6,8], target = 10
     *
     * left = 1, right = 8, sum = 9, too small -> move left.
     * left = 2, right = 8, sum = 10 -> found.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Put left at start and right at end.
     * 2. Calculate sum.
     * 3. If sum equals target, return true.
     * 4. If sum is smaller, move left.
     * 5. If sum is larger, move right.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * A hash set works even when the array is not sorted. For a sorted array,
     * two pointers are better because they use the sorted order and avoid extra
     * space.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public boolean optimized(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int sum = nums[left] + nums[right];

            if (sum == target) {
                return true;
            }

            if (sum < target) {
                // Need a larger sum; moving left rightward is the only move that can increase it.
                left++;
            } else {
                // Need a smaller sum; moving right leftward is the only move that can decrease it.
                right--;
            }
        }

        return false;
    }
}
