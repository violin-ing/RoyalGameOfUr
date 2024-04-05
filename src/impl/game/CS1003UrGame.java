import javax.swing.SwingUtilities;

/**
 * The {@code CS1003UrGame} class serves as the entry point for a graphical game application. It uses
 * {@link SwingUtilities#invokeLater} to ensure that the game's user interface is created and updated
 * on the Event Dispatch Thread (EDT), adhering to Swing's single-threaded GUI framework. The main method
 * initiates the game by displaying the start menu to the user.
 */
public class CS1003UrGame {
    /**
     * The main method that serves as the entry point of the application. It schedules the creation and
     * display of the game's start menu on the Swing event dispatch thread to ensure thread-safe operation.
     * 
     * @param args Command line arguments passed to the program (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            /**
             * Runs the code to create and display the game's start menu GUI. This method is executed
             * on the Event Dispatch Thread (EDT) to ensure compliance with Swing's single-thread model.
             */
            public void run() {
                new StartMenuGUI();
            }
        });
    }
}
