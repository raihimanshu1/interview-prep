/**
 * BINARY SEARCH — Basics & Warmup
 * 
 * Before jumping into problems, master these fundamentals:
 * 1. Binary search on sorted array (recursive & iterative)
 * 2. First/Last occurrence (lower bound / upper bound)
 * 3. Search in rotated sorted array
 * 4. Binary search on answer space (monotonic function)
 * 5. Search on 2D matrix
 */

package com.patternwisejavasolutions.binarySearch.basics;




import java.util.*;
public class BinarySearchBasics {
    // ==========================================
    // 1. STANDARD BINARY SEARCH — O(log n)
    // ==========================================
    // Array must be SORTED
    
    // Iterative binary search
    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;  // avoid overflow
            
            if (arr[mid] == target) return mid;
            else if (arr[mid] < target) left = mid + 1;    // search right half
            else right = mid - 1;                           // search left half
        }
        return -1;  // not found
    }
    
    // Recursive binary search
    public static int binarySearchRecursive(int[] arr, int target, int left, int right) {
        if (left > right) return -1;
        
        int mid = left + (right - left) / 2;
        
        if (arr[mid] == target) return mid;
        else if (arr[mid] < target) 
            return binarySearchRecursive(arr, target, mid + 1, right);
        else 
            return binarySearchRecursive(arr, target, left, mid - 1);
    }
    // ==========================================
    // 2. FIRST & LAST OCCURRENCE — O(log n)
    // ==========================================
    // Critical for problems with duplicates
    
    // First occurrence (lower bound)
    public static int firstOccurrence(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                result = mid;          // found, but keep looking left
                right = mid - 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }
    
    // Last occurrence (upper bound)
    public static int lastOccurrence(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                result = mid;          // found, but keep looking right
                left = mid + 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }
    
    // Count occurrences of target
    public static int countOccurrences(int[] arr, int target) {
        int first = firstOccurrence(arr, target);
        if (first == -1) return 0;
        int last = lastOccurrence(arr, target);
        return last - first + 1;
    }
    // ==========================================
    // 3. BOUNDARY PATTERNS
    // ==========================================
    
    // Find smallest element >= target (lower bound)
    // If all elements < target, returns arr.length
    public static int lowerBound(int[] arr, int target) {
        int left = 0;
        int right = arr.length;  // not arr.length - 1!
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] >= target) {
                right = mid;     // mid could be answer
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
    
    // Find smallest element > target (upper bound)
    // If all elements <= target, returns arr.length
    public static int upperBound(int[] arr, int target) {
        int left = 0;
        int right = arr.length;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] > target) {
                right = mid;     // mid could be answer
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
    // ==========================================
    // 4. SEARCH IN ROTATED SORTED ARRAY
    // ==========================================
    // Array was sorted, then rotated at a pivot point
    // e.g., [4,5,6,7,0,1,2]
    
    public static int searchRotated(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) return mid;
            
            // Left half is sorted
            if (arr[left] <= arr[mid]) {
                if (target >= arr[left] && target < arr[mid]) {
                    right = mid - 1;  // search left
                } else {
                    left = mid + 1;   // search right
                }
            } 
            // Right half is sorted
            else {
                if (target > arr[mid] && target <= arr[right]) {
                    left = mid + 1;   // search right
                } else {
                    right = mid - 1;  // search left
                }
            }
        }
        return -1;
    }
    
    // Find minimum in rotated sorted array
    public static int findMinRotated(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] > arr[right]) {
                left = mid + 1;        // min is in right half
            } else {
                right = mid;           // min is in left half (including mid)
            }
        }
        return arr[left];
    }
    // ==========================================
    // 5. BINARY SEARCH ON ANSWER SPACE
    // ==========================================
    // Use when: monotonic condition (FFFTTT or TTTFFF)
    // Pattern: find smallest value that satisfies condition
    
    // Example: Find sqrt(x) using binary search on answer space
    public static int sqrt(int x) {
        if (x < 2) return x;
        
        int left = 1;
        int right = x / 2;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            long sq = (long) mid * mid;
            
            if (sq == x) return mid;
            else if (sq < x) left = mid + 1;
            else right = mid - 1;
        }
        return right;  // floor sqrt
    }
    
    // Example: Find peak element in mountain array
    // Array: [1,2,3,4,5,3,1] → peak is 5 at index 4
    // Monotonic: increasing then decreasing
    public static int findPeakElement(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] > arr[mid + 1]) {
                right = mid;      // peak is in left half (including mid)
            } else {
                left = mid + 1;   // peak is in right half
            }
        }
        return left;
    }
    // ==========================================
    // 6. SEARCH IN 2D MATRIX
    // ==========================================
    
    // Matrix: sorted row-wise AND first element of each row > last of prev
    // Approach: treat as flattened sorted array
    public static boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0) return false;
        
        int rows = matrix.length;
        int cols = matrix[0].length;
        int left = 0;
        int right = rows * cols - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int row = mid / cols;
            int col = mid % cols;
            
            if (matrix[row][col] == target) return true;
            else if (matrix[row][col] < target) left = mid + 1;
            else right = mid - 1;
        }
        return false;
    }
    // ==========================================
    // MAIN — Test everything
    // ==========================================
    
    public static void main(String[] args) {
        System.out.println("=== BINARY SEARCH BASICS ===");
        
        // Standard binary search
        System.out.println("\n--- Standard ---");
        int[] arr = {1, 3, 5, 7, 9, 11, 13};
        System.out.println("Search 7: " + binarySearch(arr, 7) + " (expected: 3)");
        System.out.println("Search 6: " + binarySearch(arr, 6) + " (expected: -1)");
        
        // First/Last occurrence
        System.out.println("\n--- First/Last Occurrence ---");
        int[] dupArr = {1, 2, 2, 2, 3, 4, 4, 5};
        System.out.println("First occurrence of 2: " + firstOccurrence(dupArr, 2) + " (expected: 1)");
        System.out.println("Last occurrence of 2: " + lastOccurrence(dupArr, 2) + " (expected: 3)");
        System.out.println("Count of 2: " + countOccurrences(dupArr, 2) + " (expected: 3)");
        
        // Boundary patterns
        System.out.println("\n--- Bounds ---");
        int[] boundsArr = {1, 3, 5, 7, 9};
        System.out.println("Lower bound of 6: " + lowerBound(boundsArr, 6) + " (expected: 3, value=7)");
        System.out.println("Upper bound of 5: " + upperBound(boundsArr, 5) + " (expected: 3, value=7)");
        
        // Rotated array
        System.out.println("\n--- Rotated Array ---");
        int[] rotatedArr = {4, 5, 6, 7, 0, 1, 2};
        System.out.println("Search 0 in rotated: " + searchRotated(rotatedArr, 0) + " (expected: 4)");
        System.out.println("Min in rotated: " + findMinRotated(rotatedArr) + " (expected: 0)");
        
        // Answer space
        System.out.println("\n--- Answer Space ---");
        System.out.println("sqrt(16): " + sqrt(16) + " (expected: 4)");
        System.out.println("sqrt(10): " + sqrt(10) + " (expected: 3)");
        System.out.println("Peak in [1,2,3,4,5,3,1]: idx=" + 
                          findPeakElement(new int[]{1,2,3,4,5,3,1}) + " (expected: 4, value=5)");
        
        // 2D matrix
        System.out.println("\n--- 2D Matrix ---");
        int[][] matrix = {{1,3,5,7},{10,11,16,20},{23,30,34,60}};
        System.out.println("Search 3 in matrix: " + searchMatrix(matrix, 3) + " (expected: true)");
        System.out.println("Search 13 in matrix: " + searchMatrix(matrix, 13) + " (expected: false)");
        
        System.out.println("\n=== KEY BINARY SEARCH PATTERNS ===");
        System.out.println("1. Standard BS → sorted array, O(log n)");
        System.out.println("2. First/Last Occurrence → modify condition on equal");
        System.out.println("3. Lower/Upper Bound → boundary finding (FFFTTT pattern)");
        System.out.println("4. Rotated Array → check which half is sorted");
        System.out.println("5. Answer Space → find min that satisfies condition");
        System.out.println("6. 2D Matrix → flatten index: row = mid/cols, col = mid%cols");
    }
}
