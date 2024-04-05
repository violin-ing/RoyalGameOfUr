/**
 * This class acts as a counter for the players scores, and the number of remaining tiles they can add to the board.
 * This class also keeps track of the player score.
 */
public class Counter {
    // initilaise attributes
    private int p1Counter = 7;  
    private int p2Counter = 7;
    private int p1Score = 0;
    private int p2Score = 0;
    private boolean isPlayer1Turn = true;

    /**
     * reduces the counter amount of the player specified.
     * @param playerName string for player, either "P1" or "P2"
     */
    public void reduceCounter(String playerName) {
        if (playerName.equals("P1")) {p1Counter--;}
        else if (playerName.equals("P2")) {p2Counter--;}
        else {System.err.println("Expected arguments: |P1| or |P2|");}
    }
    /**
     * increases the counter for a player by a certain amount specified, used for when a players chip is taken.
     * @param playerName string for the player name: either "P1" or "P2"
     * @param value string for the amount to increase the player counter by.
     */
    public void increaseCounter(String playerName, int value) {
        if (playerName.equals("P1")) {p1Counter += value;}
        else if (playerName.equals("P2")) {p2Counter += value;}
        else {System.err.println("Expected arguments: |P1| or |P2|");}
    }

    /**
     * returns the counter for P1
     * @return p1 chip counter.
     */
    public int getP1Counter() {return p1Counter;}
    /**
     *  returns the counter for P2
     * @return 
     */
    public int getP2Counter() {return p2Counter;}

    /**
     * retunrs p1 score
     * @return p1 score, int
     */
    public int getP1Score() {return p1Score;}
    /**
     * retunrs p2 score
     * @return p2 score int
     */
    public int getP2Score() {return p2Score;}
    /**
     * increases the player score when a player moves a chip off the board.
     * @param player string, player to increment score by: "P1" or "P2"
     * @param amount the amount to increase score by, int.
     */
    public void increasePlayerScore(String player, int amount) {
        if (player.equals("P1")) {
            p1Score += amount;
        } else {
            p2Score += amount;
        }
    }
    /**
     * Toggle which checks the boolean value of "isPlayer1Turn",
     * if so, return "P1" and then set "isPlayer1Turn" to false for next time
     * isPlayer1Turn is a property / global variable, so this will be remembered
     * @return returns the current players turn
     */
     public String getPlayerTurn() {
        String currentPlayer = isPlayer1Turn ? "P1" : "P2";
        this.isPlayer1Turn = !this.isPlayer1Turn;
        return currentPlayer;
    }
} 
