public class CountSubarraysWithSumLessThanOrEqualK {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Count subarrays whose sum is less than or equal to k.
     * This optimized sliding-window version assumes non-negative numbers.
     *
     * Sample Input:
     * nums = [1, 2, 3], k = 3
     *
     * Sample Output:
     * 4
     *
     * Valid subarrays:
     * [1], [2], [3], [1,2]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Try every subarray and calculate its sum.
     * Count it when sum <= k.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Pick a start and extend the subarray to the right while maintaining its
     * sum. Each time the sum is small enough, count that continuous group.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * nums = [1,2,3], k = 3
     *
     * start = 0:
     * [1] sum = 1, count = 1
     * [1,2] sum = 3, count = 2
     * [1,2,3] sum = 6, not counted
     *
     * start = 1:
     * [2] sum = 2, count = 3
     * [2,3] sum = 5, not counted
     *
     * start = 2:
     * [3] sum = 3, count = 4
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Choose start.
     * 2. Expand end and keep sum.
     * 3. Count when sum <= k.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] nums, int k) {
        int answer = 0;

        for (int start = 0; start < nums.length; start++) {
            int sum = 0;

            for (int end = start; end < nums.length; end++) {
                sum += nums[end];

                if (sum <= k) {
                    answer++;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * For non-negative numbers:
     * - Adding nums[right] can only increase the sum.
     * - Removing nums[left] can only decrease the sum.
     *
     * So if the window sum becomes too big, we shrink from the left.
     *
     * Once window [left..right] is valid, every subarray ending at right and
     * starting from left to right is also valid.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [1,2,3], k = 3
     *
     * right = 0, window [1], add 1.
     * right = 1, window [1,2], add 2: [2], [1,2].
     * right = 2, sum = 6, shrink until window [3], add 1.
     * Total = 4.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Expand right and add to sum.
     * 2. While sum > k, remove nums[left] and move left.
     * 3. Add right - left + 1 valid subarrays ending at right.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * If negative numbers were allowed, this shrinking-window idea would break
     * and a prefix-sum structure would be needed. For non-negative numbers, once
     * a window sum is small enough, every shorter suffix is also valid.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] nums, int k) {
        int left = 0;
        int sum = 0;
        int answer = 0;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];

            while (left <= right && sum > k) {
                // For non-negative numbers, removing from left is the only way to reduce sum.
                sum -= nums[left];
                left++;
            }

            // All subarrays ending at right and starting inside the window are valid.
            answer += right - left + 1;
        }

        return answer;
    }
}
