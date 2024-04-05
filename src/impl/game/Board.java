import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.border.Border;

/**
 * Represents the game board of a board game. The board is divided into three strips: one for each player and a middle strip.
 * Each strip contains a series of tiles, and each tile can hold a chip. The board tracks the positions of all chips and manages
 * the movement of chips according to the game rules. It supports operations such as moving chips between tiles, adding new chips
 * to the board, and removing chips as they reach the end of their path.
 */
public class Board implements Cloneable {
    private Counter counter; // The counter object associated with this board.
    
    private Tile[] p1Strip = new Tile[6]; // The strip of tiles for player 1.
    private Tile[] midStrip = new Tile[8]; // The middle strip of tiles, shared by both players.
    private Tile[] p2Strip = new Tile[6]; // The strip of tiles for player 2.
    
    private Tile[][] board; // An array of tile strips, representing the entire board.

    /**
     * Constructs a new Board object with the specified Counter. Initializes the strips with tiles,
     * setting each tile's chip to have "none" ownership and zero amount initially. Special "rosetta" tiles are also marked.
     * 
     * @param counter The Counter object to be used with this board.
     */
    public Board(Counter counter) {
        this.counter = counter;
        for (int i = 0; i < p1Strip.length; i++) 
        {
            p1Strip[i] = new Tile(new Chip(), i);   //It's important to have a quick indexable way to access the position of the tile, to be able to identify the current position of a chip, and evalute to what tile it can move
            this.p1Strip[i].getChip().setOwnership("none");
            this.p1Strip[i].getChip().setAmn(0);
            if (i==3 || i==5) {
                this.p1Strip[i].setRosetta();
            }
        }
        for (int i = 0; i < midStrip.length; i++) 
        {
                midStrip[i] = new Tile(new Chip(), i);
                this.midStrip[i].getChip().setOwnership("none");
                this.midStrip[i].getChip().setAmn(0);
                if (i==3) {
                    this.midStrip[i].setRosetta();
                }
        }
        for (int i = 0; i < p2Strip.length; i++) 
        {
            p2Strip[i] = new Tile(new Chip(), i);
            this.p2Strip[i].getChip().setOwnership("none");
            this.p2Strip[i].getChip().setAmn(0);
            if (i==3 || i==5) {
                this.p2Strip[i].setRosetta();
            }
        }
    
        board = new Tile[][] {p1Strip, midStrip, p2Strip};  //Creates an array of strips, 0 for p1, 1 for mid, 2 for p2
    }

    /**
     * Constructs a Board object as a deep copy of another Board object.
     * 
     * @param board The Board object to copy.
     */
    /* 
    public Board(Board board) {
        this.board = (Tile[][])board.getBoard().clone();
        this.p1Strip = (Tile[])board.getBoardStrip(0).clone();
        this.midStrip = (Tile[])board.getBoardStrip(1).clone();
        this.p2Strip = (Tile[])board.getBoardStrip(2).clone();
    }

    /**
     * clones an object, so that it does not reference the same object in memory.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Returns the specified strip of the board.
     * 
     * @param index The index of the strip to return (0 for p1Strip, 1 for midStrip, 2 for p2Strip).
     * @return The Tile array representing the requested strip of the board.
     */
    public Tile[] getBoardStrip(int index) {return this.board[index];}

    /**
     * Returns the 2D array representing the entire board.
     * 
     * @return A 2D Tile array representing the board.
     */
    public Tile[][] getBoard() {return this.board;}

    public Counter getCounter() {return this.counter;}

