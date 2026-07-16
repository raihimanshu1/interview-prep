package com.patternwisejavasolutions.graphs.connectivity;

public class RedundantConnection {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input edges form a tree plus one extra edge.
     * Output the edge that creates a cycle.
     *
     * Sample Input:
     * edges = [[1,2],[1,3],[2,3]]
     *
     * Sample Output:
     * [2,3]
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Add edges one by one.
     * If two nodes are already connected before adding an edge, the new edge only creates a loop.
     * That is why the important question before each edge is: "Are these endpoints already in the same group?"
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Before adding a new edge, ask whether its two endpoints are already connected by earlier edges.
     * If yes, this new edge closes a cycle and is the redundant one.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Edges: [1,2], [1,3], [2,3]
     * After first two edges, 2 and 3 are already connected through 1.
     * Edge [2,3] creates cycle, return it.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Before adding each edge, run DFS to see if endpoints are already connected.
     * 2. If yes, return that edge.
     * 3. Otherwise add the edge.
     * Time Complexity: O(E * (V+E))
     * Space Complexity: O(V+E)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int[] bruteForce(int[][] edges) {
        java.util.Map<Integer, java.util.List<Integer>> graph = new java.util.HashMap<>();

        for (int[] edge : edges) {
            java.util.Set<Integer> visited = new java.util.HashSet<>();

            if (graph.containsKey(edge[0]) && graph.containsKey(edge[1]) && connected(edge[0], edge[1], graph, visited)) {
                return edge;
            }

            graph.putIfAbsent(edge[0], new java.util.ArrayList<>());
            graph.putIfAbsent(edge[1], new java.util.ArrayList<>());
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        return new int[0];
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force pain is running a fresh DFS before many edges.
     * Union Find keeps a group leader for every connected group, so "already connected?"
     * becomes a quick root comparison. If both endpoints have the same root, the edge is redundant.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Union 1 and 2 succeeds.
     * Union 1 and 3 succeeds.
     * Union 2 and 3 fails because both have same root.
     * Return [2,3].
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Create Union Find.
     * 2. For each edge, union endpoints.
     * 3. If endpoints already connected, return edge.
     * Time Complexity: near O(E)
     * Space Complexity: O(V)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int[] optimized(int[][] edges) {
        SimpleUnionFind uf = new SimpleUnionFind(edges.length + 1);

        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) {
                return edge;
            }
        }

        return new int[0];
    }


    private boolean connected(int source, int target, java.util.Map<Integer, java.util.List<Integer>> graph, java.util.Set<Integer> visited) {
        if (source == target) {
            return true;
        }

        visited.add(source); // Avoid walking around a cycle forever during this connected check.

        for (int next : graph.getOrDefault(source, java.util.Collections.emptyList())) {
            if (!visited.contains(next) && connected(next, target, graph, visited)) {
                return true;
            }
        }

        return false;
    }

    private static class SimpleUnionFind {
        int[] parent;

        SimpleUnionFind(int n) {
            parent = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        int find(int node) {
            if (parent[node] != node) {
                parent[node] = find(parent[node]); // Path compression points node closer to its group leader.
            }

            return parent[node];
        }

        boolean union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);

            if (rootA == rootB) {
                return false;
            }

            parent[rootB] = rootA; // Merge the two groups by making one root point to the other.
            return true;
        }
    }
}
