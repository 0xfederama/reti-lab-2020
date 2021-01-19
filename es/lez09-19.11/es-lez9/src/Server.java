import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    public static void main(String[] args) throws IOException {
        try ( DatagramSocket serverSocket = new DatagramSocket(6789)){
            byte[] receive = new byte[4];
            DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
            while (true) {
                    serverSocket.receive(receivePacket);
                    String rec = new String(receivePacket.getData());
                    if (!rec.equals("Ping")) {
                        serverSocket.close();
                        continue;
                    }
                    System.out.println("Server ha ricevuto Ping");
                    String send = "Pong";
                    DatagramPacket sendPacket = new DatagramPacket(send.getBytes(), send.getBytes().length, receivePacket.getAddress(), receivePacket.getPort());
                    serverSocket.send(sendPacket);
                    System.out.println("Server ha risposto pong");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
