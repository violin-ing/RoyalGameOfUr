package impl.graphics;
import java.awt.Dimension;

import javax.swing.*;
// TODO: make interface for frame
// render this on a new thread 
public class GameGUI extends JFrame {
    private static final int WINDOWWIDTH = 1200;
    private static final int WINDOWHEIGHT = 1000;
    private static final int BLOCKDIMENSION = 100;

    public GameGUI() {
        addComponents();
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH,WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        setVisible(true);
    }

    public void addComponents() {
        JButton[][] componentsArray = new JButton[3][8];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 8; y++) {
                componentsArray[x][y] = new JButton(x + "" + y);
                componentsArray[x][y].setBounds((WINDOWWIDTH/2)+(x*BLOCKDIMENSION)-150,y*BLOCKDIMENSION+75,BLOCKDIMENSION,BLOCKDIMENSION);
                this.add(componentsArray[x][y]);
            }
        }
        //TODO: maybe add methods to 
        // SET BOUNDS ON TEXT?!?!
        JButton rollButtonP1 = new JButton("ROLL");
        rollButtonP1.setBounds((WINDOWWIDTH/4)-200, (WINDOWHEIGHT/2)-200, 200, 75);
        this.add(rollButtonP1);
        JLabel rollAmountP1 = new JLabel("0");
        rollAmountP1.setBounds((WINDOWWIDTH/4)-100, (WINDOWHEIGHT/2)-100, 200, 75);
        this.add(rollAmountP1);
        JButton rollButtonP2 = new JButton("ROLL");
        rollButtonP2.setBounds((WINDOWWIDTH/4)*3, (WINDOWHEIGHT/2)-200, 200, 75);
        this.add(rollButtonP2);
        JLabel rollAmountP2 = new JLabel("0");
        rollAmountP2.setBounds((WINDOWWIDTH/4)*3+100, (WINDOWHEIGHT/2)-100, 200, 75);
        this.add(rollAmountP2);
        JLabel scoreP1 = new JLabel("Score: 7-0");
        scoreP1.setBounds((WINDOWWIDTH/4)-200, (WINDOWHEIGHT/2), 200, 75);
        this.add(scoreP1);
        JLabel scoreP2 = new JLabel("Score: 7-0");
        scoreP2.setBounds((WINDOWWIDTH/4)*3, (WINDOWHEIGHT/2), 200, 75);
        this.add(scoreP2);
    }

    public void closeFrame() {
        this.dispose();
    }
}
