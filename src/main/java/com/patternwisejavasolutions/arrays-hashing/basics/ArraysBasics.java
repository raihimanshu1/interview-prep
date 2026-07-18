package com.patternwisejavasolutions.arrayshashing.basics;

/**
 * ARRAYS — Basics & Warmup
 * 
 * Before jumping into problems, master these fundamentals:
 * 1. Two-pointer technique (opposite & same direction)
 * 2. Sliding window (fixed & variable size)
 * 3. Prefix-sum & subarray patterns
 * 4. In-place array manipulation
 * 5. Kadane's algorithm (max subarray)
 */

import java.util.*;
public class ArraysBasics {
    // ==========================================
    // 1. TWO-POINTER — OPPOSITE DIRECTION
    // ==========================================
    // Used for: sorted arrays, palindrome, pair sum
    
    // Two Sum II — find pair that sums to target in sorted array
    // O(n), O(1) space
    public static int[] twoSumSorted(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        
        while (left < right) {
            int sum = nums[left] + nums[right];
            if (sum == target) return new int[]{left, right};
            else if (sum < target) left++;   // need bigger sum
            else right--;                    // need smaller sum
        }
        return new int[]{-1, -1};
    }

    public static int[] twoSumSortedUsingForLoop(int[] nums, int target) {

        for (int left = 0, right = nums.length - 1; left < right; ) {

            int sum = nums[left] + nums[right];

            if (sum == target) {
                return new int[]{left, right};
            }

            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return new int[]{-1, -1};
    }


    
    // Check if array is palindrome
    public static boolean isPalindrome(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left < right) {
            if (arr[left] != arr[right]) return false;
            left++;
            right--;
        }
        return true;
    }
    
