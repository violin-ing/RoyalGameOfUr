import javax.swing.SwingUtilities;

public class CS1003UrGame {
    public static void main(String[] args) {
        // gameGUI on a new thread?
        //GameGUI gameGUI = new GameGUI(game);
        //game.setGameGUI(gameGUI);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StartMenuGUI();
            }
        });
    }
}
