package com.patternwisejavasolutions.arrayshashing.rotationsearch;

public class FindMinimumInRotatedSortedArray {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Find the smallest value in a sorted array that was rotated.
 *
 * Sample Input:
 * nums = [3, 4, 5, 1, 2]
 *
 * Sample Output:
 * 1
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * A rotated sorted array is two sorted pieces joined together. The smallest
 * value is the turning point where the order "drops."
 *
 * In [3, 4, 5, 1, 2], the drop is from 5 to 1, so 1 is the minimum.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to ignore the rotation and scan the whole array,
 * keeping the smallest value seen.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Set minimum to nums[0].
 * 2. Compare every number with minimum.
 * 3. Return the smallest.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * Compare 3, 4, 5, 1, 2.
 * minimum starts at 3, stays 3 after 4 and 5, then becomes 1.
 * Return 1.
 */

public int bruteForce(int[] nums) {
        int minimum = nums[0];
        for (int num : nums) {
            minimum = Math.min(minimum, num);
        }
        return minimum;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute scan ignores the sorted structure. Binary search removes that
 * waste by comparing middle with the right edge.
 *
 * If nums[mid] is greater than nums[right], the drop must be to the right.
 * Otherwise mid is in the sorted right piece, so the minimum is at mid or left
 * of mid.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. left = 0, right = last index.
 * 2. While left < right, compute mid.
 * 3. If nums[mid] > nums[right], move left to mid + 1.
 * 4. Else move right to mid.
 * 5. Return nums[left].
 *
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * nums = [3,4,5,1,2]
 * mid value is 5 and right value is 2.
 * Since 5 > 2, the minimum is to the right of mid.
 * Now search [1,2].
 */

public int optimized(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                // Minimum is in the unsorted right half.
                left = mid + 1;
            } else {
                // mid may be the minimum, so keep it in the search range.
                right = mid;
            }
        }

        return nums[left];
    }
}
