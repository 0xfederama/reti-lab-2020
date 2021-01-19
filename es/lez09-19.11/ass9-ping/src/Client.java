import java.io.IOException;
import java.net.*;

public class Client {

    public static void main(String[] args) throws SocketException {

        //Controllo degli argomenti
        if (args.length!=2) {
            System.out.println("Usage: java Client <server-name> <port>");
            return;
        }

        InetAddress ipAddress;
        int serverPort;
        try {
            ipAddress = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);
        } catch (UnknownHostException e) {
            System.out.println("ERR -ip "+args[0]+": unknown host");
            System.out.println("Usage: java Client <server-name> <port>");
            return;
        } catch (NumberFormatException e) {
            System.out.println("ERR -port "+args[1]+": 0 < port < 65536");
            System.out.println("Usage: java Client <server-name> <port>");
            return;
        }

        System.out.printf("Client si connette a %s sulla porta %d\n\n", ipAddress.toString(), serverPort);

        //Socket di invio da client a server
        DatagramSocket clientSocket = new DatagramSocket();

        //Variabili di controllo del ping
        int paccRicevuti = 0;
        int minRTT = Integer.MAX_VALUE;
        int maxRTT = 0;
        int sumRTT = 0;

        //Devo inviare 10 pacchetti per trovare il ping
        for (int i=0; i<10; i++) {
            try {

                //Invia il pacchetto al server
                long tSend = System.currentTimeMillis(); //ms since epoch
                String send = "PING "+i+" "+tSend; //20 caratteri
                DatagramPacket sendPacket = new DatagramPacket(send.getBytes(), send.getBytes().length, ipAddress, serverPort);
                clientSocket.send(sendPacket);
                System.out.print(send+" RTT: ");
                clientSocket.setSoTimeout(2000);

                //Attende risposta del server
                byte[] buffer = new byte[2];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(receivePacket);

                //Calcola i tempi
                long tRec = System.currentTimeMillis();
                int rtt = (int)(tRec-tSend);
                if (rtt<minRTT) minRTT=rtt;
                if (rtt>maxRTT) maxRTT=rtt;
                sumRTT+=rtt;
                paccRicevuti++;
                System.out.println(rtt+" ms");

            } catch (SocketTimeoutException e) {
                System.out.println("*");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //Stampe del ping
        double avgRTT = (double)sumRTT/(double)paccRicevuti;
        System.out.println("\n---- PING STATISTICS ----");
        System.out.printf("10 packets transmitted, %d packets received, %d%% packet loss\n", paccRicevuti, (10-paccRicevuti)*10);
        System.out.printf("round-trip (ms) min/avg/max = %d/%.2f/%d\n", minRTT, avgRTT, maxRTT);

    }
}
