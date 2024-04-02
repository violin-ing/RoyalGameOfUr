public class Tile {
    private Chip chip; 
    private boolean isRosetta;
    private int position;

    public Tile(Chip chip, int position) {
        this.chip = chip;
        this.isRosetta = false;
        this.position = position;
    }

    public Chip getChip() {return chip;}
    public boolean isRosetta() {return isRosetta;}
    public int getPos() {return position;}
    

    // TODO: add a method to check if the tile is a rosetta, and invoke the "getPlayerTurn" method so that it toggles the player turn (which will be toggled again later) to allow for a repeat turn
}