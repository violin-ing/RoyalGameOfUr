import java.net.*;

/**
 * The {@code BroadcastServer} class implements the {@link Runnable} interface to enable broadcasting the server's IP address
 * to all devices within the local network. This allows clients to discover the server automatically without needing to know
 * its IP address ahead of time. It periodically sends broadcast messages containing the server's IP address to a predefined
 * port that client applications listen on.
 */
public class BroadcastServer implements Runnable {
     // The default broadcast IP address used for sending the message to all devices in the network.
    private static final String BROADCAST_ADDR = "255.255.255.255";
    // The default port number on which the server listens for client connections.
    public static final int DEFAULT_PORT = 6969;
    
    /**
     * When an object implementing interface {@code Runnable} is used to create a thread, starting the thread causes the
     * object's {@code run} method to be called in that separately executing thread. The {@code run} method of {@code BroadcastServer}
     * performs the broadcast of the server's IP address over the network to the specified broadcast address and port.
     */
     @Override
     public void run() {
          try (DatagramSocket socket = new DatagramSocket()) {
               socket.setBroadcast(true); // Enables the broadcast option on the socket.

               String message = InetAddress.getLocalHost().getHostAddress(); // Retrieves the server's IP address.
               byte[] buffer = message.getBytes(); // Converts the server's IP address into a byte array.

               // Prepares the packet to be sent to all devices on the network by using the broadcast address.
               InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_ADDR);
               DatagramPacket packet = new DatagramPacket(
                    buffer, 
                    buffer.length, 
                    broadcastAddress, 
                    DEFAULT_PORT
               ); // Constructs the packet to be broadcast.

               // Enters a loop to continuously send broadcast messages.
               while (!Thread.currentThread().isInterrupted()) {
                    socket.send(packet); // Sends the broadcast packet.
                    Thread.sleep(5000); // Pauses for 5 seconds before sending the next broadcast.
            }
          } catch (Exception e) {
               System.err.println("Error sending server broadcasts!");
               e.printStackTrace();
          }
     }
}

