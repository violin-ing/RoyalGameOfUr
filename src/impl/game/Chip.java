/**
 * Chip 
 * This class stores values related to a chip on a particular tile:
 * - ownwership: none, P1, P2
 * - amount: 0-7 stack
 * This class contains methods to update and retreive these values.
 */
public class Chip {
    private String ownership;
    private int amount;

    /**
     * Chip constructor: simply sets default values for the ownership and stack.
     */
    public Chip() {
        this.ownership = "none";
        this.amount = 0;
    }
    /**
     * setOwnership
     * this will set the chip ownership to the player passed in as a parameter.
     * @param player player string: none, P1 or P2
     */
    public void setOwnership(String player) {
        switch (player) {
            case "P1": this.ownership = "P1"; break;
            case "P2": this.ownership = "P2"; break;
            case "none": this.ownership = "none"; break;
            default: System.err.println("Expected arguments: |P1| or |P2|"); break;
        }
        return;
    }

    /**
     * increaseAmn: increases the stack amount by parameter value.
     * @param value stack amount to increase by.
     */
    public void increaseAmn(int value) {this.amount += value;}
    /**
     * decreaseAmn: decrease the stack amount by the parameter value.
     * @param value stack amount to decrease by.
     */
    public void decreaseAmn(int value) {this.amount -= value;}
    /**
     * setAmn: set the stack amount to parameter value.
     * @param amn stack amount to set amount to.
     */
    public void setAmn(int amn) {this.amount = amn;}
    /**
     * getStackAmount: getter for stack amount.
     * @return returns amount.
     */
    public int getStackAmount() {return this.amount;}

    /**
     * getOwnership: getter for the ownership.
     * @return returns owwnership
     */
    public String getOwnership() {return ownership;}

    /**
     * getAmn: getter for the amount of chips stacked.
     * @return returns the amount of stacked chips
     */
    public int getAmn() {return amount;}
}