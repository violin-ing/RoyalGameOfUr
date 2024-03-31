import java.net.*;


public class BroadcastServer implements Runnable {
     // Broadcast IP address is by default 255.255.255.255
     private static final String BROADCAST_ADDR = "255.255.255.255";
     // Default port for client connection
     public static final int DEFAULT_PORT = 6969;
     
     @Override
     public void run() {
          try (DatagramSocket socket = new DatagramSocket()) {
               socket.setBroadcast(true);

               String message = InetAddress.getLocalHost().getHostAddress(); // Server's IP address
               byte[] buffer = message.getBytes();

               // Use the broadcast address to send the packet to all devices in the network
               InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_ADDR);
               DatagramPacket packet = new DatagramPacket(
                    buffer, 
                    buffer.length, 
                    broadcastAddress, 
                    DEFAULT_PORT
               ); // Constructs packet to be broadcast

               // Create a loop to keep sending broadcasts 
               while (!Thread.currentThread().isInterrupted()) {
                    socket.send(packet);
                    Thread.sleep(5000); // Wait for 5s before sending the next broadcast
               }
          } catch (Exception e) {
               System.err.println("Error sending server broadcasts!");
               e.printStackTrace();
          }
     }
}

