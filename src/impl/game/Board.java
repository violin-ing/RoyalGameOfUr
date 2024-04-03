import java.util.ArrayList;
import java.util.HashMap;
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
        }
        for (int i = 0; i < midStrip.length; i++) 
        {
                midStrip[i] = new Tile(new Chip(), i);
                this.midStrip[i].getChip().setOwnership("none");
                this.midStrip[i].getChip().setAmn(0);
        }
        for (int i = 0; i < p2Strip.length; i++) 
        {
            p2Strip[i] = new Tile(new Chip(), i);
            this.p2Strip[i].getChip().setOwnership("none");
            this.p2Strip[i].getChip().setAmn(0);
        }
    
        board = new Tile[][] {p1Strip, midStrip, p2Strip};  //Creates an array of strips, 0 for p1, 1 for mid, 2 for p2
    }


    //Returns a strip of the board specified by the index
    public Tile[] getBoardStrip(int index) {return this.board[index];}
    public Tile[][] getBoard() {return this.board;}


    // TODO: pieceMover method requires valid move checker so that user knows what piece can be implemented, provide wanted chip prevalidated before it enters pieceMover
    // TODO: make it so that adding token to the board is always an available move under certain circumstances.
    // Ensure an array of valid moves is returned by the checker, and then lists them to the user, and loops until they choose one of the options

    // Will handle movement through the arrays
    // Iterates through each possible position a player chip could be, and passes the ownership, roll, and wanted piece to the moveLogic method
    // so that it can handle with identifying the strip the chip is in, and moving it to the specified location
    
    public void pieceMover(String player, int roll, int[] wantedPiece) {
        // loop through all the pieces in the current board, if p1, only check p1 string and middle, if p2 only check p2 and middle
        // we then want to update this current board to only have all available moves.
        // we also need to do an additional check to see if we can add a token to the board in the availalbe moves.
        if (player.equals("P1")) {
            for (Tile tile : p1Strip) moveLogic(tile, player, roll, wantedPiece, "p1Strip");        //We can call {p1, p2, mid}strip as it is a property of this class
        }
    
        else if (player.equals("P2")) {
            for (Tile tile : p2Strip) moveLogic(tile, player, roll, wantedPiece, "p2Strip");
        }

        for (Tile tile : midStrip) moveLogic(tile, player, roll, wantedPiece, "midStrip");
    }

    public void move(int[] moveChoice, String player) {
        Tile movingFromTile;
        Tile movingToTile;
        boolean addedChip  = false;
        boolean removedChip = false;
        
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
            // CLEAR TILE AFTER
        } else if(addedChip){
            // increase value of tile we are moving to, and check if we are on a rosetta tile
            // if we are on a rosetta give player another turn
            movingToTile.getChip().increaseAmn(1);
            movingToTile.getChip().setOwnership(player);
            if (movingToTile.isRosetta()) {
                // this has the effect of giving the player another turn.
                counter.getPlayerTurn();
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
            } else {
                // MOVING TO ENEMY TILE
                String enemyPlayer = "P1".equals(player) ? "P2" : "P1";
                if (movingToTile.getChip().getOwnership().equals(enemyPlayer)) {
                    counter.increaseCounter(enemyPlayer, movingToTile.getChip().getStackAmount());
                }
                // IF MOVING TO EMPTY TILE WE DO THIS ASWELL
                movingToTile.getChip().setOwnership(player);
                movingToTile.getChip().setAmn(movingFromTile.getChip().getAmn());
            }
            // give player another turn if this is a rosetta.
            if (movingToTile.isRosetta()) {
                counter.getPlayerTurn();
            }
        }
        if (!addedChip) {
            // clear the previous tile.
            movingFromTile.getChip().setOwnership("none");
            movingFromTile.getChip().setAmn(0);
        }
    }

    /**
     * 
     * We pass in the tile from a specified strip, and explicitly state what strip the tile came from, and we also know the current position of the chip in that strip
     * With this information we can then check if the chip belongs to the player, and if it is the chip they want to move
     * 
     * There is a difference between your current position being in the first / second section of the strip vs your current position + roll. 
     * If the former, you move within that strip, if the latter, you move to the next strip
     * 
     * We check what strip (p1, p2, or midstrip) we are meant to reference tiles from, then we check if the chip belongs to the player and is the one they want to move 
     * (by matching the current strip to the wanted strip through piece.getKey() and the current tile to the wanted tile through piece.getValue())
     * If they are currently within section one of the player strip, we check if their roll will move them out of it, and move them onto midstrip, otherwise just move them within section one 
     * If they are currently within section two, we check if their roll will lead them to cheque the chip and leave section two, or just move within section 2
     * If they are currently within the midstrip, we check if their roll will lead them to move into section two of the player strip, or just move within the midstrip
     * 
     * @param tile we pass in each tile in the strip specified
     * @param player the player that is moving the chip, important for checking ownership
     * @param roll the number of positions the chip will move
     * @param wantedPiece a map containing the strip and tile of the chip they want to move, we turn this into a map.entry set to get the key and value
     * @param strip the strip the chip is in
     * 
     * @see movePlayerStrip this handles checking if a chip originated from a player strip and will move within that strip, or transfer to the midstrip. It also checks if a piece is returning from the midstrip to the player strip
     * @see moveMidStrip this handles checking if a chip originated from the midstrip and will move within that strip, or transfer to the player strip
     * @see clearPreviousPosition this resets the ownership of the previous position of the chip to none
     */

     //TODO: Map.Entry<Integer, Integer> line does not find the first key value pair in the mapping, it instead finds nothing. The map is somehow empty

    private void moveLogic(Tile tile, String player, int roll, int[] piecePos, String strip) {
        int currentPosition = tile.getPos();                                                            //So far we know what array the chip is in, and what position it is in that array

        System.out.println(strip);
        System.out.println(piecePos);


        //Check if the player owns this chip, that it is the tile they want to move, and that it is in the correct strip
        // if (strip.equals("p1Strip") || strip.equals("p2Strip")) {
        //     if (tile.getChip().getOwnership().equals(player) && (currentPosition == piecePos[1] && (piecePos[0] == 0 || piecePos[0] == 2))) {  
        //         if (currentPosition <= lengthOfPlayerStripSectionOne) {                                                                                                  // is checking within section one of the player strip                
        //             if (currentPosition + roll >= lengthOfPlayerStripSectionOne) {moveMidStrip(tile, currentPosition, roll, player, "from pStrip");}            // Move from section one of player strip to midstrip       
        //             else if (currentPosition + roll < lengthOfPlayerStripSectionOne) {movePlayerStrip(tile, currentPosition, roll, player, "in pStrip");}       // Move within section one of player strip
        //         }

        //         else if (currentPosition > lengthOfPlayerStripSectionOne) {                                                                                              // is checking within section two of the player strip
        //             if (currentPosition + roll >= lengthOfPlayerStrip) {                                                                                                 //  cheque the chip
        //                 counter.pointScorer(player); 
        //                 clearPreviousPosition(tile); 
        //             }
        //             else if (currentPosition + roll < lengthOfPlayerStrip) {movePlayerStrip(tile, currentPosition, roll, player, "in pStrip");}                 // Move within section two of player strip
        //         }
        //     }
        // }
        // if (strip.equals("midStrip")) {                                                                                                                         // is checking within the midstrip
        //     if (tile.getChip().getOwnership().equals(player) && currentPosition == piecePos[0]) { 
        //         if (currentPosition + roll >= lengthOfMidStrip) {movePlayerStrip(tile, currentPosition, roll, player, "from midStrip");}                        // Move from midstrip to section two of player strip
        //         else if (currentPosition + roll < lengthOfPlayerStripSectionOne) {moveMidStrip(tile, currentPosition, roll, player, "from midStrip");}          // Move within midstrip
        //     }
        // }   

    }

    private void moveMidStrip(Tile tile, int currentPosition, int roll, String player, String transfer) {
        if (player.equals("P1")) {
            if (transfer.equals("from pStrip")) {
                midStrip[currentPosition + roll - lengthOfPlayerStripSectionOne].getChip().setOwnership("P1");
                midStrip[currentPosition + roll - lengthOfPlayerStripSectionOne].getChip().setAmn(tile.getChip().getAmn()); //Take the amount of chips in the current tile, and set it in the new tile
            }
            else if (transfer.equals("from midStrip")) {
                midStrip[currentPosition + roll].getChip().setOwnership("P1");
                midStrip[currentPosition + roll].getChip().setAmn(tile.getChip().getAmn());
            }
        }
        else if (player.equals("P2")) {
            if (transfer.equals("from pStrip")) {
                midStrip[currentPosition + roll - lengthOfPlayerStripSectionOne].getChip().setOwnership("P2");
                midStrip[currentPosition + roll - lengthOfPlayerStripSectionOne].getChip().setAmn(tile.getChip().getAmn());
            }
            else if (transfer.equals("from midStrip")) {
                midStrip[currentPosition + roll].getChip().setOwnership("P2");
                midStrip[currentPosition + roll].getChip().setAmn(tile.getChip().getAmn());
            }
        }
        clearPreviousPosition(tile); // reset the ownership of the chip to none and the amout to 0
    }


    private void movePlayerStrip(Tile tile, int currentPosition, int roll, String player, String transfer) {
        System.out.println("MOVING PLAYER CHIP!");
        if (player.equals("P1")) {
            if (transfer.equals("in pStrip")) {
                p1Strip[currentPosition + roll].getChip().setOwnership("P1");
                p1Strip[currentPosition + roll].getChip().setAmn(tile.getChip().getAmn());
            }
            else if (transfer.equals("from midStrip")) {
                p1Strip[currentPosition + roll - lengthOfMidStrip].getChip().setOwnership("P1");
                p1Strip[currentPosition + roll - lengthOfMidStrip].getChip().setAmn(tile.getChip().getAmn());
            }
        }
        else if (player.equals("P2")) {
            if (transfer.equals("in pStrip")) {
                p2Strip[currentPosition + roll].getChip().setOwnership("P2");
                p2Strip[currentPosition + roll].getChip().setAmn(tile.getChip().getAmn());
            }
            else if (transfer.equals("from midStrip")) {
                p2Strip[currentPosition + roll - lengthOfMidStrip].getChip().setOwnership("P2");
                p2Strip[currentPosition + roll - lengthOfMidStrip].getChip().setAmn(tile.getChip().getAmn());
            }
            clearPreviousPosition(tile); // reset the ownership of the chip to none and the amout to 0
        }
    }

    private void clearPreviousPosition(Tile tile) {
        tile.getChip().setOwnership("none");
        tile.getChip().setAmn(0);
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