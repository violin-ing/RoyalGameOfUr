import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

     private static Board currentBoard;
     private static Board futureBoard;
     private static Counter counter;
     private static Dice dice;
     public static GameGUI gui;

     public static int rollAmount;
     public static boolean rollPressed = false;
     public static boolean matchFound = false;
     public static boolean moveSelected = false;

     private boolean selfWin = false;
     private boolean opponentWin = false;
     private boolean myTurn;
     private boolean opponentAlive = true, selfAlive = true;

     public String[] info = new String[5];

     public Client(Counter counterIn, Board currentBoardIn, Dice diceIn) {
          currentBoard = currentBoardIn;
          counter = counterIn;
          dice = diceIn;
          Game.networkPlay = true;
      }

     public void setGUI(GameGUI gameGUI) {
          gui = gameGUI;
          gui.disableP2();
          gui.switchP1RollButton(false);
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
               matchFound = false;

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

                    // Start a thread to listen for messages from the server
                    Thread serverListener = new Thread(() -> {
                         try (DatagramSocket dcSocket = new DatagramSocket(4445)) {
                              byte[] dcMsgBuffer = new byte[256];
                  
                              while (true) {
                                   DatagramPacket dcPacket = new DatagramPacket(dcMsgBuffer, dcMsgBuffer.length);
                                   dcSocket.receive(dcPacket);
                                   String received = new String(dcPacket.getData(), 0, dcPacket.getLength());
                                   if (received.equals("opponentdc")) {
                                        gui.closeFrame();
                                        ClientWinGUI.display("Opponent disconnected. You have won the game by default.");
                                        heartbeatSender.interrupt();
                                        return;
                                   } 
                              }
                         } catch (Exception e) {
                              gui.closeFrame();
                              ErrorWindowGUI.display();
                         } finally {
                              
                         }
                    });

                    heartbeatSender.start();
                    serverListener.start();

                    String startPacket;
                    startPacket = in.readLine();
                    if ("startfirst".equals(startPacket)) {
                         matchFound = true;
                         myTurn = true;
                         // frame.closeWindow();
                    } else if (startPacket.equals("waitfirst")) {
                         matchFound = true;
                         myTurn = false;
                         // frame.closeWindow();
                    }

                    // Main thread deals with sending messages to server
                    while (opponentAlive && selfAlive) {
                         if (myTurn) {
                              // 1. Read for dice roll and send user input to server
                              // 2. Read for move (ONLY if dice roll > 0)
                              // 3. Read for move again if the player ends up on a rosetta tile

                              SwingUtilities.invokeLater(() -> {
                                   gui.switchP1RollButton(true);
                              });
                              
                              boolean rosetta = false;
                              
                              while (!rollPressed) {
                                   System.out.println("rolling...");
                              }
                              rollPressed = false;

                              System.out.println("TEST: Testing rolling functionality");
                              System.out.println(rollAmount);

                              String diceRoll = Integer.toString(rollAmount);

                              // Send dice number to the server to send to opponent
                              new Thread(() -> {
                                   out.println("sending_dice_roll");
                                   out.println(diceRoll); // Sends die roll to server
                              }).start();
                              
                              int diceNum = Integer.parseInt(diceRoll);
                              
                              if (diceNum > 0) {
                                   do {
                                        Game.availableMoves("P1", diceNum);
                                        while (!moveSelected) {
                                             // Wait for player to make their move on the GUI
                                             System.out.println("waiting for opponent roll...");
                                        }
                                        // Stream "info" array into a usable int[] array 
                                        int[] move = Arrays.stream(info)
                                             .limit(4)
                                             .mapToInt(Integer::parseInt)
                                             .toArray();
                                        currentBoard.move(move, "P1");
                                        SwingUtilities.invokeLater(() -> {
                                             gui.updateBoard(currentBoard);
                                        });
                                        moveSelected = false;

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
                                        StringBuffer gamePacket = new StringBuffer();
                                        for (int i = 0; i < info.length; i++) {
                                             if (i == info.length - 1) {
                                                  gamePacket.append(info[i]);
                                             } else {
                                                  gamePacket.append(info[i] + ",");
                                             }
                                        }
                                        new Thread(() -> {
                                             out.println(gamePacket);
                                        }).start();
                                        
                                   } while (rosetta);
                              } 
                              
                              myTurn = false; // Reset turn after sending message
                              SwingUtilities.invokeLater(() -> {
                                   gui.switchP1RollButton(false);
                              });
                              
                              if (counter.getP1Score() == 7) {
                                   SwingUtilities.invokeLater(() -> {
                                        gui.closeFrame();
                                   });
                                   ClientWinGUI.display("You have won the game!");
                                   return;
                              }
                         } else {
                              boolean opponentTurn = true;
                              String dieRollStr = in.readLine(); // Read opponent's die roll
                              
                              int dieRoll = Integer.parseInt(dieRollStr);
                              
                              SwingUtilities.invokeLater(() -> {
                                   gui.updateRollLabel("P2", dieRoll);
                              });
                              
                              if (dieRoll > 0) {
                                   do {
                                        String data = in.readLine(); // Read opponent's move

                                        String[] info = data.split(",");
                                        String rosetta = info[4];

                                        if ("true".equals(rosetta)) {
                                             opponentTurn = true;
                                        } else {
                                             opponentTurn = false;
                                        }

                                        // Stream packet array into a usable int[] array
                                        int[] move = Arrays.stream(info)
                                             .limit(4)
                                             .mapToInt(Integer::parseInt)
                                             .toArray();
                                        currentBoard.move(move, "P2");
                                        gui.updateBoard(currentBoard);
                                        if (counter.getP2Score() == 7) {
                                             SwingUtilities.invokeLater(() -> {
                                                  gui.closeFrame();
                                             });
                                             ClientLoseGUI.display("You have lost the game!");
                                             return;
                                        }
                                   } while (opponentTurn);
                              }
                         }
                         myTurn = true;
                    }

                    // Cleanup resources
                    heartbeatSender.interrupt();
                    serverListener.interrupt();
                    return;
               } catch (Exception e) {
                    System.out.println("Error connecting to server! " + e);
               } 
          } catch (Exception e) {
               System.out.println("Client exception!" + e);
          }
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
