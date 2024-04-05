import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * EndGameGUI displays the winner of the game.
 */
public class EndGameGUI extends JFrame{
    // intiliase private attirbutes.
    private String winningPlayer;
    private final static int WINDOWHEIGHT = 100;
    private final static int WINDOWWIDTH = 450;
    /**
     * constructor for EndGameGUI
     * @param winningPlayer String, player which won.
     */
    public EndGameGUI(String winningPlayer) {
        setTitle("Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOWWIDTH, WINDOWHEIGHT);
        setLocationRelativeTo(null); 
        this.winningPlayer = winningPlayer;
        addComponents();
        this.setVisible(true);
    }
    /**
     * adds Jlabel with correct winner text to the frame.
     */
    public void addComponents() {
        JLabel outcomeLabel = new JLabel("        "+ winningPlayer + " wins.");
        this.add(outcomeLabel);
    }
}
