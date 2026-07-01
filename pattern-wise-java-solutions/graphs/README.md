# Graphs

> **Core Pattern:** BFS for shortest path in unweighted graphs, DFS for connectivity/exploration, topological sort for dependency ordering.  
> **Learning Path:** Basics → Traversal (BFS/DFS) → Connectivity → Core algorithms → Shortest path → Advanced.

---

## 📖 Conceptual Foundation

### Graph Representations
| Type | Data Structure | Use Case |
|------|---------------|----------|
| Adjacency List | `Map<Integer, List<Integer>>` | Most common, efficient |
| Adjacency Matrix | `boolean[n][n]` | Dense graphs, fast edge check |
| Edge List | `List<int[]>` | Simple, good for sorting |

### BFS Template (Shortest Path in Unweighted Graph)
```java
Queue<Integer> queue = new LinkedList<>();
boolean[] visited = new boolean[n];
queue.offer(start);
visited[start] = true;
int steps = 0;

while (!queue.isEmpty()) {
    int size = queue.size();
    for (int i = 0; i < size; i++) {
        int node = queue.poll();
        if (node == target) return steps;
        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                queue.offer(neighbor);
            }
        }
    }
    steps++;
}
```

### DFS Template (Connectivity)
```java
void dfs(int node, boolean[] visited, Map<Integer, List<Integer>> graph) {
    visited[node] = true;
    for (int neighbor : graph.get(node)) {
        if (!visited[neighbor]) dfs(neighbor, visited, graph);
    }
}
```

---

## 📚 Learning Order

### Phase 1: Graph Traversal

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Flood Fill** | [traversal/FloodFill.java](traversal/FloodFill.java) | DFS/BFS on grid, replace color | 🟢 Easy |
| 2 | **Number of Islands** | [traversal/NumberOfIslands.java](traversal/NumberOfIslands.java) | DFS/BFS each '1', count components | 🟡 Medium |
| 3 | **Pacific Atlantic Water Flow** | [traversal/PacificAtlanticWaterFlow.java](traversal/PacificAtlanticWaterFlow.java) | Reverse BFS from both oceans | 🟡 Medium |
| 4 | **Rotten Oranges** | [traversal/RottenOrganes.java](traversal/RottenOrganes.java) | BFS multi-source level order | 🟡 Medium |

### Phase 2: Connectivity & Cycle Detection

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 5 | **Number of Connected Components** | [connectivity/NumberOfConnectedComponents.java](connectivity/NumberOfConnectedComponents.java) | DFS count in undirected graph | 🟡 Medium |
| 6 | **Graph Valid Tree** | [connectivity/GraphValidTree.java](connectivity/GraphValidTree.java) | Connected + no cycles (edges = nodes - 1) | 🟡 Medium |
| 7 | **Number of Provinces** | [connectivity/NumberOfProvinces.java](connectivity/NumberOfProvinces.java) | DFS on adjacency matrix | 🟡 Medium |
| 8 | **Detect Cycle in Undirected Graph** | [connectivity/DetectCycleInUndirectedGraph.java](connectivity/DetectCycleInUndirectedGraph.java) | DFS with parent tracking | 🟡 Medium |
| 9 | **Redundant Connection** | [connectivity/RedundantConnection.java](connectivity/RedundantConnection.java) | Union-Find detect cycle | 🟡 Medium |

### Phase 3: Core Graph Algorithms

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 10 | **Clone Graph** | [core/CloneGraph.java](core/CloneGraph.java) | HashMap + DFS/BFS | 🟡 Medium |
| 11 | **Course Schedule (Topological Sort)** | [core/CourseSchedule.java](core/CourseSchedule.java) | Kahn's algorithm (BFS) — count indegrees | 🟡 Medium |
| 12 | **Course Schedule II** | [core/CourseScheduleII.java](core/CourseScheduleII.java) | Kahn's algorithm — return order | 🟡 Medium |
| 13 | **Detect Cycle in Directed Graph** | [core/DetectCycleInDirectedGraph.java](core/DetectCycleInDirectedGraph.java) | 3-color DFS (white/gray/black) | 🟡 Medium |

### Phase 4: BFS / Shortest Path

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 14 | **Shortest Path in Binary Matrix** | [bfs-shortest-path/ShortestPathInBinaryMatrix.java](bfs-shortest-path/ShortestPathInBinaryMatrix.java) | BFS on 2D grid with 8 directions | 🟡 Medium |
| 15 | **Shortest Path in Unweighted Graph** | [bfs-shortest-path/ShortestPathInUnweightedGraph.java](bfs-shortest-path/ShortestPathInUnweightedGraph.java) | Standard BFS level tracking | 🟡 Medium |
| 16 | **Word Ladder** | [bfs-shortest-path/WordLadder.java](bfs-shortest-path/WordLadder.java) | BFS on word transformations | 🔴 Hard |

### Phase 5: Advanced Graphs

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 17 | **Network Delay Time** | [advanced-graphs/NetworkDelayTime.java](advanced-graphs/NetworkDelayTime.java) | Dijkstra's (min-heap) | 🟡 Medium |
| 18 | **Cheapest Flights Within K Stops** | [advanced-graphs/CheapestFlightsWithinKStops.java](advanced-graphs/CheapestFlightsWithinKStops.java) | Bellman-Ford OR Dijkstra with stops | 🟡 Medium |
| 19 | **Min Cost to Connect All Points** | [advanced-graphs/MinCostToConnectAllPoints.java](advanced-graphs/MinCostToConnectAllPoints.java) | Prim's (min-heap) OR Kruskal's (Union-Find) | 🟡 Medium |
| 20 | **Alien Dictionary** | [advanced-graphs/AlienDictionary.java](advanced-graphs/AlienDictionary.java) | Topological sort on character ordering | 🔴 Hard |

---

## 🔑 Key Insights

1. **BFS** = shortest path in unweighted / level order
2. **DFS** = connectivity, path existence
3. **Topological sort** = dependency ordering (indegree method)
4. **Cycle detection**: undirected → parent tracking; directed → 3-color (white/gray/black)
5. **Union-Find** = connectivity with dynamic additions
6. **Dijkstra** = shortest path in weighted graphs (non-negative)
7. **Bellman-Ford** = shortest path with negative weights (K stops constraint)

---

## 🎯 Practice Checklist

- [ ] Phase 1: Traversal (Flood Fill, Islands)
- [ ] Phase 2: Connectivity & Cycle detection
- [ ] Phase 3: Core (Clone, Topological Sort)
- [ ] Phase 4: Shortest Path BFS
- [ ] Phase 5: Advanced (Dijkstra, Prim, Bellman-Ford)