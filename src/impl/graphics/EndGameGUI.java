import javax.swing.JFrame;
import javax.swing.JLabel;

public class EndGameGUI extends JFrame{

    private String winningPlayer;
    
    public EndGameGUI(String winningPlayer) {
        this.setResizable(false);
        this.winningPlayer = winningPlayer;
    }

    public void addComponents() {
        JLabel outcomeLabel = new JLabel(winningPlayer + " wins.");
        this.add(outcomeLabel);
    }
}