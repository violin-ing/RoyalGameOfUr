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
    
}