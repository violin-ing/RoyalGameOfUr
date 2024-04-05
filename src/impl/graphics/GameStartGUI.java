import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
// render this on a new thread 
public class GameStartGUI extends JFrame {
    private static final int WINDOWWIDTH = 500;
    private static final int WINDOWHEIGHT = 500;
    private boolean muliplayer, network;
    private static Counter counter = new Counter();
    private static Board currentBoard = new Board(counter);
    private static Dice dice = new Dice();
    public static GameGUI gameGUI;

    public GameStartGUI(boolean muliplayer, boolean network) {
        this.muliplayer = muliplayer;
        this.network = network;
        addComponents();
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH,WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        setVisible(true);
    }

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
            Game game = new Game(currentBoard, counter, dice, muliplayer);
            GameGUI gui = new GameGUI(game);
            game.setGameGUI(gui);
            Thread gamThread = new Thread(() -> {
                game.start();
            });
            gamThread.start();
        }
    }

    public void addComponents() {
        JButton startButton = new JButton("Start Game");
        JLabel option = new JLabel();
        if (muliplayer) {
            option.setText("Multiplayer (Local) Selected");
        } else if (network) {
            option.setText("Multiplayer (Network) Selected");
        } else {
            option.setText("Singleplayer Selected");;
        }
        option.setBounds((WINDOWWIDTH/2)-100, WINDOWHEIGHT/2-100, 200, 100);
        startButton.setBounds((WINDOWWIDTH/2)-100,WINDOWHEIGHT/2,200,100);
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

    public void closeFrame() {
        this.dispose();
    }
}
