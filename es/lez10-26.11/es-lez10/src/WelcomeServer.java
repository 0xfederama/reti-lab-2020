import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WelcomeServer {
    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getByName("239.255.1.3");
            byte[] data = "welcome".getBytes();
            int port = 6789;
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, ip, port);
            DatagramSocket datagramSocket = new DatagramSocket();
            while (true) {
                datagramSocket.send(datagramPacket);
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
