import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Represents a custom JButton for drawing tiles.
 * Extends JButton class to customize behavior and appearance.
 */
public class GraphicsButton extends JButton {

    private static final int WINDOWWIDTH = 1200;
    private static final int BLOCKDIMENSION = 100;

    private final int xPosition; // X-coordinate position of the button
    private final int yPosition; // Y-coordinate position of the button
    private boolean chipSelection; // Flag indicating if the button represents a chip selection
    private int moveStrip; // Strip for the move
    private int moveLocation; // Location for the move
    private boolean isSelected; // Flag indicating if the button is selected
    private int moveFromStrip; // Strip from which a chip is moved
    private int moveFromLocation; // Location from which a chip is moved
    private int moveButtonStrip; // Strip for the move button
    private int moveButtonLocation; // Location for the move button

    /**
     * Constructs a GraphicsButton with specified coordinates.
     * @param x The X-coordinate position of the button.
     * @param y The Y-coordinate position of the button.
     */
    public GraphicsButton(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        this.chipSelection = false;
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
        this.setBounds((WINDOWWIDTH / 2) + (xPosition * BLOCKDIMENSION) - 150, yPosition * BLOCKDIMENSION + 75, BLOCKDIMENSION, BLOCKDIMENSION);
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(true);
        this.setVisible(false);
    }

    // Methods for updating button properties

    /**
     * Updates the selection state of the button.
     * @param selection The new selection state.
     */
    public void updateSelection(boolean selection) {
        this.isSelected = selection;
    }

    /**
     * Retrieves the selection state of the button.
     * @return True if the button is selected, false otherwise.
     */
    public boolean getSelection() {
        return this.isSelected;
    }

    /**
     * Checks if the button represents a chip selection.
     * @return True if the button represents a chip selection, false otherwise.
     */
    public boolean checkIsChipSelection() {
        return chipSelection;
    }

    /**
     * Sets the button as selectable.
     */
    public void setButtonSelectable() {
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
        this.setEnabled(true);
        this.setVisible(true);
        this.chipSelection = true;
    }

    /**
     * Sets the button as representing a future move.
     */
    public void setButtonAsFutureMove() {
        this.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 5));
        this.setEnabled(false);
        this.setVisible(true);
    }

    /**
     * Sets the button as invisible.
     */
    public void setButtonInvisible() {
        this.setVisible(false);
        this.setEnabled(false);
    }

    // Methods for setting chip movement related properties

    /**
     * Sets the strip and location for the chip buttons move button.
     * @param strip The strip for the move button.
     * @param location The location for the move button.
     */
    public void setChipButtonsMoveButton(int strip, int location) {
        this.moveButtonStrip = strip;
        this.moveButtonLocation = location;
    }

    /**
     * Sets the strip and location for the chip move.
     * @param strip The strip for the move.
     * @param location The location for the move.
     */
    public void setMoveToLocation(int strip, int location) {
        this.moveStrip = strip;
        this.moveLocation = location;
    }

    /**
     * Retrieves the strip for the move button.
     * @return The strip for the move button.
     */
    public int getMoveButtonStrip() {
        return moveButtonStrip;
    }

    /**
     * Retrieves the location for the move button.
     * @return The location for the move button.
     */
    public int getMoveButtonLocation() {
        return moveButtonLocation;
    }

    /**
     * Retrieves the strip for the move.
     * @return The strip for the move.
     */
    public int getMoveStrip() {
        return moveStrip;
    }

    /**
     * Retrieves the location for the move.
     * @return The location for the move.
     */
    public int getMoveLocation() {
        return moveLocation;
    }

    /**
     * Sets the strip and location from which a chip is moved.
     * @param strip The strip from which a chip is moved.
     * @param location The location from which a chip is moved.
     */
    public void setMoveFromLocation(int strip, int location) {
        this.moveFromStrip = strip;
        this.moveFromLocation = location;
    }

    /**
     * Retrieves the location from which a chip is moved.
     * @return The location from which a chip is moved.
     */
    public int getMoveFromLocation() {
        return moveFromLocation;
    }

    /**
     * Retrieves the strip from which a chip is moved.
     * @return The strip from which a chip is moved.
     */
    public int getMoveFromStrip() {
        return moveFromStrip;
    }

    /**
     * Resets the button to its initial state.
     */
    public void resetButton() {
        setButtonInvisible();
        this.chipSelection = false;
        this.isSelected = false;
    }

    /**
     * Retrieves the X-coordinate position of the button.
     * @return The X-coordinate position of the button.
     */
    public int getXPos() {
        return xPosition;
    }

    /**
     * Retrieves the Y-coordinate position of the button.
     * @return The Y-coordinate position of the button.
     */
    public int getYPos() {
        return yPosition;
    }
}
