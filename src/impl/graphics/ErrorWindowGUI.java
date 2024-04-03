import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class ErrorWindowGUI extends JFrame {

    public ErrorWindowGUI() {
        setTitle("Game Connection Error");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLocationRelativeTo(null); 

        JTextField textField = new JTextField("You have been disconnected!");
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

    public static ErrorWindowGUI display() {
        final ErrorWindowGUI[] frameHolder = new ErrorWindowGUI[1];
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frameHolder[0] = new ErrorWindowGUI();
                frameHolder[0].showWindow();
            }
        });
        return frameHolder[0];
    }
}
