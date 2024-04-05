import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * {@code ClientWinGUI} extends {@link JFrame} to provide a graphical user interface for displaying a victory message to the player.
 * It shows a non-editable text field within a window to convey the message that the player has won the game. The class includes
 * methods for showing and closing the window, and it uses {@link SwingUtilities#invokeLater} to ensure that these GUI operations
 * are performed on the Event Dispatch Thread (EDT) for thread safety.
 */
public class ClientWinGUI extends JFrame {
     private static String msg;

     /**
     * Constructs a {@code ClientWinGUI} window with a fixed title, size, and location.
     * It initializes the window with a {@link JTextField} containing the victory message.
     */
     public ClientWinGUI() {
          setTitle("Game Over"); // Sets the title of the window.

          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Specifies the operation when the window is closed.
          setSize(450, 100); // Sets the window size.
          setLocationRelativeTo(null); // Centers the window on the screen.

          JTextField textField = new JTextField(msg); // Creates a text field for displaying the victory message.
          textField.setEditable(false); // Makes the text field non-editable.

          getContentPane().add(textField); // Adds the text field to the window's content pane.
    }

    /**
     * Makes the window visible to display the victory message to the player.
     */
     public void showWindow() {
          setVisible(true);
     }

     /**
     * Closes the window and releases its resources.
     */
     public void closeWindow() {
          setVisible(false); 
          dispose();
     }

     /**
     * Creates and displays a {@code ClientWinGUI} window with a specified victory message. This static method ensures that
     * the window is created and displayed on the Event Dispatch Thread (EDT) to comply with Swing's threading policy.
     *
     * @param message The victory message to be displayed in the window.
     * @return The {@code ClientWinGUI} instance created and displayed.
     */
     public static ClientWinGUI display(String message) {
          msg = message;
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