    /**
     * Moves a chip from one tile to another, based on the provided move choice and the player making the move.
     * Handles different move types including adding new chips, removing chips (scoring), and moving or stacking chips on tiles.
     * 
     * @param moveChoice An array representing the move (from strip, from position, to strip, to position).
     * @param player The player making the move.
     * @return A HashSet<String> indicating the types of move made (e.g., "WIN", "STACK", "MOVE").
     */
    public HashSet<String> move(int[] moveChoice, String player, boolean changePlayer) {
        Tile movingFromTile;
        Tile movingToTile;
        boolean addedChip  = false;
        boolean removedChip = false;
        HashSet<String> moveType = new HashSet<>();
        
        // if statment sets up tile we are moving fro m and to.
        // this will also check if we are adding a chip to the board / or removing one (scoring).
        if (moveChoice[1]==-1) {
            movingFromTile = null;
            movingToTile = board[moveChoice[2]][moveChoice[3]];
            addedChip = true;
        } else if((moveChoice[2]==0  || moveChoice[2]==2) && moveChoice[3]==6) {
            movingToTile = null;
            movingFromTile = board[moveChoice[0]][moveChoice[1]];
            removedChip = true;
        } else {
            movingFromTile = board[moveChoice[0]][moveChoice[1]];
            movingToTile = board[moveChoice[2]][moveChoice[3]];
        }

        if (removedChip) {
            // clear from tile
            // add tile amount to score
            if (changePlayer) {
                counter.increasePlayerScore(player, movingFromTile.getChip().getAmn());
            }
            moveType.add("WIN");
            // CLEAR TILE AFTER
        } else if(addedChip){
            // increase value of tile we are moving to, and check if we are on a rosetta tile
            // if we are on a rosetta give player another turn
            movingToTile.getChip().increaseAmn(1);
            movingToTile.getChip().setOwnership(player);
            moveType.add("ADD CHIP");

            if (movingToTile.isRosetta()) {
                // this has the effect of giving the player another turn.
                if (changePlayer) {
                    counter.getPlayerTurn();
                }
                moveType.add("ROSETTA");
            }
            // decrement the counter value for player
            if (changePlayer) {
                counter.reduceCounter(player);
            }
        } else {
            // if chip is our own;
            // increase stack amount
            // check if this is a rosetta tile
            // MOVING TO OWN TILE
            if (movingToTile.getChip().getOwnership().equals(player)) {
                movingToTile.getChip().increaseAmn(movingFromTile.getChip().getStackAmount());
                moveType.add("STACK");
            } else {
                // MOVING TO ENEMY TILE
                String enemyPlayer = "P1".equals(player) ? "P2" : "P1";
                if (movingToTile.getChip().getOwnership().equals(enemyPlayer)) {
                    if (changePlayer) {
                        counter.increaseCounter(enemyPlayer, movingToTile.getChip().getStackAmount());  
                    }
                    moveType.add("TAKE CHIP");
                }
                // IF MOVING TO EMPTY TILE WE DO THIS ASWELL
                movingToTile.getChip().setOwnership(player);
                movingToTile.getChip().setAmn(movingFromTile.getChip().getAmn());
                moveType.add("MOVE");
            }
            // give player another turn if this is a rosetta.
            if (movingToTile.isRosetta()) {
                if (changePlayer) {
                    counter.getPlayerTurn();
                }
                moveType.add("ROSETTA");
            }
        }
        if (!addedChip) {
            // clear the previous tile.
            movingFromTile.getChip().setOwnership("none");
            movingFromTile.getChip().setAmn(0);
        }
        return moveType;
    }

    /**
     * Identifies and returns a list of all positions occupied by the specified player's chips.
     * 
     * @param player The player whose chips to identify.
     * @return A list of int arrays, each representing the strip and position of a chip.
     */
    public List<int[]> identifyPieces(String player) {
        List<int[]> pieces = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (Tile tile : this.board[i]) {
                if (tile.getChip().getOwnership().equals(player)) {
                    int[] stripPos = new int[2];
                    stripPos[0] = i;
                    stripPos[1] = tile.getPos();
                    System.out.println(stripPos[0] + " " + stripPos[1]);
                    pieces.add(stripPos);
                }    
            }
        }
        return pieces;
    }
}