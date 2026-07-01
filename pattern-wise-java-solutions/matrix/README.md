# Matrix

> **Core Pattern:** 2D grid traversal with row/column manipulation, BFS/DFS for connected components.  
> **Learning Path:** Basic traversal & transform → BFS/DFS on grid → advanced patterns.

---

## 📖 Conceptual Foundation

### 2D Grid Traversal
```java
int rows = matrix.length, cols = matrix[0].length;
int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}}; // 4-directional

for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        // process matrix[r][c]
    }
}
```

### 4-Directional BFS on Grid Template
```java
Queue<int[]> queue = new LinkedList<>();
boolean[][] visited = new boolean[rows][cols];
int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

while (!queue.isEmpty()) {
    int[] cell = queue.poll();
    for (int[] d : dirs) {
        int nr = cell[0] + d[0], nc = cell[1] + d[1];
        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
            visited[nr][nc] = true;
            queue.offer(new int[]{nr, nc});
        }
    }
}
```

---

## 📚 Learning Order

### Phase 1: Traversal & Transformation

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Spiral Matrix** | [SpiralMatrix.java](SpiralMatrix.java) | Layer-by-layer traversal with 4 boundaries | 🟡 Medium |
| 2 | **Rotate Image** | [RotateImage.java](RotateImage.java) | Transpose + reverse rows (or rotate 4 cells) | 🟡 Medium |
| 3 | **Set Matrix Zeroes** | [SetMatrixZeroes.java](SetMatrixZeroes.java) | Use first row/col as markers | 🟡 Medium |

### Phase 2: BFS/DFS on Grid

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 4 | **Word Search** | [WordSearch.java](WordSearch.java) | DFS with backtracking on grid | 🟡 Medium |
| 5 | **Rotting Oranges** | [RottingOranges.java](RottingOranges.java) | Multi-source BFS with time tracking | 🟡 Medium |

---

## 🔑 Key Insights

1. **4-directional moves** = dirs array `{{0,1},{0,-1},{1,0},{-1,0}}`
2. **Matrix rotation** = transpose → reverse each row
3. **In-place markers** = use first row/col to store state (Set Matrix Zeroes)
4. **Multi-source BFS** = add all sources to queue first, then BFS level by level

---

## 🎯 Practice Checklist

- [ ] Phase 1: Spiral, Rotate, Set Zeroes
- [ ] Phase 2: Word Search, Rotting Oranges