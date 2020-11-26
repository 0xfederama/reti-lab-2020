import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress ipAddress = InetAddress.getLocalHost();
        String send = "Ping";
        DatagramPacket sendPacket = new DatagramPacket(send.getBytes(), send.getBytes().length, ipAddress, 6789);
        clientSocket.send(sendPacket);
        System.out.println("Client ha inviato Ping");
        byte[] receive = new byte[4];
        DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
        clientSocket.receive(receivePacket);
        String rec = new String(receivePacket.getData());
        System.out.println("Client riceve "+rec+" dal server");
        clientSocket.close();
    }
}
