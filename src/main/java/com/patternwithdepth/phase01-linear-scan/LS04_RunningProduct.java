package com.patternwithdepth.phase01_linear_scan;

/**
 * PROBLEM: Running Product
 * (Problem number: 21 in the DSA Playbook)
 *
 * DESCRIPTION:
 * Given an array nums, return the running product of the array.
 * The running product at index i is the product of all elements from index 0 to i.
 *
 * Example 1:
 * Input: nums = [1, 2, 3, 4]
 * Output: [1, 2, 6, 24]
 * Explanation: Running product is [1, 1*2, 1*2*3, 1*2*3*4].
 *
 * Example 2:
 * Input: nums = [1, 2, 0, 4]
 * Output: [1, 2, 0, 0]
 * Explanation: Running product becomes 0 once we encounter 0.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^4
 * * -10^5 <= nums[i] <= 10^5
 * - The product of any prefix is guaranteed to fit in a 32-bit integer.
 *
 * APPROACH:
 * BRUTE FORCE: O(n^2) - For each element, multiply all elements from 0 to i
 * OPTIMIZED:   O(n)   - Use previous running product, multiply current element
 */

public class LS04_RunningProduct {

    public static void main(String[] args) {
        // 1. Create our input array (test data).
        int[] nums = {1, 2, 3, 4};

        // 2. --- BRUTE FORCE APPROACH ---
        // Call brute force method.
        int[] bruteResult = runningProductBruteForce(nums.clone());
        System.out.print("Brute Force Result: ");
        printArray(bruteResult); // Expected: [1, 2, 6, 24]

        // 3. --- OPTIMIZED APPROACH ---
        // Call optimized method.
        int[] optimizedResult = runningProductOptimized(nums.clone());
        System.out.print("Optimized Result: ");
        printArray(optimizedResult); // Expected: [1, 2, 6, 24]

        // 4. Test with zero in the array.
        int[] nums2 = {1, 2, 0, 4};
        System.out.print("With Zero - Optimized: ");
        printArray(runningProductOptimized(nums2)); // Expected: [1, 2, 0, 0]
    }

    // -------------------------------------------------------------------------
    // BRUTE FORCE APPROACH
    // Idea: For each index i, compute the product of all elements from index 0 to i.
    // Time:  O(n^2)  |  Space: O(1) extra
    // -------------------------------------------------------------------------
    public static int[] runningProductBruteForce(int[] nums) {
        int n = nums.length;

        // 1. Iterate through each index i.
        for (int i = 0; i < n; i++) {
            int product = 1; // Initialize product (identity for multiplication).

            // 2. Multiply all elements from index 0 to i (inclusive).
            for (int j = 0; j <= i; j++) {
                product *= nums[j]; // Accumulate product.
            }

            // 3. Replace the current element with its running product.
            nums[i] = product;
        }

        return nums;
    }

    // -------------------------------------------------------------------------
    // OPTIMIZED APPROACH
    // Idea: The running product at index i = running product at index i-1 * nums[i].
    //       We just keep multiplying the current element with the previous running product.
    // Time:  O(n)   |  Space: O(1)
    // -------------------------------------------------------------------------
    public static int[] runningProductOptimized(int[] nums) {
        int n = nums.length;

        // 1. Start from index 1 because index 0 is just nums[0] itself.
        for (int i = 1; i < n; i++) {
            // 2. Multiply the current element with the previous running product.
            //    nums[i-1] already stores the product of elements from 0 to i-1.
            //    After this update, nums[i] becomes product from 0 to i.
            // 1,2,3,4 --> 1, 2, 6, 24
            nums[i] *= nums[i - 1];
        }

        return nums;
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