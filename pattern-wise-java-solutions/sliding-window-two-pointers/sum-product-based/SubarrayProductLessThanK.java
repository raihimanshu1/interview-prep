public class SubarrayProductLessThanK {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Count continuous subarrays whose product is less than k.
     *
     * Sample Input:
     * nums = [10, 5, 2, 6], k = 100
     *
     * Sample Output:
     * 8
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Directly try every subarray.
     * For each start, keep multiplying numbers until product becomes too large.
     */
    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * Choose a start and multiply numbers as the end moves right. Every time
     * the product is still less than k, count that subarray; once it reaches k,
     * stop this start because all numbers are positive.
     */


    /*
     * BRUTE FORCE DRY RUN
     *
     * start = 0:
     * [10] product = 10, count = 1
     * [10,5] product = 50, count = 2
     * [10,5,2] product = 100, not less than 100, stop
     *
     * Continue from later starts.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Pick start.
     * 2. Multiply each next number into product.
     * 3. If product < k, count it.
     * 4. If product >= k, stop this start because numbers are positive.
     *
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     */
    /*
     * BRUTE FORCE IMPLEMENTATION
     */
    public int bruteForce(int[] nums, int k) {
        if (k <= 1) {
            return 0;
        }

        int answer = 0;

        for (int start = 0; start < nums.length; start++) {
            int product = 1;

            for (int end = start; end < nums.length; end++) {
                product *= nums[end];

                if (product < k) {
                    answer++;
                } else {
                    break;
                }
            }
        }

        return answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * With positive numbers, product grows when right moves.
     * If product becomes too large, we divide out nums[left] and move left.
     *
     * When the window from left to right is valid, every subarray ending at right
     * and starting between left and right is also valid.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * nums = [10,5,2,6], k = 100
     *
     * right = 0, window [10], add 1 subarray.
     * right = 1, window [10,5], add 2: [5], [10,5].
     * right = 2, product becomes 100, shrink by removing 10.
     * window [5,2], add 2: [2], [5,2].
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. If k <= 1, no positive product can be less than k.
     * 2. Expand right and multiply product.
     * 3. While product >= k, divide nums[left] and move left.
     * 4. Add right - left + 1 valid subarrays ending at right.
     *
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    /*
     * OTHER USEFUL APPROACHES
     *
     * Taking logarithms can turn products into sums and allow prefix-sum style
     * thinking, but precision becomes awkward. With positive integers, dividing
     * from the left keeps the product window exact.
     */
    /*
     * OPTIMIZED IMPLEMENTATION
     */
    public int optimized(int[] nums, int k) {
        if (k <= 1) {
            return 0;
        }

        int left = 0;
        int product = 1;
        int answer = 0;

        for (int right = 0; right < nums.length; right++) {
            product *= nums[right];

            while (product >= k) {
                // Product is too large, so divide out the left edge to shrink it.
                product /= nums[left];
                left++;
            }

            // Every suffix of the valid window ending at right also has product < k.
            answer += right - left + 1;
        }

        return answer;
    }
}
