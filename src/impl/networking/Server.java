import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/**
 * Represents a server in a multiplayer game environment. This server is responsible for
 * broadcasting its IP address to potential clients and handling incoming player connections.
 * It pairs connected players into game sessions and manages the initiation of these game sessions.
 * The server listens on a specified port for player connections and continues to accept and pair
 * players as they connect.
 */
public class Server {
     public static final int DEFAULT_PORT = 6969; // Default port number for server

     private ServerSocket serverSocket;

     /**
     * Constructs a new Server instance that listens for incoming player connections on the specified port.
     * Upon instantiation, this server starts a separate thread to broadcast its IP address to potential clients.
     * It then continuously listens for player connections, pairs connected players, and initiates their game sessions.
     * 
     * @param port The port number on which the server will listen for incoming connections.
     */
     public Server(int port) {
          try {
               // Start a thread to broadcast the server's IP address
               System.out.println("Server: Broadcasting IP address...");
               new Thread(new BroadcastServer()).start();

                // Initialize the server socket to listen for connections
               serverSocket = new ServerSocket(port);

               // Continuously listen for player connections and pair them into game sessions
               while (true) {
                    System.out.println("Server: Listening for player connections...");
                    Socket player1 = serverSocket.accept();
                    System.out.println("Server: " + player1.getInetAddress().getHostAddress() + " has connected.");

                    try (PrintWriter player1Out = new PrintWriter(player1.getOutputStream(), true);
                         BufferedReader player1In = new BufferedReader(new InputStreamReader(player1.getInputStream()));) {

                         Socket player2 = serverSocket.accept();
                         System.out.println("Server: " + player2.getInetAddress().getHostAddress() + " has connected.");

                         try (PrintWriter player2Out = new PrintWriter(player2.getOutputStream(), true);
                              BufferedReader player2In = new BufferedReader(new InputStreamReader(player2.getInputStream()));) {

                              System.out.println("Server: Matchmaking completed.");

                              Thread.sleep(1000); // Ensure that clients receive the matchfound message

                              // Initialize and start the game session for the paired players
                              GameSession gameSession = new GameSession(player1, player2);
                              gameSession.connectionInit();

                         } catch (Exception e) {
                              e.printStackTrace();
                         }
                    } catch (Exception e) {
                         System.err.println("Server: Error finding match for players: " + e.getMessage());
                    }
               }
          } catch (Exception e) {
               System.out.println("Server: Server exception: " + e);
          }
     }

     /**
     * The main method and the entry point of the server application.
     * It creates a server instance that listens for player connections on the {@code DEFAULT_PORT}.
     * 
     * @param args Command line arguments (not used).
     */
     public static void main(String[] args) {
          new Server(DEFAULT_PORT);
     }
}
