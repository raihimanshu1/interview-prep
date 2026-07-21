package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Product of Array Except Self
 * (Problem number: 27 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an integer array nums, return an array answer such that answer[i] is
 * equal to the product of all elements of nums except nums[i].
 * Do NOT use division.
 *
 * Example 1:
 * Input: nums = [1, 2, 3, 4]
 * Output: [24, 12, 8, 6]
 * Explanation: 24=2*3*4, 12=1*3*4, 8=1*2*4, 6=1*2*3
 *
 * Example 2:
 * Input: nums = [-1, 1, 0, -3, 3]
 * Output: [0, 0, 9, 0, 0]
 * Explanation: Product contains 0, so most results are 0.
 *
 * CONSTRAINTS:
 * - 2 <= nums.length <= 10^5
 * - -30 <= nums[i] <= 30
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each element, multiply all others
 * OPTIMIZED:   O(n)   - Left pass then right pass (no extra arrays)
 */

public class LS10_ProductOfArrayExceptSelf {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] nums = {1, 2, 3, 4};

        // 2. --- BRUTE FORCE APPROACH ---
        int[] bruteResult = productExceptSelfBruteForce(nums.clone());
        System.out.print("Brute Force: ");
        printArray(bruteResult); // Expected: [24, 12, 8, 6]

        // 3. --- OPTIMIZED APPROACH ---
        int[] optimizedResult = productExceptSelfOptimized(nums.clone());
        System.out.print("Optimized: ");
        printArray(optimizedResult); // Expected: [24, 12, 8, 6]

        // 4. Test with zero.
        int[] nums2 = {-1, 1, 0, -3, 3};
        System.out.print("With Zero: ");
        printArray(productExceptSelfOptimized(nums2)); // Expected: [0, 0, 9, 0, 0]
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each index i, multiply all elements except nums[i].
    // Time:  O(n^2)  |  Space: O(1) extra
    // -------------------------------------------------------------------------
    public static int[] productExceptSelfBruteForce(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        // 1. Iterate through each index i.
        for (int i = 0; i < n; i++) {
            int product = 1; // 2. Initialize product as identity.

            // 3. Multiply all elements except nums[i].
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    product *= nums[j];
                }
            }

            // 4. Store the product for position i.
            result[i] = product;
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: For each element, the answer is: (product of left side) * (product of right side).
    //       We do a left-to-right pass to compute left products, storing in result.
    //       Then a right-to-left pass to multiply right products into result.
    // Time:  O(n)   |  Space: O(1) extra (output array doesn't count)
    // -------------------------------------------------------------------------
    public static int[] productExceptSelfOptimized(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        // 1. LEFT PASS: result[i] = product of all elements to the left of i.
        //    For index 0, left product is 1 (nothing on left).
        result[0] = 1;

        // 2. Iterate from index 1 to n-1.
        for (int i = 1; i < n; i++) {
            // 3. result[i-1] already has product of all elements left of i-1.
            //    Multiply it by nums[i-1] to get product of all elements left of i.
            result[i] = result[i - 1] * nums[i - 1];
        }

        // 4. RIGHT PASS: Multiply result[i] by product of all elements to the right of i.
        //    Maintain a running right product variable.
        int rightProduct = 1;

        // 5. Iterate from the last index to the first.
        for (int i = n - 1; i >= 0; i--) {
            // 6. Multiply left product (result[i]) by right product.
            result[i] = result[i] * rightProduct;

            // 7. Update rightProduct by including nums[i] for the next iteration.
            rightProduct *= nums[i];
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // Helper method to print array elements nicely.
    // -------------------------------------------------------------------------
    public static void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}