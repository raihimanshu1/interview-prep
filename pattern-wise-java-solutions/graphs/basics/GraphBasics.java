/**
 * GRAPH — Basics & Warmup
 * 
 * Before jumping into problems, master these fundamentals:
 * 1. Graph representations (adjacency matrix, adjacency list, edge list)
 * 2. Graph traversals: DFS (recursive & iterative) & BFS
 * 3. Connected components count
 * 4. Detect cycle in directed & undirected graphs
 * 5. Topological sort (Kahn's algorithm & DFS-based)
 * 6. Graph framework & patterns
 */

import java.util.*;

public class GraphBasics {

    // ==========================================
    // 1. GRAPH REPRESENTATIONS
    // ==========================================
    
    // ADJACENCY LIST (most common, most efficient)
    // For a graph with n nodes, edges are stored in a list per node
    static class GraphAdjList {
        int n;
        List<Integer>[] adj;  // array of lists
        
        @SuppressWarnings("unchecked")
        GraphAdjList(int n) {
            this.n = n;
            adj = new ArrayList[n];
            for (int i = 0; i < n; i++) {
                adj[i] = new ArrayList<>();
            }
        }
        
        void addEdge(int u, int v) {
            adj[u].add(v);
            adj[v].add(u);  // for undirected graph
        }
        
        void addDirectedEdge(int u, int v) {
            adj[u].add(v);
        }
    }
    
    // ADJACENCY LIST using HashMap (for sparse/arbitrary node labels)
    static class GraphHashMap {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        
        void addEdge(int u, int v) {
            adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
            adj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
        }
        
        List<Integer> getNeighbors(int u) {
            return adj.getOrDefault(u, new ArrayList<>());
        }
    }

    // ==========================================
    // 2. DFS TRAVERSAL — O(V + E)
    // ==========================================
    
    // DFS Recursive
    public static void dfsRecursive(List<Integer>[] adj, int start, boolean[] visited) {
        visited[start] = true;
        System.out.print(start + " ");
        
        for (int neighbor : adj[start]) {
            if (!visited[neighbor]) {
                dfsRecursive(adj, neighbor, visited);
            }
        }
    }
    
