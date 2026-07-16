# 🐍 Problem 43: Snake Game

> **Difficulty**: ⭐⭐ | **Company Fit**: Any tech company  
> **Est. Time**: 60 min | **Patterns**: State Machine, Observer, Strategy

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design the classic Snake game."

**What the interviewer tests**:
```
1. Can you model game state? (Snake position, food, direction)
2. Can you handle movement? (Grid-based, collision detection)
3. Can you implement game loop? (Tick-based updates)
4. Can you handle user input? (Arrow keys, swipe)
```

### Step 2: The "Aha!" Moment

The key insight: **Snake is a linked list moving on a grid.**

```
SNAKE: [H][B][B][T]  (Head, Body, Tail)

MOVEMENT:
  - Head moves in current direction
  - New head added
  - If food eaten: tail stays
  - If no food: tail removed

COLLISION DETECTION:
  - Wall collision: head.position < 0 OR head.position >= gridSize
  - Self collision: head.position IN snake.body.positions

FOOD:
  - Randomly placed on grid
  - Not on snake body
```

### Step 3: How to optimize?

```
DATA STRUCTURES:
  Snake body: LinkedList<Point> (easy add/remove)
  Grid: 2D array (for collision check)
  Food: Point

GAME LOOP:
  Every N milliseconds:
    1. Process input (direction change)
    2. Move snake
    3. Check collisions
    4. Check food
    5. Render

INPUT:
  - Prevent 180° turns (can't go DOWN if moving UP)
  - Queue input to prevent multiple turns in one tick
```

---

## 💻 Core Implementation

```java
package com.snake;

import java.util.*;

/**
 * INTUITION: SnakeGame manages the game state.
 */
public class SnakeGame {
    
    private static final int GRID_SIZE = 20;
    private static final int TICK_MS = 200;  // 5 moves per second
    
    private final Snake snake;
    private final Food food;
    private final int gridSize;
    
    private Direction currentDirection;
    private Direction nextDirection;
    private boolean gameOver;
    private int score;
    private final Random random;
    
    private final Timer timer;
    private final List<GameListener> listeners;

    public SnakeGame() {
        this(GRID_SIZE);
    }

    public SnakeGame(int gridSize) {
        this.gridSize = gridSize;
        this.random = new Random();
        this.listeners = new CopyOnWriteArrayList<>();
        
        // Initialize snake in center
        this.snake = new Snake(new Point(gridSize/2, gridSize/2));
        this.currentDirection = Direction.RIGHT;
        this.nextDirection = Direction.RIGHT;
        this.gameOver = false;
        this.score = 0;
        
        // Place first food
        this.food = new Food();
        placeFood();
        
        // Game loop
        this.timer = new Timer();
        timer.scheduleAtFixedRate(new GameTick(), 0, TICK_MS);
    }

    /**
     * INTUITION: Main game loop tick.
     */
    private class GameTick extends TimerTask {
        @Override
        public void run() {
            if (gameOver) return;
            
            try {
                update();
                render();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * INTUITION: Update game state.
     */
    private void update() {
        // Apply queued direction
        currentDirection = nextDirection;
        
        // Calculate new head position
        Point head = snake.getHead();
        Point newHead = move(head, currentDirection);
        
        // Check wall collision
        if (!isValid(newHead)) {
            endGame();
            return;
        }
        
        // Check self collision
        if (snake.collidesWith(newHead)) {
            endGame();
            return;
        }
        
        // Move snake
        snake.move(newHead);
        
        // Check food
        if (newHead.equals(food.getPosition())) {
            score += 10;
            food.eat();
            placeFood();
            onFoodEaten();
        }
    }

    /**
     * INTUITION: Move in direction.
     */
    private Point move(Point current, Direction dir) {
        switch (dir) {
            case UP:    return new Point(current.getX(), current.getY() - 1);
            case DOWN:  return new Point(current.getX(), current.getY() + 1);
            case LEFT:  return new Point(current.getX() - 1, current.getY());
            case RIGHT: return new Point(current.getX() + 1, current.getY());
            default: return current;
        }
    }

    /**
     * INTUITION: Place food randomly (not on snake).
     */
    private void placeFood() {
        Set<Point> snakeBody = snake.getPositions();
        Point pos;
        
        do {
            int x = random.nextInt(gridSize);
            int y = random.nextInt(gridSize);
            pos = new Point(x, y);
        } while (snakeBody.contains(pos));
        
        food.setPosition(pos);
    }

    /**
     * INTUITION: Change direction.
     */
    public synchronized void setDirection(Direction direction) {
        // Prevent 180° turn
        if (isOpposite(currentDirection, direction)) {
            return;
        }
        this.nextDirection = direction;
    }

    /**
     * INTUITION: Check if opposite directions.
     */
    private boolean isOpposite(Direction a, Direction b) {
        return (a == Direction.UP && b == Direction.DOWN) ||
               (a == Direction.DOWN && b == Direction.UP) ||
               (a == Direction.LEFT && b == Direction.RIGHT) ||
               (a == Direction.RIGHT && b == Direction.LEFT);
    }

    private boolean isValid(Point p) {
        return p.getX() >= 0 && p.getX() < gridSize &&
               p.getY() >= 0 && p.getY() < gridSize;
    }

    private void endGame() {
        this.gameOver = true;
        timer.cancel();
        notifyGameOver();
    }

    private void onFoodEaten() {
        for (GameListener listener : listeners) {
            listener.onFoodEaten(score);
        }
    }

    private void notifyGameOver() {
        for (GameListener listener : listeners) {
            listener.onGameOver(score);
        }
    }

    private void render() {
        char[][] grid = new char[gridSize][gridSize];
        
        // Initialize
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                grid[y][x] = '.';
            }
        }
        
        // Draw food
        Point foodPos = food.getPosition();
        grid[foodPos.getY()][foodPos.getX()] = 'F';
        
        // Draw snake
        List<Point> body = snake.getBody();
        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            grid[p.getY()][p.getX()] = (i == 0) ? 'H' : 'S';
        }
        
        // Print
        System.out.println("Score: " + score);
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                System.out.print(grid[y][x] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void start() {
        // Start game (if not already)
    }

    public void pause() {
        timer.cancel();
    }

    // --- Getters ---

    public Snake getSnake() { return snake; }
    public Food getFood() { return food; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public Direction getCurrentDirection() { return currentDirection; }
}

enum Direction { UP, DOWN, LEFT, RIGHT }

interface GameListener {
    void onFoodEaten(int score);
    void onGameOver(int score);
}
```

