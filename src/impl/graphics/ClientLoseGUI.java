import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * {@code ClientLoseGUI} extends {@link JFrame} and displays a message indicating that the client has lost the game.
 * It provides a simple graphical interface to inform the player of the game outcome through a non-editable text field.
 */
public class ClientLoseGUI extends JFrame {
     private static String msg; // Message to be displayed in the window.

    /**
     * Constructs a {@code ClientLoseGUI} window with a predefined title and size. It initializes the window
     * with a text field containing a message passed through the {@code display} static method.
     */
     public ClientLoseGUI() {
          setTitle("Game Over"); // Sets the window title.

          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Specifies the operation when the window is closed.
          setSize(450, 100); // Sets the window size.
          setLocationRelativeTo(null); // Centers the window on the screen.

          JTextField textField = new JTextField(msg); // Creates a text field with the loss message.
          textField.setEditable(false); // Makes the text field non-editable.

          getContentPane().add(textField); // Adds the text field to the window's content pane.
    }

    /**
     * Makes the window visible to the user.
     */
     public void showWindow() {
          setVisible(true);
     }

     /**
     * Closes and disposes of the window.
     */
     public void closeWindow() {
          setVisible(false); 
          dispose();
     }

     /**
     * Creates and displays a {@code ClientLoseGUI} window with a specific message. This method ensures that
     * the window is created and shown on the Event Dispatch Thread (EDT) to maintain thread safety with Swing components.
     *
     * @param message The message to be displayed in the window.
     * @return The {@code ClientLoseGUI} instance created and displayed.
     */
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
