import java.io.*;
import java.net.*;
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

     public static int diceRollGetter;
     public static boolean rollPressed;
     public static boolean matchFound = false;

     public Client() {
          
     }

     /**
     * Initiates a 1v1 match by first discovering the game server via a broadcast message
     * and then establishing a TCP connection to participate in a match.
     * This method listens for server messages to manage game state and sends heartbeat
     * messages to the server to indicate that the client is still connected.
     * The method also listens for user input to send game actions to the server.
     */
     public static void initiateMatch() {
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

               // Close server connection display window after connecting to the server
               SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                         if (frame != null) {
                              frame.closeWindow();
                         }
                    }
               });

               // Connect to the server using the discovered IP address
               int serverPort = DEFAULT_PORT;
               try (Socket socket = new Socket(serverIP, serverPort);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

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
                         // Start game + launch GUI
                    }

                    // Main thread deals with sending messages to server
                    while (opponentAlive.get() && selfAlive.get()) {
                         if (myTurn.get()) {
                              // 1. Read for dice roll and send user input to server
                              // 2. Read for move (ONLY if dice roll > 0)
                              // 3. Read for move again if the player ends up on a rosetta tile

                              // PSEUDO-CODE:
                              // rollButtonP1.setVisible(true); // Enable P1 to click stuff on the GUI
                              boolean rosetta = false;
                              
                              while (!rollPressed) {}
                              rollPressed = false;

                              // Send dice number to the server to send to opponent
                              String diceRoll = Integer.toString(diceRollGetter);
                              out.println(diceRoll);
                              
                              int diceNum = Integer.parseInt(diceRoll);
                              
                              if (diceNum > 0) {
                                   do {
                                        // String move = GUI chip move shit
                                        // Tile currentTile = last destination tile;
                                        // String move = "move";
                                        // INFORMATION TO SEND:
                                        // 1. Tile coordinates (before AND after)
                                        // 2. Rosetta boolean // For server to check if it should listen for more inputs
                                        // out.println(move);
                                        // rosetta = (currentTile.isRosetta()) ? true : false;
                                   } while (rosetta);
                              } 
                              
                              myTurn.set(false); // Reset turn after sending message
                              // check for win message
                              // rollButtonP1.setVisible(false);
                              
                         } else {
                              // 1. Read opponent's dice roll and update GUI
                              // 2. Read opponent's move and update GUI
                              
                              // PSEUDO-CODE:
                              boolean opponentTurn = true;
                              String dieRollStr = in.readLine();
                              // update local GUI with opponent's roll 
                              int dieRoll = Integer.parseInt(dieRollStr);
                              if (dieRoll > 0) {
                                   do {
                                        String move = in.readLine();
                                        // INFORMATION TO RECEIVE:
                                        // 1. Tile coordinates (before AND after)
                                        // 2. Rosetta tile -> check if opponent's turn should end
                                        // opponentTurn = (currentTile.rosettaTile) ? true : false;
                                        Thread.sleep(500);
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