```java
package com.snake;

import java.util.*;

/**
 * INTUITION: Snake is a linked list of body segments.
 * 
 * Each segment is a Point (x, y).
 */
class Snake {
    private final LinkedList<Point> body;

    Snake(Point head) {
        this.body = new LinkedList<>();
        body.addFirst(head);
    }

    /**
     * INTUITION: Move snake.
     * 
     * - Add new head
     * - If not eating, remove tail
     * - If eating, tail stays (snake grows)
     */
    void move(Point newHead) {
        body.addFirst(newHead);
        body.removeLast();  // Remove tail
    }

    /**
     * INTUITION: Grow snake (when food eaten).
     */
    void grow(Point newHead) {
        body.addFirst(newHead);
        // Don't remove tail - snake grows
    }

    Point getHead() {
        return body.getFirst();
    }

    List<Point> getBody() {
        return new ArrayList<>(body);
    }

    Set<Point> getPositions() {
        return new HashSet<>(body);
    }

    boolean collidesWith(Point point) {
        return body.contains(point);
    }
}
```

```java
package com.snake;

/**
 * Immutable Point.
 */
class Point {
    private final int x;
    private final int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }
}
```

```java
package com.snake;

import java.awt.Point;

/**
 * INTUITION: Food represents the eatable item.
 */
class Food {
    private Point position;

    void setPosition(Point position) {
        this.position = position;
    }

    Point getPosition() {
        return position;
    }

    void eat() {
        // Food will be repositioned by game
        position = null;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to add obstacles?"
> "Random walls. Fixed obstacles. Increasing difficulty: more walls as score increases."

### Q2: "How to implement different game modes?"
> "Classic: walls kill. Tunnel: wrap-around. Maze: preset walls. Multiplayer: 2 snakes."

### Q3: "How to add power-ups?"
> "Speed boost: faster movement. Reverse controls. Ghost mode: pass through self."

### Q4: "How to persist high scores?"
> "Store in file/DB. Sort by score. Show leaderboard. Hashing to prevent cheating."