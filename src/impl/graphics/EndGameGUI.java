import javax.swing.JFrame;
import javax.swing.JLabel;

public class EndGameGUI extends JFrame{

    private String winningPlayer;
    private final static int WINDOWHEIGHT = 100;
    private final static int WINDOWWIDTH = 450;

    public EndGameGUI(String winningPlayer) {
        setTitle("Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOWWIDTH, WINDOWHEIGHT);
        setLocationRelativeTo(null); 
        this.winningPlayer = winningPlayer;
        addComponents();
        this.setVisible(true);
    }

    public void addComponents() {
        JLabel outcomeLabel = new JLabel("        "+ winningPlayer + " wins.");
        this.add(outcomeLabel);
    }
}
