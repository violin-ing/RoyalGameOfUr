import java.util.*;

// General notes:
// anywhere with a "system.out.println" message should be replaced with a call to the GUI to display the message, use general intuition to determine 

public class Game {
    private static Board currentBoard;
    private static Counter counter;
    private Dice dice;
    private Ai ai;
    private static GameGUI gui;

    public static boolean networkPlay = false;

    //TEMP
    public int rollAmount;
    public boolean rollPressed = false;
    public boolean moveSelected = false;
    public int[] move = new int[4];

    public boolean multiplayer = false;

    public Game(Board currentBoard, Counter counter, Dice dice) {
        this.currentBoard = currentBoard;
        this.counter = counter;
        this.dice = dice;
    }

    Scanner scanner = new Scanner(System.in);
    Random random = new Random();
    String rollInput = "";  //Placeholder value for gui, this checks if the necessary input was provided
    String choiceInput = "";
    
    /**
         * Overall game loop
         * Contains: 
         *      The ability to check whose turn it is (import to see who to apply results to)
         *      The ability to roll the dice
         *      The ability to display legal moves
         *      The ability to move the player to those legal positions if available
         *      The ability to check the result of a players movement (takedown, score, movement)
         *      The ability to check if a player has won
    */

    public static Board getCurrentBoard() {return currentBoard;}

    public static Counter getCounter() {return counter;}
        
    public void start() {
        String currentPlayer = "";

        while (true) {         
            currentPlayer = counter.getPlayerTurn();
            gui.changePlayerTurn(currentPlayer);
            // method to change the P1/P2 value for GUI

            // ai player turn 
            if (!multiplayer && currentPlayer.equals("P2")) {
                // ai turn
            } else {

                while(!rollPressed) {
                    System.out.println("Waiting for roll input");
                }
            }

            //pass current roll amount and player to available moves
            // available moves should also calculate if the player can add a token to the board:
            //  - this can be done if the roll amount does not == 0 
            //  - and the player has at least 1 token in reserve
            //System.out.println("ROLL: " + rollAmount);
            rollPressed = false;

            // NO POSSIBLE MOVES IF ROLL = 0, GO TO NEXT PLAYER
            if (rollAmount == 0) {
                continue;
            } else {
                availableMoves(currentPlayer, rollAmount);
                while (!moveSelected) {
                    System.out.println("Waiting for move input");
                }
                // update the board.
                // move is updated in the GUI class, it is an int[] array, with 4 values in this order:
                // tile we are moving from strip, tile we are moving from position, tile we are moving to strip, and then position.
                
                currentBoard.move(move, currentPlayer);
                System.out.println("update the board");
                gui.updateBoard(currentBoard);
                moveSelected = false;
                if (currentBoard.getBoard()[move[2]][move[3]].isRosetta()) {
                    currentPlayer = counter.getPlayerTurn();
                    System.out.println("CURRENTPLAYER: " + currentPlayer);
                }
            }
        }

            //this will return the map of current and furture positions (being the current positions of tiles on the board, and the positions they can be moved)



            // send these values to the GUI, update screen to have green boarders around tile which can be moved, and orange (non-clickable boarders), around where they are going
            // make it so if you select a tile (that is green) you can then click the move location, but you can also still click other tiles which can be moved. (MAYBE MAKE ANOTHER CLASS?! or just an attribute of GraphicsTile)

            // return selected move by GUI 
            // send this move to movelogic to actually make it move.
            // send the new updated board to GUI so that it can be updated (along with score etc.)


            // TODO: Form part of gameloop that checks for valid moves, and returns a map of valid moves for the player to choose, then from those valid moves, ask which piece the player wants to move
            // System.out.println(currentPlayer + " Please choose a move");
            // String targetPiece = scanner.nextLine();

        }

    

    /**
     * We will check the current positions of each of the player's pieces through invoking ".identifyPieces()" on the current board instance returning a map of <strip, position> pairs
     * We will then input those pairs into a future board instance for each piece
     * We can then apply identifyPieces() to the future board instance in order to return a map of the moves available. A piece with an invalid move will remain in place (this is either useful or a hinderance)
     *
     * @see Board#identifyPieces(String) runs through each tile of each strip of the board and "puts" the strip and position in it into a map as <strip, position>, then returns it
     */
    public static void availableMoves(String player, int roll) {
        int currentPlayerCounter;
        if (player.equals("P1")) {
            currentPlayerCounter = counter.getP1Counter();
        } else {
            currentPlayerCounter = counter.getP2Counter();
        }

        List<int[]> currentMovablePositions = getCurrentMovablePositions(player, roll, currentBoard.identifyPieces(player), currentPlayerCounter);
    
        List<int[]> futurePositions = getFuturePositions(player, roll, currentMovablePositions);

        if (networkPlay) {
            Client.gui.updateSelectableTiles(currentMovablePositions, futurePositions);
        } else {
            gui.updateSelectableTiles(currentMovablePositions, futurePositions);
        }
        
    }

