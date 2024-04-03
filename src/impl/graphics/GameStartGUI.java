import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
// TODO: make interface for frame
// render this on a new thread 
public class GameStartGUI extends JFrame {
    private static final int WINDOWWIDTH = 500;
    private static final int WINDOWHEIGHT = 500;
    private boolean muliplayer, network;
    private static Counter counter;
    private static Board currentBoard;
    private static Dice dice;

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
        counter = new Counter();
        currentBoard = new Board(counter);
        dice = new Dice();
        Game game = new Game(currentBoard, counter, dice, muliplayer);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GameGUI gui = new GameGUI(game);
                game.setGameGUI(gui);
                game.start();
            }
        });
        this.closeFrame();
    }

    public void addComponents() {
        JButton startButton = new JButton("Start Game");
        JLabel option = new JLabel();
        if (muliplayer) {
            option.setText("Multiplayer (Local) Selected");
        } else if (network) {
            option.setText("Multiplayer (Network) Selected");
            Client client = new Client();
            GameGUI gameGUI = new GameGUI(client);
            client.setGUI(gameGUI);
            client.initiateMatch(gameGUI);
        } else {
            option.setText("Singleplayer Selected");;
        }
        option.setBounds((WINDOWWIDTH/2)-100, WINDOWHEIGHT/2-100, 200, 100);
        startButton.setBounds((WINDOWWIDTH/2)-100,WINDOWHEIGHT/2,200,100);
        startButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        this.add(startButton);
        this.add(option);
    }

    public void closeFrame() {
        this.dispose();
    }
}
