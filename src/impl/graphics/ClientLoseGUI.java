import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class ClientLoseGUI extends JFrame {
     private static String msg;

     public ClientLoseGUI() {
          setTitle("Game Over");

          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          setSize(300, 100);
          setLocationRelativeTo(null); 

          JTextField textField = new JTextField(msg);
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

     public static ClientLoseGUI display(String message) {
          msg = message;
          final ClientLoseGUI[] frameHolder = new ClientLoseGUI[1];
          SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                    frameHolder[0] = new ClientLoseGUI();
                    frameHolder[0].showWindow();
               }
          });
          return frameHolder[0];
     }
}