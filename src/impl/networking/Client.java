import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.SwingUtilities;


 /**
 * The {@code Client} class is responsible for managing the client-side functionality
 * for a network-based 1v1 game. It handles discovering the game server through broadcast
 * messages, establishing a TCP connection for gameplay, sending and receiving game data,
 * and maintaining a heartbeat to indicate presence to the server. This class also interacts
 * with the game's GUI components to reflect the game state and player interactions.
 */
public class Client {
     public static final int DEFAULT_PORT = 6969; // Server game port
     private final static int HEARTBEAT_PORT = 42069; // Heartbeat port

     public static Board currentBoard; // The current state of the game board.
     public static Board futureBoard; // Future state of the game board, for planning purposes.
     public static Counter counter; // Keeps track of various game counters.
     public static Dice dice; // Represents the game's dice.
     public static GameGUI gui; // The graphical user interface for the game.

     public static int rollAmount; // The result of the last dice roll.
     public static boolean rollPressed = false; // Flag to indicate if the roll button has been pressed.
     public static boolean matchFound = false; // Flag to indicate if a match has been found.
     public static boolean moveSelected = false; // Flag to indicate if a move has been selected.

     private boolean myTurn; // Flag to indicate if it's the client's turn.

     public String[] info = new String[5]; // Array to hold information about game moves.

     /**
     * Constructs a new {@code Client} with the specified counter, current board, and dice.
     *
     * @param counterIn      The counter to be used by the client.
     * @param currentBoardIn The current game board.
     * @param diceIn         The dice to be used by the client.
     */
     public Client(Counter counterIn, Board currentBoardIn, Dice diceIn) {
          currentBoard = currentBoardIn;
          counter = counterIn;
          dice = diceIn;
          Game.networkPlay = true;
      }

     /**
     * Sets the GUI for the client.
     *
     * @param gameGUI The {@code GameGUI} to be used by the client.
     */
     public void setGUI(GameGUI gameGUI) {
          Client.gui = gameGUI;
     }

