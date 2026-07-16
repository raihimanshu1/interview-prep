package com.patternwisejavasolutions.binarysearch.core;

public class FirstAndLastPosition {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given a sorted array and a target, return the first and last index where the target appears.
     * If the target is missing, return [-1, -1].
     *
     * Sample Input:
     * nums = [5, 7, 7, 8, 8, 10], target = 8
     *
     * Sample Output:
     * [3, 4]
     *
     * SCHOOL-LEVEL INTUITION:
     * The target can appear many times in a row because the array is sorted. We need the left
     * edge of that block and the right edge of that block.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Walk through the whole array. Whenever we see the target, update the answer. The first
     * time we see it becomes the first position; every later match becomes the latest last position.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Set first = -1 and last = -1.
     * 2. Scan every index from left to right.
     * 3. When nums[index] equals target, set first if it is still -1.
     * 4. Also set last = index for every match.
     * 5. Return [first, last].
     *
     * BRUTE FORCE DRY RUN
     * nums = [5, 7, 7, 8, 8, 10], target = 8
     * index 0,1,2 are not 8
     * index 3 is 8 -> first = 3, last = 3
     * index 4 is 8 -> last = 4
     * index 5 is not 8 -> return [3, 4]
     *
     * Time: O(n), Space: O(1)
     */
    public int[] bruteForce(int[] nums, int target) {
        int first = -1;
        int last = -1;

        for (int index = 0; index < nums.length; index++) {
            if (nums[index] == target) {
                if (first == -1) {
                    // The first match from the left is the left boundary.
                    first = index;
                }

                // Keep moving the right boundary each time we find another target.
                last = index;
            }
        }

        return new int[] { first, last };
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * The brute force pain point is scanning values that are clearly too small or too large.
     * Since the array is sorted, binary search can jump to the target block. To find edges,
     * we do two binary searches: one biased left for the first target and one biased right for the last.
     *
     * Pattern used: Binary Search Boundary Finding.
     *
     * OPTIMIZED ALGORITHM
     * 1. Find the first position by binary searching and continuing left after a match.
     * 2. Find the last position by binary searching and continuing right after a match.
     * 3. Return both positions.
     *
     * OPTIMIZED DRY RUN
     * nums = [5, 7, 7, 8, 8, 10], target = 8
     * first search finds 8 at index 4, keeps left, then finds 8 at index 3 -> first = 3
     * last search finds 8 at index 4, keeps right, then stops -> last = 4
     * return [3, 4]
     *
     * Time: O(log n), Space: O(1)
     */
    public int[] optimized(int[] nums, int target) {
        int first = findBoundary(nums, target, true);
        int last = findBoundary(nums, target, false);
        return new int[] { first, last };
    }

    private int findBoundary(int[] nums, int target, boolean searchFirst) {
        int left = 0;
        int right = nums.length - 1;
        int answer = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                answer = mid;

                if (searchFirst) {
                    // We found the target, but there may be an earlier one on the left.
                    right = mid - 1;
                } else {
                    // We found the target, but there may be a later one on the right.
                    left = mid + 1;
                }
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return answer;
    }
}

