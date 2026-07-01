# 🧬 Problem 42: Game of Life (Conway's)

> **Difficulty**: ⭐⭐ | **Company Fit**: Any tech company  
> **Est. Time**: 60 min | **Patterns**: State Machine, Matrix, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Simulate Conway's Game of Life."

**What the interviewer tests**:
```
1. Can you model cell states? (Alive, dead)
2. Can you apply the 4 rules correctly?
3. Can you optimize for large grids? (Sparse representation)
4. Can you handle infinite grids? (Toroidal)
```

### Step 2: The "Aha!" Moment

The key insight: **Game of Life is a state transition system.**

```
RULES:
  1. Underpopulation:  Live cell + < 2 neighbors → dies
  2. Survival:        Live cell + 2-3 neighbors → lives
  3. Overpopulation:  Live cell + > 3 neighbors → dies
  4. Reproduction:    Dead cell + exactly 3 neighbors → becomes alive

NEIGHBORS (8 directions):
  [NW] [N] [NE]
  [W]  [C]  [E]
  [SW] [S] [SE]

EXAMPLE:
  Generation 0:          Generation 1:
    . . . . .              . . . . .
    . . X . .              . . . . .
    . . X . .      →       . X X . .
    . . X . .              . . . . .
    . . . . .              . . . . .
```

### Step 3: How to optimize for sparse grids?

```
NAIVE (Dense):
  1000x1000 grid → 1M cells tracked
  Even if only 1000 cells alive

OPTIMIZED (Sparse):
  Only store live cells: Set<Point>
  For each live cell, count neighbors by checking 8 directions
  Only update cells that change state

For sparse grid with 1000 cells:
  - Check 1000 cells × 8 neighbors = 8000 operations
  - Much better than 1M
```

---

## 💻 Core Implementation

```java
package com.life;

import java.util.*;

/**
 * INTUITION: GameOfLife simulates Conway's Game of Life.
 * 
 * Uses sparse representation for efficiency.
 */
public class GameOfLife {
    
    // Live cells: stored as set of coordinates
    private Set<Cell> liveCells;
    private final int width;
    private final int height;
    private boolean toroidal;  // Wrap-around edges

    public GameOfLife(int width, int height) {
        this(width, height, false);
    }

    public GameOfLife(int width, int height, boolean toroidal) {
        this.width = width;
        this.height = height;
        this.toroidal = toroidal;
        this.liveCells = ConcurrentHashMap.newKeySet();
    }

    /**
     * INTUITION: Initialize with pattern.
     */
    public void initialize(Set<Cell> initialCells) {
        liveCells.clear();
        for (Cell cell : initialCells) {
            if (isValid(cell)) {
                liveCells.add(cell);
            }
        }
    }

    /**
     * INTUITION: Set cell alive.
     */
    public synchronized void setAlive(int x, int y, boolean alive) {
        Cell cell = new Cell(x, y);
        if (alive) {
            liveCells.add(cell);
        } else {
            liveCells.remove(cell);
        }
    }

    /**
     * INTUITION: Advance one generation.
     * 
     * Algorithm:
     * 1. Find all cells to check (live cells + their neighbors)
     * 2. For each cell, count live neighbors
     * 3. Apply rules to determine next state
     * 4. Update live cells set
     */
    public synchronized void nextGeneration() {
        Set<Cell> nextGen = ConcurrentHashMap.newKeySet();
        
        // Cells to evaluate: all live cells + all their neighbors
        Set<Cell> cellsToCheck = new HashSet<>();
        
        // Add all live cells
        cellsToCheck.addAll(liveCells);
        
        // Add all neighbors of live cells
        for (Cell cell : liveCells) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    Cell neighbor = getNeighbor(cell, dx, dy);
                    if (neighbor != null) {
                        cellsToCheck.add(neighbor);
                    }
                }
            }
        }
        
        // Evaluate each cell
        for (Cell cell : cellsToCheck) {
            int liveNeighbors = countLiveNeighbors(cell);
            boolean isAlive = liveCells.contains(cell);
            
            // Apply Conway's rules
            if (isAlive) {
                // Live cell survives with 2 or 3 neighbors
                if (liveNeighbors == 2 || liveNeighbors == 3) {
                    nextGen.add(cell);
                }
                // Otherwise dies (underpopulation/overpopulation)
            } else {
                // Dead cell becomes alive with exactly 3 neighbors
                if (liveNeighbors == 3) {
                    nextGen.add(cell);
                }
            }
        }
        
        // Update state
        liveCells = nextGen;
    }

    /**
     * INTUITION: Count live neighbors.
     * 
     * Check 8 surrounding cells.
     */
    private int countLiveNeighbors(Cell cell) {
        int count = 0;
        
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                
                Cell neighbor = getNeighbor(cell, dx, dy);
                if (neighbor != null && liveCells.contains(neighbor)) {
                    count++;
                }
            }
        }
        
        return count;
    }

    /**
     * INTUITION: Get neighbor cell (with toroidal wrapping if enabled).
     */
    private Cell getNeighbor(Cell cell, int dx, int dy) {
        int nx = cell.getX() + dx;
        int ny = cell.getY() + dy;
        
        if (toroidal) {
            // Wrap around edges
            nx = (nx + width) % width;
            ny = (ny + height) % height;
            return new Cell(nx, ny);
        } else {
            // Check bounds
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                return new Cell(nx, ny);
            }
            return null;
        }
    }

    private boolean isValid(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // --- Getters ---

    public Set<Cell> getLiveCells() {
        return Collections.unmodifiableSet(liveCells);
    }

    public int getLiveCellCount() {
        return liveCells.size();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isToroidal() { return toroidal; }

    /**
     * INTUITION: Print current generation.
     */
    public void print() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(liveCells.contains(new Cell(x, y)) ? "X " : ". ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

/**
 * Cell represents a position (x, y).
 * 
 * Immutable - used as key in HashSet.
 */
class Cell {
    private final int x;
    private final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    // equals and hashCode for Set storage
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
```

