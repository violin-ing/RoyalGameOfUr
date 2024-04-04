import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * The {@code GameSession} class manages a game session between two players in a multiplayer game.
 * It tracks the connection status of each player using heartbeat messages and handles the game logic
 * to ensure a smooth and responsive gaming experience.
 * This class is responsible for managing the game state, including player turns, game outcomes,
 * and disconnections due to timeouts or other issues.
 */
public class GameSession {
     private final static int HEARTBEAT_PORT = 42069; 
     private final static int TIMEOUT = 20000; // Timeout if no heartbeat sent after 20s

     boolean player1Win = false;
     boolean player2Win = false;

     private Socket player1;
     private Socket player2;

     private String p1Address;
     private String p2Address;

     private volatile ConcurrentHashMap<String, Long> lastHeartbeatTime = new ConcurrentHashMap<>();

     /**
      * Constructs a new GameSession for the specified player sockets.
      * Initializes heartbeat listening to track player connectivity and sets up the game environment.
      * 
      * @param player1 The socket for player 1.
      * @param player2 The socket for player 2.
      */
     public GameSession(Socket player1, Socket player2) {
          this.player1 = player1;
          this.player2 = player2;

          p1Address = player1.getInetAddress().getHostAddress();
          p2Address = player2.getInetAddress().getHostAddress();

          lastHeartbeatTime.put(p1Address, System.currentTimeMillis());
          lastHeartbeatTime.put(p2Address, System.currentTimeMillis());

          Thread heartbeatListener = new Thread(() -> {
               try (DatagramSocket socket = new DatagramSocket(null)) {
                    socket.setReuseAddress(true);
                    socket.bind(new InetSocketAddress(HEARTBEAT_PORT)); 

                    // Buffer for receiving incoming data
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    while (true) {
                         socket.receive(packet); // Receive packet from a client
                         String senderIP = packet.getAddress().getHostAddress();
                         lastHeartbeatTime.put(senderIP, System.currentTimeMillis());
                    }
               } catch (Exception e) {
                    System.err.println("Error receiving heartbeat: " + e.getMessage());
                    e.printStackTrace();
               }
          });

          heartbeatListener.start();
     }

     /**
      * Initializes the game session by starting the game logic and handling for each player.
      */
     public void connectionInit() {
          handlePlayers(player1, player2);
     }

     /**
      * Handles the game logic for a single player, including communication with the player
      * and managing game state based on player input and actions.
      * 
      * @param playerSocket The socket of the player to handle.
      * @param playerLabel A label identifying the player (e.g., "Player 1").
      */
     private void handlePlayers(Socket p1Socket, Socket p2Socket) {
          try (PrintWriter p1Out = new PrintWriter(p1Socket.getOutputStream(), true);
               BufferedReader p1In = new BufferedReader(new InputStreamReader(p1Socket.getInputStream()));
               PrintWriter p2Out = new PrintWriter(p2Socket.getOutputStream(), true);
               BufferedReader p2In = new BufferedReader(new InputStreamReader(p2Socket.getInputStream()));) { 
               // Run game   
               playGame(p1Out, p1In, p2Out, p2In);
          } catch (IOException e) {
               System.out.println("An IOException occurred with a player: " + e.getMessage());
          } catch (InterruptedException e) {
               return;
          }
     }

