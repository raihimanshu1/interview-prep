package com.patternwisejavasolutions.design;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DesignSnakeGame {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: width = 3, height = 2, food = [[1,2],[0,1]],
     * move("R"), move("D"), move("R")
     * Sample Output: 0, 0, 1
     *
     * Build a snake game. Each move returns the score, or -1 if the snake hits
     * a wall or itself.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The snake has a head and a tail. On normal moves, the head moves forward
     * and the tail disappears. When food is eaten, the tail stays and the snake
     * grows.
     * A deque models the body order from head to tail; a set models the occupied
     * board squares so self-collision can be checked instantly.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Store the snake body as a list of cells from head to tail. To detect self
     * collision, scan the whole list.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Compute the new head from direction.
     * 2. Check wall collision.
     * 3. Remove tail first if no food is eaten.
     * 4. Scan body to see if new head hits itself.
     * 5. Add new head and return food count.
     *
     * Time Complexity: O(length of snake) per move
     * Space Complexity: O(length of snake)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Start at (0,0). move R -> (0,1), no food, score 0.
     * move D -> (1,1), no food, score 0.
     * move R -> (1,2), food eaten, score 1.
     */

    public static class BruteForce {
        private int width;
        private int height;
        private int[][] food;
        private int foodIndex;
        private List<Integer> body = new ArrayList<>();

        public BruteForce(int width, int height, int[][] food) {
            this.width = width;
            this.height = height;
            this.food = food;
            body.add(0);
        }

        public int move(String direction) {
            int head = body.get(0);
            int row = head / width;
            int col = head % width;

            if (direction.equals("U")) {
                row--;
            }
            if (direction.equals("D")) {
                row++;
            }
            if (direction.equals("L")) {
                col--;
            }
            if (direction.equals("R")) {
                col++;
            }

            if (row < 0 || row == height || col < 0 || col == width) {
                return -1;
            }

            int newHead = row * width + col;
            boolean eatsFood = foodIndex < food.length
                    && food[foodIndex][0] == row
                    && food[foodIndex][1] == col;

            if (!eatsFood) {
                // Normal move: tail leaves first, so the snake keeps the same length.
                body.remove(body.size() - 1);
            }

            for (int cell : body) {
                if (cell == newHead) {
                    return -1;
                }
            }

            body.add(0, newHead);
            if (eatsFood) {
                // Food was consumed, so the tail stayed and the score grows by one.
                foodIndex++;
            }

            return foodIndex;
        }
    }

    /*
     * OPTIMIZED INTUITION
     *
     * A deque gives fast head/tail updates. A set gives fast "is this cell in
     * the snake body?" checks.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Store body in a deque and occupied cells in a set.
     * 2. Compute new head.
     * 3. If no food, remove tail from deque and set before self-check.
     * 4. If new head is outside board or in set, return -1.
     * 5. Add new head and return score.
     *
     * Time Complexity: O(1) per move
     * Space Complexity: O(length of snake)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Body deque [0], occupied {0}.
     * move R: remove tail 0, add cell 1.
     * move D: remove tail 1, add cell 4.
     * move R to food cell 5: keep tail, add 5, score 1.
     */

    public static class Optimized {
        private int width;
        private int height;
        private int[][] food;
        private int foodIndex;
        private Deque<Integer> body = new ArrayDeque<>();
        private Set<Integer> occupied = new HashSet<>();

        public Optimized(int width, int height, int[][] food) {
            this.width = width;
            this.height = height;
            this.food = food;
            body.offerFirst(0);
            occupied.add(0);
        }

        public int move(String direction) {
            int head = body.peekFirst();
            int row = head / width;
            int col = head % width;

            if (direction.equals("U")) {
                row--;
            }
            if (direction.equals("D")) {
                row++;
            }
            if (direction.equals("L")) {
                col--;
            }
            if (direction.equals("R")) {
                col++;
            }

            if (row < 0 || row == height || col < 0 || col == width) {
                return -1;
            }

            int newHead = row * width + col;
            boolean eatsFood = foodIndex < food.length
                    && food[foodIndex][0] == row
                    && food[foodIndex][1] == col;

            if (!eatsFood) {
                // The tail leaves before collision check, because moving into
                // the old tail square is allowed on a normal move.
                int tail = body.pollLast();
                occupied.remove(tail);
            }

            if (occupied.contains(newHead)) {
                return -1;
            }

            body.offerFirst(newHead);
            occupied.add(newHead);

            if (eatsFood) {
                foodIndex++;
            }

            return foodIndex;
        }
    }
}
