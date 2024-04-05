import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * The {@code GraphicsTile} class extends {@link JPanel} and is responsible for rendering a single tile
 * on the game board. It encapsulates properties such as the tile's position, ownership, and whether it
 * is a special 'rosette' tile or an empty tile. The class provides functionality to update and display
 * the tile's graphical representation based on its current state.
 */
public class GraphicsTile extends JPanel {
    private static final int WINDOWWIDTH = 1200; // The width of the window.
    private static final int BLOCKDIMENSION = 100; // The dimensions of each tile.

    private final int xPosition; // The x-position of the tile in the board array.
    private final int yPosition; // The y-position of the tile in the board array.
    private String ownership; // Indicates the ownership of the tile.
    private int stackAmount; // The amount of chips stacked on this tile.
    private final boolean rosette; // Indicates if this tile is a rosette tile.
    private final boolean empty; // Indicates if this tile is empty.
    private Image image; // The image to be drawn for this tile.

    /**
     * Constructs a {@code GraphicsTile} with specified properties. It initializes the tile's position,
     * whether it's a rosette or an empty tile, and sets up its graphical representation.
     * 
     * @param x The x-position of the tile.
     * @param y The y-position of the tile.
     * @param rosette Indicates if the tile is a rosette.
     * @param empty Indicates if the tile is empty.
     */
    public GraphicsTile(int x, int y, boolean rosette, boolean empty) {
        this.xPosition = x;
        this.yPosition = y;
        this.rosette = rosette;
        this.empty = empty;
        this.ownership = "none";
        this.stackAmount = 0;
        drawTileImage(ownership, 0,false);
        this.setBounds((WINDOWWIDTH/2)+(xPosition*BLOCKDIMENSION)-150,yPosition*BLOCKDIMENSION+75,BLOCKDIMENSION,BLOCKDIMENSION);
    }
    
    
    /**
     * Loads an image based on the provided image name.
     * 
     * @param imageName The name of the image file to be loaded.
     */
    private void loadImage(String imageName) {
        ImageIcon icon = new ImageIcon("./src/impl/graphics/assets/" + imageName);
        Image result = icon.getImage();
        this.image = result;
    }

    /**
     * Updates the image of the tile based on its ownership, stack amount, and whether a chip is being added.
     * This method decides which image to load and redraws the tile.
     * 
     * @param ownership The ownership of the tile.
     * @param stackAmount The number of chips stacked on the tile.
     * @param addingChip Indicates whether a chip is being added.
     */
    private void drawTileImage(String ownership, int stackAmount, boolean addingChip) {
        switch (ownership) {
            case "none":
                if (rosette) {
                    loadImage("rosettetile.png");
                } else if(empty) {
                    loadImage("");
                } else {
                    loadImage("emptytile.png");
                }
                break;
            case "P1":
                if (addingChip) {
                    loadImage("w-1chip.png");
                } else {
                    loadImage("w" + stackAmount + "chip.png");
                }
                break;
            case "P2":
                if (addingChip) {
                    loadImage("b-1chip.png");
                } else {
                    loadImage("b" + stackAmount + "chip.png");
                }
                break;
            default:
                break;
        }
        repaint();
    }

    /**
     * Custom paint component for drawing the tile's image. Overrides the {@code paintComponent} method from {@link JPanel}.
     * 
     * @param g The {@link Graphics} object to protect.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, BLOCKDIMENSION, BLOCKDIMENSION, this);
    }
    
    /**
     * Updates the tile's image based on the new amount of chips, ownership, and whether a chip is being added.
     * This method ensures the graphical representation of the tile is updated to reflect its current state.
     * 
     * @param amount The new stack amount for the tile.
     * @param ownership The new ownership of the tile.
     * @param addingChip Indicates whether a chip is being added to the tile.
     */
    public void updateImage(int amount, String ownership, boolean addingChip) {
        this.stackAmount = amount;
        this.ownership = ownership;
        // make sure image is updated after each change.
        drawTileImage(this.ownership, this.stackAmount, addingChip);
    }
}