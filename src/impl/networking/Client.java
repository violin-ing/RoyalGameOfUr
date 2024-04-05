import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.SwingUtilities;

/**
 * The {@code Client} class handles the client-side logic for connecting to a game server
 * for a 1v1 match, including discovering the server via a broadcast message, establishing
 * a connection, and handling game communication.
 * This class also manages sending periodic heartbeat messages to the server to indicate
 * that the client is still active and listening for messages from the server regarding
 * game state updates.
 */
public class Client {
     public static final int DEFAULT_PORT = 6969; // Server game port
     private final static int HEARTBEAT_PORT = 42069; // Heartbeat port

     public static Board currentBoard;
     public static Board futureBoard;
     public static Counter counter;
     public static Dice dice;
     public static GameGUI gui;

     public static int rollAmount;
     public static boolean rollPressed = false;
     public static boolean matchFound = false;
     public static boolean moveSelected = false;

     private boolean myTurn;

     public String[] info = new String[5];

     public Client(Counter counterIn, Board currentBoardIn, Dice diceIn) {
          currentBoard = currentBoardIn;
          counter = counterIn;
          dice = diceIn;
          Game.networkPlay = true;
      }

     public void setGUI(GameGUI gameGUI) {
          Client.gui = gameGUI;
     }

     /**
     * Initiates a 1v1 match by first discovering the game server via a broadcast message
     * and then establishing a TCP connection to participate in a match.
     * This method listens for server messages to manage game state and sends heartbeat
     * messages to the server to indicate that the client is still connected.
     * The method also listens for user input to send game actions to the server.
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
                              System.err.println("Error: " + e.getMessage());
                              e.printStackTrace();
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
                                                  System.out.println("\ninfo length = 2");
                                                  myTurn = true;
                                             } else if (infoPkt.length == 6) {
                                                  System.out.println("\ninfo length = 6");
                                                  String rosetta = infoPkt[5];

                                                  if ("true".equals(rosetta)) {
                                                       myTurn = false;
                                                  } else {
                                                       myTurn = true;
                                                  }

                                                  // Stream packet array into a usable int[] array
                                                  String[] moveStr = {infoPkt[1], infoPkt[2], infoPkt[3], infoPkt[4]};
                                                  int[] move = new int[4];

                                                  move[1] = Integer.parseInt(moveStr[1]);
                                                  move[3] = Integer.parseInt(moveStr[3]);

                                                  if (moveStr[0].equals("0")) {
                                                       move[0] = 2;
                                                  } else {
                                                       move[0] = 1;
                                                  }
                                                  
                                                  if (moveStr[2].equals("0")) {
                                                       move[2] = 2;
                                                  } else {
                                                       move[0] = 1;
                                                  }

                                                  currentBoard.move(move, "P2");

                                                  // System.out.println("hello there 1");

                                                  // SwingUtilities.invokeLater(new Runnable() {
                                                  //      public void run() {
                                                            gui.updateBoard(currentBoard);
                                                            gui.updateScore(counter);
                                                  //      }
                                                  // });

                                                  // System.out.println("hello there 1");
                                                  
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
                                        do {
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
          
                                             // System.out.println("diceNum String = " + diceRoll);
          
                                             if (diceNum == 0) {
                                                  out.println("0,nil");
                                                  System.out.println("sent nil packet");
                                                  myTurn = false;
                                                  rosetta = false;
                                                  rollPressed = false;
                                                  continue;
                                             } else {
                                                  // System.out.println("diceNum = " + diceRoll);
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
                                                  // System.out.println("WAITING FOR MOVE");
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
          
                                                  // INFORMATION TO SEND:
                                                  // 1. Chip's old position (strip + index)
                                                  // 2. Chip's new position (strip + index)
                                                  // 3. Rosetta boolean (of chip's new position)
                                                  Tile newTile = currentBoard.getBoardStrip(newStrip)[newIndex];
                                                  if (newTile.isRosetta()) {
                                                       info[4] = "true";
                                                       rosetta = true;
                                                  } else {
                                                       info[4] = "false";
                                                       rosetta = false;
                                                  }
                                                  
                                                  for (int i = 0; i < info.length; i++) {
                                                       packetBuilder.append("," + info[i]);
                                                  }
                                                  out.println(packetBuilder.toString());
                                                  System.out.println("sent move packet");
                                                  System.out.println(packetBuilder.toString());
                                                  // packetBuilder = {
                                                  //      diceRoll, 
                                                  //      oldStrip, 
                                                  //      oldIndex, 
                                                  //      newStrip, 
                                                  //      newIndex, 
                                                  //      "true"/"false"
                                                  // }
                                             }

                                             if (rosetta) {
                                                  myTurn = true;
                                                  rosetta = true;
                                                  rollPressed = false;
                                                  continue;
                                             }
                                        } while (rosetta);
          
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
                                             // serverListener.interrupt();
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
                         // frame.closeWindow();
                    } else if (startPacket.equals("waitfirst")) {
                         myTurn = false;
                         // frame.closeWindow();
                    }

                    serverListener.start();

                    // Main thread deals with sending messages to server
                    // while (true) {
                    //      if (myTurn) {
                    //           gui.changePlayerTurn("P1");
                              
                    //           rollPressed = false;
                    //           moveSelected = false;
                    //           myTurn = false;

                    //           boolean rosetta = false;
                    //           do {
                    //                StringBuffer packetBuilder = new StringBuffer();

                    //                while (!rollPressed) {
                    //                     try {
                    //                          wait();
                    //                     } catch (Exception e) {
                    //                          // IGNORE
                    //                     }  
                    //                }
                    //                rollPressed = false;
     
                    //                System.out.println("TEST: Testing rolling functionality");
                    //                int diceNum = rollAmount;
                    //                System.out.println(rollAmount);
     
                    //                String diceRoll = Integer.toString(diceNum);
                    //                packetBuilder.append(diceRoll);

                    //                // System.out.println("diceNum String = " + diceRoll);

                    //                if (diceNum == 0) {
                    //                     out.println("0,nil");
                    //                     System.out.println("sent nil packet");
                    //                     myTurn = false;
                    //                     rosetta = false;
                    //                     continue;
                    //                } else {
                    //                     // System.out.println("diceNum = " + diceRoll);
                    //                     if (!availableMoves("P1", diceNum)) {
                    //                          System.out.println("no moves avail");
                    //                          packetBuilder.append(",nil");
                    //                          out.println(packetBuilder.toString());
                    //                          System.out.println("sent nil packet");
                    //                          rosetta = false;
                    //                          myTurn = false;
                    //                          continue;
                    //                     }
                    //                     // System.out.println("WAITING FOR MOVE");
                    //                     while (!moveSelected) {
                    //                          try {
                    //                               wait();
                    //                          } catch (Exception e) {
                    //                               // IGNORE
                    //                          }  
                    //                     }
                    //                     moveSelected = false;

                    //                     int[] move = Arrays.stream(info)
                    //                          .limit(4)
                    //                          .mapToInt(Integer::parseInt)
                    //                          .toArray();

                    //                     currentBoard.move(move, "P1");
                    //                     System.out.println("update the board");

                    //                     SwingUtilities.invokeLater(new Runnable() {
                    //                          public void run() {
                    //                               gui.updateBoard(currentBoard);
                    //                               gui.updateScore(counter);
                    //                          }
                    //                      });

                    //                     int newStrip = move[2];
                    //                     int newIndex = move[3];

                    //                     // INFORMATION TO SEND:
                    //                     // 1. Chip's old position (strip + index)
                    //                     // 2. Chip's new position (strip + index)
                    //                     // 3. Rosetta boolean (of chip's new position)
                    //                     Tile newTile = currentBoard.getBoardStrip(newStrip)[newIndex];
                    //                     if (newTile.isRosetta()) {
                    //                          info[4] = "true";
                    //                          rosetta = true;
                    //                     } else {
                    //                          info[4] = "false";
                    //                          rosetta = false;
                    //                     }
                                        
                    //                     for (int i = 0; i < info.length; i++) {
                    //                          packetBuilder.append("," + info[i]);
                    //                     }
                    //                     out.println(packetBuilder.toString());
                    //                     System.out.println("sent move packet");
                    //                     System.out.println(packetBuilder.toString());
                    //                     // packetBuilder = {
                    //                     //      diceRoll, 
                    //                     //      oldStrip, 
                    //                     //      oldIndex, 
                    //                     //      newStrip, 
                    //                     //      newIndex, 
                    //                     //      "true"/"false"
                    //                     // }
                    //                }
                    //           } while (rosetta);

                    //           SwingUtilities.invokeLater(new Runnable() {
                    //                public void run() {
                    //                     gui.changePlayerTurn("P2");
                    //                     gui.disableP2();
                    //                }
                    //            });
                              
                    //           if (counter.getP1Score() >= 7) {
                    //                SwingUtilities.invokeLater(() -> {
                    //                     gui.closeFrame();
                    //                });
                    //                ClientWinGUI.display("You have won the game!");
                    //                serverListener.interrupt();
                    //                heartbeatSender.interrupt();
                    //                return;
                    //           }
                    //      }
                    // }

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

      public static List<int[]> getCurrentMovablePositions(String player, int roll, List<int[]> currentPositions, int tileCounter) {
          List<int[]> currentMovablePositions = new ArrayList<>();
          int[] stringPos = new int[2];
  
          for (int[] stripAndPos : currentPositions) {
              if (isMoveable(player, roll, stripAndPos[1], stripAndPos[0])) {
                  currentMovablePositions.add(stripAndPos);
              }
          }
          // this will add a possible move for a player to move a token onto the board.
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
  
      public static List<int[]> getFuturePositions(String player, int roll, List<int[]> currentMovablePositions) {
          List<int[]> futurePositions = new ArrayList<>();
          // position of winning tile, 6 on a player strip
          for (int[] piecePos : currentMovablePositions) {
              // futureBoard.pieceMover(player, roll, piecePos);
              futurePositions.add(newPosition(piecePos, player, roll));
          }
          //loop through current movable like above,
  
          // List<int[]> futurePositions = futureBoard.identifyPieces(player); 
          return futurePositions;
      }
      // this method will calculate the strip and index position of where a chip will end up after a particular move.
      public static int[] newPosition(int[] stripPos, String player, int roll) {
          int[] newPos = new int[2];
          int strip = stripPos[0];
          int movePosition = stripPos[1];
          
          // for each position, we are going to find only the STRIP (0,1,2) and index (0-7) that it ends up in. 
          // already know which player it is, so we just check for each valid move if:
          // 1.) if you are in the p1strip / p2string: 
          //        * take current position of a tile and add roll amount, if this is greater than the length of the first part of the strip
          //        * otherwise you are just moving on this strip. 
          // then when it comes to moving the chips, we already know where a particular tile will end up if selected
          // so all we need to check is if its taking or stacking / ending up on a rosette 
  
          int checkTileAfter;
          if (strip == 1) {
              checkTileAfter = movePosition + roll;
              if (checkTileAfter > 7) {
                      checkTileAfter = (checkTileAfter - 4); // Position on new strip
                      strip = ("P1".equals(player)) ? 0 : 2;
              }
              if (checkTileAfter > 5) {
                  checkTileAfter = 6;
              }
          } else {
              checkTileAfter = movePosition + roll;
              if (movePosition>=4) {
                  // moving off board on strip
                  if (checkTileAfter>5) {
                      checkTileAfter=6;
                      // don't change strip
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
  
      // will return -1 if this chip cannot be moved, otherwise will return the postion and strip it will be moved to.
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
  
          if (strip != 1 && checkTileAfter == 6) {
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


    // TODO: Temporary main method for debugging
     // public static void main(String[] args) {
     //      Counter counter = new Counter();
     //      Board currentBoard = new Board(counter);
     //      Dice dice = new Dice();
     //      Client client = new Client(counter, currentBoard, dice);
     //      GameGUI gameGUI = new GameGUI(client);
     //      client.setGUI(gameGUI);
     //      client.initiateMatch();
     // }
}
