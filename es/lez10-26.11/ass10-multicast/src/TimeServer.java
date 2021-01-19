import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class TimeServer {
    public static void main(String[] args) {

        //Controllo degli argomenti
        if (args.length != 1) {
            System.out.println("Usage: java TimeServer <server-name>");
            return;
        }

        InetAddress ip;
        try {
            ip = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println("ERR -ip "+args[0]+": unknown host");
            System.out.println("Usage: java TimeServer <server-name>");
            return;
        }

        //Invio di data e ora ogni secondo
        try {
            int port = 6789;
            DatagramSocket datagramSocket = new DatagramSocket();
            //MulticastSocket ms = new MulticastSocket(6789);
            //ms.joinGroup(ip);
            //ms.setTimeToLive(255);
            System.out.println("Server apre per invio a "+ip.toString()+":"+port+"\n");
            while(true) {
                Date date=new Date();
                String s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
                byte[] data = s.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length, ip, port);
                datagramSocket.send(datagramPacket);
                //ms.send(datagramPacket);
                System.out.println("Server ha inviato "+s);
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
