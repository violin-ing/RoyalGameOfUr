public class Board {
    private Counter counter = new Counter();
    
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

    public Board() {
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
    // Ensure an array of valid moves is returned by the checker, and then lists them to the user, and loops until they choose one of the options

    // Will handle movement through the arrays
    // Iterates through each possible position a player chip could be, and passes the ownership, roll, and wanted piece to the moveLogic method
    // so that it can handle with identifying the strip the chip is in, and moving it to the specified location
    
    public void pieceMover(String player, int roll, int wantedPiece) {
        if (player.equals("P1")) {
            for (Tile tile : p1Strip) moveLogic(tile, player, roll, wantedPiece, "p1Strip");
            for (Tile tile : midStrip) moveLogic(tile, player, roll, wantedPiece, "midStrip");
        }
        else if (player.equals("P2")) {
            for (Tile tile : p2Strip) moveLogic(tile, player, roll, wantedPiece, "p2Strip");
            for (Tile tile : midStrip) moveLogic(tile, player, roll, wantedPiece, "midStrip");
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
     * If they are currently within section one of the player strip, we check if their roll will move them out of it, and move them onto midstrip, otherwise just move them within section one 
     * If they are currently within section two, we check if their roll will lead them to cheque the chip and leave section two, or just move within section 2
     * If they are currently within the midstrip, we check if their roll will lead them to move into section two of the player strip, or just move within the midstrip
     * 
     * @param tile we pass in each tile in the strip specified
     * @param player the player that is moving the chip, important for checking ownership
     * @param roll the number of positions the chip will move
     * @param wantedPiece the position containing a piece that player has chosen to move with however many movement points they have (based on roll)
     * @param strip the strip the chip is in
     * 
     * @see movePlayerStrip this handles checking if a chip originated from a player strip and will move within that strip, or transfer to the midstrip. It also checks if a piece is returning from the midstrip to the player strip
     * @see moveMidStrip this handles checking if a chip originated from the midstrip and will move within that strip, or transfer to the player strip
     * @see clearPreviousPosition this resets the ownership of the previous position of the chip to none
     */

    private void moveLogic(Tile tile, String player, int roll, int wantedPiece, String strip) {
        int currentPosition = tile.getPos();                        //So far we know what array the chip is in, and what position it is in that array

        if (strip.equals("p1Strip") || strip.equals("p2Strip")) {
            if (tile.getChip().getOwnership().equals(player) && currentPosition == wantedPiece) { 
                if (currentPosition <= lengthOfPlayerStripSectionOne) {                                     // within section one of the player strip                
                    if (currentPosition + roll >= lengthOfPlayerStripSectionOne) {moveMidStrip(tile, currentPosition, roll, player, "from pStrip");}            // Move from section one of player strip to midstrip       
                    else if (currentPosition + roll < lengthOfPlayerStripSectionOne) {movePlayerStrip(tile, currentPosition, roll, player, "in pStrip");}       // Move within section one of player strip
                }

                else if (currentPosition > lengthOfPlayerStripSectionOne) {                                 // within section two of the player strip
                    if (currentPosition + roll >= lengthOfPlayerStrip) {                                   //  cheque the chip
                        counter.pointScorer("P1"); 
                        clearPreviousPosition(tile); 
                    }
                    else if (currentPosition + roll < lengthOfPlayerStrip) {movePlayerStrip(tile, currentPosition, roll, player, "in pStrip");} // Move within section two of player strip
                }
            }
        }
        if (strip.equals("midStrip")) {                                                             // within the midstrip
            if (tile.getChip().getOwnership().equals(player) && currentPosition == wantedPiece) { 
                if (currentPosition + roll >= lengthOfMidStrip) {movePlayerStrip(tile, currentPosition, roll, player, "from midStrip");}                // Move from midstrip to section two of player strip
                else if (currentPosition + roll < lengthOfPlayerStripSectionOne) {moveMidStrip(tile, currentPosition, roll, player, "from midStrip");}  // Move within midstrip
            }
        }   
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
}