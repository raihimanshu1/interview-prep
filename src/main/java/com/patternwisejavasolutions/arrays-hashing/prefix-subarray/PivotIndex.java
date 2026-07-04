
package com.patternwisejavasolutions.arraysHashing.prefixSubarray;
public class PivotIndex {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Find an index where the sum on the left equals the sum on the right.
 *
 * Sample Input:
 * nums = [1, 7, 3, 6, 5, 6]
 *
 * Sample Output:
 * 3
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * In [1, 7, 3, 6, 5, 6], index 3 holds value 6, but that 6 is not included on
 * either side. The left sum is 1 + 7 + 3 = 11, and the right sum is 5 + 6 = 11.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * A pivot is a balance point. Values before it are on the left side, and values
 * after it are on the right side. The pivot value itself is not counted on
 * either side.
 *
 * This fits prefix sums because we can keep the left sum while deriving the
 * right sum from the total.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to test each index as the possible balance point,
 * separately computing its left sum and right sum.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. For each index i, sum all values before i.
 * 2. Sum all values after i.
 * 3. If both sums match, return i.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * At index 3, value is 6.
 * left sum = 1 + 7 + 3 = 11.
 * right sum = 5 + 6 = 11.
 * Both sides match, so return 3.
 */

public int bruteForce(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int leftSum = 0;
            int rightSum = 0;

            for (int left = 0; left < i; left++) {
                leftSum += nums[left];
            }
            for (int right = i + 1; right < nums.length; right++) {
                rightSum += nums[right];
            }

            if (leftSum == rightSum) {
                return i;
            }
        }

        return -1;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is recalculating sums for every candidate pivot.
 * If we know total sum and current left sum, then right sum is:
 * total - left sum - nums[i].
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Compute total sum.
 * 2. Walk left to right with leftSum.
 * 3. For each i, rightSum = total - leftSum - nums[i].
 * 4. If equal, return i; otherwise add nums[i] to leftSum.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * total = 28.
 * At index 3, leftSum = 11.
 * rightSum = 28 - 11 - 6 = 11.
 * Equal sides mean index 3 is the pivot.
 */

public int optimized(int[] nums) {
        int total = 0;
        for (int num : nums) {
            total += num;
        }

        int leftSum = 0;
        for (int i = 0; i < nums.length; i++) {
            // Right side is whatever remains after left side and pivot value.
            int rightSum = total - leftSum - nums[i];
            if (leftSum == rightSum) {
                return i;
            }

            // After checking index i, nums[i] becomes part of the left side.
            leftSum += nums[i];
        }

        return -1;
    }
}
