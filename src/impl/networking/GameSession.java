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

     PrintWriter p1Out, p2Out;
     BufferedReader p1In, p2In;

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

          try {
               this.p1Out = new PrintWriter(player1.getOutputStream(), true);
               this.p1In = new BufferedReader(new InputStreamReader(player1.getInputStream()));
               this.p2Out = new PrintWriter(player2.getOutputStream(), true);
               this.p2In = new BufferedReader(new InputStreamReader(player2.getInputStream()));
          } catch (IOException e) {
               e.printStackTrace();
          }

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
     private void handlePlayers(Socket p1Socket, Socket p2Socket) throws IOException, InterruptedException {
          playGame(p1Out, p1In, p2Out, p2In);
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
                                   p2Out.print("opponentdc\r\n");
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

          // In this method, we need to pass in the array(s) that correspond to the game GUI
          // On the client side, this will directly update the GUI displayed on their screen
          // Server updates the GUI on both clients' machines after every move (roll + chip movement)

          Thread p1Listener = new Thread(() -> {
               while (true) {
                    try {
                         String p1info = p1In.readLine();
                         if (p1info != null) {
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
     }
}