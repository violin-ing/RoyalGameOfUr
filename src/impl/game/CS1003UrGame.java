import javax.swing.SwingUtilities;

public class CS1003UrGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StartMenuGUI();
            }
        });
    }
}
