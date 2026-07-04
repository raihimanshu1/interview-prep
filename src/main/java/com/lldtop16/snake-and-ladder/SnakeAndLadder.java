/*build **Snake and Ladder LLD** in the same style:
        * Single runnable Java file
* Explanation at the top
* Clean OOP design
* Multiple players
* Dice abstraction
* Board abstraction
* Snake/Ladder behavior

---
        # High Level Design Explanation
```text
====================================================
SNAKE AND LADDER SYSTEM
Game starts
Player rolls dice
        |
v
Move player position
        |
v
Check board cell
        |
                |
                +---- Snake
        |
                +---- Ladder
        |
v
Update position
Winner:
Player reaches last cell
====================================================
        ```
        ---
        # Core Entities
```text
        Game
Board
        Cell
Player
        Dice
Snake
        Ladder
```
        ---
        # Design Decisions
## 1. Board owns cells
Board:
        ```text
1  2  3  4  5
        6  7  8  9 10
        ```
Each cell can have:
        * nothing
* snake
* ladder
---
        ## 2. Snake and Ladder abstraction
Both have same behavior:
Move player from one place to another.
        Example:
Snake:
        ```text
15 -> 5
        ```
Ladder:
        ```text
3 -> 22
        ```
So create:
        ```java
        Jump
Snake extends Jump
Ladder extends Jump
```
        ---
        ## 3. Dice abstraction
Today:
        ```text
Normal Dice
1-6
        ```
Tomorrow:
        ```text
Loaded Dice
Multiple Dice
```
So:
        ```java
Dice interface
```
        ---
        # Runnable Java Code
```java*/

package com.lldtop16.snakeAndLadder;



import java.util.*;
public class SnakeAndLadder {
    /*
    =====================================================
    DICE
    =====================================================
    */
    interface Dice {
        int roll();
    }
    static class NormalDice implements Dice {
        private Random random = new Random();
        public int roll() {
            return random.nextInt(6) + 1;
        }
    }
    /*
    =====================================================
    PLAYER
    =====================================================
    */
    static class Player {
        private final String name;
        private int position;
        Player(String name) {
            this.name = name;
            this.position = 0;
        }
        public void move(int position) {
            this.position = position;
        }
        public int getPosition() {
            return position;
        }
        public String getName() {
            return name;
        }
    }
    /*
    =====================================================
    JUMP
    Common parent for snake and ladder
    =====================================================
    */
    static abstract class Jump {
        protected int start;
        protected int end;
        Jump(int start, int end) {
            this.start = start;
            this.end = end;
        }
        public boolean exists(int position) {
            return start == position;
        }
        public int move() {
            return end;
        }
    }
    static class Snake extends Jump {
        Snake(int head, int tail) {
            super(head, tail);
        }
    }
    static class Ladder extends Jump {
        Ladder(int start, int end) {
            super(start, end);
        }
    }
    /*
    =====================================================
    BOARD
    =====================================================
    */
    static class Board {
        private final int size;
        private final List<Jump> jumps;
        Board(int size, List<Jump> jumps) {
            this.size = size;
            this.jumps = jumps;
        }
        public int getFinalPosition(int position) {
            for (Jump jump : jumps) {
                if (jump.exists(position)) {
                    return jump.move();
                }
            }
            return position;
        }
        public int getSize() {
            return size;
        }
    }
    /*
    =====================================================
    GAME
    =====================================================
    */
    static class Game {
        private final Board board;
        private final Dice dice;
        private final Queue<Player> players;
        Game(Board board, Dice dice, List<Player> players) {
            this.board = board;
            this.dice = dice;
            this.players = new LinkedList<>(players);
        }
        public void start() {
            while (true) {
                Player player = players.poll();
                int roll = dice.roll();
                System.out.println(player.getName() + " rolled " + roll);
                int newPosition = player.getPosition() + roll;
                // Cannot exceed board
                if (newPosition <= board.getSize()) {
                    newPosition = board.getFinalPosition(newPosition);
                    player.move(newPosition);
                }
                System.out.println(player.getName() + " is at " + player.getPosition());
                // Winner
                if (player.getPosition() == board.getSize()) {
                    System.out.println(player.getName() + " WON");
                    break;
                }
                // Next turn
                players.offer(player);
            }
        }
    }
    /*
    =====================================================
    MAIN
    =====================================================
    */
    public static void main(String[] args) {
        List<Jump> jumps = List.of(
                // Snake
                new Snake(25, 5),
                new Snake(40, 20),
                // Ladder
                new Ladder(3, 22),
                new Ladder(10, 35)
        );
        Board board = new Board(50, jumps);
        Player p1 = new Player("Alice");
        Player p2 = new Player("Bob");
        Game game = new Game(board, new NormalDice(), List.of(p1, p2));
        game.start();
    }
}
/*
```
        ---
        # Example Output
```text
Alice rolled 3
Alice is at 22
Bob rolled 5
Bob is at 5
Alice rolled 4
Alice is at 26
        ...
Bob WON
```
        ---
        # Flow
## Player turn
```text
        Player
 |
v
Dice.roll()
 |
v
New position
 |
v
Board checks:
Snake?
        |
v
move down
Ladder?
        |
v
move up
 |
v
Update player
```
        ---
        # Design Patterns Used
## Strategy Pattern
Dice:
        ```text
        Dice
 |
         |
NormalDice
        LoadedDice
MultipleDice
```
        ---
        ## Inheritance
Jump:
        ```text
        Jump
 |
         |
         +--- Snake
 +--- Ladder
```
Because both share:
        * start
* end
* movement
---
        # Interview Follow-ups
## 1. Multiple dice
Add:
        ```java
class MultipleDice implements Dice
```
        ---
        ## 2. Different board sizes
Already supported:
        ```java
new Board(100,jumps)
```
        ---
        ## 3. Player ranking
Add:
        ```java
List<Player>
```
sorted by position.
---
        ## 4. Concurrent games
Create:
        ```text
        GameSession
GameManager
```
to manage multiple games.
This is the standard Snake and Ladder LLD expected in Java interviews.
*/
