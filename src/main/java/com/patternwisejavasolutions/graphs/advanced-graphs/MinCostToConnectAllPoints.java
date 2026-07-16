package com.patternwisejavasolutions.graphs.advancedgraphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinCostToConnectAllPoints {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given points on a 2D plane.
     * Cost to connect two points is Manhattan distance.
     * Return minimum cost to connect all points.
     *
     * Sample Input:
     * points = [[0,0],[2,2],[3,10],[5,2],[7,0]]
     *
     * Sample Output:
     * 20
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * This is asking for a minimum spanning tree.
     * We need connect all points with minimum total edge cost.
     * The final network should have just enough connections to keep every point reachable, without wasteful cycles.
     *
     * From first principles:
     * If all points are already connected and we add one more edge inside that connected group,
     * that extra edge creates a cycle. A cycle means there are two ways to reach the same place,
     * so one of those wires is unnecessary for connectivity.
     *
     * So the goal is not "choose many cheap wires"; it is "choose cheap wires that connect new
     * points without creating wasteful cycles." That idea is called a minimum spanning tree.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * A beginner can first list every possible wire between two points.
     * Then choose the cheapest wires that connect new groups without making a cycle.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Build all pair edges: 0-1 costs 4, 0-2 costs 13, 1-3 costs 3, and so on.
     * Sort all edges by cost.
     * Take the cheapest edge if its endpoints are in different groups.
     * Skip any edge whose endpoints are already connected, because it would make a cycle.
     * Stop after choosing n - 1 edges.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Generate all pair edges.
     * 2. Sort edges by cost.
     * 3. Use Union Find to pick edges that do not form cycles.
     * Time Complexity: O(n^2 log n)
     * Space Complexity: O(n^2)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(int[][] points) {
        int n = points.length;
        List<int[]> edges = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int cost = manhattan(points, i, j);
                // Store every possible connection, then let Kruskal pick the cheapest safe ones.
                edges.add(new int[] { cost, i, j });
            }
        }

        Collections.sort(edges, (a, b) -> Integer.compare(a[0], b[0]));

        UnionFind unionFind = new UnionFind(n);
        int totalCost = 0;
        int edgesUsed = 0;

        for (int[] edge : edges) {
            int cost = edge[0];
            int from = edge[1];
            int to = edge[2];

            if (unionFind.union(from, to)) {
                totalCost += cost;
                edgesUsed++;

                if (edgesUsed == n - 1) {
                    break;
                }
            }
        }

        return totalCost;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * Kruskal's brute force stores every possible wire, which costs lots of memory.
     * Prim's algorithm avoids storing all edges.
     * It grows one connected group and repeatedly chooses the nearest unconnected point to that group.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Connected starts with point 0.
     * Pick cheapest distance from point 0.
     * Now connected group has two points.
     * Again pick cheapest edge from group to outside.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Use minDistanceToTree array.
     * 2. Repeatedly pick unvisited point with smallest distance.
     * 3. Add its distance to cost.
     * 4. Update distances to remaining points.
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(int[][] points) {
        int n = points.length;
        boolean[] used = new boolean[n];
        int[] minDistance = new int[n];
        java.util.Arrays.fill(minDistance, Integer.MAX_VALUE);
        minDistance[0] = 0;
        int totalCost = 0;

        for (int count = 0; count < n; count++) {
            int point = -1;

            for (int candidate = 0; candidate < n; candidate++) {
                if (!used[candidate] && (point == -1 || minDistance[candidate] < minDistance[point])) {
                    point = candidate;
                }
            }

            used[point] = true; // This point is now part of the growing connected network.
            totalCost += minDistance[point];

            for (int next = 0; next < n; next++) {
                if (!used[next]) {
                    int distance = manhattan(points, point, next);
                    // Keep the cheapest known way to attach next to the current network.
                    minDistance[next] = Math.min(minDistance[next], distance);
                }
            }
        }

        return totalCost;
    }

    private int manhattan(int[][] points, int first, int second) {
        return Math.abs(points[first][0] - points[second][0])
                + Math.abs(points[first][1] - points[second][1]);
    }

    static class UnionFind {
        int[] parent;
        int[] rank;

        UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];

            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        int find(int node) {
            if (parent[node] != node) {
                // Path compression keeps future checks short.
                parent[node] = find(parent[node]);
            }

            return parent[node];
        }

        boolean union(int first, int second) {
            int rootFirst = find(first);
            int rootSecond = find(second);

            if (rootFirst == rootSecond) {
                return false;
            }

            if (rank[rootFirst] < rank[rootSecond]) {
                // Shorter group tree points to taller group tree, keeping future find calls shallow.
                parent[rootFirst] = rootSecond;
            } else if (rank[rootFirst] > rank[rootSecond]) {
                // The second point's group is shorter, so attach it under the first point's group.
                parent[rootSecond] = rootFirst;
            } else {
                // Equal ranks: choose one leader and increase its rank because its tree got taller.
                parent[rootSecond] = rootFirst;
                rank[rootFirst]++;
            }

            return true;
        }
    }
}
