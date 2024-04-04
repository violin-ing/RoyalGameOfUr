import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Board {
    private Counter counter;
    
    private int lengthOfPlayerStrip = 6;
    private int lengthOfPlayerStripSectionOne = 4;
    private int lengthOfMidStrip = 8;
    
    private Tile[] p1Strip = new Tile[6];
    private Tile[] midStrip = new Tile[8];
    private Tile[] p2Strip = new Tile[6];
    
    // Create an aray of strips, has three positions, 0 for p1, 1 for mid, 2 for p2]
    private Tile[][] board;

    // Constructor for the board, initializes each strip with a tile in each positon (which itself contains a chip)
    // The chip is initialized with ownership "none" and amount 0, and the position is set to the index of the tile
    // We then form a board 2D array with the initialised strips

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


    //Returns a strip of the board specified by the index
    public Tile[] getBoardStrip(int index) {return this.board[index];}
    public Tile[][] getBoard() {return this.board;}

    public HashSet<String> move(int[] moveChoice, String player) {
        Tile movingFromTile;
        Tile movingToTile;
        boolean addedChip  = false;
        boolean removedChip = false;
        HashSet<String> moveType = new HashSet<>();
        
        // if statment sets up tile we are moving from and to.
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
            counter.increasePlayerScore(player, movingFromTile.getChip().getAmn());
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
                counter.getPlayerTurn();
                moveType.add("ROSETTA");
            }
            // decrement the counter value for player
            counter.reduceCounter(player);
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
                    counter.increaseCounter(enemyPlayer, movingToTile.getChip().getStackAmount());
                    moveType.add("TAKE CHIP");
                }
                // IF MOVING TO EMPTY TILE WE DO THIS ASWELL
                movingToTile.getChip().setOwnership(player);
                movingToTile.getChip().setAmn(movingFromTile.getChip().getAmn());
                moveType.add("MOVE");
            }
            // give player another turn if this is a rosetta.
            if (movingToTile.isRosetta()) {
                counter.getPlayerTurn();
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

    public List<int[]> identifyPieces(String player) {
        List<int[]> pieces = new ArrayList<>();
        int[] stripPos = new int[2];

        for (int i = 0; i < 3; i++) {
            for (Tile tile : this.board[i]) {
                if (tile.getChip().getOwnership().equals(player)) {
                    stripPos[0] = i;
                    stripPos[1] = tile.getPos();
                    pieces.add(stripPos);
                }    
            }
        }
        return pieces;
    }
}