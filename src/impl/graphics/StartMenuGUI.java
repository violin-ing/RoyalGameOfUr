import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * StartMenuGUI start menu to select player options (multiplayer, local multiplayer, or singleplayer)
 */
public class StartMenuGUI extends JFrame {
    private static final int WINDOWWIDTH = 500;
    private static final int WINDOWHEIGHT = 800;
    /**
     * StartMenuGUI consturctor, intilises display options.
     */
    public StartMenuGUI() {
        addComponents();
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH,WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        setVisible(true);
    }
    /**
     * closes the frame, and starts a GameStartGUI window.
     * @param muliplayer takes in boolean multiplayer if it was selected
     * @param network takes in boolean network if selected.
     */
    public void initialiseGameStart(boolean muliplayer, boolean network) {
        closeFrame();
        System.out.println(muliplayer + " SELECTED");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameStartGUI(muliplayer, network);
            }
        });
        // pass some params somewhere if muliplayers (would also change UI)
    }
    /**
     * adds all swing components to the screen, this includes the Singleplayer, Multiplayer options.
     */
    public void addComponents() {
        JButton startSingleButton = new JButton("Singleplayer");
        JButton startMultiButton = new JButton("Multiplayer (Local)");
        JButton startNetwkButton = new JButton("Multiplayer (Network)");
        startSingleButton.setBounds((WINDOWWIDTH/2)-100,WINDOWHEIGHT/2-200,200,100);
        startMultiButton.setBounds((WINDOWWIDTH/2)-100, WINDOWHEIGHT/2, 200, 100);
        startNetwkButton.setBounds((WINDOWWIDTH/2)-100, WINDOWHEIGHT/2+200, 200, 100);
        addStartButtonAction(startSingleButton, false, false);
        addStartButtonAction(startMultiButton, true, false);
        addStartButtonAction(startNetwkButton, false, true);
        this.add(startSingleButton);
        this.add(startMultiButton);
        this.add(startNetwkButton);
    }
    /**
     * adds actionlisteners to buttons to start game and change multiplayer and network options.
     * @param button
     * @param muliplayer
     * @param network
     */
    public void addStartButtonAction(JButton button,boolean muliplayer, boolean network) {
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                initialiseGameStart(muliplayer, network);
            }
        });
    }
    /**
     * closes the frame once option has been selected.
     */
    public void closeFrame() {
        this.setVisible(false);
        this.dispose();
    }
}