    /**
     * Initiates a 1v1 match by discovering the game server using a broadcast message,
     * establishing a TCP connection for game communication, and maintaining a heartbeat
     * to indicate active connection. This method also listens for server messages to
     * manage game state and handles user inputs to send game actions to the server.
     */
     public void initiateMatch() {
          try {
               Client.gui.disableP2();
               Client.gui.switchP1RollButton(false);

               // Connecting to server display
               // ServerConnectionGUI frame = ServerConnectionGUI.display();

               // Listen for server broadcast to discover the server
               DatagramSocket broadcastSocket = new DatagramSocket(DEFAULT_PORT);
               broadcastSocket.setBroadcast(true);
               byte[] buffer = new byte[1024];
               DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

               broadcastSocket.receive(packet);
               broadcastSocket.close();

               // Extract the server IP address from the broadcast message
               String serverIP = new String(packet.getData(), 0, packet.getLength()).trim();

               // Connect to the server using the discovered IP address
               int serverPort = DEFAULT_PORT;
               try (Socket socket = new Socket(serverIP, serverPort);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

                    // Send periodic heartbeats to server in case of connection loss
                    Thread heartbeatSender = new Thread(() -> {
                         String host = serverIP; 

                         try (DatagramSocket hbSkt = new DatagramSocket()) {
                              InetAddress address = InetAddress.getByName(host);

                              String message = "Heartbeat";
                              byte[] hbBfr = message.getBytes();

                              DatagramPacket hbPkt = new DatagramPacket(hbBfr, hbBfr.length, address, HEARTBEAT_PORT);

                              while (true) {
                                   hbSkt.send(hbPkt); // Send the heartbeat message
                                   Thread.sleep(5000); // Send heartbeat every 5s
                              }
                         } catch (Exception e) {
                              System.err.println("Client Exception: " + e.getMessage());
                         }
                    });

                    Thread serverListener = new Thread(() -> {
                         System.out.println("Listening for server msgs");
                         while (true) {
                              if (!myTurn) {
                                   try {
                                        String opponentPkt = in.readLine();
                                        if (opponentPkt != null) {
                                             System.out.println("from opponent: " + opponentPkt);
                                             String[] infoPkt = opponentPkt.split(",");
                                             if (infoPkt.length == 2) {
                                                  System.out.println("info length = 2");
                                                  myTurn = true;
                                             } else if (infoPkt.length == 6) {
                                                  System.out.println("info length = 6");
                                                  String rosetta = infoPkt[5];

                                                  if ("true".equals(rosetta)) {
                                                       myTurn = false;
                                                  } else {
                                                       myTurn = true;
                                                  }

                                                  Thread.sleep(500);

                                                  // Stream packet array into a usable int[] array
                                                  String[] moveStr = {infoPkt[1], infoPkt[2], infoPkt[3], infoPkt[4]};
                                                  int[] move = new int[4];

                                                  move[1] = Integer.parseInt(moveStr[1]);
                                                  move[3] = Integer.parseInt(moveStr[3]);

                                                  if (moveStr[0].equals("0")) {
                                                       move[0] = 2;
                                                  } else if (moveStr[0].equals("1")) {
                                                       move[0] = 1;
                                                  }
                                                  
                                                  if (moveStr[2].equals("0")) {
                                                       move[2] = 2;
                                                  } else if (moveStr[2].equals("1")) {
                                                       move[2] = 1;
                                                  }

                                                  if (move[3] >= 6 && (move[2] == 0 || move[2] == 2)){
                                                       myTurn = true;
                                                  } 

                                                  currentBoard.move(move, "P2");

                                                  gui.updateBoard(currentBoard);
                                                  gui.updateScore(counter);
                                                  
                                                  if (counter.getP2Score() == 7) {
                                                       gui.closeFrame();
                                                       ClientLoseGUI.display("You have lost the game!");
                                                       heartbeatSender.interrupt();
                                                       return;
                                                  }

                                                  continue;
                                             } else if (opponentPkt.equals("opponentdc")) {
                                                  gui.closeFrame();
                                                  ClientWinGUI.display("Opponent disconnected. You win!");
                                                  heartbeatSender.interrupt();
                                                  return;
                                             }
                                        } else {
                                             continue;
                                        }
                                   } catch (Exception e) {
                                        e.printStackTrace();
                                   }
                              } else {
                                   if (myTurn) {
                                        gui.changePlayerTurn("P1");
                                        
                                        rollPressed = false;
                                        moveSelected = false;
                                        myTurn = false;
                                        boolean rosetta = false;

                                        StringBuffer packetBuilder = new StringBuffer();
     
                                        while (!rollPressed) {
                                             try {
                                                  wait();
                                             } catch (Exception e) {
                                                  // IGNORE
                                             }  
                                        }
                                        rollPressed = false;
          
                                        System.out.println("TEST: Testing rolling functionality");
                                        int diceNum = rollAmount;
                                        System.out.println(rollAmount);
          
                                        String diceRoll = Integer.toString(diceNum);
                                        packetBuilder.append(diceRoll);
     
                                        if (diceNum == 0) {
                                             out.println("0,nil");
                                             System.out.println("sent nil packet");
                                             myTurn = false;
                                             rosetta = false;
                                             rollPressed = false;
                                             continue;
                                        } else {
                                             if (!availableMoves("P1", diceNum)) {
                                                  System.out.println("no moves avail");
                                                  packetBuilder.append(",nil");
                                                  out.println(packetBuilder.toString());
                                                  System.out.println("sent nil packet");
                                                  rosetta = false;
                                                  myTurn = false;
                                                  rollPressed = false;
                                                  continue;
                                             }
                                             while (!moveSelected) {
                                                  try {
                                                       wait();
                                                  } catch (Exception e) {
                                                       // IGNORE
                                                  }  
                                             }
                                             moveSelected = false;
     
                                             int[] move = Arrays.stream(info)
                                                  .limit(4)
                                                  .mapToInt(Integer::parseInt)
                                                  .toArray();
     
                                             currentBoard.move(move, "P1");
                                             System.out.println("update the board");
     
                                             SwingUtilities.invokeLater(new Runnable() {
                                                  public void run() {
                                                       gui.updateBoard(currentBoard);
                                                       gui.updateScore(counter);
                                                  }
                                                  });
     
                                             int newStrip = move[2];
                                             int newIndex = move[3];

                                             if (! (newIndex >= 6 && newStrip != 1)) {
                                                  Tile newTile = currentBoard.getBoardStrip(newStrip)[newIndex];
                                                  if (newTile.isRosetta()) {
                                                       info[4] = "true";
                                                       rosetta = true;
                                                  } else {
                                                       info[4] = "false";
                                                       rosetta = false;
                                                  }
                                             }
                                             for (int i = 0; i < info.length; i++) {
                                                  packetBuilder.append("," + info[i]);
                                             }
                                             out.println(packetBuilder.toString());
                                             System.out.println("sent move packet");
                                             System.out.println(packetBuilder.toString());
                                        }

                                        if (rosetta) {
                                             myTurn = true;
                                             rosetta = false;
                                             rollPressed = false;
                                             moveSelected = false;
                                             continue;
                                        }
          
                                        SwingUtilities.invokeLater(new Runnable() {
                                             public void run() {
                                                  gui.changePlayerTurn("P2");
                                                  gui.disableP2();
                                             }
                                         });
                                        
                                        if (counter.getP1Score() >= 7) {
                                             SwingUtilities.invokeLater(() -> {
                                                  gui.closeFrame();
                                             });
                                             ClientWinGUI.display("You have won the game!");
                                             heartbeatSender.interrupt();
                                             return;
                                        }
                                   }
                              }
                         }
                    });

                    heartbeatSender.start();

                    String startPacket;
                    startPacket = in.readLine();
                    if ("startfirst".equals(startPacket)) {
                         myTurn = true;
                    } else if (startPacket.equals("waitfirst")) {
                         myTurn = false;
                    }

                    serverListener.start();

                    while (true) {
                         try {
                              wait();
                         } catch (Exception e) {
                              // IGNORE
                         }  
                    }
               } catch (Exception e) {
                    e.printStackTrace();
               } 
          } catch (Exception e) {
               e.printStackTrace();
          }
     }

