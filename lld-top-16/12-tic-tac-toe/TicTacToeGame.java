/*
============================================================
TIC TAC TOE GAME SYSTEM
============================================================


PROBLEM STATEMENT
============================================================


Design a Tic Tac Toe game.

The system should support:


- Two players
- 3 x 3 board
- Player moves
- Validate moves
- Detect winner
- Detect draw



Example:



Player X:

places X


Board:

X |   |
---------
  | O |
---------
  |   |



After every move:

Check:

- Row
- Column
- Diagonal



============================================================
CORE ENTITIES
============================================================


TicTacToeGame


        |
        |
        +----------------+
        |                |
      Board           Player



Board:

Responsible for:

- storing moves
- validating position
- displaying board



Player:

Responsible for:

- name
- symbol



Game:

Responsible for:

- turns
- winner
- game state



============================================================
DESIGN DECISIONS
============================================================



1. ENUM FOR SYMBOL


Instead of:


String:

"X"

"O"



Use:


Symbol.X

Symbol.O



Benefits:

- Type safety
- Prevent invalid values



------------------------------------------------------------


2. BOARD SEPARATE FROM GAME


Why?


Board only knows:

where pieces are placed


Game knows:

whose turn

winner

rules



------------------------------------------------------------


3. SINGLE RESPONSIBILITY


Player:

Stores player information


Board:

Manages cells


Game:

Controls gameplay



============================================================
FLOW
============================================================


Game starts


        |

        v


Player turn


        |

        v


Place symbol


        |

        v


Validate move


        |

        v


Check winner


        |

        v


Switch player



============================================================
WINNER LOGIC
============================================================


Check:


1. Rows


XXX


2. Columns


X

X

X



3. Diagonal


X

 X

  X



If any condition true:


Winner found.



============================================================
TIME COMPLEXITY
============================================================


Move:


O(1)


Winner check:


3 rows

3 columns

2 diagonals


Total:

O(1)



Because board size is fixed.



============================================================
SPACE COMPLEXITY


O(1)


Fixed 3x3 board.



============================================================
INTERVIEW FOLLOW UPS
============================================================


1. Bigger board


Example:


10 x 10


Make Board dynamic.



------------------------------------------------------------


2. Multiplayer


Add:


GameSession



------------------------------------------------------------


3. Undo move


Maintain:


Stack<Move>



------------------------------------------------------------


4. AI Player


Add:


MoveStrategy


RandomMove

MinimaxStrategy



============================================================

*/


public class TicTacToeGame {



    /*
    ========================================================
    SYMBOL ENUM
    ========================================================
    */


    enum Symbol {


        X,


        O,


        EMPTY


    }








    /*
    ========================================================
    PLAYER
    ========================================================
    */


    static class Player {


        private final String name;


        private final Symbol symbol;




        Player(
                String name,
                Symbol symbol
        ){

            this.name=name;

            this.symbol=symbol;

        }



        public Symbol getSymbol(){

            return symbol;

        }



        public String getName(){

            return name;

        }


    }









    /*
    ========================================================
    BOARD


    Stores game state


    ========================================================
    */


    static class Board {


        private final Symbol[][] board;



        Board(){


            board =
            new Symbol[3][3];



            for(int i=0;i<3;i++){


                for(int j=0;j<3;j++){


                    board[i][j] =
                            Symbol.EMPTY;


                }


            }


        }








        public boolean placeMove(
                int row,
                int col,
                Symbol symbol
        ){


            /*
            
            Invalid position

            or already filled

            */

            if(
                row < 0 ||
                row >=3 ||
                col <0 ||
                col>=3
            ){

                return false;

            }




            if(
                board[row][col]
                !=
                Symbol.EMPTY
            ){

                return false;

            }




            board[row][col] =
                    symbol;



            return true;


        }









        public boolean checkWinner(
                Symbol symbol
        ){



            // Check rows


            for(int i=0;i<3;i++){



                if(
                    board[i][0] == symbol
                    &&
                    board[i][1] == symbol
                    &&
                    board[i][2] == symbol
                ){

                    return true;

                }

            }








            // Check columns


            for(int i=0;i<3;i++){



                if(
                    board[0][i] == symbol
                    &&
                    board[1][i] == symbol
                    &&
                    board[2][i] == symbol
                ){

                    return true;

                }

            }









            // Main diagonal


            if(
                board[0][0] == symbol
                &&
                board[1][1] == symbol
                &&
                board[2][2] == symbol
            ){

                return true;

            }








            // Other diagonal


            if(
                board[0][2] == symbol
                &&
                board[1][1] == symbol
                &&
                board[2][0] == symbol
            ){

                return true;

            }



            return false;


        }








        public void display(){


            for(int i=0;i<3;i++){


                for(int j=0;j<3;j++){


                    System.out.print(
                            board[i][j]
                            +
                            " "
                    );


                }


                System.out.println();

            }


            System.out.println();


        }



    }









    /*
    ========================================================
    GAME


    Controls turns


    ========================================================
    */


    static class Game {


        private final Board board;



        private final Player player1;



        private final Player player2;



        private Player currentPlayer;







        Game(
                Player player1,
                Player player2
        ){


            board =
                    new Board();



            this.player1 =
                    player1;



            this.player2 =
                    player2;



            currentPlayer =
                    player1;


        }








        public void makeMove(
                int row,
                int col
        ){



            boolean success =
                    board.placeMove(
                            row,
                            col,
                            currentPlayer.getSymbol()
                    );





            if(!success){


                System.out.println(
                        "Invalid move"
                );


                return;


            }






            board.display();







            if(
                board.checkWinner(
                        currentPlayer.getSymbol()
                )
            ){


                System.out.println(
                        currentPlayer.getName()
                        +
                        " WON"
                );


                return;


            }





            switchTurn();



        }








        private void switchTurn(){


            if(
                currentPlayer ==
                player1
            ){


                currentPlayer =
                        player2;


            }
            else{


                currentPlayer =
                        player1;


            }


        }



    }









    /*
    ========================================================
    MAIN
    ========================================================
    */


    public static void main(String[] args) {



        Player p1 =
                new Player(
                        "Alice",
                        Symbol.X
                );



        Player p2 =
                new Player(
                        "Bob",
                        Symbol.O
                );





        Game game =
                new Game(
                        p1,
                        p2
                );







        /*
        
        Game:

        X | O | X
        O | X |
          |   | X


        Alice wins diagonal


        */



        game.makeMove(0,0);


        game.makeMove(0,1);


        game.makeMove(1,1);


        game.makeMove(1,0);


        game.makeMove(2,2);



    }


}