    // Reverse array in-place (two-pointer)
    public static void reverse(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        while (left < right) {
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }
    // ============================================================
// LEETCODE: 26. Remove Duplicates from Sorted Array
// Difficulty: Easy
// Pattern: Two Pointers (Same Direction)
// ============================================================
//
// Problem Statement
// -----------------
// Given an integer array `nums` sorted in non-decreasing order,
// remove the duplicates in-place such that each unique element
// appears only once.
//
// The relative order of the elements should be preserved.
//
// Since the array cannot be resized in Java, place the unique
// elements in the first `k` positions of the array and return `k`.
//
// Do NOT allocate another array.
//
// ------------------------------------------------------------
// Example 1
// ------------------------------------------------------------
// Input:
// nums = [1, 1, 2]
//
// Output:
// k = 2
// nums = [1, 2, _, _]
//
// Explanation:
// Unique elements are 1 and 2.
// Return k = 2.
// The values after index k-1 do not matter.
//
// ------------------------------------------------------------
// Example 2
// ------------------------------------------------------------
// Input:
// nums = [0,0,1,1,1,2,2,3,3,4]
//
// Output:
// k = 5
// nums = [0,1,2,3,4,_,_,_,_,_]
//
// Explanation:
// The first five positions contain all unique elements.
// Return k = 5.
//
// ------------------------------------------------------------
// Example 3
// ------------------------------------------------------------
// Input:
// nums = [1,2,3,4,5]
//
// Output:
// k = 5
// nums = [1,2,3,4,5]
//
// Explanation:
// There are no duplicates.
// Every element is already unique.
//
// ------------------------------------------------------------
// Example 4
// ------------------------------------------------------------
// Input:
// nums = [5,5,5,5,5]
//
// Output:
// k = 1
// nums = [5,_,_,_,_]
//
// Explanation:
// All elements are the same.
// Only one copy should remain.
//
// ------------------------------------------------------------
// Constraints
// ------------------------------------------------------------
// 1 <= nums.length <= 30,000
// -100 <= nums[i] <= 100
// nums is sorted in non-decreasing order.
//
// ------------------------------------------------------------
// Interview Hint
// ------------------------------------------------------------
// Use two pointers:
//
// - Read Pointer  -> Scans every element.
// - Write Pointer -> Points to where the next unique element
//                    should be placed.
//

// Since the array is sorted, duplicates are always adjacent,
// making it easy to detect a new unique element.

    // ============================================================
// APPROACH
// ============================================================
//
// Since the array is already sorted, all duplicate elements
// appear next to each other.
//
// We use two pointers:
//
// 1. slow (Write Pointer)
//    - Points to the position of the last unique element.
//    - Also indicates where the next unique element should be placed.
//
// 2. fast (Read Pointer)
//    - Traverses every element in the array.
//    - Checks whether the current element is a new unique value.
//


// Algorithm:
//
// Step 1:
// Initialize slow = 0 because the first element is always unique.
//
// Step 2:
// Move fast from index 1 to the end of the array.
//
// Step 3:
// Compare nums[fast] with nums[slow].
//
//      nums[fast] == nums[slow]
//          -> Duplicate element.
//          -> Ignore it and continue scanning.
//
//      nums[fast] != nums[slow]
//          -> Found a new unique element.
//          -> Move slow one step ahead.
//          -> Copy nums[fast] to nums[slow].
//
// Step 4:
// After traversal, indices [0...slow] contain all unique elements.
//
// Number of unique elements = slow + 1.
//
// Time Complexity : O(n)
// Space Complexity: O(1)

    public static int removeDuplicates(int[] nums) {

        // Edge case: empty array
        if (nums.length == 0)
            return 0;

        // slow points to the last unique element found so far.
        // Initially the first element is always unique.
        int slow = 0;

        // fast scans every remaining element.
        for (int fast = 1; fast < nums.length; fast++) {

            // If current element is different,
            // we discovered a new unique value.
            if (nums[fast] != nums[slow]) {

                // Move slow to the next position where
                // the new unique element should be stored.
                slow++;

                // Place the unique element in its correct position.
                nums[slow] = nums[fast];
            }

            // If elements are equal, it is a duplicate.
            // Do nothing and continue scanning.
        }

        // Total unique elements stored from index 0 to slow.
        return slow + 1;
    }

    // ============================================================
// LEETCODE: 283. Move Zeroes
// Difficulty: Easy
// Pattern: Two Pointers (Same Direction)
// ============================================================
//
// Problem Statement
// -----------------
// Given an integer array `nums`, move all the 0's to the end
// of the array while maintaining the relative order of the
// non-zero elements.
//
// The operation must be performed in-place without making
// a copy of the array.
//
// ------------------------------------------------------------
// Example 1
// ------------------------------------------------------------
// Input:
// nums = [0,1,0,3,12]
//
// Output:
// [1,3,12,0,0]
//
// Explanation:
// All non-zero elements keep their original order.
// Every zero is moved to the end.
//
// ------------------------------------------------------------
// Example 2
// ------------------------------------------------------------
// Input:
// nums = [0]
//
// Output:
// [0]
//
// ------------------------------------------------------------
// Example 3
// ------------------------------------------------------------
// Input:
// nums = [1,2,3]
//
// Output:
// [1,2,3]
//
// Explanation:
// No zeros exist, so the array remains unchanged.
//
// ------------------------------------------------------------
// Example 4
// ------------------------------------------------------------
// Input:
// nums = [0,0,1]
//
// Output:
// [1,0,0]
//
// ------------------------------------------------------------
// Constraints
// ------------------------------------------------------------
// 1 <= nums.length <= 10^4
// -2^31 <= nums[i] <= 2^31 - 1
//
// ------------------------------------------------------------
// Approach
// ------------------------------------------------------------
//
// We use two pointers.
//
// 1. i (Read Pointer)
//    - Scans every element in the array.
//
// 2. nonZeroPos (Write Pointer)
//    - Points to the position where the next non-zero
//      element should be placed.
//
// Algorithm:
//
// Step 1:
// Start both pointers at index 0.
//
// Step 2:
// Traverse the array using the read pointer.
//
// Step 3:
// Whenever a non-zero element is found,
// swap it with the element at nonZeroPos.
//
// Step 4:
// Increment nonZeroPos because one more
// non-zero element has been placed correctly.
//
// Why does this work?
//
// - Every non-zero element is moved toward the front.
// - Zeros are naturally pushed toward the end.
// - Relative order of non-zero elements remains unchanged.
//
// Time Complexity : O(n)
// Space Complexity: O(1)

    public static void moveZeros(int[] nums) {

        // Position where the next non-zero element
        // should be placed.
        int nonZeroPos = 0;

        // Scan every element.
        for (int i = 0; i < nums.length; i++) {

            // Process only non-zero elements.
            if (nums[i] != 0) {

                // Swap current non-zero element with
                // the position reserved for the next
                // non-zero element.
                int temp = nums[nonZeroPos];
                nums[nonZeroPos] = nums[i];
                nums[i] = temp;

                // Next non-zero element should be placed
                // at the following position.
                nonZeroPos++;
            }

            // If current element is zero,
            // simply continue scanning.
        }
    }



// ============================================================
// PATTERN 3 : SLIDING WINDOW (FIXED SIZE)
// ============================================================
//
// When to use?
// ------------
// Use this pattern whenever:
//
// ✔ The problem asks about a SUBARRAY.
// ✔ The subarray size is FIXED (Exactly K elements).
// ✔ We need to calculate something for every window.
//      - Maximum Sum
//      - Minimum Sum
//      - Average
//      - Count
//      - Maximum/Minimum Element
//
// Keywords:
//
// • Contiguous
// • Subarray
// • Window Size = K
// • Exactly K Elements
//
// ------------------------------------------------------------
// Generic Template
// ------------------------------------------------------------
//
// Step 1:
// Build the first window of size K.
//
// Step 2:
// Store the result of the first window.
//
// Step 3:
// Slide the window one position.
//
//      Remove Left Element
//      Add Right Element
//
// Step 4:
// Update answer.
//
// Step 5:
// Continue until the end.
//
// ------------------------------------------------------------
// Generic Template
// ------------------------------------------------------------
//
// int window = 0;
//
// // Build first window
// for (int i = 0; i < k; i++)
//     window += arr[i];
//
// answer = window;
//
// // Slide the window
// for (int i = k; i < arr.length; i++) {
//
//     window -= arr[i-k];   // Remove outgoing element
//     window += arr[i];     // Add incoming element
//
//     answer = update(answer, window);
// }
//
// return answer;
//
// ------------------------------------------------------------
// Why Sliding Window?
// ------------------------------------------------------------
//
// Consider
//
// arr = [2,1,5,1,3,2]
// k = 3
//
// Brute Force:
//
// Window 1
// [2,1,5] = 8
//
// Window 2
// [1,5,1] = ?
//
// Normally we'd calculate
//
// 1 + 5 + 1
//
// again.
//
// But notice...
//
// Previous Window
//
// [2,1,5]
//
// Current Window
//
// [1,5,1]
//
// Only TWO things changed.
//
// Removed
// 2
//
// Added
// 1
//
// Therefore
//
// New Sum
//
// = Old Sum
// - Removed Element
// + Added Element
//
// = 8 - 2 + 1
// = 7
//
// We avoid recalculating the entire window.
//
// ------------------------------------------------------------
// Sliding Window Formula
// ------------------------------------------------------------
//
// windowSum
//
// = Previous Window Sum
//
// - Outgoing Element
//
// + Incoming Element
//
// Code:
//
// windowSum = windowSum
//             - arr[i-k]
//             + arr[i];
//
// This single formula is the heart of
// every Fixed Sliding Window problem.
//
// ------------------------------------------------------------
// Dry Run
// ------------------------------------------------------------
//
// arr = [2,1,5,1,3,2]
// k = 3
//
// First Window
//
// [2,1,5]
//
// Sum = 8
//
// -------------------------
//
// Slide 1
//
// Remove 2
// Add 1
//
// [1,5,1]
//
// New Sum
//
// 8 - 2 + 1 = 7
//
// -------------------------
//
// Slide 2
//
// Remove 1
// Add 3
//
// [5,1,3]
//
// New Sum
//
// 7 - 1 + 3 = 9
//
// -------------------------
//
// Slide 3
//
// Remove 5
// Add 2
//
// [1,3,2]
//
// New Sum
//
// 9 - 5 + 2 = 6
//
// Maximum = 9
//
// ============================================================
// Example Problem
// ============================================================
//
// Given an integer array and an integer K,
// return the maximum sum among all contiguous
// subarrays of size K.
//
// Example 1
//
// Input:
// arr = [2,1,5,1,3,2]
// k = 3
//
// Output:
// 9
//
// Explanation:
//
// [2,1,5] = 8
// [1,5,1] = 7
// [5,1,3] = 9
// [1,3,2] = 6
//
// Maximum = 9
//
// ============================================================
// Time Complexity : O(n)
// Space Complexity: O(1)
// ============================================================

    public static int maxSumSubarrayOfSizeK(int[] arr, int k) {

        /*
         * If the array has fewer than k elements,
         * we cannot form a valid window.
         */
        if (arr.length < k) {
            return -1;
        }

        /*
         * --------------------------------------------------------
         * Step 1
         * Build the very first window.
         *
         * Example:
         *
         * [2,1,5] 1 3 2
         *
         * Window Sum = 8
         * --------------------------------------------------------
         */
        int windowSum = 0;

        for (int i = 0; i < k; i++) {
            windowSum += arr[i];
        }

        /*
         * Initially,
         * the first window is also the maximum window.
         */
        int maxSum = windowSum;

        /*
         * --------------------------------------------------------
         * Step 2
         * Slide the window one position at a time.
         *
         * Every slide performs two operations:
         *
         * 1. Remove the leftmost element.
         * 2. Add the new rightmost element.
         *
         * Previous Window
         *
         * [2,1,5] 1 3 2
         *
         * Current Window
         *
         * 2 [1,5,1] 3 2
         *
         * New Sum
         *
         * = Old Sum
         * - 2
         * + 1
         * --------------------------------------------------------
         */
        for (int i = k; i < arr.length; i++) {

            /*
             * Remove the element that is no longer
             * part of the current window.
             */
            windowSum -= arr[i - k];

            /*
             * Include the new element that has
             * entered the window.
             */
            windowSum += arr[i];

            /*
             * Compare the current window with
             * the best answer found so far.
             */
            maxSum = Math.max(maxSum, windowSum);
        }

        /*
         * After processing every possible window,
         * return the maximum sum.
         */
        return maxSum;
    }





    // ============================================================
// PATTERN 4 : SLIDING WINDOW (VARIABLE SIZE)
// ============================================================
//
// When to use?
// ------------
// Use this pattern whenever:
//
// ✔ The problem involves a contiguous subarray/substring.
// ✔ The window size is NOT fixed.
// ✔ We need the Longest / Shortest window satisfying a condition.
//
// Common Keywords:
//
// • Longest
// • Shortest
// • Minimum Length
// • Maximum Length
// • At Most K
// • At Least K
// • Sum >= Target
// • Distinct Characters
// • Without Repeating Characters
//
// ------------------------------------------------------------
// Fixed Window vs Variable Window
// ------------------------------------------------------------
//
// Fixed Window
//
// Window Size is predetermined.
//
// Example:
//
// Size = 3
//
// [2,1,5]
// [1,5,1]
// [5,1,3]
//
// Every window has exactly 3 elements.
//
// ------------------------------------------------------------
//
// Variable Window
//
// Window size changes dynamically.
//
// Example:
//
// Target = 7
//
// [2]
// [2,3]
// [2,3,1]
// [2,3,1,2]   Sum = 8
//
// Window satisfies the condition.
//
// Try shrinking.
//
// [3,1,2]     Sum = 6
//
// Stop shrinking.
//
// Continue expanding.
//
// ------------------------------------------------------------
// Generic Template
// ------------------------------------------------------------
//
// left = 0;
//
// for(right = 0; right < n; right++) {
//
//     // Expand Window
//     include arr[right];
//
//     while(window satisfies/violates condition) {
//
//         // Update Answer
//
//         // Shrink Window
//         remove arr[left];
//         left++;
//     }
// }
//
// ------------------------------------------------------------
// Core Idea
// ------------------------------------------------------------
//
// Right Pointer
// -------------
// Always expands the window.
//
// Left Pointer
// ------------
// Shrinks the window only when the desired
// condition is satisfied (or violated,
// depending on the problem).
//
// Unlike Fixed Sliding Window,
// both pointers move independently.
//
// ============================================================
// LEETCODE 209
// Minimum Size Subarray Sum
// ============================================================
//
// Problem Statement
// -----------------
//
// Given an array of positive integers nums
// and an integer target,
//
// return the minimum length of a contiguous
// subarray whose sum is greater than or equal
// to target.
//
// If no such subarray exists,
// return 0.
//
// ------------------------------------------------------------
// Example 1
// ------------------------------------------------------------
//
// Input:
//
// target = 7
//
// nums = [2,3,1,2,4,3]
//
// Output:
//
// 2
//
// Explanation:
//
// [4,3]
//
// Sum = 7
//
// Minimum Length = 2
//
// ------------------------------------------------------------
// Example 2
// ------------------------------------------------------------
//
// target = 4
//
// nums = [1,4,4]
//
// Output
//
// 1
//
// ------------------------------------------------------------
// Example 3
// ------------------------------------------------------------
//
// target = 11
//
// nums = [1,1,1,1,1,1]
//
// Output
//
// 0
//
// No valid subarray exists.
//
// ------------------------------------------------------------
// Time Complexity : O(n)
// Space Complexity: O(1)
// ============================================================

    public static int minSubArrayLen(int target, int[] nums) {

        /*
         * Left boundary of the current window.
         */
        int left = 0;

        /*
         * Running sum of the current window.
         */
        int sum = 0;

        /*
         * Stores the minimum window length found.
         *
         * Initialize with a very large value so
         * every valid window becomes smaller.
         */
        int minLen = Integer.MAX_VALUE;

        /*
         * --------------------------------------------------------
         * Expand the Window
         * --------------------------------------------------------
         *
         * The right pointer keeps moving forward,
         * increasing the size of the window.
         */
        for (int right = 0; right < nums.length; right++) {

            /*
             * Include the new element
             * entering the window.
             */
            sum += nums[right];

            /*
             * ----------------------------------------------------
             * Shrink the Window
             * ----------------------------------------------------
             *
             * Once the current window satisfies
             * the required condition
             *
             *      sum >= target
             *
             * try making it smaller while still
             * satisfying the condition.
             *
             * This is the key idea of Variable
             * Sliding Window.
             */
            while (sum >= target) {

                /*
                 * Current window is valid.
                 *
                 * Compare its size with the
                 * smallest valid window found so far.
                 */
                minLen = Math.min(minLen, right - left + 1);

                /*
                 * Remove the leftmost element
                 * from the window.
                 */
                sum -= nums[left];

                /*
                 * Shrink the window by moving
                 * the left pointer.
                 */
                left++;
            }
        }

        /*
         * If no valid window was found,
         * return 0.
         */
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    // ==========================================
    // 5. PREFIX SUM — O(n) precompute, O(1) range sum
    // ==========================================
    
    static class PrefixSum {
        private int[] prefix;
        
        PrefixSum(int[] arr) {
            prefix = new int[arr.length + 1];
            for (int i = 0; i < arr.length; i++) {
                prefix[i + 1] = prefix[i] + arr[i];
            }
        }
        
        // Sum of elements from l to r (inclusive)
        int rangeSum(int l, int r) {
            return prefix[r + 1] - prefix[l];
        }
    }
    
    // Subarray sum equals k (uses prefix sum + hashmap)
    // O(n) time, O(n) space
    public static int subarraySumEqualsK(int[] nums, int k) {
        Map<Integer, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0, 1);  // empty prefix
        
        int sum = 0;
        int count = 0;
        
        for (int num : nums) {
            sum += num;
            // If sum - k exists as prefix, then subarray with sum = k exists
            count += prefixSumCount.getOrDefault(sum - k, 0);
            prefixSumCount.put(sum, prefixSumCount.getOrDefault(sum, 0) + 1);
        }
        return count;
    }
    // ==========================================
    // 6. KADANE'S ALGORITHM — O(n)
    // ==========================================
    // Maximum subarray sum (contiguous)
    
    public static int maxSubarraySum(int[] nums) {
        int maxEndingHere = nums[0];
        int maxSoFar = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            // Either extend current subarray or start new
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        return maxSoFar;
    }
    
    // Maximum subarray sum with indices
    public static int[] maxSubarrayWithIndices(int[] nums) {
        int maxEndingHere = nums[0];
        int maxSoFar = nums[0];
        int start = 0, end = 0, tempStart = 0;
        
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > maxEndingHere + nums[i]) {
                maxEndingHere = nums[i];
                tempStart = i;
            } else {
                maxEndingHere = maxEndingHere + nums[i];
            }
            
            if (maxEndingHere > maxSoFar) {
                maxSoFar = maxEndingHere;
                start = tempStart;
                end = i;
            }
        }
        return new int[]{maxSoFar, start, end};
    }
    // ==========================================
    // 7. IN-PLACE OPERATIONS
    // ==========================================
    
    // Rotate array right by k positions (in-place)
    // [1,2,3,4,5], k=2 → [4,5,1,2,3]
    public static void rotate(int[] nums, int k) {
        k = k % nums.length;
        reverse(nums, 0, nums.length - 1);       // reverse entire array
        reverse(nums, 0, k - 1);                  // reverse first k
        reverse(nums, k, nums.length - 1);        // reverse rest
    }
    
    private static void reverse(int[] arr, int l, int r) {
        while (l < r) {
            int temp = arr[l];
            arr[l] = arr[r];
            arr[r] = temp;
            l++;
            r--;
        }
    }
    // ==========================================
    // MAIN — Test everything
    // ==========================================
    
    public static void main(String[] args) {
        System.out.println("=== ARRAYS BASICS ===");
        
        // Two-pointers (opposite)
        System.out.println("\n--- Two-Pointer (Opposite) ---");
        int[] sortedArr = {2, 7, 11, 15};
        System.out.println("Two Sum [2,7,11,15] target=9: " + 
                          Arrays.toString(twoSumSorted(sortedArr, 9)));
        System.out.println("Is palindrome [1,2,3,2,1]: " + isPalindrome(new int[]{1,2,3,2,1}));
        
        // Two-pointers (same direction)
        System.out.println("\n--- Two-Pointer (Same Direction) ---");
        int[] dupArr = {0,0,1,1,1,2,2,3,3,4};
        int len = removeDuplicates(dupArr);
        System.out.println("Length after removing dupes: " + len + 
                          ", array: " + Arrays.toString(Arrays.copyOf(dupArr, len)));
        
        int[] zerosArr = {0,1,0,3,12};
        moveZeros(zerosArr);
        System.out.println("After moving zeros: " + Arrays.toString(zerosArr));
        
        // Sliding window
        System.out.println("\n--- Sliding Window ---");
        int[] arr = {2, 1, 5, 1, 3, 2};
        System.out.println("Max sum of size 3: " + maxSumSubarrayOfSizeK(arr, 3));
        System.out.println("Min subarray len sum>=7: " + minSubArrayLen(7, arr));
        
        // Prefix sum
        System.out.println("\n--- Prefix Sum ---");
        PrefixSum ps = new PrefixSum(new int[]{1, 2, 3, 4, 5});
        System.out.println("Range sum [1..3]: " + ps.rangeSum(1, 3) + " (expected: 9)");
        System.out.println("Subarray sum equals 3: " + subarraySumEqualsK(new int[]{1,1,1}, 2));
        
        // Kadane
        System.out.println("\n--- Kadane's ---");
        int[] kadaneArr = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Max subarray sum: " + maxSubarraySum(kadaneArr) + " (expected: 6)");
        int[] result = maxSubarrayWithIndices(kadaneArr);
        System.out.println("Max subarray: sum=" + result[0] + ", indices=[" + result[1] + "," + result[2] + "]");
        
        // Rotation
        System.out.println("\n--- Rotation ---");
        int[] rotArr = {1,2,3,4,5};
        rotate(rotArr, 2);
        System.out.println("Rotated by 2: " + Arrays.toString(rotArr) + " (expected: [4,5,1,2,3])");
        
        System.out.println("\n=== KEY ARRAYS PATTERNS ===");
        System.out.println("1. Two-Pointer (Opposite) → sorted pair sum, palindrome, reverse");
        System.out.println("2. Two-Pointer (Same) → remove duplicates, move zeros, partition");
        System.out.println("3. Sliding Window (Fixed) → max/min sum of size k");
        System.out.println("4. Sliding Window (Variable) → smallest window with condition");
        System.out.println("5. Prefix Sum → O(1) range sum, subarray sum with hashmap");
        System.out.println("6. Kadane's → max subarray sum in O(n)");
        System.out.println("7. In-place → rotate, reverse parts of array");
    }
}
