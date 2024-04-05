import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Represents the graphical user interface for starting the game.
 * Extends JFrame to create the game start window.
 */
public class GameStartGUI extends JFrame {
    private static final int WINDOWWIDTH = 500;
    private static final int WINDOWHEIGHT = 500;
    private boolean multiplayer; // Flag indicating if multiplayer mode is selected
    private boolean network; // Flag indicating if network mode is selected
    private static Counter counter = new Counter(); // Counter object for game state
    private static Board currentBoard = new Board(counter); // Board object for the current game board
    private static Dice dice = new Dice(); // Dice object for dice rolling
    public static GameGUI gameGUI; // GameGUI object for game interface

    /**
     * Constructs a GameStartGUI object with specified multiplayer and network mode settings.
     * @param multiplayer Indicates if multiplayer mode is selected.
     * @param network Indicates if network mode is selected.
     */
    public GameStartGUI(boolean multiplayer, boolean network) {
        this.multiplayer = multiplayer;
        this.network = network;
        addComponents();
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH, WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        setVisible(true);
    }

    /**
     * Starts the game based on the selected mode.
     */
    public void startGame() {
        if (network) {
            Counter counter = new Counter();
            Board currentBoard = new Board(counter);
            Dice dice = new Dice();
            Client client = new Client(counter, currentBoard, dice);
            GameGUI gameGUI = new GameGUI(client);
            client.setGUI(gameGUI);
            Thread cliThread = new Thread(() -> {
                client.initiateMatch();
            });
            cliThread.start();
        } else {
            counter = new Counter();
            currentBoard = new Board(counter);
            dice = new Dice();
            Game game = new Game(currentBoard, counter, dice, multiplayer);
            GameGUI gui = new GameGUI(game);
            game.setGameGUI(gui);
            Thread gamThread = new Thread(() -> {
                game.start();
            });
            gamThread.start();
        }
    }

    /**
     * Adds components to the game start window.
     */
    public void addComponents() {
        JButton startButton = new JButton("Start Game");
        JLabel option = new JLabel();
        if (multiplayer) {
            option.setText("Multiplayer (Local) Selected");
        } else if (network) {
            option.setText("Multiplayer (Network) Selected");
        } else {
            option.setText("Singleplayer Selected");
        }
        option.setBounds((WINDOWWIDTH / 2) - 100, WINDOWHEIGHT / 2 - 100, 200, 100);
        startButton.setBounds((WINDOWWIDTH / 2) - 100, WINDOWHEIGHT / 2, 200, 100);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        startGame();
                        return null;
                    }

                    @Override
                    protected void done() {
                        // Close the start frame after the background task completes
                        closeFrame();
                    }
                }.execute();
            }
        });
        this.add(startButton);
        this.add(option);
    }

    /**
     * Closes the game start window.
     */
    public void closeFrame() {
        this.setVisible(false);
        this.dispose();
    }
}
