import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class ClientWinGUI extends JFrame {

    public ClientWinGUI() {
        setTitle("Game Over");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLocationRelativeTo(null); 

        JTextField textField = new JTextField("You have lost!");
        textField.setEditable(false); 

        getContentPane().add(textField);
    }

    public void showWindow() {
        setVisible(true);
    }
    
    public void closeWindow() {
        setVisible(false); 
        dispose();
    }

    public static ClientWinGUI display() {
        final ClientWinGUI[] frameHolder = new ClientWinGUI[1];
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frameHolder[0] = new ClientWinGUI();
                frameHolder[0].showWindow();
            }
        });
        return frameHolder[0];
    }
}
