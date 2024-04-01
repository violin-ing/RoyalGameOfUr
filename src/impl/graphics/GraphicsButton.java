import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

    public GraphicsButton(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        // addActionListenerToButton();
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN,5));
        this.setBounds((WINDOWWIDTH/2)+(xPosition*BLOCKDIMENSION)-150,yPosition*BLOCKDIMENSION+75,BLOCKDIMENSION,BLOCKDIMENSION);
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(true);
        this.setVisible(false);
    }

    // public void addActionListenerToButton() {
    //     this.addActionListener(new ActionListener() {
    //         @Override
    //         public void actionPerformed(ActionEvent e) {
    //             System.out.println("CLICKED");
    //         }
    //     });
    // }

    // method with action listener to register a click on roll and update the roll value.
}
