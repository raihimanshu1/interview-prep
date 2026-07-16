package com.patternwisejavasolutions.graphs.connectivity;

import java.util.ArrayList;
import java.util.List;

public class DetectCycleInUndirectedGraph {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given an undirected graph with n nodes and edges, return true if the graph has a cycle.
     *
     * Sample Input: n = 3, edges = [[0,1],[1,2],[2,0]]
     * Sample Output: true
     *
     * SCHOOL-LEVEL INTUITION
     * In an undirected graph, a cycle means we can start at a node, follow edges, and come back
     * without simply turning around on the same edge.
     * That parent edge matters because every undirected road appears in both directions.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Use DFS and remember the node we came from.
     * Seeing the parent again is normal in an undirected graph, but seeing any other visited
     * neighbor means there is another route back, so a cycle exists.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Build an adjacency list.
     * 2. Start DFS from every unvisited node.
     * 3. Pass the parent node into DFS.
     * 4. If a visited neighbor is not the parent, return true.
     *
     * BRUTE FORCE DRY RUN
     * edges 0-1, 1-2, 2-0
     * DFS 0 -> 1 -> 2, node 2 sees visited neighbor 0 that is not parent 1 -> cycle.
     *
     * Time Complexity: O(V + E)
     * Space Complexity: O(V + E)
     */
    public boolean bruteForce(int n, int[][] edges) {
        List<List<Integer>> graph = buildGraph(n, edges);
        boolean[] visited = new boolean[n];

        for (int node = 0; node < n; node++) {
            if (!visited[node] && hasCycleDfs(node, -1, graph, visited)) {
                return true;
            }
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * DFS looks through neighbors to discover the loop.
     * Union Find catches the loop while edges are added: if an edge connects two nodes already
     * in the same group, that edge closes a cycle.
     *
     * OPTIMIZED ALGORITHM
     * 1. Start every node as its own group.
     * 2. For each edge, find both group leaders.
     * 3. If leaders are equal, a cycle exists.
     * 4. Otherwise union the two groups.
     *
     * OPTIMIZED DRY RUN
     * union 0-1, union 1-2.
     * edge 2-0 finds both already in one group -> cycle.
     *
     * Time Complexity: near O(E)
     * Space Complexity: O(V)
     */
    public boolean optimized(int n, int[][] edges) {
        UnionFind unionFind = new UnionFind(n);

        for (int[] edge : edges) {
            if (!unionFind.union(edge[0], edge[1])) {
                return true;
            }
        }

        return false;
    }

    private List<List<Integer>> buildGraph(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        return graph;
    }

    private boolean hasCycleDfs(int node, int parent, List<List<Integer>> graph, boolean[] visited) {
        visited[node] = true; // This node is now on a known DFS route.

        for (int next : graph.get(node)) {
            if (!visited[next]) {
                if (hasCycleDfs(next, node, graph, visited)) {
                    return true;
                }
            } else if (next != parent) {
                // Visited neighbor that is not the edge we came from means a real cycle.
                return true;
            }
        }

        return false;
    }

    private static class UnionFind {
        private final int[] parent;
        private final int[] rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        int find(int node) {
            if (parent[node] != node) {
                parent[node] = find(parent[node]); // Path compression keeps roots quick to find.
            }

            return parent[node];
        }

        boolean union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);

            if (rootA == rootB) {
                return false;
            }

            if (rank[rootA] < rank[rootB]) {
                parent[rootA] = rootB;
            } else if (rank[rootA] > rank[rootB]) {
                parent[rootB] = rootA;
            } else {
                parent[rootB] = rootA;
                rank[rootA]++;
            }

            return true;
        }
    }
}
