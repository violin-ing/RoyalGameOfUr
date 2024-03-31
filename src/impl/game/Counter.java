public class Counter {
    private int p1Counter = 7;  // We start at 7, the goal is to reach 0
    private int p2Counter = 7;
    private boolean isPlayer1Turn = true;

    // Counter for the points, decrements the counter for the player who scored a point
    // we decrement since the goal of the game is to remove all of your chips
    public void pointScorer(String playerName) {
        if (playerName.equals("P1")) {p1Counter--;}
        else if (playerName.equals("P2")) {p2Counter--;}
        else {System.err.println("Expected arguments: |P1| or |P2|");}
    }

    public int getP1Counter() {return p1Counter;}
    public int getP2Counter() {return p2Counter;}


    /**
     * Toggle which checks the boolean value of "isPlayer1Turn",
     * if so, return "P1" and then set "isPlayer1Turn" to false for next time
     * isPlayer1Turn is a property / global variable, so this will be remembered
     */

     public String getPlayerTurn() {
        String currentPlayer = isPlayer1Turn ? "P1" : "P2";
        this.isPlayer1Turn = !this.isPlayer1Turn;
        return currentPlayer;
    }
} 