     /**
      * Facilitates the gameplay between the two players, alternating turns, and processing player inputs.
      * This method also monitors for player disconnection and timeouts, updating game state accordingly.
      * 
      * @param p1Out PrintWriter for player 1 output.
      * @param p1In BufferedReader for player 1 input.
      * @param p2Out PrintWriter for player 2 output.
      * @param p2In BufferedReader for player 2 input.
      * @throws IOException If an I/O error occurs during communication with the players.
      */
     private void playGame(PrintWriter p1Out, BufferedReader p1In, PrintWriter p2Out, BufferedReader p2In) throws IOException, InterruptedException {
          // Send signal for client to know if they start first or wait for their turn first
          p1Out.print("startfirst\r\n");
          p2Out.print("waitfirst\r\n");

          System.out.println("Server: Player turns assigned.");

          // Schedule a task to check for timeouts
          ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
          executorService.scheduleAtFixedRate(() -> {
               long currentTime = System.currentTimeMillis();
               lastHeartbeatTime.forEach((ipAddr, lastHeartbeatTime) -> {
                    if ((currentTime - lastHeartbeatTime) > TIMEOUT) {
                         if (ipAddr.equals(p1Address)) {
                              System.out.println("Server: Player 1 (" + ipAddr + ") has disconnected due to timeout.");
                              try {
                                   // Start the timer
                                   final long durationMillis = 10000; // 10 seconds
                                   long startTime = System.currentTimeMillis();
                               
                                   // Create the socket outside the loop to reuse for all packets
                                   try (DatagramSocket socket = new DatagramSocket()) {
                                       while (System.currentTimeMillis() - startTime < durationMillis) {
                                           byte[] buffer = "opponentdc".getBytes();
                                           InetAddress address = player2.getInetAddress();
                                           DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
                                           socket.send(packet);
                               
                                           // Sleep for a short period to avoid flooding
                                           Thread.sleep(500); // Sleep for 500 milliseconds
                                       }
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                                   System.out.println("Server: Player 2 (" + p2Address + ") socket closed.");
                                   player2.close();
                                   throw new InterruptedException();
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }                          
                         } else {
                              System.out.println("Server: Player 2 (" + ipAddr + ") has disconnected due to timeout.");
                              try {
                                   // Start the timer
                                   final long durationMillis = 10000; // 10 seconds
                                   long startTime = System.currentTimeMillis();
                               
                                   // Create the socket outside the loop to reuse for all packets
                                   try (DatagramSocket socket = new DatagramSocket()) {
                                       while (System.currentTimeMillis() - startTime < durationMillis) {
                                           byte[] buffer = "opponentdc".getBytes();
                                           InetAddress address = player1.getInetAddress();
                                           DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
                                           socket.send(packet);
                               
                                           // Sleep for a short period to avoid flooding
                                           Thread.sleep(500); // Sleep for 500 milliseconds
                                       }
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                                   System.out.println("Server: Player 1 (" + p1Address + ") socket closed.");
                                   player1.close();
                                   throw new InterruptedException();
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }     
                         }
                         executorService.shutdown();
                    }
               });
          }, 0, 1, TimeUnit.SECONDS); // Check for heartbeat every second

          // In this method, we need to pass in the array(s) that correspond to the game GUI
          // On the client side, this will directly update the GUI displayed on their screen
          // Server updates the GUI on both clients' machines after every move (roll + chip movement)

          boolean p1Turn = true;

          // TODO: Main game loop
          while (true) {
               if (executorService.isShutdown()) {
                    throw new IOException();
               }

               if (p1Turn) {
                    boolean rosetta = false;
                    String diceRoll = p1In.readLine();

                    p2Out.print(diceRoll + "\r\n"); // Send opponent dice roll

                    System.out.println("Player 1: " + diceRoll);
                    int diceNum = Integer.parseInt(diceRoll);
                    if (diceNum > 0) {
                         do {
                              String gamePacket = p1In.readLine();
                              if (!gamePacket.equals("nil")) {
                                   System.out.println("Player 1: " + gamePacket);
                                   String data[] = gamePacket.split(",");
                                   // Check if the player stepped onto a rosetta
                                   if (data[4].equals("true")) {
                                        rosetta = true;
                                   } else {
                                        rosetta = false;
                                   }
                              } 
                              new Thread(() -> {
                                   p2Out.print(gamePacket + "\r\n");
                              }).start();
                         } while (rosetta);
                    } 
               } else {
                    // Same as above, but just swap players (ie. p1 <-> p2)
                    boolean rosetta = false;
                    String diceRoll = p2In.readLine();

                    p2Out.print(diceRoll + "\r\n"); // Send opponent dice roll

                    System.out.println("Player 2: " + diceRoll);
                    int diceNum = Integer.parseInt(diceRoll);
                    if (diceNum > 0) {
                         do {
                              String gamePacket = p2In.readLine();
                              if (!gamePacket.equals("nil")) {
                                   System.out.println("Player 2: " + gamePacket);
                                   String data[] = gamePacket.split(",");
                                   // Check if the player stepped onto a rosetta
                                   if (data[4].equals("true")) {
                                        rosetta = true;
                                   } else {
                                        rosetta = false;
                                   }
                              } 
                              new Thread(() -> {
                                   p1Out.print(gamePacket + "\r\n");
                              }).start();
                         } while (rosetta);
                    } 
               }
               // Swap turns
               p1Turn = !p1Turn;
          }
     }
}