import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/**
 * The {@code Server} class represents a server for a multiplayer game.
 * It listens for connections from players, pairs them up, and starts their game sessions.
 * This server broadcasts its IP address and waits for players to connect on a specified port.
 * Once two players have connected, it matches them together and initiates a game session.
 */
public class Server {
     public static final int DEFAULT_PORT = 6969; // Default port number for server

     private ServerSocket serverSocket;

     /**
      * Constructs a new {@code Server} that listens for player connections on the given port.
      * Upon instantiation, it starts broadcasting the server's IP address and listens for player connections.
      * When two players connect, it pairs them and starts a new game session.
      * 
      * @param port The port number on which the server will listen for connections.
      */
     public Server(int port) {
          try {
               // Start broadcasting server IP address
               System.out.println("Server: Broadcasting IP address...");
               new Thread(new BroadcastServer()).start();

               serverSocket = new ServerSocket(port);

               while (true) {
                    System.out.println("Server: Listening for player connections...");
                    Socket player1 = serverSocket.accept();
                    System.out.println("Server: " + player1.getInetAddress().getHostAddress() + " has connected.");

                    // player1.setSoTimeout(60000); // Set timeout to 60 seconds

                    try (PrintWriter player1Out = new PrintWriter(player1.getOutputStream(), true);
                         BufferedReader player1In = new BufferedReader(new InputStreamReader(player1.getInputStream()));) {
                         
                         player1Out.println("Searching for opponent...");

                         Socket player2 = serverSocket.accept();
                         System.out.println("Server: " + player2.getInetAddress().getHostAddress() + " has connected.");

                         // player2.setSoTimeout(60000);

                         try (PrintWriter player2Out = new PrintWriter(player2.getOutputStream(), true);
                              BufferedReader player2In = new BufferedReader(new InputStreamReader(player2.getInputStream()));) {

                              player1Out.println("Match found!");
                              player2Out.println("Match found!");

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
      * The entry point of the server application.
      * It creates an instance of {@code Server} using the {@code DEFAULT_PORT}.
      * 
      * @param args Command line arguments (not used).
      */
     public static void main(String[] args) {
          new Server(DEFAULT_PORT);
     }
}
