import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
//TODO: make interface for frame
// render this on a new thread 
public class StartMenuGUI extends JFrame {
    private static final int WINDOWWIDTH = 500;
    private static final int WINDOWHEIGHT = 500;
    public static void main(String[] args) {
        new StartMenuGUI();
    }

    public StartMenuGUI() {
        addComponents();
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH,WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        setVisible(true);
    }

    public void initiliseGameStart(boolean muliplayer) {
        closeFrame();
        new GameStartGUI(muliplayer);
        // pass some params somewhere if muliplayers (would also change UI)
    }

    public void addComponents() {
        JButton startSingleButton = new JButton("Singleplayer");
        JButton startMultiButton = new JButton("Multiplayer");
        startSingleButton.setBounds((WINDOWWIDTH/2)-100,WINDOWHEIGHT/2-200,200,100);
        startMultiButton.setBounds((WINDOWWIDTH/2)-100, WINDOWHEIGHT/2, 200, 100);
        addStartButtonAction(startSingleButton, false);
        addStartButtonAction(startMultiButton, true);
        this.add(startSingleButton);
        this.add(startMultiButton);
    }

    public void addStartButtonAction(JButton button,boolean muliplayer) {
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                initiliseGameStart(muliplayer);
            }
        });
    }

    public void closeFrame() {
        this.dispose();
    }
}
