package com.patternwisejavasolutions.matrix;

/**
 * ROTTING ORANGES — Multi-source BFS
 * 
 * You are given an m x n grid where each cell can have:
 *   0 = empty, 1 = fresh orange, 2 = rotten orange
 * 
 * Every minute, any fresh orange that is 4-directionally adjacent to a rotten
 * orange becomes rotten. Return the minimum number of minutes until no cell
 * has a fresh orange. If impossible, return -1.
 * 
 * Pattern: Multi-source BFS (start from ALL rotten oranges simultaneously)
 * LeetCode 994 - Medium
 */

import java.util.*;
public class RottingOranges {
    
    public static int orangesRotting(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int rows = grid.length;
        int cols = grid[0].length;
        
        Queue<int[]> queue = new LinkedList<>();
        int freshCount = 0;
        
        // Step 1: Add all initially rotten oranges to queue, count fresh
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    freshCount++;
                }
            }
        }
        
        // If no fresh oranges, return 0
        if (freshCount == 0) return 0;
        
        int minutes = 0;
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        // BFS level by level
        while (!queue.isEmpty()) {
            int size = queue.size();
            boolean rottedThisMinute = false;
            
            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                
                for (int[] dir : dirs) {
                    int nr = cell[0] + dir[0];
                    int nc = cell[1] + dir[1];
                    
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols 
                        && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;  // rot it
                        queue.offer(new int[]{nr, nc});
                        freshCount--;
                        rottedThisMinute = true;
                    }
                }
            }
            
            if (rottedThisMinute) minutes++;
        }
        
        return freshCount == 0 ? minutes : -1;
    }
    
    public static void main(String[] args) {
        System.out.println("=== ROTTING ORANGES ===");
        
        // Test 1
        int[][] grid1 = {
            {2, 1, 1},
            {1, 1, 0},
            {0, 1, 1}
        };
        System.out.println("Minutes: " + orangesRotting(grid1) + " (expected: 4)");
        
        // Test 2: Impossible
        int[][] grid2 = {
            {2, 1, 1},
            {0, 1, 1},
            {1, 0, 1}
        };
        System.out.println("Minutes: " + orangesRotting(grid2) + " (expected: -1)");
        
        // Test 3: No fresh
        int[][] grid3 = {{0, 2}};
        System.out.println("Minutes: " + orangesRotting(grid3) + " (expected: 0)");
        
        System.out.println("\n=== KEY INSIGHT ===");
        System.out.println("Multi-source BFS: Start from ALL rotten oranges simultaneously");
        System.out.println("Track minutes by BFS level (size of queue = current minute)");
        System.out.println("If freshCount > 0 after BFS → impossible (-1)");
    }
}
