package main.java.com.patternwithdepth.phase00;

import java.util.Arrays;

public class A12_UltimateRotateArray {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5};
        int n = nums.length;
        
        // TEST CASES you can try:
        // k = 2   -> Standard Right by 2 -> Expected: [4, 5, 1, 2, 3]
        // k = -2  -> Negative Right by 2 (means Left 2) -> Expected: [3, 4, 5, 1, 2]
        int k = -2; 

        // 1. The Magic Formula: Universally normalizes ANY k input (positive, negative, or oversized)
        // into a clean, standard positive RIGHT rotation value.
        k = (k % n + n) % n;

        // =================================================================
        // THE EXACT SAME RIGHT ROTATION CORE (No changes whatsoever)
        // =================================================================
        
        // Step 1: Reverse the entire array from start to finish
        reverse(nums, 0, n - 1);

        // Step 2: Reverse the first 'k' elements (index 0 to k - 1)
        reverse(nums, 0, k - 1);

        // Step 3: Reverse the remaining elements (index k to the very end)
        reverse(nums, k, n - 1);

        // Print out the final transformed array
        System.out.println("Rotated Array: " + Arrays.toString(nums));
    }

    // Single reusable, in-place utility
    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }
}