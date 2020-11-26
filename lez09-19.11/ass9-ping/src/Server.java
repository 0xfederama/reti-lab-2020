import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Server {
    public static void main(String[] args) {

        DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(6789);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        byte[] receive = new byte[20];
        DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
        Random rand = new Random();

        System.out.println("Server attivo sulla porta 6789\n");

        while (true) {
            try {

                //Ricevo richiesta dal client
                serverSocket.receive(receivePacket);
                String rec = new String(receivePacket.getData());
                System.out.print(receivePacket.getAddress()+":"+receivePacket.getPort()+"> "+rec+" ACTION: ");

                //Probabilita' di perdita del pacchetto 1/4
                if (rand.nextInt(4) == 0) {
                    System.out.println("not sent");
                    continue;
                }
                //Ritardo nella risposta 100 <= delay <= 500
                try {
                    int delay = rand.nextInt(400) +100;
                    Thread.sleep(delay);
                    System.out.println("delayed "+delay+" ms");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Invio risposta al client
                String send = "OK";
                DatagramPacket sendPacket = new DatagramPacket(send.getBytes(), send.getBytes().length, receivePacket.getAddress(), receivePacket.getPort());
                serverSocket.send(sendPacket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
