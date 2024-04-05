import javax.swing.SwingUtilities;

/**
 * The {@code CS1006UrGame} class serves as the entry point for a graphical game application. It uses
 * {@link SwingUtilities#invokeLater} to ensure that the game's user interface is created and updated
 * on the Event Dispatch Thread (EDT), adhering to Swing's single-threaded GUI framework. The main method
 * initiates the game by displaying the start menu to the user.
 */
public class CS1006UrGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StartMenuGUI();
            }
        });
    }
}
