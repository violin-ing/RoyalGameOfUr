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
                    // System.err.println("Error receiving heartbeat: " + e.getMessage());
                    e.printStackTrace();
               }
          });

          heartbeatListener.start();
     }

     /**
      * Initializes the game session by starting the game logic and handling for each player.
      */
     public void connectionInit() throws IOException, InterruptedException {
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
                                        e.printStackTrace();
                                        }                          
                              } else {
                                   System.out.println("Server: Player 2 (" + ipAddr + ") has disconnected due to timeout.");
                                   try {
                                        p1Out.println("opponentdc");
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
     
               Thread p1Listener = new Thread(() -> {
                    while (true) {
                         try {
                              String p1info = p1In.readLine();
                              if (p1info != null) {
                                   System.out.println("from P1: " + p1info);
                                   p2Out.println(p1info);
                              }
                         } catch (Exception e) {
                              e.printStackTrace();
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
                              e.printStackTrace();
                              break;
                         }
                    }
               });
     
               p1Listener.start();
               p2Listener.start();

               while (true) {
                    // Run game
               }
          } catch (Exception e) {
               
          } 
     }
}
