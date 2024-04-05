import java.util.Arrays;

/**
 * Runnable class representing an AI thread that calculates the best move asynchronously.
 */
public class AiThread implements Runnable {
    private Ai ai;
    private int rollAmount;
    private int[] move;
    private Board currentBoard;

    /**
     * Constructs an AiThread object.
     * 
     * @param ai            The Ai object responsible for AI calculations.
     * @param rollAmount    The number obtained from the dice roll.
     * @param currentBoard  The current state of the game board.
     */
    public AiThread(Ai ai, int rollAmount, Board currentBoard) {
        this.ai = ai;
        this.rollAmount = rollAmount;
        this.currentBoard = currentBoard;
    }

    /**
     * Retrieves the calculated move.
     * 
     * @return The calculated move as an array of integers.
     */
    public int[] getMove() {
        return this.move;
    }

    /**
     * Overrides the run method of the Runnable interface.
     * This method is invoked when the thread is started.
     */
    @Override
    public void run() {
        // Create the game tree and perform AI calculations
        Node root = ai.createTree(rollAmount);
        ai.setRoot(root);

        // Calculate the expectimax score for the root node
        double expectimax = ai.expectiminimax(ai.getRoot(), "max");
        ai.getRoot().setScore(expectimax);
        
        // Print the tree structure for debugging
        ai.printTree(ai.getRoot(), 1);
        
        // Choose the best child node based on the calculated score
        Node bestChild = ai.filterChildren(expectimax);
        move = bestChild.getPos();
        
        // Print the chosen move
        System.out.println("Selected Move: " + Arrays.toString(move));
    }
}
