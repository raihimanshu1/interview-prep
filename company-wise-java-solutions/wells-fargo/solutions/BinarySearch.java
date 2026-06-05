package wellsfargo.solutions;

public class BinarySearch {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given a sorted array and a target number, return the index where the target is found.
     * If the target is not present, return -1.
     *
     * Sample Input:
     * nums = [-1, 0, 3, 5, 9, 12], target = 9
     *
     * Sample Output:
     * 4
     *
     * SCHOOL-LEVEL INTUITION:
     * Imagine a dictionary. Because words are sorted, you do not read every word from page 1.
     * You open near the middle and decide whether to go left or right. A sorted array gives us
     * the same power: one middle check can remove half of the places where the target cannot be.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * The simplest idea is to ignore that the array is sorted. Check index 0, then index 1,
     * then index 2, and keep going until we either find the target or run out of numbers.
     * This is correct because every possible answer is inspected.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Start at index 0.
     * 2. Compare nums[index] with target.
     * 3. If they are equal, return index.
     * 4. If the loop finishes, return -1.
     *
     * BRUTE FORCE DRY RUN
     * nums = [-1, 0, 3, 5, 9, 12], target = 9
     * index 0 -> -1, not target
     * index 1 -> 0, not target
     * index 2 -> 3, not target
     * index 3 -> 5, not target
     * index 4 -> 9, found; return 4
     *
     * Time: O(n), Space: O(1)
     */
    public int bruteForce(int[] nums, int target) {
        for (int index = 0; index < nums.length; index++) {
            // Every index is a possible answer in the brute force approach.
            if (nums[index] == target) {
                return index;
            }
        }

        // Reaching here means no inspected element matched the target.
        return -1;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is that it checks values even when the sorted order could
     * already tell us they are impossible. Binary search fixes that.
     *
     * Pattern used: Binary Search.
     * Check the middle. If nums[mid] is smaller than target, every value to the left of mid
     * is also too small, so we move right. If nums[mid] is larger, every value to the right
     * is too large, so we move left.
     *
     * OPTIMIZED ALGORITHM
     * 1. Keep left at 0 and right at nums.length - 1.
     * 2. While left <= right, compute mid.
     * 3. If nums[mid] == target, return mid.
     * 4. If nums[mid] < target, move left to mid + 1.
     * 5. Otherwise move right to mid - 1.
     * 6. Return -1 if the search window becomes empty.
     *
     * OPTIMIZED DRY RUN
     * nums = [-1, 0, 3, 5, 9, 12], target = 9
     * left = 0, right = 5, mid = 2, nums[mid] = 3 -> target is bigger, search right half
     * left = 3, right = 5, mid = 4, nums[mid] = 9 -> found; return 4
     *
     * Time: O(log n), Space: O(1)
     */
    public int optimized(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            // This middle formula avoids overflow and divides the remaining search area in half.
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            }

            if (nums[mid] < target) {
                // Sorted order proves the left part, including mid, is too small.
                left = mid + 1;
            } else {
                // Sorted order proves the right part, including mid, is too large.
                right = mid - 1;
            }
        }

        return -1;
    }
}

