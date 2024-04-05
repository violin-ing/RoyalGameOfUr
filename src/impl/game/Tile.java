/**
 * Represents a single tile on a game board. Each tile can hold a chip and has properties to indicate
 * its position and whether it is a special "rosetta" tile. The class provides methods to access and
 * modify these properties, allowing the game logic to interact with the tiles as part of the game's
 * state management.
 */
public class Tile {
    private Chip chip; // The chip placed on this tile, if any.
    private boolean rosetta; // Flag indicating whether this tile is a rosetta tile.
    private int position; // The position of this tile on the game board.

    /**
     * Constructs a new {@code Tile} with the specified chip and position. By default, a tile is not
     * a rosetta tile upon creation.
     *
     * @param chip The {@code Chip} object to be placed on the tile. Can be {@code null} if the tile is empty.
     * @param position The position of the tile on the game board.
     */
    public Tile(Chip chip, int position) {
        this.chip = chip;
        this.rosetta = false; // Default value; can be changed with setRosetta().
        this.position = position;
    }

    /**
     * Returns the chip placed on this tile.
     *
     * @return The {@code Chip} object on this tile. Returns {@code null} if the tile is empty.
     */
    public Chip getChip() {return chip;}

    /**
     * Checks if this tile is a rosetta tile.
     *
     * @return {@code true} if this tile is a rosetta tile, otherwise {@code false}.
     */
    public boolean isRosetta() {return rosetta;}

    /**
     * Returns the position of this tile on the game board.
     *
     * @return The position of the tile.
     */
    public int getPos() {return position;}

    /**
     * Marks this tile as a rosetta tile. This method changes the tile's {@code rosetta} property to {@code true}.
     */
    public void setRosetta() {
        this.rosetta = true;
    }
    
}