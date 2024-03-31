import java.util.Scanner;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

public class Game {
    Board board = new Board();
    Counter counter = new Counter();
    Dice dice = new Dice();

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
        
    public void start() {
        String currentPlayer = "";
        int roll = 0;

        while (true) {
            boolean hasRolled = false;
            
            currentPlayer = counter.getPlayerTurn();
            System.out.println(currentPlayer + " Please hit enter to roll");

            while (!hasRolled) {
                if (scanner.nextLine().equals(rollInput)) {
                    roll = dice.roll();
                    System.out.println("You rolled a " + roll + "!");
                    hasRolled = true;
                }
            }

            // TODO: Form part of gameloop that checks for valid moves, and returns a map of valid moves for the player to choose, then from those valid moves, ask which piece the player wants to move

            Optional<HashMap<Integer, Integer>> validMoves = checkValidMoves(currentPlayer, roll);
            if (!validMoves.isPresent()) {System.out.println("No moves available, passing turn."); continue;}
            else {
                 System.out.println("You can move to: ");
            //    for (Optional<HashMap.Entry<Integer, Integer>> entry : validMoves.entrySet()) System.out.println("piece" + validMoves.getKey() + "can move " + validMoves.getValue() + ", ");
            //          System.out.println("Please which piece you want to move player " + currentPlayer);
            //          choiceInput = scanner.nextLine();
            //          makeMove(choiceInput, roll);
            }
        }
    }



    /**
     * TODO: Method to check what moves they can make, and return as an optional map
     * 
     * Method to check the valid moves for a player
     * It goes through each strip in the board array
     * It stores what positions hold a chip of the current player and returns them ()
     * @param currentPlayer
     * @param roll
     * @return <Optional<HashMap<Integer,Integer>> validMoves, we return optinonal in the case no valid moves are found, otherwise this will return the valid moves available for each chip
     * @info I found that returning Optional<HashMap> was considered a "raw type" since it didn't have a type argument, just as a note
     */

     //THIS METHOD IS CURRENTLY USELESS, IT NEEDS TO BE IMPLEMENTED
    public Optional<HashMap<Integer,Integer>> checkValidMoves(String currentPlayer, int roll) {
        Optional<HashMap<Integer,Integer>> validMoves = Optional.of(new HashMap<Integer, Integer>());
        return validMoves;
    }
}