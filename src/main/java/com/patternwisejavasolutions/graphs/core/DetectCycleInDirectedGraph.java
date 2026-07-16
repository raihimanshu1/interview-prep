package com.patternwisejavasolutions.graphs.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DetectCycleInDirectedGraph {

    /*
     * PROBLEM IN SIMPLE WORDS
     * Given a directed graph with n nodes and edges from one node to another, return true if it has
     * a cycle.
     *
     * Sample Input: n = 3, edges = [[0,1],[1,2],[2,0]]
     * Sample Output: true
     *
     * SCHOOL-LEVEL INTUITION
     * A directed cycle means arrows can lead us back to a node already in the current path.
     * Reaching an old finished node is fine; reaching a node still being explored is the danger.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Use DFS and track the current recursion path. Seeing a node already in this path means cycle.
     *
     * BRUTE FORCE ALGORITHM
     * 1. Build graph.
     * 2. DFS from every unvisited node.
     * 3. Mark node as visiting while it is in the current path.
     * 4. If DFS reaches a visiting node, return true.
     * 5. Mark node as done after all outgoing edges finish.
     *
     * BRUTE FORCE DRY RUN
     * 0 -> 1 -> 2 -> 0
     * DFS reaches 0 while 0 is still visiting -> cycle.
     *
     * Time Complexity: O(V + E)
     * Space Complexity: O(V + E)
     */
    public boolean bruteForce(int n, int[][] edges) {
        List<List<Integer>> graph = buildGraph(n, edges);
        int[] state = new int[n];

        for (int node = 0; node < n; node++) {
            if (hasCycleDfs(node, graph, state)) {
                return true;
            }
        }

        return false;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     * DFS uses recursion states to catch a back edge.
     * Kahn's topological sort uses indegree instead: remove nodes with indegree 0.
     * If a cycle exists, every node in that cycle keeps waiting on another cycle node and never becomes removable.
     *
     * OPTIMIZED ALGORITHM
     * 1. Build graph and indegree array.
     * 2. Queue all nodes with indegree 0.
     * 3. Remove queued nodes and reduce indegrees of neighbors.
     * 4. If removed count is less than n, the leftover nodes are in a cycle.
     *
     * OPTIMIZED DRY RUN
     * For 0 -> 1 -> 2 -> 0, every node has indegree 1.
     * Queue starts empty, removed count is 0, so cycle exists.
     *
     * Time Complexity: O(V + E)
     * Space Complexity: O(V + E)
     */
    public boolean optimized(int n, int[][] edges) {
        List<List<Integer>> graph = buildGraph(n, edges);
        int[] indegree = new int[n];

        for (int[] edge : edges) {
            indegree[edge[1]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int node = 0; node < n; node++) {
            if (indegree[node] == 0) {
                queue.offer(node);
            }
        }

        int removed = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            removed++;

            for (int next : graph.get(node)) {
                indegree[next]--; // Removing node deletes one incoming arrow into next.

                if (indegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }

        return removed != n;
    }

    private List<List<Integer>> buildGraph(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
        }

        return graph;
    }

    private boolean hasCycleDfs(int node, List<List<Integer>> graph, int[] state) {
        if (state[node] == 1) {
            return true;
        }

        if (state[node] == 2) {
            return false;
        }

        state[node] = 1; // 1 means currently exploring; reaching it again is a directed cycle.

        for (int next : graph.get(node)) {
            if (hasCycleDfs(next, graph, state)) {
                return true;
            }
        }

        state[node] = 2; // 2 means fully explored and safe.
        return false;
    }
}
