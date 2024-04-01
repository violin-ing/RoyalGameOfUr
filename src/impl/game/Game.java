import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

// General notes:
// anywhere with a "system.out.println" message should be replaced with a call to the GUI to display the message, use general intuition to determine 

public class Game {
    private Board currentBoard;
    private Board futureBoard;
    private Counter counter;
    private Dice dice;
    private GameGUI gui;
    //TEMP
    public int rollAmount;
    public boolean rollPressed = false;

    public Game(Board currentBoard, Board futureBoard, Counter counter, Dice dice) {
        this.currentBoard = currentBoard;
        this.futureBoard = futureBoard;
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

    public Board getCurrentBoard() {return this.currentBoard;}
    public Board getFutureBoard() {return this.futureBoard;}
        
    public void start() {
        String currentPlayer = "";

        while (true) {
            boolean hasRolled = false;
            
            currentPlayer = counter.getPlayerTurn();
            gui.changePlayerTurn(currentPlayer);
            // method to change the P1/P2 value for GUI
            System.out.println(currentPlayer + " Please hit enter to roll");

            while(!rollPressed) {
                // System.out.println("nuh uh");
            }

            rollPressed = false;
            // TODO: Form part of gameloop that checks for valid moves, and returns a map of valid moves for the player to choose, then from those valid moves, ask which piece the player wants to move
            System.out.println("ROLL: " + rollAmount);
            availableMoves(currentPlayer, rollAmount);
            // System.out.println(currentPlayer + " Please choose a move");
            // String targetPiece = scanner.nextLine();
            break;

        }
    }


    /**
     * We will check the current positions of each of the player's pieces through invoking ".identifyPieces()" on the current board instance returning a map of <strip, position> pairs
     * We will then input those pairs into a future board instance for each piece
     * We can then apply identifyPieces() to the future board instance in order to return a map of the moves available. A piece with an invalid move will remain in place (this is either useful or a hinderance)
     *
     * @see Board#identifyPieces(String) runs through each tile of each strip of the board and "puts" the strip and position in it into a map as <strip, position>, then returns it
     */
    public void availableMoves(String player, int roll) {
        this.futureBoard = this.currentBoard;   
        Map<Integer,Integer> currentPositions = this.currentBoard.identifyPieces(player);
    
        futureBoard.pieceMover(player, roll, currentPositions);
        Map<Integer,Integer> futurePositions = this.futureBoard.identifyPieces(player);

        // for each piece in the currentPositions map, we will print the strip its in and the position it is in
        System.out.println("You have pieces in the following positions:");

        for (Map.Entry<Integer, Integer> entry : currentPositions.entrySet()) {
            System.out.println("Strip: " + entry.getKey() + " Tile: " + entry.getValue());
        }
    }

    public void setGameGUI(GameGUI gui) {
        this.gui = gui;
    }
}