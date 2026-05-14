public class RunningSumOfArray {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Build an array where each position stores the sum from the start up to that position.
 *
 * Sample Input:
 * nums = [1, 2, 3, 4]
 *
 * Sample Output:
 * [1, 3, 6, 10]
 */

/*
 * WHAT TO NOTICE FIRST
 *
 * The sample output [1, 3, 6, 10] keeps each earlier total: 3 is 1 + 2, 6 is
 * 1 + 2 + 3, and 10 is 1 + 2 + 3 + 4. Every answer position includes all
 * numbers to its left.
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * Running sum is like a scoreboard that keeps adding the latest score to the
 * previous total. Each answer position is the full total from the start up to
 * that position.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to answer each index separately: for index i, add
 * nums[0] through nums[i] from scratch.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. Create answer array.
 * 2. For each index i, sum nums[0] through nums[i].
 * 3. Store that sum in answer[i].
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(n)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * nums = [1,2,3,4]
 * index 0: 1
 * index 1: 1 + 2 = 3
 * index 2: 1 + 2 + 3 = 6
 */

public int[] bruteForce(int[] nums) {
        int[] answer = new int[nums.length];

        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = 0; j <= i; j++) {
                sum += nums[j];
            }
            answer[i] = sum;
        }

        return answer;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is recomputing old sums. The previous answer
 * already has the sum up to the previous index, so add only the current number.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Keep a variable runningTotal.
 * 2. Add each number to runningTotal.
 * 3. Store runningTotal in answer.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */

/*
 * OPTIMIZED DRY RUN
 *
 * runningTotal starts 0. Add 1 -> 1, add 2 -> 3, add 3 -> 6, add 4 -> 10.
 */

public int[] optimized(int[] nums) {
        int[] answer = new int[nums.length];
        int runningTotal = 0;

        for (int i = 0; i < nums.length; i++) {
            // Reuse the previous total instead of summing from the start again.
            runningTotal += nums[i];
            answer[i] = runningTotal;
        }

        return answer;
    }
}
