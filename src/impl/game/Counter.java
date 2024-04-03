public class Counter {
    private int p1Counter = 7;  // We start at 7, the goal is to reach 0
    private int p2Counter = 7;
    private int p1Score = 0;
    private int p2Score = 0;
    private boolean isPlayer1Turn = true;

    // Counter for the points, decrements the counter for the player who scored a point
    // we decrement since the goal of the game is to remove all of your chips
    public void reduceCounter(String playerName) {
        if (playerName.equals("P1")) {p1Counter--;}
        else if (playerName.equals("P2")) {p2Counter--;}
        else {System.err.println("Expected arguments: |P1| or |P2|");}
    }

    public void increaseCounter(String playerName, int value) {
        if (playerName.equals("P1")) {p1Counter += value;}
        else if (playerName.equals("P2")) {p2Counter += value;}
        else {System.err.println("Expected arguments: |P1| or |P2|");}
    }

    public int getP1Counter() {return p1Counter;}
    public int getP2Counter() {return p2Counter;}

    public int getP1Score() {return p1Score;}
    public int getP2Score() {return p2Score;}

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
     */

     public String getPlayerTurn() {
        String currentPlayer = isPlayer1Turn ? "P1" : "P2";
        this.isPlayer1Turn = !this.isPlayer1Turn;
        return currentPlayer;
    }
} 
