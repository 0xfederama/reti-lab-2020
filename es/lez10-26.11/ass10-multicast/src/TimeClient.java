import java.io.IOException;
import java.net.*;

public class TimeClient {
    public static void main(String[] args) throws UnknownHostException {

        //Controllo degli argomenti
        if (args.length != 1) {
            System.out.println("Usage: java TimeClient <server-name>");
            return;
        }

        InetAddress group;
        int port = 6789;
        try {
            group = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println("ERR -ip "+args[0]+": unknown host");
            System.out.println("Usage: java TimeClient <server-name>");
            return;
        }

        System.out.println("Client apre la connessione su "+ group.toString() +":"+port+"\n");

        //Ricevo la data e ora dal server per 10 volte
        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(group);
            for (int i=0; i<10; ++i) {
                byte[] buffer = new byte[19];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(datagramPacket);
                String s = new String(datagramPacket.getData());
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Chiudo la socket e esco dal gruppo
        if (multicastSocket!=null) {
            try {
                multicastSocket.leaveGroup(group);
                multicastSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nClient chiude la connessione...");

    }
}
