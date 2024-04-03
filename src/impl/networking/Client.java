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

     private Board currentBoard;
     private Board futureBoard;
     private Counter counter;
     private Dice dice;
     public static GameGUI gui;

     public static int rollAmount;
     public static boolean rollPressed = false;
     public static boolean matchFound = false;
     public boolean moveSelected = false;

     public String[] info = new String[5];

     public Client() {
          Counter counter = new Counter();
          Board currentBoard = new Board(counter);
          Dice dice = new Dice();
          this.currentBoard = currentBoard;
          this.counter = counter;
          this.dice = dice;
          Game.networkPlay = true;
      }

     public void setGUI(GameGUI gui) {
          this.gui = gui;
     }

     /**
     * Initiates a 1v1 match by first discovering the game server via a broadcast message
     * and then establishing a TCP connection to participate in a match.
     * This method listens for server messages to manage game state and sends heartbeat
     * messages to the server to indicate that the client is still connected.
     * The method also listens for user input to send game actions to the server.
     */
     public void initiateMatch(GameGUI gui) {
          try {
               // Connecting to server display
               ServerConnectionGUI frame = ServerConnectionGUI.display();

               matchFound = false;

               // Listen for server broadcast to discover the server
               DatagramSocket broadcastSocket = new DatagramSocket(DEFAULT_PORT);
               broadcastSocket.setBroadcast(true);
               byte[] buffer = new byte[1024];
               DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

               broadcastSocket.receive(packet);
               broadcastSocket.close();

               // Extract the server IP address from the broadcast message
               String serverIP = new String(packet.getData(), 0, packet.getLength()).trim();
               // System.out.println("Connected to server at " + serverIP);

               // Connect to the server using the discovered IP address
               int serverPort = DEFAULT_PORT;
               try (Socket socket = new Socket(serverIP, serverPort);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)); ) {

                    AtomicBoolean myTurn = new AtomicBoolean(false); // Tracks player's turn
                    AtomicBoolean opponentAlive = new AtomicBoolean(true); // Tracks if opponent is connected
                    AtomicBoolean selfAlive = new AtomicBoolean(true); // Tracks if self is connected
         
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
                         String fromServer;
                         try {
                              while ((fromServer = in.readLine()) != null) {
                                   // System.out.println("Server: " + fromServer);
                                   if ("Match found!".equals(fromServer)) {
                                        matchFound = true;
                                   }

                                   if ("Your turn:".equals(fromServer.trim())) {
                                        myTurn.set(true);
                                   } else if ("You have disconnected.".equals(fromServer.trim())) {
                                        selfAlive.set(false);
                                        System.out.println("You have disconnected and forfeited the match!");
                                        System.exit(1); // Display an error screen here
                                   } else if ("Opponent has disconnected.".equals(fromServer.trim())) {
                                        opponentAlive.set(false);
                                        System.out.println("You have won by default!");
                                        System.exit(1); // Display some screen here as well
                                   } 
                              }
                         } catch (IOException e) {
                              // IGNORE
                         }
                    });

                    heartbeatSender.start();
                    serverListener.start();

                    if (matchFound) {
                         // Initiate singleplayer game
                         // Close server connection display window after connecting to the server with another player
                         SwingUtilities.invokeLater(new Runnable() {
                              @Override
                              public void run() {
                                   if (frame != null) {
                                        frame.closeWindow();
                                   }
                              }
                         });

                         gui.disableP2();
                    }

                    // Main thread deals with sending messages to server
                    while (opponentAlive.get() && selfAlive.get()) {
                         if (myTurn.get()) {
                              // 1. Read for dice roll and send user input to server
                              // 2. Read for move (ONLY if dice roll > 0)
                              // 3. Read for move again if the player ends up on a rosetta tile

                              // PSEUDO-CODE:
                              gui.switchP1RollButton(true);
                              boolean rosetta = false;
                              
                              while (!rollPressed) {
                                   // Wait until the player rolls the dice
                              }
                              rollPressed = false;

                              // Send dice number to the server to send to opponent
                              String diceRoll = Integer.toString(rollAmount);
                              out.println(diceRoll); // Sends die roll to server
                              
                              int diceNum = Integer.parseInt(diceRoll);
                              
                              if (diceNum > 0) {
                                   do {
                                        Game.availableMoves("P1", diceNum);
                                        while (!moveSelected) {
                                             // Wait for player to make their move on the GUI
                                        }
                                        // Stream "info" array into a usable int[] array 
                                        int[] move = Arrays.stream(info)
                                             .limit(4)
                                             .mapToInt(Integer::parseInt)
                                             .toArray();
                                        currentBoard.move(move, "P1");
                                        gui.updateBoard(currentBoard);
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
                                        out.println(gamePacket);
                                   } while (rosetta);
                              } 
                              
                              myTurn.set(false); // Reset turn after sending message
                              gui.switchP1RollButton(false);
                              if (counter.getP1Score() == 7) {
                                   gui.closeFrame();
                              }
                         } else {
                              boolean opponentTurn = true;
                              String dieRollStr = in.readLine(); // Read opponent's die roll
                              int dieRoll = Integer.parseInt(dieRollStr);

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
                                             gui.closeFrame();
                                             break;
                                        }
                                   } while (opponentTurn);
                              }
                              // check if opponent has won (should get losing message if so)
                         }
                    }

                    // Cleanup resources
                    heartbeatSender.interrupt();
                    serverListener.interrupt();
                    System.exit(0);
               } catch (Exception e) {
                    System.out.println("Error connecting to server!" + e);
               } 
        } catch (Exception e) {
            System.out.println("Client exception!" + e);
        }
    }
}
