import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * {@code ErrorWindowGUI} extends {@link JFrame} to provide a graphical interface for displaying error messages to the user.
 * Specifically, this class is designed to inform the user of a disconnection error during a game. It displays a
 * non-editable text field within a window, conveying a disconnection message to the user.
 */
public class ErrorWindowGUI extends JFrame {

     /**
     * Constructs an {@code ErrorWindowGUI} window with a fixed title, size, and a predefined error message.
     * The window is initialized with a {@link JTextField} containing the message "You have been disconnected!",
     * indicating a connection error to the user.
     */
    public ErrorWindowGUI() {
        setTitle("Game Connection Error"); // Sets the title of the window.

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Specifies the operation that will happen by default when the user initiates a "close" on this frame.
        setSize(300, 100); // Sets the window size.
        setLocationRelativeTo(null); // Centers the window on the screen.

        JTextField textField = new JTextField("You have been disconnected!"); // Creates a text field for displaying the error message.
        textField.setEditable(false); // Makes the text field non-editable.

        getContentPane().add(textField); // Adds the text field to the window's content pane.
    }

    /**
     * Makes the window visible to display the error message to the user.
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
     * Creates and displays an {@code ErrorWindowGUI} window with a predefined error message. This static method
     * ensures that the window is created and displayed on the Event Dispatch Thread (EDT) to comply with Swing's threading policy.
     *
     * @return The {@code ErrorWindowGUI} instance created and displayed.
     */
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
