import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
// This will be a class to draw all the basic tiles on the board.
/**
 * GraphicsTile
 */
public class GraphicsTile extends JPanel {
    private static final int WINDOWWIDTH = 1200;
    private static final int BLOCKDIMENSION = 100;
    // position of tile in the array.
    private final int xPosition;
    private final int yPosition;
    // who owns this tile / chip 
    private String ownership;
    // stack amount
    private int stackAmount;
    private final boolean rosette;
    private final boolean empty;
    // image.
    private Image image;

    // write a method to draw the tile in the correct position + with the correct image -> this will be updated each turn.

    public GraphicsTile(int x, int y, boolean rosette, boolean empty) {
        this.xPosition = x;
        this.yPosition = y;
        this.rosette = rosette;
        this.empty = empty;
        this.ownership = "none";
        this.stackAmount = 0;
        drawTileImage(ownership, 0);
        this.setBounds((WINDOWWIDTH/2)+(xPosition*BLOCKDIMENSION)-150,yPosition*BLOCKDIMENSION+75,BLOCKDIMENSION,BLOCKDIMENSION);
    }
    // PATH WILL HAVE TO BE CHANGED SOON
    private void loadImage(String imageName) {
        ImageIcon icon = new ImageIcon("./RoyalGameOfUr/src/impl/graphics/assets/" + imageName);
        Image result = icon.getImage();
        this.image = result;
    }
    // updates the image of the tile.
    private void drawTileImage(String ownership, int stackAmount) {
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
                loadImage("w" + stackAmount + "chip.png");
                break;
            case "P2":
                loadImage("b" + stackAmount + "chip.png");
                break;
            default:
                break;
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, BLOCKDIMENSION, BLOCKDIMENSION, this);
    }
    // changes graphic tile ownership

    public void updateImage(int amount, String ownership) {
        this.stackAmount = amount;
        this.ownership = ownership;
        // make sure image is updated after each change.
        drawTileImage(this.ownership, this.stackAmount);
    }
}