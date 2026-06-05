package wellsfargo.solutions;

public class BinarySearchRotatedArray {

    /*
     * PROBLEM IN SIMPLE WORDS
     * A sorted array was rotated. Return the index of target, or -1 if target is missing.
     *
     * Sample Input: nums = [4, 5, 6, 7, 0, 1, 2], target = 0
     * Sample Output: 4
     *
     * SCHOOL-LEVEL INTUITION:
     * Rotation breaks one sorted array into two sorted halves. At every middle index, at least one
     * side is still properly sorted, and that sorted side tells us whether the target can live there.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Check every index. Rotation does not matter if we inspect all values.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Loop through all indices.
     * 2. If nums[index] equals target, return index.
     * 3. Return -1 after the loop.
     *
     * BRUTE FORCE DRY RUN
     * [4, 5, 6, 7, 0, 1, 2], target = 0
     * indices 0..3 do not match; index 4 is 0; return 4
     *
     * Time: O(n), Space: O(1)
     */
    public int bruteForce(int[] nums, int target) {
        for (int index = 0; index < nums.length; index++) {
            if (nums[index] == target) {
                return index;
            }
        }

        return -1;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is not using order. Even after rotation, one half around mid is
     * sorted. If the target range fits inside that sorted half, search there; otherwise search the
     * other half.
     *
     * Pattern used: Modified Binary Search.
     *
     * OPTIMIZED ALGORITHM
     * 1. Use left and right pointers.
     * 2. Compute mid.
     * 3. If nums[mid] is target, return mid.
     * 4. Decide whether left half or right half is sorted.
     * 5. Keep the half that can contain target and discard the other.
     *
     * OPTIMIZED DRY RUN
     * nums = [4,5,6,7,0,1,2], target = 0
     * mid = 3 value 7; left half [4..7] sorted, target not inside -> move right
     * mid = 5 value 1; left half [0..1] sorted, target inside -> move left
     * mid = 4 value 0 -> return 4
     *
     * Time: O(log n), Space: O(1)
     */
    public int optimized(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            }

            if (nums[left] <= nums[mid]) {
                // Left half is sorted, so we can test whether target belongs inside it.
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // Right half is sorted, so we can test whether target belongs inside it.
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return -1;
    }
}