     /**
     * Checks if there are any available moves for the specified player and roll.
     *
     * @param player The player identifier ("P1" or "P2").
     * @param roll   The dice roll value.
     * @return {@code true} if there are possible moves, {@code false} otherwise.
     */
     public static boolean availableMoves(String player, int roll) {
          boolean possibleMoves;
          int currentPlayerCounter;
          
          System.out.println("Moving for network play");
          if (player.equals("P1")) {
               currentPlayerCounter = Client.counter.getP1Counter();
          } else {
               currentPlayerCounter = Client.counter.getP2Counter();
          }
  
          System.out.println("getting positions");
          List<int[]> currentMovablePositions = getCurrentMovablePositions(player, roll, currentBoard.identifyPieces(player), currentPlayerCounter);
          List<int[]> futurePositions = getFuturePositions(player, roll, currentMovablePositions);
          if (futurePositions.size()==0) {
               possibleMoves = false;
          } else {
               possibleMoves = true;
          }
          System.out.println("updating tiles");
          SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                    gui.updateSelectableTiles(currentMovablePositions, futurePositions);
               }
           });
  
          return possibleMoves;
      }

      /**
     * Checks for the current positions that the player can move to.
     *
     * @param player     The player identifier ("P1" or "P2").
     * @param roll  The dice roll value.
     * @param currentPositions     The player's current chip positions.
     * @param tileCounter     
     * @return A list with the possible positions the player can move. 
     */
      public static List<int[]> getCurrentMovablePositions(String player, int roll, List<int[]> currentPositions, int tileCounter) {
          List<int[]> currentMovablePositions = new ArrayList<>();
          int[] stringPos = new int[2];
  
          for (int[] stripAndPos : currentPositions) {
              if (isMoveable(player, roll, stripAndPos[1], stripAndPos[0])) {
                  currentMovablePositions.add(stripAndPos);
              }
          }
          
          if (player.equals("P1")) {
              if (tileCounter != 0) {
                  stringPos[0] = 0;
                  stringPos[1] = -1;
                  currentMovablePositions.add(stringPos);
              }
          } else {
              if (tileCounter !=0 ) {
                  stringPos[0] = 2;
                  stringPos[1] = -1;
                  currentMovablePositions.add(stringPos);
              }
          }
  
          return currentMovablePositions;
      }
  
     /**
     * Checks for the possible future positions on the GUI for after the player moves.
     *
     * @param player     The player identifier ("P1" or "P2").
     * @param roll  The dice roll value.
     * @param currentPositions     The player's current chip positions.
     * @return A list with the possible future positions. 
     */
      public static List<int[]> getFuturePositions(String player, int roll, List<int[]> currentMovablePositions) {
          List<int[]> futurePositions = new ArrayList<>();
          
          for (int[] piecePos : currentMovablePositions) {
              futurePositions.add(newPosition(piecePos, player, roll));
          }

          return futurePositions;
      }
      
     /**
     * Gets the new chip position for the player's last moved chip.
     *
     * @param stripPos     The player old chip's position.
     * @param player  The player identifier ("P1" or "P2").
     * @param roll     The dice roll value.
     * @return An int array with the new position of the player's chip
     */
      public static int[] newPosition(int[] stripPos, String player, int roll) {
          int[] newPos = new int[2];
          int strip = stripPos[0];
          int movePosition = stripPos[1];
          
          int checkTileAfter;
          if (strip == 1) {
              checkTileAfter = movePosition + roll;
              if (checkTileAfter > 7) {
                      checkTileAfter = (checkTileAfter - 4); // Position on new strip
                      strip = ("P1".equals(player)) ? 0 : 2;
              }
          } else {
              checkTileAfter = movePosition + roll;
              if (movePosition>=4) {
                  if (checkTileAfter>5) {
                      checkTileAfter=6;
                  }
              } else if (checkTileAfter > 3) {
                  checkTileAfter = (checkTileAfter - 4); // Position on new strip
                  strip = 1;
              }
          }
  
          newPos[0] = strip;
          newPos[1] = checkTileAfter;
  
          return newPos;
      }
  
     /**
     * Checks if a player's chip is moveable given their dice roll.
     *
     * @param player  The player identifier ("P1" or "P2").
     * @param roll     The dice roll value.
     * @param movePosition    The player's current chip position (index)
     * @param strip The player's current chip's strip number
     * @return An int array with the new position of the player's chip
     */
      private static boolean isMoveable(String player, int roll, int movePosition, int strip) {
          int checkTileAfter;
          if (strip == 1) {
              checkTileAfter = movePosition + roll;
              if (checkTileAfter > 7) {
                      checkTileAfter = (checkTileAfter - 4); // Position on new strip
                      strip = ("P1".equals(player)) ? 0 : 2;
              }
          } else {
              checkTileAfter = movePosition + roll;
              if (checkTileAfter > 3) {
                      checkTileAfter = (checkTileAfter - 4); // Position on new strip
                      strip=1;
              }
          }
  
          if (strip == 1) {
                  if (currentBoard.getBoardStrip(strip)[checkTileAfter].isRosetta()) {
                      String enemyPlayer = "P2";
                      if (currentBoard.getBoardStrip(strip)[checkTileAfter].getChip().getOwnership().equals("none")) {
                          return true;
                      } else if (currentBoard.getBoardStrip(strip)[checkTileAfter].getChip().getOwnership().equals(enemyPlayer)) {
                          return false;
                      }
                  }
          }
          return true;
      }
}
