import java.util.*;
/**
 * Game class which contains all the logic for running the game loop, calculating the available moves, getting
 * available moves for the roll amount, and then executing gui updates based on the move recieved.
 * This class also terminates the game loop once the win condition has been reached (a player has moved all their pieces off the board) 
 */
public class Game {
    // initilising all attributes
    private static Board currentBoard;
    private static Counter counter;
    private Dice dice;
    private Ai ai;
    private static GameGUI gui;

    public static boolean networkPlay = false;
    public int rollAmount;
    public boolean rollPressed = false;
    public boolean moveSelected = false;
    public int[] move = new int[4];

    public boolean multiplayer = false;


    /**
     * Game constructor, this is called in GameStartGUI to start the game.
     * All parameters passed in are initilised in GameStartGUI class.
     * @param currentBoard Board object
     * @param counter Counter object
     * @param dice Dice object
     * @param muliplayer boolean multiplayer,  if option is selected in StartMenuGUI
     */
    public Game(Board currentBoard, Counter counter, Dice dice, boolean muliplayer) {
        Game.currentBoard = currentBoard;
        Game.counter = counter;
        this.dice = dice;
        this.multiplayer = muliplayer;
        // create an Ai object.
        this.ai = new Ai();
    }

    /**
     * set GUI object, so that the Game class can call updates on the GameGUI.
     * @param gameGui GameGUI object.
     */
    public void setGameGUI(GameGUI gameGui) {
        Game.gui = gameGui;
    }
    /**
     * returns current game Board.
     * @return returns Board object, currentBoard.
     */
    public static Board getCurrentBoard() {return currentBoard;}
    /**
     * gets the current counter object.
     * @return Counter object.
     */
    public static Counter getCounter() {return counter;}
        
    public void start() {
        System.out.println("GAME STARTED");
        String currentPlayer = "";
        
        if (!multiplayer) {
            gui.disableP2();
        }
        
        while (true) {
            rollPressed = false;
            moveSelected = false;         
            currentPlayer = counter.getPlayerTurn();
            gui.changePlayerTurn(currentPlayer);
            // method to change the P1/P2 value for GUI
            System.out.println("PICKING TURN");
            // ai player turn 
            if (!multiplayer && currentPlayer.equals("P2")) {
                System.out.println("AI PLAYER PICKING MOVE");
                rollAmount = dice.roll();
                gui.editP2Roll(rollAmount);
                if (rollAmount!=0) {
                    Board saveBoard = new Board(currentBoard);
                    Node root = ai.createTree(rollAmount);
                    ai.setRoot(root);
                    
                    double expectimax = ai.expectiminimax(ai.getRoot(), "max");
                    ai.getRoot().setScore(expectimax);
                    ai.printTree(ai.getRoot(), 1);
                    Node bestChild = ai.filterChildren(expectimax);
                    move = bestChild.getPos();
                    System.out.println(Arrays.toString(move));
                    currentBoard = new Board(saveBoard);
                    // currentBoard = saveBoard;
                }
            } else {
                // players turn
                System.out.println("WAITING FOR ROLL");
                while(!rollPressed) {
                    // ai.printTree(ai.getRoot(), 1);
                    try {
                        wait();
                    } catch (Exception e) {
                    }
                }
                // NO POSSIBLE MOVES IF ROLL = 0, GO TO NEXT PLAYER
                if (rollAmount == 0) {
                    continue;
                } else {
                    // go to next iteration if there are no available moves
                    if (!availableMoves(currentPlayer, rollAmount)) {
                        continue;
                    }
                    System.out.println("WAITING FOR MOVE");
                    while (!moveSelected) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                        // System.out.println("Waiting for move");
                    }
                    // update the board.
                    // move is updated in the GUI class, it is an int[] array, with 4 values in this order:
                    // tile we are moving from strip, tile we are moving from position, tile we are moving to strip, and then position.
                    // if (currentBoard.getBoard()[move[2]][move[3]].isRosetta()) {
                    //     for (int i = 0; i < 10000; i++) {
                    //         System.out.println(currentPlayer + " gets another turn");  
                    //     } 
                    // }
                }
            }
            currentBoard.move(move, currentPlayer, true);
            System.out.println("update the board");
            gui.updateBoard(currentBoard);
            gui.updateScore(counter);

            //CHECK WIN CONDITION
            if (currentPlayer.equals("P1")) {
                if (counter.getP1Score()==7) {
                    System.out.println("P1 WINS");
                    if (!networkPlay) {
                        new EndGameGUI(currentPlayer);
                        gui.closeFrame(); 
                    }
                    break;
                }
            } else {
                if (counter.getP2Score()==7) {
                    System.out.println("P2 WINS");
                    if (!networkPlay) {
                        new EndGameGUI(currentPlayer);
                        gui.closeFrame();
                    }
                    break;
                }
            }

            List<int[]> pieces = currentBoard.identifyPieces("P1");
            System.out.println("pieces p1");
            for (int[] pos : pieces) {
                System.out.println(Arrays.toString(pos));
            }

            List<int[]> pieces2 = currentBoard.identifyPieces("P2");
            System.out.println("pieces p2");
            for (int[] pos : pieces2) {
                System.out.println(Arrays.toString(pos));
            }
        }
    }
    
    /**
     * We will check the current positions of each of the player's pieces through invoking ".identifyPieces()" on the current board instance returning a map of <strip, position> pairs
     * We will then input those pairs into a future board instance for each piece
     * We can then apply identifyPieces() to the future board instance in order to return a map of the moves available. A piece with an invalid move will remain in place (this is either useful or a hinderance)
     *
     * @see Board#identifyPieces(String) runs through each tile of each strip of the board and "puts" the strip and position in it into a map as <strip, position>, then returns it
     */
    public static boolean availableMoves(String player, int roll) {
        boolean possibleMoves;
        int currentPlayerCounter;
        
        if (player.equals("P1")) {
            currentPlayerCounter = counter.getP1Counter();
        } else {
            currentPlayerCounter = counter.getP2Counter();
        }

        List<int[]> currentMovablePositions = getCurrentMovablePositions(player, roll, currentBoard.identifyPieces(player), currentPlayerCounter);
        List<int[]> futurePositions = getFuturePositions(player, roll, currentMovablePositions);
        if (futurePositions.size()==0) {
            possibleMoves = false;
        } else {
            possibleMoves = true;
        }
        gui.updateSelectableTiles(currentMovablePositions, futurePositions);

        return possibleMoves;
    }

    // this will check if a particular chip on the board is movable.
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
                System.out.println(stringPos[0] + " " + stringPos[1]);
                currentMovablePositions.add(stringPos);
            }
        } else {
            if (tileCounter !=0 ) {
                stringPos[0] = 2;
                stringPos[1] = -1;
                System.out.println(stringPos[0] + " " + stringPos[1]);
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
        
        int checkTileAfter;
        if (strip == 1) {
            checkTileAfter = movePosition + roll;
            if (checkTileAfter > 7) {
                    checkTileAfter = (checkTileAfter - 4); // Position on new strip
                    strip = ("P1".equals(player)) ? 0 : 2;
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

        if (strip == 1) {
                if (currentBoard.getBoardStrip(strip)[checkTileAfter].isRosetta()) {
                    String enemyPlayer = "P1".equals(player) ? "P2" : "P1";
                    if (currentBoard.getBoardStrip(strip)[checkTileAfter].getChip().getOwnership().equals("none")) {
                        return true;
                    } else if (currentBoard.getBoardStrip(strip)[checkTileAfter].getChip().getOwnership().equals(enemyPlayer)) {
                        return false;
                    }
                }
            }
        return true;
    }
}