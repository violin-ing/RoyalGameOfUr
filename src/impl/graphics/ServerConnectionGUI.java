import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class ServerConnectionGUI extends JFrame {

    public ServerConnectionGUI() {
        setTitle("Server Connection");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLocationRelativeTo(null); 

        JTextField textField = new JTextField("Connecting to server...");
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

    public static ServerConnectionGUI display() {
        final ServerConnectionGUI[] frameHolder = new ServerConnectionGUI[1];
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frameHolder[0] = new ServerConnectionGUI();
                frameHolder[0].showWindow();
            }
        });
        return frameHolder[0];
    }
}
