
package com.patternwisejavasolutions.graphs.connectivity;
import java.util.ArrayList;
import java.util.List;

public class NumberOfConnectedComponents {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input n nodes and undirected edges.
     * Output number of connected groups.
     *
     * Sample Input:
     * n = 5, edges = [[0,1],[1,2],[3,4]]
     *
     * Sample Output:
     * 2
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A component is one friend circle of nodes.
     * If we start from an unvisited node and explore every reachable node, that whole exploration
     * is exactly one component.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Walk from each unvisited node and mark everyone reachable from it.
     * Each new walk starts a new connected group.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Nodes 0-1-2 are connected.
     * DFS from 0 visits 0,1,2 and counts one component.
     * Another unvisited node later starts another component.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build adjacency list.
     * 2. Visit every node.
     * 3. If unvisited, count component and DFS.
     * Time Complexity: O(V+E)
     * Space Complexity: O(V+E)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        boolean[] visited = new boolean[n];
        int components = 0;

        for (int node = 0; node < n; node++) {
            if (!visited[node]) {
                components++;
                dfs(node, graph, visited);
            }
        }

        return components;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * DFS is already linear, but Union Find teaches another graph pattern.
     * Initially every node is its own component.
     * Each edge is a chance to join two components; only a join between different roots lowers the count.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * n=3, edge [0,1]
     * components go from 3 to 2.
     * edge [1,2] joins same group with 2, components go from 2 to 1.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start count = n.
     * 2. For every edge, union two nodes.
     * 3. If union actually joins different roots, decrease count.
     * Time Complexity: near O(E)
     * Space Complexity: O(V)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(int n, int[][] edges) {
        UnionFind uf = new UnionFind(n);
        int components = n;

        for (int[] edge : edges) {
            if (uf.union(edge[0], edge[1])) {
                components--;
            }
        }

        return components;
    }


    private void dfs(int node, List<List<Integer>> graph, boolean[] visited) {
        visited[node] = true; // This node is now part of the component currently being explored.

        for (int next : graph.get(node)) {
            if (!visited[next]) {
                dfs(next, graph, visited);
            }
        }
    }

    private static class UnionFind {
        int[] parent;
        int[] rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        int find(int node) {
            if (parent[node] != node) {
                parent[node] = find(parent[node]); // Compress the path to make later finds faster.
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
                parent[rootA] = rootB; // Attach the shorter tree under the taller one.
            } else if (rank[rootA] > rank[rootB]) {
                parent[rootB] = rootA; // Attach the shorter tree under the taller one.
            } else {
                parent[rootB] = rootA; // Same rank: choose one root and increase its rank.
                rank[rootA]++;
            }

            return true;
        }
    }
}
