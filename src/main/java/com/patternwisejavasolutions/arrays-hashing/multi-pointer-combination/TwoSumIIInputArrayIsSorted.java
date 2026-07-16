package com.patternwisejavasolutions.arrayshashing.multipointercombination;

public class TwoSumIIInputArrayIsSorted {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Find two numbers in a sorted array that add to target. Return 1-based indices.
 *
 * Sample Input:
 * numbers = [2, 7, 11, 15], target = 9
 *
 * Sample Output:
 * [1, 2]
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Because the array is sorted, the smallest remaining number is on the left
 * and the largest remaining number is on the right. Moving left rightward makes
 * the sum bigger; moving right leftward makes the sum smaller.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * Try every pair exactly like normal Two Sum. This is natural because the
 * problem asks for two numbers, so a beginner first checks all possible pairs.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Choose i.
 * 2. Choose j after i.
 * 3. Return [i + 1, j + 1] when sum equals target.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * numbers = [2, 7, 11, 15], target = 9
 * Pair 2 and 7 sums to 9.
 * Return [1, 2] because this problem uses 1-based positions.
 */

public int[] bruteForce(int[] numbers, int target) {
        for (int i = 0; i < numbers.length; i++) {
            for (int j = i + 1; j < numbers.length; j++) {
                if (numbers[i] + numbers[j] == target) {
                    return new int[] { i + 1, j + 1 };
                }
            }
        }
        return new int[] {};
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is trying pairs even when the sorted order already
 * tells us what to do. If the sum is too small, the left value is the only side
 * we can increase. If the sum is too large, the right value is the side we can
 * decrease.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. left starts at 0, right starts at last index.
 * 2. Compare numbers[left] + numbers[right] with target.
 * 3. Move left if sum is too small.
 * 4. Move right if sum is too large.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * left = 2, right = 15 -> sum 17, too large, move right leftward.
 * left = 2, right = 11 -> sum 13, too large, move right again.
 * left = 2, right = 7 -> sum 9, return [1, 2].
 */

public int[] optimized(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];

            if (sum == target) {
                return new int[] { left + 1, right + 1 };
            }

            if (sum < target) {
                // Need a bigger sum, and moving left rightward increases it.
                left++;
            } else {
                // Need a smaller sum, and moving right leftward decreases it.
                right--;
            }
        }

        return new int[] {};
    }
}
