public class SearchInRotatedSortedArray {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Find the index of target in a rotated sorted array, or return -1.
 *
 * Sample Input:
 * nums = [4, 5, 6, 7, 0, 1, 2], target = 0
 *
 * Sample Output:
 * 4
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Even after rotation, at least one half around the middle is still sorted.
 * Binary search can use that sorted half like a signboard: if target is not
 * inside that sorted range, it must be in the other half.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to scan each value until target is found. This is
 * easy to trust because rotation does not matter for a full scan.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Loop over every index.
 * 2. If nums[i] equals target, return i.
 * 3. Return -1 if the loop ends.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * Check 4, 5, 6, 7, then 0.
 * 0 is found at index 4, so return 4.
 */

public int bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                return i;
            }
        }
        return -1;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute scan ignores order. At each mid, decide which half is sorted. If
 * target falls inside the sorted half, search there; otherwise throw that half
 * away and search the other half.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Use left and right pointers.
 * 2. Find mid.
 * 3. If nums[mid] is target, return mid.
 * 4. Decide whether left half or right half is sorted.
 * 5. Keep the half where target can exist.
 *
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * nums = [4,5,6,7,0,1,2], target = 0
 * mid value 7. Left half [4,5,6,7] is sorted.
 * Target 0 is not between 4 and 7, so search the right half.
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
                // Left half is sorted, so we can test whether target fits there.
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // Right half is sorted, so use its range to decide.
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
