import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ShortestPathInUnweightedGraph {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given an unweighted graph, source, and target.
     * Return the minimum number of edges from source to target.
     *
     * Sample Input:
     * n = 5, edges = [[0,1],[1,2],[0,3],[3,4],[4,2]], source = 0, target = 2
     *
     * Sample Output:
     * 2
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * In an unweighted graph, every edge costs the same.
     * So the shortest route is the one with the fewest edge hops.
     * BFS is perfect because it explores all nodes 0 hops away, then 1 hop away, then 2 hops away.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * A beginner can try possible paths from source and remember the shortest one that reaches target.
     * This may revisit many partial paths, but it follows the definition of shortest path directly.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * DFS first tries one complete route, for example 0 -> 1 -> 2, and records length 2.
     * Then it backtracks and tries another complete route, such as 0 -> 3 -> 4 -> 2,
     * recording length 3.
     * After every simple path has been tried, the smallest recorded length is the answer.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Try all paths with DFS and track shortest.
     * 2. Avoid revisiting nodes in current path.
     * Time Complexity: exponential
     * Space Complexity: O(V)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(int n, int[][] edges, int source, int target) {
        List<List<Integer>> graph = build(n, edges);
        boolean[] visited = new boolean[n];
        int answer = dfsShortest(source, target, graph, visited);
        return answer >= Integer.MAX_VALUE / 2 ? -1 : answer;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * DFS tries full paths and may wander deep before discovering a shorter route.
     * BFS fixes that pain by using a queue level for each distance.
     * The first time target is removed from the queue, no shorter distance is still waiting.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Queue source.
     * Process one level, distance becomes 1.
     * If target appears in that level, answer is 1.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build graph.
     * 2. Queue source and mark visited.
     * 3. Process level by level.
     * 4. Return distance when target found.
     * Time Complexity: O(V+E)
     * Space Complexity: O(V+E)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(int n, int[][] edges, int source, int target) {
        List<List<Integer>> graph = build(n, edges);
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[n];
        queue.offer(source);
        visited[source] = true;
        int distance = 0;

        while (!queue.isEmpty()) {
            int size = queue.size(); // These nodes are exactly distance edges away from source.

            for (int i = 0; i < size; i++) {
                int node = queue.poll();

                if (node == target) {
                    return distance;
                }

                for (int next : graph.get(node)) {
                    if (!visited[next]) {
                        visited[next] = true; // Mark on enqueue so the same node is not queued many times.
                        queue.offer(next);
                    }
                }
            }

            distance++;
        }

        return -1;
    }


    private List<List<Integer>> build(int n, int[][] edges) {
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

    private int dfsShortest(int node, int target, List<List<Integer>> graph, boolean[] visited) {
        if (node == target) {
            return 0;
        }

        visited[node] = true; // Current path now contains this node, so do not loop back to it.
        int best = Integer.MAX_VALUE / 2;

        for (int next : graph.get(node)) {
            if (!visited[next]) {
                best = Math.min(best, 1 + dfsShortest(next, target, graph, visited));
            }
        }

        visited[node] = false; // Backtrack so other possible paths may use this node later.
        return best;
    }
}
