import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * The {@code GameSession} class encapsulates the functionality required to manage a game session between two players in a multiplayer game context. It tracks the connection status of each player using heartbeat messages, manages game state transitions such as player turns and game outcomes, and handles player disconnections due to timeouts or other network issues. This class utilizes sockets for network communication between players and a scheduled executor service to monitor heartbeat messages to ensure player connectivity.
 */
public class GameSession {
     private final static int HEARTBEAT_PORT = 42069; 
     private final static int TIMEOUT = 20000; // Timeout if no heartbeat sent after 20s

     boolean player1Win = false; // Flag indicating if player 1 has won the game.
     boolean player2Win = false; // Flag indicating if player 2 has won the game.

     private Socket player1; // Socket connection for player 1.
     private Socket player2; // Socket connection for player 2.

     private String p1Address; // IP address of player 1.
     private String p2Address; // IP address of player 2.

     // A thread-safe map to track the last heartbeat time from each player.
     private volatile ConcurrentHashMap<String, Long> lastHeartbeatTime = new ConcurrentHashMap<>();

     /**
     * Constructs a new {@code GameSession} instance for the specified player sockets. It initializes the player addresses, sets up the heartbeat listening mechanism to track player connectivity, and starts the heartbeat listener thread.
     *
     * @param player1 The socket connection for player 1.
     * @param player2 The socket connection for player 2.
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
                    System.err.println("Server Exception: Error receiving heartbeat: " + e.getMessage());
               }
          });

          heartbeatListener.start();
     }

     /**
     * Initializes the game session by starting the game logic and managing the communication and game state for each player.
     *
     * @throws IOException If an I/O error occurs during communication with the players.
     * @throws InterruptedException If the thread is interrupted while waiting or sleeping.
     */
     public void connectionInit() throws IOException, InterruptedException {
          handlePlayers(player1, player2);
     }

     /**
     * Manages the game logic for both players, including initializing communication channels, sending initial game state information, and setting up a scheduled task to monitor for timeouts based on heartbeat messages. This method also starts threads to listen for messages from each player, facilitating communication between them and ensuring game state is maintained and synchronized.
     *
     * @param p1Socket The socket connection for player 1.
     * @param p2Socket The socket connection for player 2.
     * @throws IOException If an I/O error occurs during setup or communication.
     */
     private void handlePlayers(Socket p1Socket, Socket p2Socket) throws IOException {
          // Send signal for client to know if they start first or wait for their turn first
          try (PrintWriter p1Out = new PrintWriter(p1Socket.getOutputStream(), true);
               BufferedReader p1In = new BufferedReader(new InputStreamReader(p1Socket.getInputStream()));
               PrintWriter p2Out = new PrintWriter(p2Socket.getOutputStream(), true);
               BufferedReader p2In = new BufferedReader(new InputStreamReader(p2Socket.getInputStream())); ) {

               p1Out.println("startfirst");
               p2Out.println("waitfirst");
     
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
                                        p2Out.println("opponentdc");
                                        System.out.println("Server: Player 2 (" + p2Address + ") socket closed.");
                                        player2.close();
                                        throw new InterruptedException();
                                   } catch (Exception e) {
                                        System.out.println("Server Exception: Error closing Player 2 socket.");
                                   }                          
                              } else {
                                   System.out.println("Server: Player 2 (" + ipAddr + ") has disconnected due to timeout.");
                                   try {
                                        p1Out.println("opponentdc");
                                        System.out.println("Server: Player 1 (" + p1Address + ") socket closed.");
                                        player1.close();
                                        throw new InterruptedException();
                                   } catch (Exception e) {
                                        System.out.println("Server Exception: Error closing Player 1 socket.");
                                   }     
                              }
                              executorService.shutdown();
                         }
                    });
               }, 0, 1, TimeUnit.SECONDS); // Check for heartbeat every second
     
               Thread p1Listener = new Thread(() -> {
                    while (true) {
                         try {
                              String p1info = p1In.readLine();
                              if (p1info != null) {
                                   System.out.println("from P1: " + p1info);
                                   p2Out.println(p1info);
                              }
                         } catch (Exception e) {
                              System.out.println("Server Exception: Error reading from Player 1.");
                              break;
                         }
                    }
               });
     
               Thread p2Listener = new Thread(() -> {
                    while (true) {
                         try {
                              String p2info = p2In.readLine();
                              if (p2info != null) {
                                   System.out.println("from P2: " + p2info);
                                   p1Out.println(p2info);
                              }
                         } catch (Exception e) {
                              System.out.println("Server Exception: Error reading from Player 2.");
                              break;
                         }
                    }
               });
     
               p1Listener.start();
               p2Listener.start();

               while (true) {
                    try {
                         wait();
                    } catch (Exception e) {
                         // IGNORE
                    } 
               }
          } catch (Exception e) {
               throw new IOException();
          } 
     }
}
