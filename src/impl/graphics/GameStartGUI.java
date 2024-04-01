import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
//TODO: make interface for frame
// render this on a new thread 
public class GameStartGUI extends JFrame {
    private static final int WINDOWWIDTH = 500;
    private static final int WINDOWHEIGHT = 500;
    private boolean muliplayer;

    public GameStartGUI(boolean muliplayer) {
        this.muliplayer = muliplayer;
        addComponents();
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH,WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        setVisible(true);
    }

    public void startGame() {
        closeFrame();
        //new GameGUI();
    }

    public void addComponents() {
        JButton startButton = new JButton("Start Game");
        JLabel option = new JLabel();
        if (muliplayer) {
            option.setText("Multiplayer Selected");
        } else {
            option.setText("Singleplayer Selected");
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