    // this will check if a particular chip on the board is movable.
    // TODO: make a method which will calculate where this particular piece will be moved (edit the above method).

    public static List<int[]> getCurrentMovablePositions(String player, int roll, List<int[]> currentPositions, int tileCounter) {
        List<int[]> currentMovablePositions = new ArrayList<>();
        int[] stringPos = new int[2];

        for (int[] stripAndPos : currentPositions) {
            if (isMoveable(player, roll, stripAndPos[1], stripAndPos[0])) {
                currentMovablePositions.add(stripAndPos);
            }
        }
        // this will add a possible move for a player to move a token onto the board.
        if (player.equals("P1")) {
            if (tileCounter != 0) {
                stringPos[0] = 0;
                stringPos[1] = -1;
                currentMovablePositions.add(stringPos);
            }
        } else {
            if (tileCounter !=0 ) {
                stringPos[0] = 2;
                stringPos[1] = -1;
                currentMovablePositions.add(stringPos);
            }
        }

        return currentMovablePositions;
    }

    public static List<int[]> getFuturePositions(String player, int roll, List<int[]> currentMovablePositions) {
        List<int[]> futurePositions = new ArrayList<>();
        // position of winning tile, 6 on a player strip
        for (int[] piecePos : currentMovablePositions) {
            // futureBoard.pieceMover(player, roll, piecePos);
            futurePositions.add(newPosition(piecePos, player, roll));
        }
        //loop through current movable like above,

        // List<int[]> futurePositions = futureBoard.identifyPieces(player); 
        return futurePositions;
    }
    // this method will calculate the strip and index position of where a chip will end up after a particular move.
    public static int[] newPosition(int[] stripPos, String player, int roll) {
        int[] newPos = new int[2];
        int strip = stripPos[0];
        int movePosition = stripPos[1];
        
        // for each position, we are going to find only the STRIP (0,1,2) and index (0-7) that it ends up in. 
        // already know which player it is, so we just check for each valid move if:
        // 1.) if you are in the p1strip / p2string: 
        //        * take current position of a tile and add roll amount, if this is greater than the length of the first part of the strip
        //        * otherwise you are just moving on this strip. 
        // then when it comes to moving the chips, we already know where a particular tile will end up if selected
        // so all we need to check is if its taking or stacking / ending up on a rosette 

        int checkTileAfter;
        if (strip == 1) {
            checkTileAfter = movePosition + roll;
            if (checkTileAfter > 7) {
                    checkTileAfter = (checkTileAfter - 4); // Position on new strip
                    strip = ("P1".equals(player)) ? 0 : 2;
            }
            if (checkTileAfter > 5) {
                checkTileAfter = 6;
            }
        } else {
            checkTileAfter = movePosition + roll;
            if (movePosition>=4) {
                // moving off board on strip
                if (checkTileAfter>5) {
                    checkTileAfter=6;
                    // don't change strip
                }
            } else if (checkTileAfter > 3) {
                checkTileAfter = (checkTileAfter - 4); // Position on new strip
                strip = 1;
            }
        }

        newPos[0] = strip;
        newPos[1] = checkTileAfter;

        return newPos;
    }

    // will return -1 if this chip cannot be moved, otherwise will return the postion and strip it will be moved to.
    private static boolean isMoveable(String player, int roll, int movePosition, int strip) {
        // validiation of moves happens, here, so the above method can be simplified more easily.

        // Tile tile = currentBoard.getBoardStrip(0,1,2 strip)[position of tile].isRosetta();
        // Tile tile = currentBoard.getBoardStrip(0,1,2 strip)[position of tile].getChip().getOwnership();

        // find when chip is not movable:
        // check if chip is moving onto a rosette tile
        // check the tile its moving to by using: currentBoard, the strip its moving to (p1, p2, middle)
        // if this rosette tile is unoccupied or has current player chip on it can move here.
        // otherwise, this is not a valid move.
        // Check if the tile before has a chip on it

        //TODO: MAKE THIS A METHOD BELOW 
        
        int checkTileAfter;
        if (strip == 1) {
            checkTileAfter = movePosition + roll;
            if (checkTileAfter > 7) {
                    checkTileAfter = (checkTileAfter - 4); // Position on new strip
                    strip = ("P1".equals(player)) ? 0 : 2;
            }
        } else {
            checkTileAfter = movePosition + roll;
            if (checkTileAfter > 3) {
                    checkTileAfter = (checkTileAfter - 4); // Position on new strip
                    strip=1;
            }
        }


        if (currentBoard.getBoardStrip(strip)[checkTileAfter].isRosetta()) {
            String enemyPlayer = "P1".equals(player) ? "P2" : "P1";
            if (currentBoard.getBoardStrip(strip)[checkTileAfter].getChip().getOwnership().equals("none")) {
                return true;
            } else if (currentBoard.getBoardStrip(strip)[checkTileAfter].getChip().getOwnership().equals(enemyPlayer)) {
                return false;
            }
        }


        return true;
    }


    public void setGameGUI(GameGUI gameGui) {
        gui = gameGui;
    }
}