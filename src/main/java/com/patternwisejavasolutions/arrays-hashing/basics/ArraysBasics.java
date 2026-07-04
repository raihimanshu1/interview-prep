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

package com.patternwisejavasolutions.arraysHashing.basics;




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
    // ==========================================
    // 2. TWO-POINTER — SAME DIRECTION
    // ==========================================
    // Used for: removing duplicates, in-place operations
    
    // Remove duplicates from sorted array (in-place)
    // Returns length of resulting array
    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        
        int slow = 0;  // points to last unique element
        
        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];
            }
        }
        return slow + 1;
    }
    
    // Move zeros to end (in-place)
    // [0,1,0,3,12] → [1,3,12,0,0]
    public static void moveZeros(int[] nums) {
        int nonZeroPos = 0;  // position to place next non-zero
        
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                int temp = nums[nonZeroPos];
                nums[nonZeroPos] = nums[i];
                nums[i] = temp;
                nonZeroPos++;
            }
        }
    }
    // ==========================================
    // 3. SLIDING WINDOW — FIXED SIZE
    // ==========================================
    
    // Max sum subarray of size k (fixed window)
    // O(n) time, O(1) space
    public static int maxSumSubarrayOfSizeK(int[] arr, int k) {
        if (arr.length < k) return -1;
        
        // Calculate sum of first window
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += arr[i];
        }
        
        int maxSum = windowSum;
        
        // Slide the window
        for (int i = k; i < arr.length; i++) {
            windowSum += arr[i] - arr[i - k];  // add new, remove old
            maxSum = Math.max(maxSum, windowSum);
        }
        return maxSum;
    }
    // ==========================================
    // 4. SLIDING WINDOW — VARIABLE SIZE
    // ==========================================
    // Expand right, shrink left when condition violated
    
    // Smallest subarray with sum >= target
    public static int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int sum = 0;
        int minLen = Integer.MAX_VALUE;
        
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];  // expand window
            
            // Shrink while condition holds
            while (sum >= target) {
                minLen = Math.min(minLen, right - left + 1);
                sum -= nums[left];
                left++;
            }
        }
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
