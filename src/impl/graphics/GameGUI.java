import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
public class GameGUI extends JFrame {
    private static final int WINDOWWIDTH = 1200;
    private static final int WINDOWHEIGHT = 1000;
    private String player;
    private Dice dice = new Dice();
    private Game game;
    private Client client;
    private JButton rollButtonP1;
    private JLabel rollAmountP1;
    private JLabel rollAmountP2;
    private JButton rollButtonP2;
    private GraphicsButton[][] buttonArray;
    private GraphicsTile[][] componentsArray;
    private JLabel scoreP1;
    private JLabel scoreP2;

    private boolean networkPlay = false;

    // sets up the game screen on first run.
    public GameGUI(Game game) {
        this.game = game;
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH,WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        addComponents();
        setVisible(true);
    }
    public GameGUI(Client client) {
        this.networkPlay = true;
        this.client = client;
        this.setLayout(null);
        this.setSize(new Dimension(WINDOWWIDTH,WINDOWHEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Royal Game of Ur");
        addComponents();
        setVisible(true);
    }

    // adds all required components to the screen.
    public void addComponents() {
        componentsArray = new GraphicsTile[3][8];
        buttonArray = new GraphicsButton[3][8];
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

        // create roll buttons and add them to the screen.
        rollButtonP1 = new JButton("ROLL");
        rollButtonP1.setBounds((WINDOWWIDTH/4)-200, (WINDOWHEIGHT/2)-200, 200, 75);
        rollAmountP1 = new JLabel("0");
        rollAmountP1.setBounds((WINDOWWIDTH/4)-100, (WINDOWHEIGHT/2)-100, 200, 75);
        rollButtonActionListener(rollButtonP1,rollAmountP1);
        this.add(rollButtonP1);
        this.add(rollAmountP1);

        rollButtonP2 = new JButton("ROLL");
        rollButtonP2.setBounds((WINDOWWIDTH/4)*3, (WINDOWHEIGHT/2)-200, 200, 75);
        rollAmountP2 = new JLabel("0");
        rollAmountP2.setBounds((WINDOWWIDTH/4)*3+100, (WINDOWHEIGHT/2)-100, 200, 75);
        rollButtonActionListener(rollButtonP2,rollAmountP2);
        this.add(rollButtonP2);
        this.add(rollAmountP2);

        scoreP1 = new JLabel("Score: 7-0");
        scoreP1.setBounds((WINDOWWIDTH/4)-200, (WINDOWHEIGHT/2), 200, 75);
        this.add(scoreP1);
        scoreP2 = new JLabel("Score: 7-0");
        scoreP2.setBounds((WINDOWWIDTH/4)*3, (WINDOWHEIGHT/2), 200, 75);
        this.add(scoreP2);
    }

    public void changePlayerTurn(String player) {
        this.player = player;
        if (player.equals("P1")) {
            rollButtonP1.setVisible(true);
           // rollAmountP1.setVisible(true);
            rollButtonP2.setVisible(false);
            //rollAmountP2.setVisible(false);
        } else {
            rollButtonP1.setVisible(false);
           // rollAmountP1.setVisible(false);
            rollButtonP2.setVisible(true);
           // rollAmountP2.setVisible(true);
        }
    }

    public void updateRollLabel(String player, int rollAmount) {
        if (player.equals("P1")) {
            rollAmountP1.setText(rollAmount+"");
        } else {
            rollAmountP2.setText(rollAmount+"");
        }
    }

    // For singleplayer and multiplayer (network) game modes
    public void disableP2() {
        rollButtonP2.setEnabled(false);
    }

    // For singleplayer and multiplayer (network) game modes
    public void switchP1RollButton(boolean switcher) {
        rollButtonP1.setEnabled(switcher);
    }

    public void editP2Roll(int roll) {
        rollAmountP2.setText("" + roll);
    }

    public void rollButtonActionListener(JButton rollbutton, JLabel rollAmountText) {
        rollbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // depending on the status of that button (waiting to select a move) / already selected move.
                // some method will then need to be called which will change which other buttons are visible.
                int roll = dice.roll();
                SwingUtilities.invokeLater(() -> {
                    rollAmountText.setText("" + roll);
                });
                if (networkPlay) {
                    Client.rollAmount = roll;
                    Client.rollPressed = true;
                } else {
                    game.rollAmount = roll;
                    game.rollPressed = true;
                    // THESE VALUES ARE EVEN UPDATE HERE, BUT NOT IN THE ACTUAL GAME OBJECT
                }

                System.out.println("ROLLED");

                // make attribute in game call roll amount, then make a method to update it, this is called here to update the roll amonut.
                SwingUtilities.invokeLater(() -> {
                    rollbutton.setEnabled(false);
                });
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
                // pass tile which was selected and move position to game.

                // if button is both chip and move then do the extend check, then do the checks below
                // extended check: 
                // 
                if (button.checkIsBothSelection()) {
                    // check to see if the player is trying to move a chip here first:
                    // if moveFromTile is selected, then this is true.
                    if (buttonArray[button.getMoveFromStrip()][button.getMoveFromLocation()].isSelected()) {
                        if (networkPlay) {
                            sendClientMoveInfo(button);
                        } else { 
                            sendMoveInformation(button);
                        }
                    } else {
                        // button was clicked to select chip
                        if (GraphicsButton.tileSelected) {
                            // clear all moveselection tiles, make sure they are no selectable.
                            resetChipSelection();
                        }
                        // make this chip button selected
                        button.setSelected(true);
                        GraphicsButton.tileSelected = true;
                        if (!buttonArray[button.getMoveButtonStrip()][button.getMoveButtonLocation()].checkIsBothSelection()) {
                            buttonArray[button.getMoveButtonStrip()][button.getMoveButtonLocation()].setButtonSelectable();
                        }
                    }
                } else if (button.checkIsChipSelection()) {
                    if (GraphicsButton.tileSelected) {
                        // make sure all moveselections are not selectable and all chip ones are selectable.
                        // revert to orignal selecetion.
                        resetChipSelection();
                    }
                    button.setSelected(true);
                    button.setButtonFutureSelectable();
                    GraphicsButton.tileSelected = true;
                    // turn the next non selectable button to selectable.
                    if(!buttonArray[button.getMoveButtonStrip()][button.getMoveButtonLocation()].checkIsBothSelection()) {
                        buttonArray[button.getMoveButtonStrip()][button.getMoveButtonLocation()].setButtonSelectable();
                    }
                } else if (button.checkIsMoveSelection()) {
                    // send game the previous tile to be moved, and the position its moving to.
                    System.out.println("MOVED A PIECE!");
                    sendMoveInformation(button);
                }
            }
        });
    }
    // this method is called if a button is already selected and the player selected another chip
    // this will reset all the selected and selectable properties of the buttons.
    public void resetChipSelection() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if (buttonArray[i][j].checkIsBothSelection()) {
                    buttonArray[i][j].setSelected(false);
                }
                if (buttonArray[i][j].checkIsChipSelection()) {
                    buttonArray[i][j].setButtonSelectable();
                    buttonArray[i][j].setSelected(false);
                } else if(buttonArray[i][j].checkIsMoveSelection()) {
                    buttonArray[i][j].setButtonFutureSelectable();
                    buttonArray[i][j].setSelected(false);
                }
            }
        }
    }

    public void sendMoveInformation(GraphicsButton button) {
        game.move[0] = button.getMoveFromStrip();
        game.move[1] = button.getMoveFromLocation();
        game.move[2] = button.getMoveStrip();
        game.move[3] = button.getMoveLocation();
        game.moveSelected = true;
    }

    public void sendClientMoveInfo(GraphicsButton button) {
        client.info[0] = String.valueOf(button.getMoveFromStrip());
        client.info[1] = String.valueOf(button.getMoveFromLocation());
        client.info[2] = String.valueOf(button.getMoveStrip());
        client.info[3] = String.valueOf(button.getMoveLocation());
        client.moveSelected = true;
    } 

    public void closeFrame() {
        this.dispose();
    }

    public void updateSelectableTiles(List<int[]> currentMovable, List<int[]> futureMovable) {
        for (int i = 0; i < currentMovable.size(); i++) {
            // if current movable is -1. make sure to print token there
            // add token image if you can add a token to the screen
            if (currentMovable.get(i)[1]==-1) {
                componentsArray[currentMovable.get(i)[0]][4].updateImage(1, player,true);
            }
            
            System.out.println(currentMovable.get(i)[0] + " " + currentMovable.get(i)[1]);
            System.out.println(futureMovable.get(i)[0] + " " + futureMovable.get(i)[1]);
            int[] currentMovePos = getButtonArrayPosition(currentMovable.get(i));
            int[] futureMovePos = getButtonArrayPosition(futureMovable.get(i));
            // checks to correctly set a tile to both a selection and move tile, if a chip can be moved to and from this position.
            if(buttonArray[currentMovePos[0]][currentMovePos[1]].checkIsMoveSelection()) {
                buttonArray[currentMovePos[0]][currentMovePos[1]].setBothSelectableAndFutureSelectable();
            } else {
                buttonArray[currentMovePos[0]][currentMovePos[1]].setButtonSelectable();
            }
            buttonArray[currentMovePos[0]][currentMovePos[1]].setChipButtonsMoveButton(futureMovePos[0], futureMovePos[1]);
            if(buttonArray[futureMovePos[0]][futureMovePos[1]].checkIsChipSelection()){
                buttonArray[futureMovePos[0]][futureMovePos[1]].setBothSelectableAndFutureSelectable();;
            } else {
                buttonArray[futureMovePos[0]][futureMovePos[1]].setButtonFutureSelectable();
            }
            buttonArray[futureMovePos[0]][futureMovePos[1]].setMoveToLocation(futureMovePos[0], futureMovable.get(i)[1]);
            buttonArray[futureMovePos[0]][futureMovePos[1]].setMoveFromLocation(currentMovePos[0], currentMovable.get(i)[1]);
        }
    }
    // converts position of tile in strips to a position in the graphicbutton array.
    public int[] getButtonArrayPosition(int[] stripPlace) {
        int[] positionInArray = new int[2];
        positionInArray[0] = stripPlace[0];
        int[] playerStripPos = {3,2,1,0};
        int[] playerStripBottonPos = {7,6};
        if (stripPlace[0]==0 || stripPlace[0] ==2) {
            if (stripPlace[1]==-1) {
                positionInArray[1] = 4;
            } else if(stripPlace[1]==6) {
                positionInArray[1] = 5;
            } else if(stripPlace[1] >= 0 && stripPlace[1] <= 3) {
                positionInArray[1] = playerStripPos[stripPlace[1]];
            } else {
                positionInArray[1] = playerStripBottonPos[stripPlace[1]-4];
            }
        } else {
            positionInArray[1] = stripPlace[1];
        }
        return positionInArray;
    }

    public void updateScore(Counter counter) {
        scoreP1.setText("Score: " + counter.getP1Counter() + "-" + counter.getP1Score());
        scoreP2.setText("Score: " + counter.getP2Counter() + "-" + counter.getP2Score());
    }

    public void updateBoard(Board currentBoard) {
        // this will update the sprites on the board after a move has been made.
        // update side strips:
        Tile[] p1Strip = currentBoard.getBoardStrip(0);
        Tile[] p2Strip = currentBoard.getBoardStrip(2);
        Tile[] middleStrip = currentBoard.getBoardStrip(1);
        int[] inversedValues =  {3,2,1,0};
        int[] inversedValuesBottom = {5,4};
        //p1Strip
        for (int i = 0; i < 8; i++) {
            if(i >= 0 && i <= 3) {
                componentsArray[0][i].updateImage(p1Strip[inversedValues[i]].getChip().getAmn(), p1Strip[inversedValues[i]].getChip().getOwnership(),false);
            } else if (i >= 6) {
                componentsArray[0][i].updateImage(p1Strip[inversedValuesBottom[i-6]].getChip().getAmn(),p1Strip[inversedValuesBottom[i-6]].getChip().getOwnership(),false);
            } else {
                componentsArray[0][i].updateImage(0, "none",false);
            }
        }
        //p2Strip
        for (int i = 0; i < 8; i++) {
            if(i >= 0 && i <= 3) {
                componentsArray[2][i].updateImage(p2Strip[inversedValues[i]].getChip().getAmn(),p2Strip[inversedValues[i]].getChip().getOwnership(),false);
            } else if (i >= 6) {
                componentsArray[2][i].updateImage(p2Strip[inversedValuesBottom[i-6]].getChip().getAmn(), p2Strip[inversedValuesBottom[i-6]].getChip().getOwnership(),false);
            } else {
                componentsArray[2][i].updateImage(0, "none",false);
            }
        }
        //middle string
        for (int i = 0; i < 7; i++) {
            componentsArray[1][i].updateImage(middleStrip[i].getChip().getAmn(), middleStrip[i].getChip().getOwnership(),false);
        }
        // reset the properties of the buttons.
        for (GraphicsButton[] buttonStrip : buttonArray) {
            for (GraphicsButton button : buttonStrip) {
                button.resetButton();
            }
        }
    }
}
