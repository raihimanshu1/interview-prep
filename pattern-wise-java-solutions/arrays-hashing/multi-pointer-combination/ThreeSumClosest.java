/**
 * THREE SUM CLOSEST — Find three integers whose sum is closest to target
 * 
 * Input:  nums = [-1, 2, 1, -4], target = 1
 * Output: 2  (-1 + 2 + 1 = 2, closest to 1)
 * 
 * Input:  nums = [0, 0, 0], target = 1
 * Output: 0  (0 + 0 + 0 = 0)
 * 
 * Pattern: Sort + Fix one element + Two-pointer on the rest
 * O(n²) time, O(1) or O(log n) space for sorting
 */

import java.util.Arrays;

public class ThreeSumClosest {
    
    public static int threeSumClosest(int[] nums, int target) {
        if (nums == null || nums.length < 3) {
            throw new IllegalArgumentException("Need at least 3 elements");
        }
        
        Arrays.sort(nums);
        int closestSum = nums[0] + nums[1] + nums[2];
        
        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicates for optimization
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            int left = i + 1;
            int right = nums.length - 1;
            
            while (left < right) {
                int currentSum = nums[i] + nums[left] + nums[right];
                
                // Update if closer to target
                if (Math.abs(currentSum - target) < Math.abs(closestSum - target)) {
                    closestSum = currentSum;
                }
                
                if (currentSum == target) {
                    return currentSum;  // perfect match
                } else if (currentSum < target) {
                    left++;  // need larger sum
                } else {
                    right--;  // need smaller sum
                }
            }
        }
        
        return closestSum;
    }
    
    // Variation: Return the triple itself
    public static int[] threeSumClosestTriple(int[] nums, int target) {
        if (nums == null || nums.length < 3) return new int[]{};
        
        Arrays.sort(nums);
        int closestDiff = Integer.MAX_VALUE;
        int[] result = new int[]{nums[0], nums[1], nums[2]};
        
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            int left = i + 1;
            int right = nums.length - 1;
            
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                int diff = Math.abs(sum - target);
                
                if (diff < closestDiff) {
                    closestDiff = diff;
                    result = new int[]{nums[i], nums[left], nums[right]};
                }
                
                if (sum == target) {
                    return result;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        System.out.println("=== THREE SUM CLOSEST ===");
        
        // Test 1: Basic
        int[] nums1 = {-1, 2, 1, -4};
        int target1 = 1;
        System.out.println(String.format("nums=%s, target=%d → closest=%d (expected: 2)",
            Arrays.toString(nums1), target1, threeSumClosest(nums1, target1)));
        
        // Test 2: Exact match
        int[] nums2 = {-3, -1, 2, 5, 4};
        int target2 = 6;
        System.out.println(String.format("nums=%s, target=%d → closest=%d (expected: 6)",
            Arrays.toString(nums2), target2, threeSumClosest(nums2, target2)));
        
        // Test 3: All zeros
        int[] nums3 = {0, 0, 0};
        int target3 = 1;
        System.out.println(String.format("nums=%s, target=%d → closest=%d (expected: 0)",
            Arrays.toString(nums3), target3, threeSumClosest(nums3, target3)));
        
        // Test 4: With triple
        int[] nums4 = {1, 2, 4, 8, 16};
        int target4 = 10;
        int[] triple = threeSumClosestTriple(nums4, target4);
        System.out.println(String.format("nums=%s, target=%d → closest triple=%s, sum=%d",
            Arrays.toString(nums4), target4, Arrays.toString(triple), triple[0] + triple[1] + triple[2]));
        
        System.out.println("\n=== KEY INSIGHT ===");
        System.out.println("Pattern: Sort + Fix + Two-pointer");
        System.out.println("  • Sort the array first");
        System.out.println("  • Fix one element (i), use two-pointer on rest (left, right)");
        System.out.println("  • Track closest sum by comparing absolute differences");
        System.out.println("  • Skip duplicates to optimize (optional)");
        System.out.println("  • O(n²) time — can't beat this for 3-sum problems");
    }
}