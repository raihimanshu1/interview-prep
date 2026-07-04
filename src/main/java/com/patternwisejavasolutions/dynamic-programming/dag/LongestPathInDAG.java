
package com.patternwisejavasolutions.dynamicProgramming.dag;
public class LongestPathInDAG {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: n = 4, edges = [[0,1,2], [0,2,1], [1,3,3], [2,3,5]]
     * Sample Output: 6
     *
     * In a directed acyclic graph, find the maximum total edge weight of any path.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Because there is no cycle, nodes can be placed in an order where every
     * edge goes forward. Then we can safely build answers from earlier nodes.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Start DFS from every node and try every possible path.
     * Keep the largest weight seen.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Build adjacency list.
     * 2. Start DFS from each node.
     * 3. Explore every outgoing edge and return the best path.
     * Time Complexity: Exponential in number of paths
     * Space Complexity: O(n + e)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * From 0, path 0 -> 1 -> 3 has weight 5.
     * Path 0 -> 2 -> 3 has weight 6.
     * Best is 6.
     */
    public int bruteForce(int n, int[][] edges) {
        java.util.List<java.util.List<int[]>> graph = buildGraph(n, edges);
        int best = 0;
        for (int node = 0; node < n; node++) {
            best = Math.max(best, dfsAllPaths(graph, node));
        }
        return best;
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Topological order lets us relax edges once in the right order.
     * dp[v] stores the best path ending at v. Once all incoming edges to v have
     * been processed, dp[v] is final and can safely help its neighbors.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build graph and indegree.
     * 2. Use Kahn's algorithm for topological order.
     * 3. For each edge u -> v, try improving dp[v].
     * Time Complexity: O(n + e)
     * Space Complexity: O(n + e)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Process 0 first.
     * dp[1] = 2 and dp[2] = 1.
     * Later dp[3] becomes max(2 + 3, 1 + 5) = 6.
     */
    public int optimized(int n, int[][] edges) {
        java.util.List<java.util.List<int[]>> graph = buildGraph(n, edges);
        int[] indegree = new int[n];
        for (int[] edge : edges) {
            indegree[edge[1]]++;
        }

        java.util.Queue<Integer> queue = new java.util.ArrayDeque<>();
        for (int node = 0; node < n; node++) {
            if (indegree[node] == 0) {
                // Source nodes have no prerequisites in topological order.
                queue.offer(node);
            }
        }

        int[] dp = new int[n];
        int best = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            best = Math.max(best, dp[node]);

            for (int[] edge : graph.get(node)) {
                int next = edge[0];
                int weight = edge[1];
                // Best path ending at next may improve by extending path ending at node.
                dp[next] = Math.max(dp[next], dp[node] + weight);
                indegree[next]--;
                if (indegree[next] == 0) {
                    // All incoming edges for next have now been considered.
                    queue.offer(next);
                }
            }
        }

        return best;
    }

    private java.util.List<java.util.List<int[]>> buildGraph(int n, int[][] edges) {
        java.util.List<java.util.List<int[]>> graph = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new java.util.ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(new int[] {edge[1], edge[2]});
        }
        return graph;
    }

    private int dfsAllPaths(java.util.List<java.util.List<int[]>> graph, int node) {
        int best = 0;
        for (int[] edge : graph.get(node)) {
            // Try this outgoing edge, then continue along every possible suffix path.
            best = Math.max(best, edge[1] + dfsAllPaths(graph, edge[0]));
        }
        return best;
    }
}
