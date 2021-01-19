import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Server {
    public static void main(String[] args) throws SocketException {

        //Controllo degli argomenti
        int port;
        int seed = 0;
        if (args.length==0) {
            port = 6789;    //Porta di default se non viene specificata da terminale
        } else {
            try {
                port = Integer.parseInt(args[0]);
                if (args.length==2) seed = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("ERR -port -seed : not numbers");
                System.out.println("Usage: java Server <port> <seed>");
                return;
            }
        }

        //Apro il server in ascolto
        DatagramSocket serverSocket = new DatagramSocket(port);
        byte[] receive = new byte[20];
        DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);

        //Random per le probabilita' di delay e perdita
        Random rand;
        if (seed==0) rand = new Random();
        else rand = new Random(seed);

        System.out.printf("Server attivo sulla porta %d ", port);
        if (seed==0) System.out.println("senza seed\n");
        else System.out.printf("con seed=%d\n\n", seed);

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