```java
package com.life;

import java.util.*;

/**
 * INTUITION: GameOfLifeVariant supports different rule sets.
 * 
 * Uses Strategy pattern for rules.
 */
class GameOfLifeVariant {
    
    private Set<Cell> liveCells;
    private final int width;
    private final int height;
    private RuleSet rules;

    GameRuleSetVariant(int width, int height) {
        this(width, height, new ConwayRules());
    }

    GameOfLifeVariant(int width, int height, RuleSet rules) {
        this.width = width;
        this.height = height;
        this.rules = rules;
        this.liveCells = ConcurrentHashMap.newKeySet();
    }

    public void nextGeneration() {
        Set<Cell> nextGen = ConcurrentHashMap.newKeySet();
        Set<Cell> cellsToCheck = new HashSet<>(liveCells);
        
        for (Cell cell : liveCells) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    Cell neighbor = getNeighbor(cell, dx, dy);
                    if (neighbor != null) {
                        cellsToCheck.add(neighbor);
                    }
                }
            }
        }
        
        for (Cell cell : cellsToCheck) {
            int liveNeighbors = countLiveNeighbors(cell);
            boolean isAlive = liveCells.contains(cell);
            
            if (rules.shouldSurvive(isAlive, liveNeighbors)) {
                nextGen.add(cell);
            }
        }
        
        liveCells = nextGen;
    }

    private Cell getNeighbor(Cell cell, int dx, int dy) {
        int nx = cell.getX() + dx;
        int ny = cell.getY() + dy;
        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
            return new Cell(nx, ny);
        }
        return null;
    }

    private int countLiveNeighbors(Cell cell) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Cell neighbor = getNeighbor(cell, dx, dy);
                if (neighbor != null && liveCells.contains(neighbor)) {
                    count++;
                }
            }
        }
        return count;
    }
}

/**
 * RuleSet interface for different Game of Life variants.
 */
interface RuleSet {
    boolean shouldSurvive(boolean isAlive, int liveNeighbors);
}

/**
 * Conway's original rules.
 */
class ConwayRules implements RuleSet {
    @Override
    public boolean shouldSurvive(boolean isAlive, int liveNeighbors) {
        if (isAlive) {
            return liveNeighbors == 2 || liveNeighbors == 3;
        } else {
            return liveNeighbors == 3;
        }
    }
}

/**
 * HighLife variant: B2/S12 (6,8 rule)
 */
class HighLifeRules implements RuleSet {
    @Override
    public boolean shouldSurvive(boolean isAlive, int liveNeighbors) {
        if (isAlive) {
            return liveNeighbors == 2 || liveNeighbors == 3;
        } else {
            return liveNeighbors == 3 || liveNeighbors == 6;  // Extra birth at 6
        }
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to detect stable patterns (still lifes, oscillators)?"
> "Hash generation state. If hash repeats → found cycle. Track last N generations. Common: Block, Beehive, Blinker (period 2)."

### Q2: "How to implement HashLife algorithm?"
> "Quadtree decomposition. Cache 5x5 patterns. Hash as (2x2 signature, 2x2). Super-exponential speedup for large grids."

### Q3: "How to visualize in real-time?"
> "Double buffering: compute next gen in background, swap when done. Use BufferStrategy in Swing/JavaFX. WebSocket for browser."

### Q4: "How to handle infinite grids?"
> "Sparse representation only tracks live cells. For toroidal, use modulo arithmetic. Otherwise, bounds check and expand dynamically."