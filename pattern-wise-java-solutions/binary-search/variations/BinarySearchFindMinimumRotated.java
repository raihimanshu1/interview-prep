public class BinarySearchFindMinimumRotated {

    /*
     * PROBLEM IN SIMPLE WORDS
     * A sorted array was rotated. Return the smallest number.
     *
     * Sample Input: nums = [3, 4, 5, 1, 2]
     * Sample Output: 1
     *
     * SCHOOL-LEVEL INTUITION:
     * Rotation means one sorted line was cut and swapped. The smallest value is the point where
     * the order restarts. In [3,4,5,1,2], the restart happens at 1.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Ignore rotation and inspect every value. The smallest value seen is the answer.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Start minimum as nums[0].
     * 2. Visit every number.
     * 3. Update minimum when a smaller number appears.
     * 4. Return minimum.
     *
     * BRUTE FORCE DRY RUN
     * [3, 4, 5, 1, 2]
     * minimum = 3 -> see 4 no change -> see 5 no change -> see 1 update -> see 2 no change
     * return 1
     *
     * Time: O(n), Space: O(1)
     */
    public int bruteForce(int[] nums) {
        int minimum = nums[0];

        for (int num : nums) {
            if (num < minimum) {
                minimum = num;
            }
        }

        return minimum;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is checking both sorted parts. In a rotated sorted array, compare
     * mid with the rightmost value. If nums[mid] > nums[right], the smallest value must be to the
     * right. Otherwise mid could be the smallest, so keep mid and search left.
     *
     * Pattern used: Binary Search on a rotated sorted array.
     *
     * OPTIMIZED ALGORITHM
     * 1. Set left = 0, right = last index.
     * 2. While left < right, compute mid.
     * 3. If nums[mid] > nums[right], move left to mid + 1.
     * 4. Otherwise move right to mid.
     * 5. Return nums[left].
     *
     * OPTIMIZED DRY RUN
     * nums = [3, 4, 5, 1, 2]
     * mid = 2, nums[mid] = 5, nums[right] = 2 -> minimum is right of mid
     * left = 3, right = 4, mid = 3, nums[mid] = 1 <= 2 -> keep mid, move right to 3
     * return nums[3] = 1
     *
     * Time: O(log n), Space: O(1)
     */
    public int optimized(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                // The left part is still high, so the drop point must be after mid.
                left = mid + 1;
            } else {
                // Mid belongs to the lower sorted part and may itself be the minimum.
                right = mid;
            }
        }

        return nums[left];
    }
}

