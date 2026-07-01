/**
 * TWO SUM CLOSEST — Find pair with sum closest to target
 * 
 * Input:  nums = [-1, 2, 1, -4], target = 1
 * Output: 2  (sum = -1 + 2 = 1, which equals target)
 *
 * Input:  nums = [1, 5, 3, 2], target = 6
 * Output: 5  (sum = 3 + 2 = 5, or 1 + 5 = 6 → both work, return diff 0)
 *
 * Pattern: Sort + Two-pointer (opposite direction)
 * O(n log n) time, O(1) space (or O(n) for sorting)
 */

import java.util.Arrays;

public class TwoSumClosest {
    
    // Returns the sum of the pair closest to target (not the indices)
    public static int twoSumClosest(int[] nums, int target) {
        if (nums == null || nums.length < 2) {
            throw new IllegalArgumentException("Need at least 2 elements");
        }
        
        Arrays.sort(nums);  // O(n log n)
        
        int left = 0;
        int right = nums.length - 1;
        int closestSum = nums[left] + nums[right];
        
        while (left < right) {
            int currentSum = nums[left] + nums[right];
            
            // Update if closer to target
            if (Math.abs(currentSum - target) < Math.abs(closestSum - target)) {
                closestSum = currentSum;
            }
            
            if (currentSum == target) {
                return currentSum;  // can't get any closer
            } else if (currentSum < target) {
                left++;  // need larger sum
            } else {
                right--;  // need smaller sum
            }
        }
        
        return closestSum;
    }
    
    // Variation: Return the actual pair values
    public static int[] twoSumClosestPair(int[] nums, int target) {
        if (nums == null || nums.length < 2) return new int[]{};
        
        Arrays.sort(nums);
        
        int left = 0;
        int right = nums.length - 1;
        int closestDiff = Integer.MAX_VALUE;
        int[] result = new int[]{nums[left], nums[right]};
        
        while (left < right) {
            int sum = nums[left] + nums[right];
            int diff = Math.abs(sum - target);
            
            if (diff < closestDiff) {
                closestDiff = diff;
                result[0] = nums[left];
                result[1] = nums[right];
            }
            
            if (sum == target) {
                return new int[]{nums[left], nums[right]};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        System.out.println("=== TWO SUM CLOSEST ===");
        
        // Test 1: Exact match
        int[] nums1 = {-1, 2, 1, -4};
        int target1 = 1;
        System.out.println(String.format("nums=%s, target=%d → closest sum=%d (expected: 2)",
            Arrays.toString(nums1), target1, twoSumClosest(nums1, target1)));
        System.out.println(String.format("  Pair: %s", 
            Arrays.toString(twoSumClosestPair(nums1, target1))));
        
        // Test 2: No exact match
        int[] nums2 = {1, 5, 3, 2};
        int target2 = 6;
        System.out.println(String.format("nums=%s, target=%d → closest sum=%d (expected: 5 or 6)",
            Arrays.toString(nums2), target2, twoSumClosest(nums2, target2)));
        
        // Test 3: Negative numbers
        int[] nums3 = {-5, -2, 0, 3, 8};
        int target3 = 4;
        System.out.println(String.format("nums=%s, target=%d → closest sum=%d",
            Arrays.toString(nums3), target3, twoSumClosest(nums3, target3)));
        
        System.out.println("\n=== KEY INSIGHT ===");
        System.out.println("Pattern: Sort + Two-pointer (opposite direction)");
        System.out.println("  • Sort the array first");
        System.out.println("  • Track the closest diff seen so far");
        System.out.println("  • If sum < target → move left (increase sum)");
        System.out.println("  • If sum > target → move right (decrease sum)");
        System.out.println("  • If sum == target → return immediately (perfect match)");
    }
}