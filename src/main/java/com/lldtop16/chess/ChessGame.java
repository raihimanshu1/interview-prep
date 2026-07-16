package com.lldtop16.chess;

public class ChessGame {
    /*
    ========================================================
    ENUM
    ========================================================
    */

    enum Color {
        WHITE,
        BLACK
    }
    /*
    ========================================================
    CELL
    Represents one square
    ========================================================
    */
    static class Cell {
        int row;
        int col;
        Piece piece;
        Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }
        boolean isEmpty() {
            return piece == null;
        }
    }
    /*
    ========================================================
    ABSTRACT PIECE
    Parent class for all pieces
    ========================================================
    */
    static abstract class Piece {
        Color color;
        Piece(Color color) {
            this.color = color;
        }
        abstract boolean canMove(int startRow, int startCol, int endRow, int endCol);
    }
    /*
    ========================================================
    PAWN
    ========================================================
    */
    static class Pawn extends Piece {
        Pawn(Color color) {
            super(color);
        }
        public boolean canMove(int startRow, int startCol, int endRow, int endCol) {
            int direction = color == Color.WHITE ? 1 : -1;
            return
                    startCol == endCol
                            &&
                            endRow - startRow == direction;
        }
    }
    /*
    ========================================================
    ROOK
    Moves horizontally/vertically
    ========================================================
    */
    static class Rook extends Piece {
        Rook(Color color) {
            super(color);
        }
        public boolean canMove(int startRow, int startCol, int endRow, int endCol) {
            return
                    startRow == endRow
                            ||
                            startCol == endCol;
        }
    }
    /*
    ========================================================
    BISHOP
    Diagonal movement
    ========================================================
    */
    static class Bishop extends Piece {
        Bishop(Color color) {
            super(color);
        }
        public boolean canMove(int startRow, int startCol, int endRow, int endCol) {
            return
                    Math.abs(endRow - startRow)
                            ==
                            Math.abs(endCol - startCol);
        }
    }
    /*
    ========================================================
    KNIGHT
    L shape movement
    ========================================================
    */
    static class Knight extends Piece {
        Knight(Color color) {
            super(color);
        }
        public boolean canMove(int startRow, int startCol, int endRow, int endCol) {
            int row = Math.abs(endRow - startRow);
            int col = Math.abs(endCol - startCol);
            return
                    (row == 2 && col == 1)
                            ||
                            (row == 1 && col == 2);
        }
    }
    /*
    ========================================================
    KING
    One step any direction
    ========================================================
    */
    static class King extends Piece {
        King(Color color) {
            super(color);
        }
        public boolean canMove(int startRow, int startCol, int endRow, int endCol) {
            return
                    Math.abs(endRow - startRow) <= 1
                            &&
                            Math.abs(endCol - startCol) <= 1;
        }
    }
    /*
    ========================================================
    QUEEN
    Combination of rook + bishop
    ========================================================
    */
    static class Queen extends Piece {
        Queen(Color color) {
            super(color);
        }
        public boolean canMove(int startRow, int startCol, int endRow, int endCol) {
            return
                    startRow == endRow
                            ||
                            startCol == endCol
                            ||
                            Math.abs(endRow - startRow)
                                    ==
                                    Math.abs(endCol - startCol);
        }
    }
    /*
    ========================================================
    BOARD
    ========================================================
    */
    static class Board {
        Cell[][] cells;
        Board() {
            cells = new Cell[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    cells[i][j] = new Cell(i, j);
                }
            }
        }
        public void placePiece(Piece piece, int row, int col) {
            cells[row][col].piece = piece;
        }
        public boolean movePiece(int startRow, int startCol, int endRow, int endCol) {
            Piece piece = cells[startRow][startCol].piece;
            if (piece == null) {
                return false;
            }
            if (!piece.canMove(startRow, startCol, endRow, endCol)) {
                return false;
            }
            cells[endRow][endCol].piece = piece;
            cells[startRow][startCol].piece = null;
            return true;
        }
    }
    /*
    ========================================================
    PLAYER
    ========================================================
    */
    static class Player {
        String name;
        Color color;
        Player(String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }
    /*
    ========================================================
    MAIN
    ========================================================
    */
    public static void main(String[] args) {
        Board board = new Board();
        Player white = new Player("White Player", Color.WHITE);
        Player black = new Player("Black Player", Color.BLACK);
        /*
        Put pieces
        White pawn:
        row 1 col 0
        Move:
        row 2 col 0
        */
        board.placePiece(new Pawn(white.color), 1, 0);
        boolean moved = board.movePiece(1, 0, 2, 0);
        System.out.println("Move successful : " + moved);
    }
}
/*
============================================================
CHESS GAME SYSTEM
============================================================
PROBLEM STATEMENT
============================================================
Design a Chess game system.
The system should support:
- Chess board
- Players
- Pieces
- Movement rules
- Valid moves
- Turn management
- Capturing pieces
- Winner detection
Example:
White Player turn
Move:
Pawn from e2 -> e4
System checks:
Is move valid?
If yes:
Update board
============================================================
CORE ENTITIES
============================================================
ChessGame
        |
        |
        +------------+
        |            |
      Board       Player
Board contains:
8 x 8 Cells
Cell contains:
Position
Piece
Piece hierarchy:
Piece (abstract)
    |
    |
----------------------------
King
Queen
Rook
Bishop
Knight
Pawn
============================================================
DESIGN DECISIONS
============================================================
1. ABSTRACT CLASS FOR PIECE
All pieces share:
- Color
- Current position
- Movement validation
But each piece has different movement.
Example:
Pawn:
Moves one step forward
Knight:
L shape
Queen:
Any direction
So:
abstract Piece
child classes implement:
canMove()
============================================================
2. POLYMORPHISM
Instead of:
if(piece == PAWN)
else if(piece == KING)
We do:
piece.canMove()
Java calls correct implementation.
============================================================
3. BOARD RESPONSIBILITY
Board manages:
- Cells
- Piece placement
- Moving pieces
It does not know chess rules.
============================================================
4. GAME RESPONSIBILITY
Game manages:
- Turns
- Winner
- Move validation
============================================================
FLOW
============================================================
Player turn
        |
        v
Select piece
        |
        v
Check canMove()
        |
        v
Move piece
        |
        v
Switch turn
============================================================
TIME COMPLEXITY
============================================================
Move validation:
Most pieces:
O(1)
Board lookup:
O(1)
because:
8 x 8 fixed array
============================================================
INTERVIEW FOLLOW UPS
============================================================
1. Checkmate
Need:
Attack detection
King safety validation
------------------------------------------------------------
2. Undo move
Maintain:
MoveHistory stack
------------------------------------------------------------
3. Multiplayer
Add:
GameSession
Socket communication
------------------------------------------------------------
4. AI Player
Add:
ChessEngine
============================================================
*/
        
