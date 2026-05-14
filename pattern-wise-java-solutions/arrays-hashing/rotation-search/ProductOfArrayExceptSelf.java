public class ProductOfArrayExceptSelf {

    /*
 * PROBLEM IN SIMPLE WORDS
 *
 * Return an array where answer[i] is the product of every number except nums[i].
 *
 * Sample Input:
 * nums = [1, 2, 3, 4]
 *
 * Sample Output:
 * [24, 12, 8, 6]
 */

/*
 * SCHOOL-LEVEL INTUITION
 *
 * For each index, we need everything on its left multiplied by everything on
 * its right. The current number itself must be skipped.
 *
 * That "left part times right part" wording is the clue for prefix and suffix
 * products.
 */

/*
 * APPROACH 1: BRUTE FORCE INTUITION
 *
 * The first natural idea is to stand on each index and multiply every other
 * position one by one.
 */

/*
 * BRUTE FORCE ALGORITHM
 *
 * 1. For each i, set product to 1.
 * 2. For each j, multiply nums[j] if j != i.
 * 3. Store product in answer[i].
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(n)
 */

/*
 * BRUTE FORCE DRY RUN
 *
 * For index 1, multiply nums[0] * nums[2] * nums[3] = 1 * 3 * 4 = 12.
 */

public int[] bruteForce(int[] nums) {
        int[] answer = new int[nums.length];

        for (int i = 0; i < nums.length; i++) {
            int product = 1;
            for (int j = 0; j < nums.length; j++) {
                if (i != j) {
                    product *= nums[j];
                }
            }
            answer[i] = product;
        }

        return answer;
    }

/*
 * APPROACH 2: OPTIMIZED INTUITION
 *
 * The brute force pain point is multiplying the same left and right parts again
 * for nearby indices. Store left products in answer, then multiply by right
 * products in a backward pass.
 */

/*
 * OPTIMIZED ALGORITHM
 *
 * 1. Forward pass: answer[i] gets product of numbers to the left.
 * 2. Backward pass: keep rightProduct and multiply it into answer[i].
 * 3. Update rightProduct by nums[i].
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1) besides output
 */

/*
 * OPTIMIZED DRY RUN
 *
 * nums = [1,2,3,4]
 * Forward pass stores left products: [1,1,2,6].
 * Backward pass uses right products 1,4,12,24.
 * Final answer becomes [24,12,8,6].
 */

public int[] optimized(int[] nums) {
        int[] answer = new int[nums.length];
        int leftProduct = 1;

        for (int i = 0; i < nums.length; i++) {
            // answer[i] first stores product of everything before i.
            answer[i] = leftProduct;
            leftProduct *= nums[i];
        }

        int rightProduct = 1;
        for (int i = nums.length - 1; i >= 0; i--) {
            // answer already has left product; multiply the missing right product.
            answer[i] *= rightProduct;
            // After using it, include nums[i] for positions further left.
            rightProduct *= nums[i];
        }

        return answer;
    }
}
