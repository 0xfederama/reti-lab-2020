import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class WelcomeClient {
    public static void main(String[] args) throws UnknownHostException {

        InetAddress group = InetAddress.getByName("239.255.1.3");
        int port = 6789;

        System.out.println("Client apre la connessione su "+ group.toString() +":"+port);

        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(group);
            byte[] buffer = new byte[7];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            multicastSocket.receive(datagramPacket);
            String s = new String(datagramPacket.getData());
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (multicastSocket!=null) {
                try {
                    multicastSocket.leaveGroup(group);
                    multicastSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Client chiude la connessione...");

    }
}