    // DFS Iterative (using stack)
    public static void dfsIterative(List<Integer>[] adj, int start) {
        boolean[] visited = new boolean[adj.length];
        Stack<Integer> stack = new Stack<>();
        stack.push(start);
        
        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (visited[node]) continue;
            
            visited[node] = true;
            System.out.print(node + " ");
            
            // Push neighbors in reverse order to maintain order
            List<Integer> neighbors = adj[node];
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                if (!visited[neighbors.get(i)]) {
                    stack.push(neighbors.get(i));
                }
            }
        }
    }

    // ==========================================
    // 3. BFS TRAVERSAL — O(V + E)
    // ==========================================
    // Uses a queue. Finds shortest path in unweighted graphs.
    
    public static void bfs(List<Integer>[] adj, int start) {
        boolean[] visited = new boolean[adj.length];
        Queue<Integer> queue = new LinkedList<>();
        
        visited[start] = true;
        queue.offer(start);
        
        while (!queue.isEmpty()) {
            int node = queue.poll();
            System.out.print(node + " ");
            
            for (int neighbor : adj[node]) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
    }
    
    // BFS with distance tracking
    public static int[] bfsDistances(List<Integer>[] adj, int start) {
        int n = adj.length;
        int[] dist = new int[n];
        Arrays.fill(dist, -1);
        Queue<Integer> queue = new LinkedList<>();
        
        dist[start] = 0;
        queue.offer(start);
        
        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int neighbor : adj[node]) {
                if (dist[neighbor] == -1) {
                    dist[neighbor] = dist[node] + 1;
                    queue.offer(neighbor);
                }
            }
        }
        return dist;
    }

    // ==========================================
    // 4. CONNECTED COMPONENTS — O(V + E)
    // ==========================================
    
    public static int countComponents(int n, int[][] edges) {
        // Build adjacency list
        List<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] edge : edges) {
            adj[edge[0]].add(edge[1]);
            adj[edge[1]].add(edge[0]);
        }
        
        boolean[] visited = new boolean[n];
        int components = 0;
        
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                components++;
                dfsRecursive(adj, i, visited);
                System.out.println();  // new line per component
            }
        }
        return components;
    }

    // ==========================================
    // 5. CYCLE DETECTION
    // ==========================================
    
    // Undirected graph: check if a neighbor (already visited) is NOT the parent
    public static boolean hasCycleUndirected(List<Integer>[] adj) {
        int n = adj.length;
        boolean[] visited = new boolean[n];
        
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (hasCycleUndirectedUtil(adj, i, -1, visited)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean hasCycleUndirectedUtil(List<Integer>[] adj, int node, 
                                                   int parent, boolean[] visited) {
        visited[node] = true;
        for (int neighbor : adj[node]) {
            if (!visited[neighbor]) {
                if (hasCycleUndirectedUtil(adj, neighbor, node, visited)) {
                    return true;
                }
            } else if (neighbor != parent) {
                return true;  // visited neighbor that's not parent → cycle
            }
        }
        return false;
    }
    
    // Directed graph: track recursion stack (path visited)
    public static boolean hasCycleDirected(List<Integer>[] adj) {
        int n = adj.length;
        boolean[] visited = new boolean[n];
        boolean[] recStack = new boolean[n];  // nodes in current DFS path
        
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (hasCycleDirectedUtil(adj, i, visited, recStack)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean hasCycleDirectedUtil(List<Integer>[] adj, int node,
                                                boolean[] visited, boolean[] recStack) {
        visited[node] = true;
        recStack[node] = true;
        
        for (int neighbor : adj[node]) {
            if (!visited[neighbor]) {
                if (hasCycleDirectedUtil(adj, neighbor, visited, recStack)) {
                    return true;
                }
            } else if (recStack[neighbor]) {
                return true;  // found back edge to a node in current path
            }
        }
        
        recStack[node] = false;  // backtrack
        return false;
    }

    // ==========================================
    // 6. TOPOLOGICAL SORT (Directed Acyclic Graph)
    // ==========================================
    
    // Kahn's Algorithm (BFS-based)
    public static List<Integer> topologicalSort(List<Integer>[] adj) {
        int n = adj.length;
        int[] indegree = new int[n];
        
        // Calculate indegree for each node
        for (int u = 0; u < n; u++) {
            for (int v : adj[u]) {
                indegree[v]++;
            }
        }
        
        // Add all nodes with 0 indegree to queue
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) queue.offer(i);
        }
        
        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            result.add(u);
            
            for (int v : adj[u]) {
                indegree[v]--;
                if (indegree[v] == 0) queue.offer(v);
            }
        }
        
        // If result size != n, graph has a cycle
        if (result.size() != n) {
            System.out.println("Graph has a cycle! Topological sort not possible.");
            return new ArrayList<>();
        }
        return result;
    }

    // ==========================================
    // 7. FRAMEWORKS & PATTERNS
    // ==========================================
    
    // DFS Framework
    public static void dfsFramework(List<Integer>[] adj) {
        int n = adj.length;
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsRecursive(adj, i, visited);
            }
        }
    }
    
    // BFS Framework
    public static void bfsFramework(List<Integer>[] adj, int start) {
        boolean[] visited = new boolean[adj.length];
        Queue<Integer> queue = new LinkedList<>();
        visited[start] = true;
        queue.offer(start);
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int node = queue.poll();
                // Process node
                for (int neighbor : adj[node]) {
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        queue.offer(neighbor);
                    }
                }
            }
        }
    }
    
    // Build adjacency list from edge list
    @SuppressWarnings("unchecked")
    public static List<Integer>[] buildAdjacencyList(int n, int[][] edges, boolean directed) {
        List<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        
        for (int[] edge : edges) {
            adj[edge[0]].add(edge[1]);
            if (!directed) {
                adj[edge[1]].add(edge[0]);
            }
        }
        return adj;
    }

    // ==========================================
    // MAIN — Test everything
    // ==========================================
    
    public static void main(String[] args) {
        System.out.println("=== GRAPH BASICS ===");
        
        // Build graph: 0-1-2-3-4 (chain)
        //                |
        //                5
        int n = 6;
        List<Integer>[] adj = buildAdjacencyList(n, 
            new int[][]{{0,1}, {1,2}, {2,3}, {3,4}, {2,5}}, false);
        
        System.out.println("\nGraph: 0-1-2-3-4, 2-5");
        System.out.print("DFS Recursive from 0: ");
        dfsRecursive(adj, 0, new boolean[n]);
        System.out.println();
        
        System.out.print("DFS Iterative from 0: ");
        dfsIterative(adj, 0);
        System.out.println();
        
        System.out.print("BFS from 0: ");
        bfs(adj, 0);
        System.out.println();
        
        System.out.println("BFS distances from 0: " + 
                          Arrays.toString(bfsDistances(adj, 0)));
        
        // Cyclic graph
        List<Integer>[] cyclicAdj = buildAdjacencyList(3, 
            new int[][]{{0,1}, {1,2}, {2,0}}, false);
        System.out.println("\nHas cycle (undirected): " + hasCycleUndirected(cyclicAdj));
        
        // Directed cyclic graph
        List<Integer>[] dirAdj = buildAdjacencyList(3, 
            new int[][]{{0,1}, {1,2}, {2,0}}, true);
        System.out.println("Has cycle (directed): " + hasCycleDirected(dirAdj));
        
        // DAG for topological sort
        List<Integer>[] dag = buildAdjacencyList(6, 
            new int[][]{{5,2}, {5,0}, {4,0}, {4,1}, {2,3}, {3,1}}, true);
        System.out.println("Topological sort: " + topologicalSort(dag));
        
        System.out.println("\n=== KEY GRAPH PATTERNS ===");
        System.out.println("1. DFS: Explore deep first → connectivity, cycles, topological sort");
        System.out.println("2. BFS: Explore level by level → shortest path (unweighted)");
        System.out.println("3. Adjacency List: Most common representation");
        System.out.println("4. Cycle Detection: Parent tracking (undirected) + RecStack (directed)");
        System.out.println("5. Topological Sort: Kahn's algorithm (BFS + indegree)");
        System.out.println("6. Connected Components: Run DFS/BFS from each unvisited node");
    }
}