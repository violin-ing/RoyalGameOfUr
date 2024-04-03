import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
// this class will handle drawing tiles as buttons.
public class GraphicsButton extends JButton {
    private static final int WINDOWWIDTH = 1200;
    private static final int BLOCKDIMENSION = 100;
    // should only be drawn at a given position if a chip can be moved
    // should take information from previous tile and then draw over it with the same stuff.
    // this is just an invisible button which will be drawn ontop of chips that can be moved
    // ALL OF THESE ARE GREEN BY DEFAULT?!?!?!
    private final int xPosition;
    private final int yPosition;
    private boolean chipSelection;
    private boolean moveSelection;
    private int moveStrip;
    private int moveLocation;
    public static boolean tileSelected;
    private boolean isSelected;
    private int moveFromStrip;
    private int moveFromLocation;
    // corresponding location of button to move a chip.
    private int moveButtonStrip;
    private int moveButtonLocation;

    public GraphicsButton(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        this.chipSelection = false;
        this.moveSelection = false;
        // addActionListenerToButton();
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN,5));
        this.setBounds((WINDOWWIDTH/2)+(xPosition*BLOCKDIMENSION)-150,yPosition*BLOCKDIMENSION+75,BLOCKDIMENSION,BLOCKDIMENSION);
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(true);
        this.setVisible(false);
    }

    public void updateSelection(boolean selection) {
        this.isSelected = selection;
    }

    public boolean getSelection() {return this.isSelected;}

    public boolean checkIsChipSelection() {return chipSelection;}

    public boolean checkIsMoveSelection() {return moveSelection;}

    public boolean checkIsBothSelection() {
        if (chipSelection && moveSelection) {
            return true;
        } else {
            return false;
        }
    }

    public void setButtonSelectable() {
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN,5));
        this.setEnabled(true);
        this.setVisible(true);
        if (!moveSelection) {
            this.chipSelection = true;  
        }
    }

    public void setBothSelectableAndFutureSelectable() {
        this.chipSelection = true;
        this.moveSelection = true;
        Border border1 = BorderFactory.createLineBorder(Color.GREEN,5);
        Border border2 = BorderFactory.createLineBorder(Color.ORANGE, 5);
        this.setBorder(new CompoundBorder(border1,border2));
        this.setEnabled(true);
        this.setVisible(true);
    }

    public void setButtonFutureSelectable() {
        this.setBorder(BorderFactory.createLineBorder(Color.ORANGE,5));
        this.setEnabled(true);
        this.setVisible(true);
        if (!chipSelection) {
            this.moveSelection = true;
        }
    }

    public void setButtonInvisible(){
        this.setVisible(false);
    }

    public void setChipButtonsMoveButton(int strip, int location) {
        this.moveButtonStrip = strip;
        this.moveButtonLocation = location;
    }

    public void setMoveToLocation(int strip, int location) {
        this.moveStrip = strip;
        this.moveLocation = location;
    }

    public int getMoveButtonStrip() {
        return moveButtonStrip;
    }

    public int getMoveButtonLocation() {
        return moveButtonLocation;
    }

    public int getMoveStrip() {
        return moveStrip;
    }

    public int getMoveLocation() {
        return moveLocation;
    }

    public void setMoveFromLocation(int strip, int location) {
        this.moveFromStrip = strip;
        this.moveFromLocation = location;
    }

    public int getMoveFromLocation() {
        return moveFromLocation;
    }

    public int getMoveFromStrip() {
        return moveFromStrip;
    }

    public void resetButton() {
        setButtonInvisible();
        this.chipSelection = false;
        this.moveSelection = false;
    }
}
