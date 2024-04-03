public class Tile {
    private Chip chip; 
    private boolean rosetta;
    private int position;

    public Tile(Chip chip, int position) {
        this.chip = chip;
        this.rosetta = false;
        this.position = position;
    }

    public Chip getChip() {return chip;}
    public boolean isRosetta() {return rosetta;}
    public int getPos() {return position;}

    public void setRosetta() {
        this.rosetta = true;
    }
    

    // TODO: add a method to check if the tile is a rosetta, and invoke the "getPlayerTurn" method so that it toggles the player turn (which will be toggled again later) to allow for a repeat turn
}