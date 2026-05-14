import java.util.ArrayList;
import java.util.List;

public class GraphValidTree {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given n nodes and undirected edges, decide if they form a valid tree.
     * A tree must be connected and have no cycle.
     *
     * Sample Input:
     * n = 5, edges = [[0,1],[0,2],[0,3],[1,4]]
     *
     * Sample Output:
     * true
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A valid tree with n nodes must have exactly n - 1 edges.
     * Too few edges leaves separate islands of nodes; too many edges forces a cycle.
     * With exactly n - 1 edges, we still check that all nodes are connected.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Check the two tree rules directly: the graph must have exactly n - 1 edges and all nodes must be reachable.
     * If either rule fails, it cannot be a valid tree.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * n = 5, edges count should be 4.
     * If DFS from 0 visits all 5 nodes, graph is connected.
     * So it is a tree.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. If edges count is not n - 1, return false.
     * 2. Build graph.
     * 3. DFS from node 0.
     * 4. Check every node visited.
     * Time Complexity: O(V+E)
     * Space Complexity: O(V+E)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public boolean bruteForce(int n, int[][] edges) {
        if (edges.length != n - 1) {
            return false;
        }

        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        boolean[] visited = new boolean[n];
        visit(0, graph, visited);

        for (boolean seen : visited) {
            if (!seen) {
                return false;
            }
        }

        return true;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force version builds the graph and explores it.
     * Union Find checks the same tree rules while reading edges:
     * every edge must join two different groups, and n - 1 edges handles connectedness.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Union 0 and 1.
     * Union 1 and 2.
     * If later edge 2 and 0 appears, both already have same root, so cycle.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Check edge count n - 1.
     * 2. Union every edge.
     * 3. If any union fails, cycle exists.
     * 4. Otherwise valid.
     * Time Complexity: near O(E)
     * Space Complexity: O(V)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public boolean optimized(int n, int[][] edges) {
        if (edges.length != n - 1) {
            return false;
        }

        SimpleUnionFind unionFind = new SimpleUnionFind(n);

        for (int[] edge : edges) {
            if (!unionFind.union(edge[0], edge[1])) {
                return false;
            }
        }

        return true;
    }


    private void visit(int node, List<List<Integer>> graph, boolean[] visited) {
        visited[node] = true; // Mark this node as reachable from node 0.

        for (int next : graph.get(node)) {
            if (!visited[next]) {
                visit(next, graph, visited);
            }
        }
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
                parent[node] = find(parent[node]); // Path compression points node to its group root.
            }

            return parent[node];
        }

        boolean union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);

            if (rootA == rootB) {
                return false;
            }

            parent[rootB] = rootA; // Join the two groups; a later same-root edge would be a cycle.
            return true;
        }
    }
}
