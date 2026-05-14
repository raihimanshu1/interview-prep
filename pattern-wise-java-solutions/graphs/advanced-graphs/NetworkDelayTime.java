import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class NetworkDelayTime {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input directed weighted edges times, n nodes, start k.
     * Return time needed for signal from k to reach every node.
     * If some node cannot be reached, return -1.
     *
     * Sample Input:
     * times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2
     *
     * Sample Output:
     * 2
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * We need shortest time from k to every node.
     * The signal spreads through roads with different travel times.
     * With positive weights, Dijkstra's algorithm is the standard tool because the smallest known
     * unvisited time can be trusted next.
     *
     * Why can we trust the smallest known time?
     * If all edge weights are positive, any future path that reaches this node later would only add
     * more positive cost. So once this node is the cheapest unvisited option, no hidden route can
     * come back and make it cheaper.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Keep the best known signal time for every node.
     * Each time, manually choose the unvisited node with the smallest known time and relax its outgoing roads.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Start node k has distance 0.
     * Relax outgoing edges.
     * Always next process the unvisited node with smallest known distance.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Keep distance array.
     * 2. Repeatedly scan all unvisited nodes to pick smallest distance.
     * 3. Relax its outgoing edges.
     * Time Complexity: O(V^2 + E)
     * Space Complexity: O(V+E)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(int[][] times, int n, int k) {
        List<int[]>[] graph = buildGraph(times, n);
        int[] dist = new int[n + 1];
        boolean[] visited = new boolean[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE / 2);
        dist[k] = 0;

        for (int count = 1; count <= n; count++) {
            int node = -1;

            for (int candidate = 1; candidate <= n; candidate++) {
                if (!visited[candidate] && (node == -1 || dist[candidate] < dist[node])) {
                    node = candidate;
                }
            }

            if (node == -1 || dist[node] >= Integer.MAX_VALUE / 2) {
                break;
            }

            visited[node] = true;

            for (int[] edge : graph[node]) {
                // Relax edge: if going through node is faster, improve the neighbor's time.
                dist[edge[0]] = Math.min(dist[edge[0]], dist[node] + edge[1]);
            }
        }

        return finish(dist, n);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The brute force Dijkstra version wastes time scanning all nodes to find the next smallest time.
     * A priority queue keeps the smallest time at the front, which is the optimized Dijkstra approach.
     *
     * The invariant we protect:
     * whenever we pop a node with time == dist[node], that is the best final time known for that node.
     * Any old heap entry with time > dist[node] is stale and should be ignored.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Queue starts [k,0].
     * Pop smallest time node.
     * If edge reaches neighbor with better time, update and push neighbor.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build adjacency list.
     * 2. dist[k] = 0.
     * 3. Use min-heap of node/time.
     * 4. Relax edges.
     * 5. Return max distance if all reachable.
     * Time Complexity: O(E log V)
     * Space Complexity: O(V+E)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(int[][] times, int n, int k) {
        List<int[]>[] graph = buildGraph(times, n);
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE / 2);
        dist[k] = 0;
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        heap.offer(new int[] { k, 0 });

        while (!heap.isEmpty()) {
            int[] current = heap.poll();
            int node = current[0];
            int time = current[1];

            if (time > dist[node]) {
                /*
                 * We already found a faster way to this node after this heap entry was inserted.
                 * This old entry cannot teach us anything new.
                 */
                continue;
            }

            for (int[] edge : graph[node]) {
                int next = edge[0];
                int newTime = time + edge[1];

                if (newTime < dist[next]) {
                    // Relax edge: found a faster signal arrival time for next.
                    dist[next] = newTime;
                    heap.offer(new int[] { next, newTime });
                }
            }
        }

        return finish(dist, n);
    }


    private List<int[]>[] buildGraph(int[][] times, int n) {
        List<int[]>[] graph = new ArrayList[n + 1];

        for (int i = 1; i <= n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] edge : times) {
            graph[edge[0]].add(new int[] { edge[1], edge[2] });
        }

        return graph;
    }

    private int finish(int[] dist, int n) {
        int answer = 0;

        for (int node = 1; node <= n; node++) {
            if (dist[node] >= Integer.MAX_VALUE / 2) {
                return -1;
            }

            answer = Math.max(answer, dist[node]);
        }

        return answer;
    }
}
