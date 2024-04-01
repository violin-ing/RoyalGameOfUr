import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
// TODO: make interface for frame
// render this on a new thread 
public class GameGUI extends JFrame {
    private static final int WINDOWWIDTH = 1200;
    private static final int WINDOWHEIGHT = 1000;
    private static final int BLOCKDIMENSION = 100;
    private String player;
    private Dice dice = new Dice();
    private Game game;
    private JButton rollButtonP1;
    private JButton rollButtonP2;

    // sets up the game screen on first run.
    public GameGUI(Game game) {
        addComponents();
        this.game = game;
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH,WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        setVisible(true);
    }
    // adds all required components to the screen.
    public void addComponents() {
        GraphicsTile[][] componentsArray = new GraphicsTile[3][8];
        GraphicsButton[][] buttonArray = new GraphicsButton[3][8];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 8; y++) {
                // check if tile is a rosette tile.
                if((x == 0 && y == 0) || (x == 2 && y == 0) || (x==1 && y== 3) || (x==0 && y==6) || (x==2 && y==6)) {
                    componentsArray[x][y] = new GraphicsTile(x, y, true,false);
                // check if there is not tile
                } else if((x==0 && (y==4 || y==5)) || (x==2 && (y==4 || y==5))) {
                    componentsArray[x][y] = new GraphicsTile(x, y, false,true);
                } else {
                    // otherwise this is an empty normal tile
                    componentsArray[x][y] = new GraphicsTile(x, y, false,false);
                }
                // add button ontop of each panel.
                buttonArray[x][y] = new GraphicsButton(x, y);
                addButtonActionListener(buttonArray[x][y]);
                this.add(buttonArray[x][y]);
                this.add(componentsArray[x][y]);
            }
        }

        componentsArray[1][2].updateImage(3, "P2");
        buttonArray[1][2].setVisible(true);
        //TODO: maybe add methods to 
        // SET BOUNDS ON TEXT?!?!
        rollButtonP1 = new JButton("ROLL");
        rollButtonP1.setBounds((WINDOWWIDTH/4)-200, (WINDOWHEIGHT/2)-200, 200, 75);
        JLabel rollAmountP1 = new JLabel("0");
        rollAmountP1.setBounds((WINDOWWIDTH/4)-100, (WINDOWHEIGHT/2)-100, 200, 75);
        rollButtonActionListener(rollButtonP1,rollAmountP1);
        this.add(rollButtonP1);
        this.add(rollAmountP1);

        rollButtonP2 = new JButton("ROLL");
        rollButtonP2.setBounds((WINDOWWIDTH/4)*3, (WINDOWHEIGHT/2)-200, 200, 75);
        JLabel rollAmountP2 = new JLabel("0");
        rollAmountP2.setBounds((WINDOWWIDTH/4)*3+100, (WINDOWHEIGHT/2)-100, 200, 75);
        rollButtonActionListener(rollButtonP2,rollAmountP2);
        this.add(rollButtonP2);
        this.add(rollAmountP2);

        JLabel scoreP1 = new JLabel("Score: 7-0");
        scoreP1.setBounds((WINDOWWIDTH/4)-200, (WINDOWHEIGHT/2), 200, 75);
        this.add(scoreP1);
        JLabel scoreP2 = new JLabel("Score: 7-0");
        scoreP2.setBounds((WINDOWWIDTH/4)*3, (WINDOWHEIGHT/2), 200, 75);
        this.add(scoreP2);
    }

    public void changePlayerTurn(String player) {
        this.player = player;
        if (player.equals("P1")) {
            rollButtonP1.setVisible(true);
            rollButtonP2.setVisible(false);
        } else {
            rollButtonP1.setVisible(false);
            rollButtonP2.setVisible(true);
        }
    }


    public void rollButtonActionListener(JButton rollbutton, JLabel rollAmountText) {
        rollbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // depending on the status of that button (waiting to select a move) / already selected move.
                // some method will then need to be called which will change which other buttons are visible.
                int rollAmount = dice.roll();
                rollAmountText.setText("" + rollAmount);
                game.rollAmount = rollAmount;
                game.rollPressed = true;
                
                // make attribute in game call roll amount, then make a method to update it, this is called here to update the roll amonut.
            }
        });
    }

    public void addButtonActionListener(GraphicsButton button) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // depending on the status of that button (waiting to select a move) / already selected move.
                // some method will then need to be called which will change which other buttons are visible.
                System.out.println("CLICKED");
            }
        });
    }

    public void closeFrame() {
        this.dispose();
    }
}
